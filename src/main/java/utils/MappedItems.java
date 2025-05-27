package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

public class MappedItems {
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
        public DisplayString display_string;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Binding{
        public String type;
        public Name name;
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
        public DisplayString display_string;
        public DamageClass damage_class;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DamageClass{
        public String type;
        public Name name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Description{
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
    public static class Display{
        public DisplayString display_string;
        public Color color;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DisplayString{
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
    public static class Dps{
        public double value;
        public DisplayString display_string;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Durability{
        public int value;
        public DisplayString display_string;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InventoryType{
        public String type;
        public Name name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item{
        public Key key;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemClass{
        public Key key;
        public Name name;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemSubclass{
        public Key key;
        public Name name;
        public int id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Key{
        public String href;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Level{
        public int value;
        public DisplayString display_string;
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
    public static class PreviewItem{
        public Item item;
        public int context;
        public ArrayList<Integer> bonus_list;
        public Quality quality;
        public Name name;
        public Media media;
        public ItemClass item_class;
        public ItemSubclass item_subclass;
        public InventoryType inventory_type;
        public Binding binding;
        public UniqueEquipped unique_equipped;
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
        public Name name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Requirements{
        public Level level;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Root{
        public Links _links;
        public int id;
        public Name name;
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
    public static class Spell{
        public Spell spell;
        public Description description;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Spell2{
        public Key key;
        public Name name;
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
        public Name name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UniqueEquipped{
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
    public static class Weapon{
        public Damage damage;
        public AttackSpeed attack_speed;
        public Dps dps;
    }


}
