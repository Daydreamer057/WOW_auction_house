package application;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Currency;
import entity.CurrencyId;
import entity.Item;
import entity.Realm;
import jakarta.annotation.PostConstruct;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import service.CurrencyService;
import service.ItemService;
import service.RealmService;
import utils.MappedAuctions;
import utils.MappedConnectedRealm;
import utils.MappedItems;
import utils.MappedRealm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component // Marks this class as a Spring-managed component
public class UpdateDB {

    private static final Logger log = LoggerFactory.getLogger(UpdateDB.class);

    private static int compteurPrice = 0;

    @Value("${CLIENT_ID}")
    private String clientId; // OAuth client ID

    @Value("${CLIENT_SECRET}")
    private String clientSecret; // OAuth client secret

    String token = ""; // Stores OAuth token

    // Services injected via constructor
    private final RealmService realmService;
    private final ItemService itemService;
    private final CurrencyService currencyService;

    @Autowired
    public UpdateDB(RealmService realmService, ItemService itemService, CurrencyService currencyService) {
        this.realmService = realmService;
        this.itemService = itemService;
        this.currencyService = currencyService;
    }

    @PostConstruct
    public void init() {
        System.out.println("Starting application.UpdateDB...");
        this.token = getAccessToken(); // Retrieve access token when Spring initializes the bean
        long begin = System.currentTimeMillis();
//        saveAllItem();
        getAllPrices();
        long end = System.currentTimeMillis();
        System.out.println("Time " + (end - begin) / 60000);
    }

    // Gets the access token from Blizzard's OAuth service
    public String getAccessToken() {
        String TOKEN_URL = "https://eu.battle.net/oauth/token";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(TOKEN_URL);

            // Set Authorization header with base64 encoded clientId:clientSecret
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            post.setHeader("Authorization", "Basic " + encodedAuth);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            // Set body content to get client credentials token
            post.setEntity(new StringEntity("grant_type=client_credentials"));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(result);
                return jsonObject.getString("access_token");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void saveAllItem() {
        System.out.println("Begin Search Items " + System.currentTimeMillis());

        String listId = "";
        try {
            FileReader fr = new FileReader("e://listes/mounts.txt");
            BufferedReader br = new BufferedReader(fr);

            listId = br.readLine();

            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] ids = listId.split(",");

        for (String idTemp : ids) {
            int i = Integer.parseInt(idTemp.trim());
            Item item = itemService.getById(i);
            if (item == null) {
                try {
                    item = new Item();
                    String auctionUrl = "https://eu.api.blizzard.com/data/wow/item/" + i +
                            "?namespace=static-eu&access_token=" + token;

                    CloseableHttpClient httpClient = HttpClients.createDefault();
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    HttpGet request = new HttpGet(auctionUrl);
                    request.setHeader("Authorization", "Bearer " + token);

                    try (CloseableHttpResponse response = httpClient.execute(request)) {
                        int status = response.getStatusLine().getStatusCode();

                        if (status == 200) {
                            String result = EntityUtils.toString(response.getEntity());
                            if (result != null) {
                                MappedItems.Root root = objectMapper.readValue(result, MappedItems.Root.class);

                                item.setId(root.id);
                                item.setDeDe(root.name.de_DE);
                                item.setEnGb(root.name.en_GB);
                                item.setEsEs(root.name.es_ES);
                                item.setFrFr(root.name.fr_FR);
                                item.setItIt(root.name.it_IT);
                                item.setPtBr(root.name.pt_BR);
                                item.setRuRu(root.name.ru_RU);
                                item.setCurrencies(new HashSet<>());
                                item.setItemClass(root.item_class.name.en_GB);

                                itemService.save(item);
                            }
                        }
                        response.close();
                        httpClient.close();
                    } catch (Exception e) {
                        System.err.println("Failed item ID: " + i);
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getAllPrices() {
        List<Realm> realmList = realmService.getDistinctConnectedRealms();
        List<Item> itemList = itemService.getAll();

        // Use Map for O(1) item lookup
        Map<Integer, Item> itemMap = itemList.stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Efficient HTTP client with connection pooling
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(50);
        cm.setDefaultMaxPerRoute(10);
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

        ExecutorService executor = Executors.newFixedThreadPool(10); // or dynamically tuned

        int compteurRealm = 0;
        for (Realm realm : realmList) {
            executor.submit(() -> {
                try {
                    System.out.println("realm "+realm.getConnectedRealmId());
                    String url = "https://eu.api.blizzard.com/data/wow/connected-realm/" +
                            realm.getConnectedRealmId() + "/auctions?namespace=dynamic-eu&locale=en_GB&access_token=" + token;

                    HttpGet request = new HttpGet(url);
                    request.setHeader("Authorization", "Bearer " + token);

                    try (CloseableHttpResponse response = httpClient.execute(request)) {
                        if (response.getStatusLine().getStatusCode() != 404) {

                            String result = EntityUtils.toString(response.getEntity());
                            if (result != null && !result.isEmpty()) {

                                MappedAuctions.Auctions auctions = mapper.readValue(result, MappedAuctions.Auctions.class);

                                for (MappedAuctions.Auction auction : auctions.auctions) {
                                    Item item = itemMap.get(auction.item.id);
                                    if (item != null) {
                                        CurrencyId currencyId = new CurrencyId(item.getId(), realm.getId());
                                        Currency currency = currencyService.getById(currencyId);

                                        long buyout = auction.buyout;
                                        if (currency == null) {
                                            currency = new Currency();
                                            currency.setId(currencyId);
                                            currency.setItem(item);
                                            currency.setRealm(realm);
                                            currency.setCost(buyout);
                                            currencyService.save(currency);
                                        } else if (!currency.getCost().equals(buyout)) {
                                            currency.setCost(buyout);
                                            currencyService.save(currency);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Error processing realm " + realm.getConnectedRealmId(), e);
                }
            });
        }

        executor.shutdown();

        while(!executor.isTerminated()){
            try {
                Thread.sleep(1000);
            } catch (Exception e){

            }
        }
        System.out.println("End Search Items " + System.currentTimeMillis());
    }


    public void saveRealms(){
        try {
            String auctionUrl = "https://eu.api.blizzard.com/data/wow/connected-realm/index?namespace=dynamic-eu&access_token=" + token;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(auctionUrl);
            request.setHeader("Authorization", "Bearer " + token);
            CloseableHttpResponse response = httpClient.execute(request);
            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() != 404) {
                String result = EntityUtils.toString(response.getEntity());

                if (result != null || !result.isEmpty()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    MappedConnectedRealm.ConnectedRealms connectedRealms = objectMapper.readValue(result, MappedConnectedRealm.ConnectedRealms.class);

                    for(MappedConnectedRealm.ConnectedRealm connectedRealm : connectedRealms.connected_realms) {
                        auctionUrl = connectedRealm.href + "&access_token=" + token;
                        httpClient = HttpClients.createDefault();
                        request = new HttpGet(auctionUrl);
                        request.setHeader("Authorization", "Bearer " + token);
                        response = httpClient.execute(request);
                        System.out.println(response.getStatusLine().getStatusCode());
                        if (response.getStatusLine().getStatusCode() != 404) {
                            result = EntityUtils.toString(response.getEntity());

                            if (result != null || !result.isEmpty()) {
                                objectMapper = new ObjectMapper();
                                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                                MappedRealm.Root realmRoot = objectMapper.readValue(result, MappedRealm.Root.class);

                                MappedRealm.Realm realm = realmRoot.realms.get(0);


                                int realmId = Integer.parseInt(realm.connected_realm.href.substring(realm.connected_realm.href.lastIndexOf("/") + 1, realm.connected_realm.href.lastIndexOf("?")));
                                String realmLocale = realm.locale;
                                String realmName = "";

                                switch (realmLocale) {

                                    case "ptBR":
                                        realmName = realm.name.pt_BR;
                                        break;

                                    case "deDE":
                                        realmName = realm.name.de_DE;
                                        break;

                                    case "enGB":
                                        realmName = realm.name.en_GB;
                                        break;

                                    case "esES":
                                        realmName = realm.name.es_ES;
                                        break;

                                    case "frFR":
                                        realmName = realm.name.fr_FR;
                                        break;

                                    case "itIT":
                                        realmName = realm.name.it_IT;
                                        break;

                                    case "ruRU":
                                        realmName = realm.name.ru_RU;
                                        break;

                                    default:
                                        realmName = realm.name.en_GB;
                                        break;
                                }

//                                System.out.println("Realm id "+realm.id+"    connectedId "+realmId+"    population "+populationType);

                                Realm realmEntity = realmService.getByConnectedRealmId(realmId);
                                if (realmEntity == null) {
                                    realmEntity = new Realm();
                                    realmEntity.setId(realm.id);
                                    realmEntity.setCurrencies(new HashSet<>()); // initialize if new
                                    realmEntity.setLocale(realm.locale.substring(0, 2) + "_" + realm.locale.substring(2, 4));
                                    realmEntity.setName(realmName);
                                    realmEntity.setConnectedRealmId(realmId);
                                    realmEntity.setPopulationType(realmRoot.population.type);

                                    realmService.save(realmEntity);
                                }
                            }
                        }
                        response.close();
                        httpClient.close();
                    }
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Item getItemById(final List<Item> list, final int id) throws NoSuchElementException {
        return list.stream().filter(o -> o.getId() == id).findFirst().orElse(null);
    }

}