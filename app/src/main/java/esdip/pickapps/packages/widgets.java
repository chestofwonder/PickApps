package esdip.pickapps.packages;


import java.util.ArrayList;
import java.util.List;

import esdip.pickapps.R;

public class widgets {

    public static final List<widget> ITEMS = new ArrayList<widget>();

    static {
        addWidget("esdip.weather_school", R.drawable.widget_weather_school, "https://play.google.com/store/apps/details?id=mypackagename");
        addWidget("esdip.angelas_secret", R.drawable.widget_angelas_secret, "https://play.google.com/store/apps/details?id=mypackagename");
        addWidget("esdip.weather_in_your_hands", R.drawable.widget_in_your_hands, "https://play.google.com/store/apps/details?id=mypackagename");
        addWidget("esdip.widget_cute_cartoon", R.drawable.widget_cute_cartoon, "https://play.google.com/store/apps/details?id=mypackagename");
        addWidget("esdip.widget_weather_block", R.drawable.widget_weather_block, "https://play.google.com/store/apps/details?id=mypackagename");
    }

    public static class widget {
        public final String package_uri;
        public final int thumbnail;
        public final String buy_link;

        public widget(String name, int image_id, String url) {
            this.package_uri = name;
            this.thumbnail = image_id;
            this.buy_link = url;
        }
    }

    public static List<widget> getWidgets(){
        return ITEMS;
    }

    private static void addWidget(String name, int image_id, String url) {
        ITEMS.add(new widget(name, image_id, url));
    }


   /* public static final List<widget> ITEMS = new ArrayList<widget>();
    static {
        // Add some sample items.
        for (int i = 1; i <= 5; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(widget item) {
        ITEMS.add(item);
    }

    private static widget createDummyItem(int position) {
        return new widget(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }


    public static class widget {
        public final String id;
        public final String content;
        public final String details;

        public widget(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

    }*/
}
