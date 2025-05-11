package application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.BattlePet;
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
import service.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component // Marks this class as a Spring-managed component
public class UpdateDB {

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
        getAllBattlePets();
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

    public void getAllBattlePets() {
        String url = "https://eu.api.blizzard.com/data/wow/pet/index?namespace=static-eu&locale=en_GB&access_token=" + token;

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", "Bearer " + token);

            CloseableHttpResponse response = httpClient.execute(request);
            System.out.println(response.getStatusLine().getStatusCode());

            if (response.getStatusLine().getStatusCode() != 404) {
                String result = EntityUtils.toString(response.getEntity());

                if (result != null && !result.isEmpty()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    Pets pets = objectMapper.readValue(result, Pets.class);

                    // Loop through each mount
                    for (Pet petTemp : pets.pets) {
                        // Try to find items that contain the mount name
                        List<entity.Item> items = itemService.getByNameContaining(petTemp.name.toLowerCase());

                        if (items != null && items.size() > 0) {
                            // If we haven't already saved this mount, do it
                            entity.BattlePet existing = battlePetService.getById(items.get(0).getId());
                            if (existing == null) {
                                BattlePet battlePet = new BattlePet();
                                battlePet.setId(items.get(0).getId());
                                battlePet.setName(petTemp.name);
                                battlePetService.save(battlePet);
                            }
                        }
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

    //===================================================================================================================================
    // Battle Pets

    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */


    public static class Pet{
        public Key key;
        public String name;
        public int id;
    }

    public static class Pets{
        public Links _links;
        public ArrayList<Pet> pets;
    }
}