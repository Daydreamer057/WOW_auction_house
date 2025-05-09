package application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import service.CurrencyService;
import service.ItemService;
import service.MountService;
import service.RealmService;

import java.util.ArrayList;
import java.util.Base64;

@Component
public class UpdateDB {
    private final MountService mountService;
    @Value("${CLIENT_ID}")
    private String clientId;

    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    String token = "";

    private final RealmService realmService;
    private final ItemService itemService;
    private final CurrencyService currencyService;

    @Autowired
    public UpdateDB(RealmService realmService, ItemService itemService, CurrencyService currencyService, MountService mountService) {
        this.realmService = realmService;
        this.itemService = itemService;
        this.currencyService = currencyService;
        this.mountService = mountService;
    }

    @PostConstruct
    public void init() {
        System.out.println("Starting application.UpdateDB...");
        this.token = getAccessToken();
        getAllItem();
    }

    public String getAccessToken(){
        String TOKEN_URL = "https://eu.battle.net/oauth/token";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(TOKEN_URL);

            // Base64 encode the client_id and client_secret
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            post.setHeader("Authorization", "Basic " + encodedAuth);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            StringEntity entity = new StringEntity("grant_type=client_credentials");
            post.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String result = EntityUtils.toString(response.getEntity());

                httpClient.close();

                JSONObject jsonObject = new JSONObject(result);

                return jsonObject.getString("access_token");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void getAllItem() {
        int i = 27948;
        int empty = 0;
        while(true) {
            System.out.println("I " + i);

//            String auctionUrl = "https://eu.api.blizzard.com/data/wow/search/item?namespace=static-eu&name.en_GB=&orderby=id&_pageSize=1000&id=[" + ((1000 * i) + 1) + "]&_page=1&access_token=" + token;
            String auctionUrl = "https://eu.api.blizzard.com/data/wow/item/"+i+"?namespace=static-eu&locale=en_GB&access_token=" + token;

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

                        ItemMain root = objectMapper.readValue(result, ItemMain.class);

                        entity.Item item = new entity.Item();

                        item.setId(root.id);
                        item.setName(root.name);

                        itemService.save(item);

                        empty = 0;

                    } else {
                        System.out.println("No Further items found");
                    }
                } else {
                    System.out.println("No items found");
                    empty++;
//                    if(empty > 1000) {
//                        System.exit(0);
//                    }
                }
                Thread.sleep(200);
                i++;
                if(i>200000){
                    System.exit(0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void getAllMounts(){
        String auctionUrl = "https://eu.api.blizzard.com/data/wow/mount/index?namespace=static-eu&locale=en_GB&access_token=" + token;

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

                    Mounts mounts = objectMapper.readValue(result, Mounts.class);

                    for(Mount mountTemp : mounts.mounts) {
                        entity.Item item = itemService.getByName(mountTemp.name);

                        entity.Mount mount = new entity.Mount();

                        mount.setId(item.getId());
                        mount.setName(mountTemp.name);

                        mountService.save(mount);
                    }
                } else {
                    System.out.println("Mounts not found");
                }
            } else {
                System.out.println("Error 404");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //=================================================================================================================================================
    // Items
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
    public static class DisplayStrings{
        public String header;
        public String gold;
        public String silver;
        public String copper;
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
    public static class Item{
        public Key key;
        public int id;
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
    public static class Key{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Level{
        public int value;
        public String display_string;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links{
        public Self self;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Media{
        public Key key;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PreviewItem{
        public Item item;
        public Quality quality;
        public String name;
        public Media media;
        public ItemClass item_class;
        public ItemSubclass item_subclass;
        public InventoryType inventory_type;
        public Binding binding;
        public Weapon weapon;
        public SellPrice sell_price;
        public Level level;
        public Durability durability;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Quality{
        public String type;
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemMain{
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
    public static class Self{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SellPrice{
        public int value;
        public DisplayStrings display_strings;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weapon{
        public Damage damage;
        public AttackSpeed attack_speed;
        public Dps dps;
    }

    //=================================================================================================================================
    // mounts

    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Mount{
        public Key key;
        public String name;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Mounts{
        public Links _links;
        public ArrayList<Mount> mounts;
    }

}