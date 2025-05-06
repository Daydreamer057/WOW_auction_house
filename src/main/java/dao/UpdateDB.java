package dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.entity.Item;
import dao.service.CurrencyService;
import dao.service.ItemService;
import dao.service.RealmService;
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

import java.util.ArrayList;
import java.util.Base64;

@Component
public class UpdateDB {
    @Value("${CLIENT_ID}")
    private String clientId;

    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    String token = "";

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
        System.out.println("Starting UpdateDB...");
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
        int i = 0;
        while(true) {
            System.out.println("I " + i);

//            String auctionUrl = "https://eu.api.blizzard.com/data/wow/search/item?namespace=static-eu&name.en_GB=&orderby=id&_pageSize=1000&id=[" + ((1000 * i) + 1) + "]&_page=1&access_token=" + token;
            String auctionUrl = "https://eu.api.blizzard.com/data/wow/item/"+i+"?namespace=static-eu&locale=en_GB&access_token=" + token;

            try {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet request = new HttpGet(auctionUrl);
                request.setHeader("Authorization", "Bearer " + token);
                CloseableHttpResponse response = httpClient.execute(request);
                if (response.getStatusLine().getStatusCode() != 404) {
                    String result = EntityUtils.toString(response.getEntity());

                    if (result != null || !result.isEmpty()) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                        Root root = objectMapper.readValue(result, Root.class);

                        for (Result resultItem : root.results) {
                            Item item = new Item();
                            item.setId(resultItem.data.id);
                            item.setName(resultItem.data.name.en_GB);

                            itemService.save(item);
                        }
                    } else {
                        System.out.println("No Further items found");
                    }
                } else {
                    System.out.println("No items found");
                }
                i++;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data{
        public int level;
        public int required_level;
        public int sell_price;
        public ItemSubclass item_subclass;
        public boolean is_equippable;
        public int purchase_quantity;
        public Media media;
        public ItemClass item_class;
        public Quality quality;
        public int max_count;
        public boolean is_stackable;
        public ArrayList<Appearance> appearances;
        public Name name;
        public int purchase_price;
        public int id;
        public InventoryType inventory_type;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InventoryType{
        public Name name;
        public String type;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemClass{
        public Name name;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemSubclass{
        public Name name;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Key{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Media{
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Name{
        public String it_IT;
        public String ru_RU;
        public String en_GB;
        public String zh_TW;
        public String ko_KR;
        public String en_US;
        public String es_MX;
        public String pt_BR;
        public String es_ES;
        public String zh_CN;
        public String fr_FR;
        public String de_DE;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Quality{
        public Name name;
        public String type;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result{
        public Key key;
        public Data data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Root{
        public int page;
        public int pageSize;
        public int maxPageSize;
        public int pageCount;
        public ArrayList<Result> results;
    }



}