package dao;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.HashMap;

@Component
public class UpdateDB {
    @Value("${CLIENT_ID}")
    private String clientId;

    @Value("${CLIENT_SECRET}")
    private String clientSecret;

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
        HashMap<Integer, String> items = new HashMap<>();

        int i=0;

        System.out.println("I "+i);


        String auctionUrl = "https://eu.api.blizzard.com/data/wow/search/item?namespace=static-eu&orderby=id&_pageSize=1000&id=["+1000*i+"]&_page=1&access_token=" + token;

        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(auctionUrl);
            request.setHeader("Authorization", "Bearer " + token);
            CloseableHttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 404) {
                String result = EntityUtils.toString(response.getEntity());

                if (result != null || !result.isEmpty()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//                    ItemMain item = objectMapper.readValue(result, ItemMain.class);

//                    items.put(item.id, item.name);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //=================================================================================================================================================
    // Items


}