package dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.service.CurrencyService;
import dao.service.ItemService;
import dao.service.RealmService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;

@Component
public class UpdateDB {
    String token = getAccessToken();

    private final RealmService realmService;
    private final ItemService itemService;
    private final CurrencyService currencyService;

    @Autowired
    public UpdateDB(RealmService realmService, ItemService itemService, CurrencyService currencyService) {
        this.realmService = realmService;
        this.itemService = itemService;
        this.currencyService = currencyService;

        System.out.println("Starting UpdateDB");
        alimRealms();
    }

    public String getAccessToken(){
        String TOKEN_URL = "https://eu.battle.net/oauth/token";
        String CLIENT_ID = "652651cc7f04402ea349c51415a6787f";
        String CLIENT_SECRET = "RBgCazanJADsZcLOlOXHe7UrtqE1wH85";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(TOKEN_URL);

            // Base64 encode the client_id and client_secret
            String auth = CLIENT_ID + ":" + CLIENT_SECRET;
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

    public void alimRealms(){
        String auctionUrl = "https://eu.api.blizzard.com/data/wow/realm/index?namespace=dynamic-eu&locale=en_GB&access_token="+token;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(auctionUrl);
            request.setHeader("Authorization", "Bearer " + token);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String result = EntityUtils.toString(response.getEntity());

                HashSet<Realm> realms = getListRealms(result);

                for(Realm realm : realms) {
                    dao.entity.Realm realmEntity = new dao.entity.Realm();
                    realmEntity.setId(realm.id);
                    realmEntity.setName(realm.name);

                    realmService.save(realmEntity);
                }

            } catch (Exception ex){
                ex.printStackTrace();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public HashSet<Realm> getListRealms(String result){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Realms realms = objectMapper.readValue(result, Realms.class);

            return new HashSet(realms.getRealms());
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return new HashSet<>();
    }

    public void getAllItem() {
        HashMap<Integer, String> items = new HashMap<>();

        for (int i = 1; i < 500000; i++) {
            System.out.println(i);
            String auctionUrl = "https://eu.api.blizzard.com/data/wow/item/" + i + "?namespace=static-eu&locale=en_GB&access_token=" + token;

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(auctionUrl);
                request.setHeader("Authorization", "Bearer " + token);
                CloseableHttpResponse response = httpClient.execute(request);
                if (response.getStatusLine().getStatusCode() != 404) {
                    String result = EntityUtils.toString(response.getEntity());

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    ItemMain item = objectMapper.readValue(result, ItemMain.class);

                    items.put(item.id, item.name);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        try (FileOutputStream fileOut = new FileOutputStream("./listeItems.txt");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(new Items(items));
            out.flush();
            out.close();
            System.out.println("Object Items has been serialized and saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashSet<Realm> getListItems(String result){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Realms realms = objectMapper.readValue(result, Realms.class);

            return new HashSet(realms.getRealms());
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return new HashSet<>();
    }

    //=================================================================================================================================================
    // Realms

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Key{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links{
        public Self self;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Realm{
        public Key key;
        public String name;
        public int id;
        public String slug;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Realms{
        public Links _links;
        public HashSet<Realm> realms;

        public HashSet<Realm> getRealms() {
            return realms;
        }

        public void setRealms(HashSet<Realm> realms) {
            this.realms = realms;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Self{
        public String href;
    }

    //=================================================================================================================================================
    // Items

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
        public Quality quality;
        public String name;
        public Media media;
        public ItemClass item_class;
        public ItemSubclass item_subclass;
        public InventoryType inventory_type;
        public Binding binding;
        public Weapon weapon;
        public ArrayList<Stat> stats;
        public SellPrice sell_price;
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
    public static class SellPrice{
        public int value;
        public DisplayStrings display_strings;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stat{
        public Type type;
        public int value;
        public boolean is_negated;
        public Display display;
        public boolean is_equip_bonus;
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

    //==========================================================================================================================

    public class Items implements Serializable {
        private HashMap<Integer, String> listItems = new HashMap<>();

        public Items(HashMap<Integer, String> list) {
            this.listItems = list;
        }
        @Override
        public String toString() {
            return "test";
        }

        public HashMap<Integer, String> getListItems() {
            return listItems;
        }

        public void setListItems(HashMap<Integer, String> listItems) {
            this.listItems = listItems;
        }
    }
}