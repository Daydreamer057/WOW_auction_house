package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

public class MappedRealm {
    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Auctions{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Category{
        public String en_US;
        public String es_MX;
        public String pt_BR;
        public String de_DE;
        public String en_GB;
        public String es_ES;
        public String fr_FR;
        public String it_IT;
        public String ru_RU;
        public String ko_KR;
        public String zh_TW;
        public String zh_CN;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConnectedRealm{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Key{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links{
        public Self self;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MythicLeaderboards{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Name{
        public String en_US;
        public String es_MX;
        public String pt_BR;
        public String de_DE;
        public String en_GB;
        public String es_ES;
        public String fr_FR;
        public String it_IT;
        public String ru_RU;
        public String ko_KR;
        public String zh_TW;
        public String zh_CN;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Population{
        public String type;
        public Name name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Realm{
        public int id;
        public Region region;
        public ConnectedRealm connected_realm;
        public Name name;
        public Category category;
        public String locale;
        public String timezone;
        public Type type;
        public boolean is_tournament;
        public String slug;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Region{
        public Key key;
        public Name name;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Root{
        public Links _links;
        public int id;
        public boolean has_queue;
        public Status status;
        public Population population;
        public ArrayList<Realm> realms;
        public MythicLeaderboards mythic_leaderboards;
        public Auctions auctions;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Self{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status{
        public String type;
        public Name name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Type{
        public String type;
        public Name name;
    }


}
