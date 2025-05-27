package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

public class MappedAuctions {
    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Auction{
        public int id;
        public Item item;
        public long buyout;
        public int quantity;
        public String time_left;
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
    public static class Item{
        public int id;
        public int context;
        public ArrayList<Integer> bonus_lists;
        public ArrayList<Modifier> modifiers;
        public int pet_breed_id;
        public int pet_level;
        public int pet_quality_id;
        public int pet_species_id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links{
        public Self self;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Modifier{
        public int type;
        public int value;
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


}
