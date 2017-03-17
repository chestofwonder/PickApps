package esdip.pickapps.packages;


import java.util.ArrayList;
import java.util.List;

import esdip.pickapps.R;

public class wallpapers {

    public static final List<wallpaper> ITEMS = new ArrayList<wallpaper>();

    static {
        addWallpaper("esdip.inn_wallpaper", R.drawable.wallpaper_the_inn, "https://play.google.com/store/apps/details?id=mypackagename");
        addWallpaper("esdip.theatre_wallpaper", R.drawable.wallpaper_theatre, "https://play.google.com/store/apps/details?id=mypackagename");
        addWallpaper("esdip.monster_wallpaper", R.drawable.wallpaper_monster_weather, "https://play.google.com/store/apps/details?id=mypackagename");
        addWallpaper("esdip.pug_wallpaper", R.drawable.wallpaper_weather_pug, "https://play.google.com/store/apps/details?id=mypackagename");
        addWallpaper("esdip.cartoon_city_wallpaper", R.drawable.wallpaper_cartoon_city, "https://play.google.com/store/apps/details?id=mypackagename");
        addWallpaper("esdip.pink_lady_wallpaper", R.drawable.wallpaper_pink_lady, "https://play.google.com/store/apps/details?id=mypackagename");
        addWallpaper("esdip.snail_day_wallpaper", R.drawable.wallpaper_snail_day, "https://play.google.com/store/apps/details?id=mypackagename");
        addWallpaper("esdip.zombie_day_wallpaper", R.drawable.wallpaper_zombie_day, "https://play.google.com/store/apps/details?id=mypackagename");
    }

    public static class wallpaper {
        public final String package_uri;
        public final int thumbnail;
        public final String buy_link;

        public wallpaper(String name, int image_id, String url) {
            this.package_uri = name;
            this.thumbnail = image_id;
            this.buy_link = url;
        }
    }

    public static List<wallpaper> getWallpapers(){
        return ITEMS;
    }

    private static void addWallpaper(String name, int image_id, String url) {
        ITEMS.add(new wallpaper(name, image_id, url));
    }
}
