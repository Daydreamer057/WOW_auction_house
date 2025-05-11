package application;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.*;
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

import java.math.BigDecimal;
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

    public void getMountPrice(){
        List<Mount> mountList = mountService.getAll();
        List<entity.Realm> realmList = realmService.getAll();

        for(Mount mountTemp : mountList){
            for(entity.Realm realmTemp : realmList) {
                String url = "https://eu.api.blizzard.com/data/wow/connected-realm/"+realmTemp.getId()+"/auctions?namespace=dynamic-eu&locale=en_GB&access_token=" + token;

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

                            Auctions auctions = objectMapper.readValue(result, Auctions.class);
                            long minPrice = 999999999999999999L;
                            int id = 0;
                            for(Auction auctionTemp : auctions.auctions){
                                if(auctionTemp.buyout < minPrice){
                                    minPrice = auctionTemp.buyout;
                                    id = auctionTemp.id;
                                }
                            }

                            CurrencyId currencyId = new CurrencyId(mountTemp.getId(),realmTemp.getId());
                            Currency currency = currencyService.getById(currencyId);

                            if(currency == null) {
                                currency.setCost(BigDecimal.valueOf(minPrice));
                                currency.setItem(itemService.getById(mountTemp.getId()));
                                currency.setRealm(realmTemp);
                                currency.setId(currencyId);
                            } else {
                                currency.setCost(BigDecimal.valueOf(minPrice));
                            }

                            currencyService.save(currency);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
    public static class Auction{
        public int id;
        public Item item;
        public int buyout;
        public int quantity;
        public String time_left;
    }

    public static class Commodities{
        public String href;
    }

    public static class ConnectedRealm{
        public String href;
    }

    public static class Item{
        public int id;
        public ArrayList<Integer> bonus_lists;
        public ArrayList<Modifier> modifiers;
        public int context;
        public int pet_breed_id;
        public int pet_level;
        public int pet_quality_id;
        public int pet_species_id;
    }

    public static class Links{
        public Self self;
    }

    public static class Modifier{
        public int type;
        public int value;
    }

    public static class Auctions{
        public Links _links;
        public ConnectedRealm connected_realm;
        public ArrayList<Auction> auctions;
        public Commodities commodities;
    }

    public static class Self{
        public String href;
    }

    //==================================================================================================================================================
    // Realms

    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
    public static class Realm{
        public Key key;
        public String name;
        public int id;
        public String slug;
    }

    public static class Realms{
        public Links _links;
        public ArrayList<Realm> realms;
    }

    public static class Key{
        public String href;
    }
}