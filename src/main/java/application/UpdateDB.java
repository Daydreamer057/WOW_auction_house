package application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.*;
import entity.Currency;
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
import service.*;
import utils.MountPrice;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component // Marks this class as a Spring-managed component
public class UpdateDB {

    private static final Logger log = LoggerFactory.getLogger(UpdateDB.class);

    private static int compteurPrice = 0;

    // Injects mountService
    private final MountService mountService;
    private final BattlePetService battlePetService;

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
    public UpdateDB(RealmService realmService, ItemService itemService, CurrencyService currencyService, MountService mountService, BattlePetService battlePetService) {
        this.realmService = realmService;
        this.itemService = itemService;
        this.currencyService = currencyService;
        this.mountService = mountService;
        this.battlePetService = battlePetService;
    }

    @PostConstruct
    public void init() {
        System.out.println("Starting application.UpdateDB...");
        this.token = getAccessToken(); // Retrieve access token when Spring initializes the bean
        long begin = System.currentTimeMillis();
//        getAllItem();
        getMountPrice();
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

    public void getMountPrice() {
        List<Mount> mountList = mountService.getAll();
        List<BattlePet> battlePetList = battlePetService.getAll();
        List<entity.Realm> realmList = realmService.getAll();

        Map<Integer, MountPrice> itemMap = new HashMap<>();
        battlePetList.forEach(i -> itemMap.put(i.getId(), new MountPrice(i.getId(), i.getName())));
        mountList.forEach(m -> itemMap.put(m.getId(), new MountPrice(m.getId(), m.getName())));

        ExecutorService executor = Executors.newFixedThreadPool(10);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        for (Realm realm : realmList) {
            executor.submit(() -> {
                compteurPrice++;
                System.out.println("Compteur price: " + compteurPrice + "/ " + realmList.size());
                try {
                    String url = "https://eu.api.blizzard.com/data/wow/connected-realm/" +
                            realm.getId() + "/auctions?namespace=dynamic-eu&locale=en_GB&access_token=" + token;

                    HttpGet request = new HttpGet(url);
                    request.setHeader("Authorization", "Bearer " + token);
                    CloseableHttpResponse response = httpClient.execute(request);

                    if (response.getStatusLine().getStatusCode() != 404) {
                        String result = EntityUtils.toString(response.getEntity());
                        if (result != null && !result.isEmpty()) {
                            Auctions auctions = mapper.readValue(result, Auctions.class);

                            Map<Integer, Auction> cheapestMap = new HashMap<>();
                            for (Auction auction : auctions.auctions) {
                                int itemId = auction.item.id;
                                if (!cheapestMap.containsKey(itemId) || auction.buyout < cheapestMap.get(itemId).buyout) {
                                    cheapestMap.put(itemId, auction);
                                }
                            }

                            for (Map.Entry<Integer, Auction> entry : cheapestMap.entrySet()) {
                                int itemId = entry.getKey();
                                Auction cheapest = entry.getValue();

                                if (itemMap.containsKey(itemId)) {
                                    CurrencyId currencyId = new CurrencyId(itemId, realm.getId());
                                    Currency currency = currencyService.getById(currencyId);

                                    if (currency == null) {
                                        Item item = itemService.getById(itemId);
                                        if (item != null) {
                                            currency = new Currency();
                                            currency.setId(currencyId);
                                            currency.setItem(item);
                                            currency.setCost(BigDecimal.valueOf(cheapest.buyout));
                                            currency.setRealm(realm);
                                            currencyService.save(currency);
                                        }
                                    } else {
                                        if (currency.getCost().compareTo(BigDecimal.valueOf(cheapest.buyout)) != 0) {
                                            currency.setCost(BigDecimal.valueOf(cheapest.buyout));
                                            currencyService.save(currency);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void getAllItem() {
        List<Item> items = itemService.getAll();
        List<Mount> mountList = mountService.getAll();
        List<BattlePet> battlePetList = battlePetService.getAll();

        ArrayList<MountPrice> listItemCheck = new ArrayList<>();
        battlePetList.forEach(i -> listItemCheck.add(new MountPrice(i.getId(), i.getName())));
        mountList.forEach(m -> listItemCheck.add(new MountPrice(m.getId(), m.getName())));
        for (MountPrice mount : listItemCheck) {
            String auctionUrl = "https://eu.api.blizzard.com/data/wow/item/" + mount.getId() + "?namespace=static-eu&locale=en_GB&access_token=" + token;

            try {
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

                        Items root = objectMapper.readValue(result, Items.class);

                        if((root.quality.type.toLowerCase().contains("common")||root.quality.type.toLowerCase().contains("poor"))&&!root.quality.type.toLowerCase().contains("uncommon")){
                            Mount mountEntity = mountService.getById(mount.getId());
                            if(mountEntity!=null) {
                                mountService.delete(mountEntity);
                            }
                            BattlePet battlePetEntity = battlePetService.getById(mount.getId());
                            if(battlePetEntity!=null) {
                                battlePetService.delete(battlePetEntity);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }



    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Auction{
        public int id;
        public ItemInnerClass item;
        public long buyout;
        public int quantity;
        public String time_left;
        public long bid;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Commodities{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConnectedRealm{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemInnerClass{
        public int id;
        public ArrayList<Modifier> modifiers;
        public int pet_breed_id;
        public int pet_level;
        public int pet_quality_id;
        public int pet_species_id;
        public int context;
        public ArrayList<Integer> bonus_lists;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links{
        public Self self;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Modifier{
        public int type;
        public long value;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Auctions{
        public Links _links;
        public ConnectedRealm connected_realm;
        public ArrayList<Auction> auctions;
        public Commodities commodities;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Self{
        public String href;
    }



    //==================================================================================================================================================
    // Realms

    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RealminnerClass {
        public Key key;
        public String name;
        public int id;
        public String slug;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Realms{
        public Links _links;
        public ArrayList<RealminnerClass> realminnerClasses;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Key{
        public String href;
    }

    //=====================================================================================================================================================
    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Appearance{
        public Key key;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttackSpeed{
        public int value;
        public String display_string;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Binding{
        public String type;
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Color{
        public int r;
        public int g;
        public int b;
        public int a;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Damage{
        public int min_value;
        public int max_value;
        public String display_string;
        public DamageClass damage_class;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DamageClass{
        public String type;
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Display{
        public String display_string;
        public Color color;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Dps{
        public double value;
        public String display_string;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Durability{
        public int value;
        public String display_string;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InventoryType{
        public String type;
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemClass{
        public Key key;
        public String name;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemSubclass{
        public Key key;
        public String name;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Level{
        public int value;
        public String display_string;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Media{
        public Key key;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PreviewItem{
        public Item item;
        public int context;
        public ArrayList<Integer> bonus_list;
        public Quality quality;
        public String name;
        public Media media;
        public ItemClass item_class;
        public ItemSubclass item_subclass;
        public InventoryType inventory_type;
        public Binding binding;
        public String unique_equipped;
        public Weapon weapon;
        public ArrayList<Stat> stats;
        public ArrayList<Spell> spells;
        public Requirements requirements;
        public Level level;
        public Durability durability;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Quality{
        public String type;
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Requirements{
        public Level level;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items{
        public Links _links;
        public int id;
        public String name;
        public Quality quality;
        public int level;
        public int required_level;
        public Media media;
        public ItemClass item_class;
        public ItemSubclass item_subclass;
        public InventoryType inventory_type;
        public int purchase_price;
        public int sell_price;
        public int max_count;
        public boolean is_equippable;
        public boolean is_stackable;
        public PreviewItem preview_item;
        public int purchase_quantity;
        public ArrayList<Appearance> appearances;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Spell{
        public Spell spell;
        public String description;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Spell2{
        public Key key;
        public String name;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stat{
        public Type type;
        public int value;
        public boolean is_negated;
        public Display display;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Type{
        public String type;
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weapon{
        public Damage damage;
        public AttackSpeed attack_speed;
        public Dps dps;
    }


}