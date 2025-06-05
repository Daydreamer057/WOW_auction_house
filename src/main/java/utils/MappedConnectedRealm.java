package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

public class MappedConnectedRealm {
    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConnectedRealm{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links{
        public Self self;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConnectedRealms{
        public Links _links;
        public ArrayList<ConnectedRealm> connected_realms;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Self{
        public String href;
    }

}
