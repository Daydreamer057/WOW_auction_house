package application;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Item;
import jakarta.annotation.PostConstruct;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
import utils.ItemSell;
import utils.MappedItems;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        saveAllItem();
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
        System.out.println("Begin Search Items "+System.currentTimeMillis());
        final int start = 25;
        final int end = 3000000;
        final int THREAD_COUNT = 20;
        final int BATCH_SIZE = 50;

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        for (int i = start; i < end; i++) {
            final int itemId = i;

            executor.submit(() -> {
                List<Item> batch = new ArrayList<>();

                try {
                    String auctionUrl = "https://eu.api.blizzard.com/data/wow/item/" + itemId +
                            "?namespace=static-eu&access_token=" + token;

                    HttpGet request = new HttpGet(auctionUrl);
                    request.setHeader("Authorization", "Bearer " + token);

                    try (CloseableHttpResponse response = httpClient.execute(request)) {
                        int status = response.getStatusLine().getStatusCode();

                        if (status == 404) return;

                        String result = EntityUtils.toString(response.getEntity());
                        if (result == null || result.isEmpty()) return;

                        MappedItems.Root root = objectMapper.readValue(result, MappedItems.Root.class);

                        Map<String, String> nameMap = Map.of(
                                "pt_BR", root.name.pt_BR,
                                "de_DE", root.name.de_DE,
                                "en_GB", root.name.en_GB,
                                "es_ES", root.name.es_ES,
                                "fr_FR", root.name.fr_FR,
                                "it_IT", root.name.it_IT,
                                "ru_RU", root.name.ru_RU
                        );

                        for (Map.Entry<String, String> entry : nameMap.entrySet()) {
                            String locale = entry.getKey();
                            String name = entry.getValue();
//                            System.out.println("Running in thread: " + Thread.currentThread().getName());
                            if (itemService.getByItemId(root.id) == null) {
                                Item item = new Item();
                                item.setItemId(root.id);
                                item.setName(name);
                                item.setLocale(locale);
                                item.setCurrencies(new HashSet<>());
                                batch.add(item);
                            }

                            if (batch.size() >= BATCH_SIZE) {
                                System.out.println("Batch save "+batch.size());
                                itemService.batchInsertItems(batch);
                                batch.clear();
                            }
                        }

                        // Save remaining
                        if (!batch.isEmpty()) {
                            System.out.println("Batch save "+batch.size());
                            itemService.batchInsertItems(batch);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed item ID: " + itemId);
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }


    public void printResults(List<ItemSell> itemSellList){
        try {
            PrintWriter pw = new PrintWriter("e://temp/List_Wow_CrossRealm.txt");

            for(ItemSell itemSell : itemSellList){
                pw.println("");
            }

            pw.flush();
            pw.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }

    }
}