package esdip.pickapps;

import android.os.AsyncTask;
/*
private class GoogleAPIConnection extends AsyncTask<String, Integer, Long> {
    protected Long doInBackground(String... urls) {

        int count = urls.length;
        long totalSize = 0;
        for (int i = 0; i < count; i++) {
            // totalSize += Downloader.downloadFile(urls[i]);
            publishProgress((int) ((i / (float) count) * 100));
            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        return totalSize;
    }

    protected void onProgressUpdate(Integer... progress) {
        // setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {
        //showDialog("Downloaded " + result + " bytes");
        getLocation();
    }
}

   //new GoogleAPIConnection().execute("asd", "asd", "ads");
*/

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class PickAppsContentProvider extends ContentProvider{

    private static final String DEBUG_CP = "DEBUG_CONTENT_PROVIDER";
    private final boolean debug = false;

    private DBManager dbManager;

    // Uri Matcher constants
    private static final int WIDGET = 1;
    private static final int WIDGETS = 11;
    private static final int WIDGET_DOWNLOAD = 12;
    private static final int WIDGETS_DOWNLOADED = 13;

    private static final int WALLPAPER = 2;
    private static final int WALLPAPERS = 21;
    private static final int WALLPAPER_DOWNLOAD = 22;
    private static final int WALLPAPERS_DOWNLOADED = 23;
    private static final int CUSTOM_WALLPAPER = 24;
    private static final int CUSTOM_WALLPAPERS = 25;

    private static final int IMAGE = 3;
    private static final int IMAGES = 31;

    // Path constants
    private static final String AUTHORITY = "pickapps.contentprovider";

    private static final String WIDGETS_PATH = "widgets";
    private static final String WALLPAPERS_PATH = "wallpapers";
    private static final String CUSTOM_WALLPAPERS_PATH = "custom_wallpapers";
    private static final String IMAGES_PATH = "images";

    // Uri Matcher
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        uriMatcher.addURI(AUTHORITY, WIDGETS_PATH + "/name/*", WIDGET);
        uriMatcher.addURI(AUTHORITY, WIDGETS_PATH, WIDGETS);
        uriMatcher.addURI(AUTHORITY, WIDGETS_PATH + "/download/name/*", WIDGET_DOWNLOAD);
        uriMatcher.addURI(AUTHORITY, WIDGETS_PATH + "/downloaded", WIDGETS_DOWNLOADED);
        uriMatcher.addURI(AUTHORITY, WALLPAPERS_PATH + "/name/*", WALLPAPER);
        uriMatcher.addURI(AUTHORITY, WALLPAPERS_PATH, WALLPAPERS);
        uriMatcher.addURI(AUTHORITY, WALLPAPERS_PATH + "/download/name/*", WALLPAPER_DOWNLOAD);
        uriMatcher.addURI(AUTHORITY, WALLPAPERS_PATH + "/downloaded", WALLPAPERS_DOWNLOADED);
        uriMatcher.addURI(AUTHORITY, CUSTOM_WALLPAPERS_PATH + "/name/*", CUSTOM_WALLPAPER);
        uriMatcher.addURI(AUTHORITY, CUSTOM_WALLPAPERS_PATH, CUSTOM_WALLPAPERS);
        uriMatcher.addURI(AUTHORITY, IMAGES_PATH + "/package_name/*", IMAGES);
        uriMatcher.addURI(AUTHORITY, IMAGES_PATH + "/image_name/*/package_name/*", IMAGE);
    }


    //public static final String CONTENT_URI = "content://" + AUTHORITY + "/" + BASE_PATH;
    //public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/packages";
    //public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/packages";


    @Override
    public boolean onCreate() {

        if(debug)Log.i(DEBUG_CP, "update");

        dbManager = new DBManager(getContext(), null, null, 1);

        return true;
    }



    /*
    public void downloadWidget(String name)
    public void downloadWallpaper(String name)
    */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if(debug)Log.i(DEBUG_CP, "update");

        switch (uriMatcher.match(uri)){
            case WIDGET_DOWNLOAD:
                dbManager.downloadWidget(values.getAsString("name"));
                break;
            case WALLPAPER_DOWNLOAD:
                dbManager.downloadWallpaper(values.getAsString("name"));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        //getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }



    /*
    public Long addWidget(String name, byte[] thumbnail, String behavior) - Insert single widget
    public Long addWallpaper(String name, byte[] thumbnail, String behavior) - Insert single wallpaper
    public Long addImage(String package_name, String name, byte[] data)
    */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        if(debug)Log.i(DEBUG_CP, "insert");

        switch(uriMatcher.match(uri)){
            case WIDGET:
                if(debug)Log.i(DEBUG_CP, "insert widget");
                dbManager.addWidget(values.getAsString(DBManager.COLUMN_WIDGET_NAME), values.getAsByteArray(DBManager.COLUMN_WIDGET_THUMBNAIL), values.getAsString(DBManager.COLUMN_WIDGET_BEHAVIOR));
                break;
            case WALLPAPER:
                if(debug)Log.i(DEBUG_CP, "insert wallpaper");
                dbManager.addWallpaper(values.getAsString(DBManager.COLUMN_WALLPAPER_NAME), values.getAsByteArray(DBManager.COLUMN_WALLPAPER_THUMBNAIL_ON), values.getAsByteArray(DBManager.COLUMN_WALLPAPER_THUMBNAIL_OFF), values.getAsString(DBManager.COLUMN_WALLPAPER_BEHAVIOR));
                break;
            case CUSTOM_WALLPAPER:
                if(debug)Log.i(DEBUG_CP, "insert custom wallpaper");
                dbManager.addCustomWallpaper(values.getAsString(DBManager.COLUMN_CUSTOM_WALLPAPER_NAME), values.getAsString(DBManager.COLUMN_WALLPAPER_BEHAVIOR));
                break;
            case IMAGE:
                if(debug)Log.i(DEBUG_CP, "insert image");
                dbManager.addImage(values.getAsString(DBManager.COLUMN_IMAGE_PACKAGE_NAME), values.getAsString(DBManager.COLUMN_IMAGE_NAME), values.getAsByteArray(DBManager.COLUMN_IMAGE_DATA));
                break;
        }

        return null;
    }


    /*
    public void deleteWidget(String name)
    public void deleteWallpaper(String name)
    */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // DB methods take care of deleting all images belongs to packages

        if(debug)Log.i(DEBUG_CP, "delete");

        String name = uri.getPathSegments().get(2);

        switch(uriMatcher.match(uri)) {
            case WIDGET:
                dbManager.deleteWidget(name);
                break;
            case WALLPAPER:
                dbManager.deleteWallpaper(name);
                break;
        }

        return 1;
    }


    /*
    public Cursor getWidget(String name)
    public Cursor getWidgets()
    public Cursor getWidgetsDownloaded()
    public Cursor getWallpaper(String name)
    public Cursor getWallpapers()
    public Cursor getWallpapersDownloaded()
    public Cursor getImage(String image_name, String package_name)
    public Cursor getImagesPackage(String package_name)
    */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if(debug)Log.i(DEBUG_CP, "query Uri: " + String.valueOf(uri));

        switch(uriMatcher.match(uri)){
            case WIDGET:
                return dbManager.getWidget(uri.getPathSegments().get(2));
            case WIDGETS:
                return dbManager.getWidgets();
            case WIDGETS_DOWNLOADED:
                return dbManager.getWidgetsDownloaded();
            case WALLPAPER:
                return dbManager.getWallpaper(uri.getPathSegments().get(2));
            case WALLPAPERS:
                return dbManager.getWallpapers();
            case CUSTOM_WALLPAPERS:
                return dbManager.getCustomWallpapers();
            case WALLPAPERS_DOWNLOADED:
                return dbManager.getWallpapersDownloaded();
            case IMAGE:
                return dbManager.getImage(uri.getPathSegments().get(2), uri.getPathSegments().get(4));
            case IMAGES:
                return dbManager.getImagesPackage(uri.getPathSegments().get(2));
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        if(debug)Log.i(DEBUG_CP, "getType");

        switch(uriMatcher.match(uri)){
            case WIDGET:
                return "vnd.android.cursor.dir/vnd.esdip.pickapps.provider.widgets";
            case WALLPAPER:
                return "vnd.android.cursor.dir/vnd.esdip.pickapps.provider.wallpapers";
            case IMAGE:
                return "vnd.android.cursor.dir/vnd.esdip.pickapps.provider.images";
        }
        return "";
    }
}
