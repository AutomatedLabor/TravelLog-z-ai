package com.example.travellog.data.model;

public class Category {

    public static final String RESTAURANT = "restaurant";
    public static final String CAFE = "cafe";
    public static final String BAR = "bar";
    public static final String SHOPPING = "shopping";
    public static final String ATTRACTION = "attraction";
    public static final String HOTEL = "hotel";
    public static final String NIGHTLIFE = "nightlife";
    public static final String NATURE = "nature";
    public static final String CULTURE = "culture";
    public static final String TRANSPORT = "transport";
    public static final String OTHER = "other";

    public final String id;
    public final String name;
    public final String icon;
    public final int colorRes;

    public Category(String id, String name, String icon, int colorRes) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.colorRes = colorRes;
    }

    public static final Category[] ALL_CATEGORIES = {
        new Category(RESTAURANT, "Restaurant", "restaurant", 0xFFFF6B6B),
        new Category(CAFE, "Cafe", "cafe", 0xFFFFA726),
        new Category(BAR, "Bar", "bar", 0xFFAB47BC),
        new Category(SHOPPING, "Shopping", "shopping", 0xFF42A5F5),
        new Category(ATTRACTION, "Attraction", "attraction", 0xFFEF5350),
        new Category(HOTEL, "Hotel", "hotel", 0xFF26A69A),
        new Category(NIGHTLIFE, "Nightlife", "nightlife", 0xFF7E57C2),
        new Category(NATURE, "Nature", "nature", 0xFF66BB6A),
        new Category(CULTURE, "Culture", "culture", 0xFFEC407A),
        new Category(TRANSPORT, "Transport", "transport", 0xFF78909C),
        new Category(OTHER, "Other", "other", 0xFFBDBDBD),
    };

    public static String getCategoryName(String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) return OTHER;
        for (Category c : ALL_CATEGORIES) {
            if (c.id.equals(categoryId)) return c.name;
        }
        return OTHER;
    }

    public static int getCategoryColor(String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) return 0xFFBDBDBD;
        for (Category c : ALL_CATEGORIES) {
            if (c.id.equals(categoryId)) return c.colorRes;
        }
        return 0xFFBDBDBD;
    }

    public static String[] getCategoryNames() {
        String[] names = new String[ALL_CATEGORIES.length];
        for (int i = 0; i < ALL_CATEGORIES.length; i++) {
            names[i] = ALL_CATEGORIES[i].name;
        }
        return names;
    }

    public static String[] getCategoryIds() {
        String[] ids = new String[ALL_CATEGORIES.length];
        for (int i = 0; i < ALL_CATEGORIES.length; i++) {
            ids[i] = ALL_CATEGORIES[i].id;
        }
        return ids;
    }
}
