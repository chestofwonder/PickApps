package esdip.pickapps;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

/*
TABLAS:

WIDGETS
    COLUMN_WIDGET_ID
    COLUMN_WIDGET_NAME
    COLUMN_WIDGET_THUMBNAIL
    COLUMN_WIDGET_BEHAVIOR
    COLUMN_WIDGET_DOWNLOADED

WIDGETS_IMAGES
    COLUMN_IMAGE_ID
    COLUMN_IMAGE_PACKAGE_NAME
    COLUMN_IMAGE_NAME
    COLUMN_IMAGE_DATA

WALLPAPERS
    COLUMN_WALLPAPER_ID
    COLUMN_WALLPAPER_NAME
    COLUMN_WALLPAPER_THUMBNAIL
    COLUMN_WALLPAPER_BEHAVIOR
    COLUMN_WALLPAPER_DOWNLOADED

WALLPAPERS_IMAGES
    COLUMN_IMAGE_ID
    COLUMN_IMAGE_PACKAGE_NAME
    COLUMN_IMAGE_NAME
    COLUMN_IMAGE_DATA


SETTINGS:

active_wallpaper - nombre del wallpaper activo

settings de cómo cambiar el wallpaper activo:
-Tiempo
-Día de la semana
-Día del año
-Hora?

Cuando cambio los setting de comportamiento del fondo, llamo de nuevo al MainService, con el parámetro de cómo debe cambiar

settings de widget: cada uno lleva su comportamiento (aunque se guarda en la app)
 */

/* Link to play store */
/* final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
*/

/* BEHAVIORS
0 - No behavior
1 - By weather
2 - Day of the week
3 - Hour of day
*/

/* DATABASE METHODS

public Long addWidget(String name, byte[] thumbnail, String behavior) - Insert single widget
public Long addWallpaper(String name, byte[] thumbnail, String behavior) - Insert single wallpaper
public Long addImage(String package_name, String name, byte[] data)
public void deleteWidget(String name)
public void deleteWallpaper(String name)
public void downloadWidget(String name)
public void downloadWallpaper(String name)
public Cursor getWidget(String name)
public Cursor getWidgets()
public Cursor getWidgetsDownloaded()
public Cursor getWallpaper(String name)
public Cursor getWallpapers()
public Cursor getWallpapersDownloaded()
public Cursor getImage(String image_name, String package_name)
public Cursor getImagesPackage(String package_name)

*/

public class DBManager extends SQLiteOpenHelper{

    private static final String DEBUG = "DEBUG";
    private final boolean debug = false;

    private static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pickapps.db";

    private static Context DBContext;
    private static SharedPreferences settings;

    public static final String TABLE_WIDGETS = "widgets";
    public static final String COLUMN_WIDGET_ID = "_id";
    public static final String COLUMN_WIDGET_NAME = "widget_name";
    public static final String COLUMN_WIDGET_THUMBNAIL = "widget_thumbnail";
    public static final String COLUMN_WIDGET_BEHAVIOR = "widget_behavior";
    public static final String COLUMN_WIDGET_DOWNLOADED = "widget_downloaded";

    public static final String TABLE_WALLPAPERS = "wallpapers";
    public static final String COLUMN_WALLPAPER_ID = "_id";
    public static final String COLUMN_WALLPAPER_NAME = "wallpaper_name";
    public static final String COLUMN_WALLPAPER_THUMBNAIL_ON = "wallpaper_thumbnail_on";
    public static final String COLUMN_WALLPAPER_THUMBNAIL_OFF = "wallpaper_thumbnail_off";
    public static final String COLUMN_WALLPAPER_BEHAVIOR = "wallpaper_behavior";
    public static final String COLUMN_WALLPAPER_DOWNLOADED = "wallpaper_downloaded";

    public static final String TABLE_CUSTOM_WALLPAPERS = "custom_wallpapers";
    public static final String COLUMN_CUSTOM_WALLPAPER_ID = "_id";
    public static final String COLUMN_CUSTOM_WALLPAPER_NAME = "wallpaper_name";
    public static final String COLUMN_CUSTOM_WALLPAPER_BEHAVIOR = "wallpaper_behavior";

    public static final String TABLE_IMAGES = "images";
    public static final String COLUMN_IMAGE_ID = "_id";
    public static final String COLUMN_IMAGE_PACKAGE_NAME = "package_name";
    public static final String COLUMN_IMAGE_NAME = "image_name";
    public static final String COLUMN_IMAGE_DATA = "image_data";


    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        DBContext = context;
        if(debug)Log.i(DEBUG, "DBManager");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if(debug)Log.i(DEBUG, "onCreate db");

        String query;
        try {
            query = "CREATE TABLE " + TABLE_WIDGETS + "(" +
                    COLUMN_WIDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_WIDGET_NAME + " TEXT, " +
                    COLUMN_WIDGET_THUMBNAIL + " BLOB, " +
                    COLUMN_WIDGET_BEHAVIOR + " TEXT, " +
                    COLUMN_WIDGET_DOWNLOADED + " TEXT " +
                    ");";
            if(debug)Log.i(DEBUG, query);
            db.execSQL(query);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        try {
            query = "CREATE TABLE " + TABLE_WALLPAPERS + "(" +
                    COLUMN_WALLPAPER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_WALLPAPER_NAME + " TEXT, " +
                    COLUMN_WALLPAPER_THUMBNAIL_ON + " BLOB, " +
                    COLUMN_WALLPAPER_THUMBNAIL_OFF + " BLOB, " +
                    COLUMN_WALLPAPER_BEHAVIOR + " TEXT, " +
                    COLUMN_WALLPAPER_DOWNLOADED + " TEXT " +
                    ");";
            if(debug)Log.i(DEBUG, query);
            db.execSQL(query);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        try {
            query = "CREATE TABLE " + TABLE_CUSTOM_WALLPAPERS + "(" +
                    COLUMN_CUSTOM_WALLPAPER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CUSTOM_WALLPAPER_NAME + " TEXT, " +
                    COLUMN_CUSTOM_WALLPAPER_BEHAVIOR + " TEXT " +
                    ");";
            if(debug)Log.i(DEBUG, query);
            db.execSQL(query);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        try {
            query = "CREATE TABLE " + TABLE_IMAGES + "(" +
                    COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_IMAGE_PACKAGE_NAME + " TEXT, " +
                    COLUMN_IMAGE_NAME + " TEXT, " +
                    COLUMN_IMAGE_DATA + " BLOB " +
                    ");";
            if(debug)Log.i(DEBUG, query);
            db.execSQL(query);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        if(debug)Log.i(DEBUG, "BD creada");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(debug)Log.i(DEBUG, "onUpgrade db");

        String query;

        query = "DROP TABLE IF EXISTS " + TABLE_WIDGETS;
        db.execSQL(query);

        query = "DROP TABLE IF EXISTS " + TABLE_WALLPAPERS;
        db.execSQL(query);

        query = "DROP TABLE IF EXISTS " + TABLE_IMAGES;
        db.execSQL(query);

        onCreate(db);
    }

    public Long addWidget(String name, byte[] thumbnail, String behavior){

        if(debug)Log.i(DEBUG, "addWidget");

        ContentValues values = new ContentValues();
        Long id;

        // Insert widget info
        values.put(COLUMN_WIDGET_NAME, name);
        values.put(COLUMN_WIDGET_THUMBNAIL, thumbnail);
        values.put(COLUMN_WIDGET_BEHAVIOR, behavior);
        values.put(COLUMN_WIDGET_DOWNLOADED, 0);

        SQLiteDatabase db = getWritableDatabase();
        id = db.insert(TABLE_WIDGETS, null, values);

        return id;
    }

    public Long addWallpaper(String name, byte[] thumbnail_on, byte[] thumbnail_off, String behavior){

        if(debug)Log.i(DEBUG, "addWallpaper");

        ContentValues values = new ContentValues();
        Long id;

        // Insert widget info
        values.put(COLUMN_WALLPAPER_NAME, name);
        values.put(COLUMN_WALLPAPER_THUMBNAIL_ON, thumbnail_on);
        values.put(COLUMN_WALLPAPER_THUMBNAIL_OFF, thumbnail_off);
        values.put(COLUMN_WALLPAPER_BEHAVIOR, behavior);
        values.put(COLUMN_WALLPAPER_DOWNLOADED, 0);

        SQLiteDatabase db = getWritableDatabase();
        id = db.insert(TABLE_WALLPAPERS, null, values);

        return id;
    }

    public Long addCustomWallpaper(String name, String behavior){

        if(debug)Log.i(DEBUG, "addCustomWallpaper");

        ContentValues values = new ContentValues();
        Long id;

        // Insert widget info
        values.put(COLUMN_CUSTOM_WALLPAPER_NAME, name);
        values.put(COLUMN_CUSTOM_WALLPAPER_BEHAVIOR, behavior);

        SQLiteDatabase db = getWritableDatabase();
        id = db.insert(TABLE_CUSTOM_WALLPAPERS, null, values);

        return id;
    }


    public Long addImage(String package_name, String name, byte[] data){

        if(debug)Log.i(DEBUG, "addImage");

        ContentValues values = new ContentValues();
        Long id;
        Log.i(DEBUG, "contenido a insertar: package_name:" + package_name + " image_name: " + name + " image: " + String.valueOf(data.length));
        values.put(COLUMN_IMAGE_PACKAGE_NAME, package_name);
        values.put(COLUMN_IMAGE_NAME, name);
        values.put(COLUMN_IMAGE_DATA, data);

        SQLiteDatabase db = getWritableDatabase();
        id = db.insert(TABLE_IMAGES, null, values);

        if(debug)Log.i(DEBUG, "Imagen insertada id: " + id);
        return id;
    }



    public void deleteWidget(String name){

        if(debug)Log.i(DEBUG, "deleteWidget name: " + name);
// TODO: meterlo en una transaccion
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_WIDGETS + " WHERE " + COLUMN_WIDGET_NAME + "='" + name + "';");
        db.execSQL("DELETE FROM " + TABLE_IMAGES + " WHERE " + COLUMN_IMAGE_PACKAGE_NAME + "='" + name + "';");
    }

    public void deleteWallpaper(String name){

        if(debug)Log.i(DEBUG, "deleteWallpaper name: " + name);
// TODO: meterlo en una transaccion
        SQLiteDatabase db = getWritableDatabase();

        // If wallpaper to delete is the active one, reset active_package setting
        settings = PreferenceManager.getDefaultSharedPreferences(DBContext);

        if(name  == settings.getString(DBContext.getString(R.string.active_package), "")){
            SharedPreferences.Editor edit = settings.edit();
            edit.putString(DBContext.getString(R.string.active_package), "");
            edit.apply();
        }

        String query;

        query = "DELETE FROM " + TABLE_WALLPAPERS + " WHERE " + COLUMN_WALLPAPER_NAME + "='" + name + "';";
        db.execSQL(query);

        if(debug)Log.i(DEBUG, query);

        query = "DELETE FROM " + TABLE_IMAGES + " WHERE " + COLUMN_IMAGE_PACKAGE_NAME + "='" + name + "';";
        db.execSQL(query);

        if(debug)Log.i(DEBUG, query);
    }

    public void downloadWidget(String name){

        if(debug)Log.i(DEBUG, "downloadWidget name: " + name);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_WIDGETS + " SET " + COLUMN_WIDGET_DOWNLOADED + " = 1 WHERE " + COLUMN_WIDGET_NAME + "='" + name + "';");
    }

    public void downloadWallpaper(String name){

        if(debug)Log.i(DEBUG, "downloadWallpaper name: " + name);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_WALLPAPERS + " SET " + COLUMN_WALLPAPER_DOWNLOADED + " = 1 WHERE " + COLUMN_WALLPAPER_NAME + "='" + name + "';");
    }

    public Cursor getWidget(String name){

        if(debug)Log.i(DEBUG, "getWidget");

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_WIDGETS + " WHERE " + COLUMN_WIDGET_NAME + " = " + name + ";";

            Cursor c = db.rawQuery(query, null);

            return c;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public Cursor getWidgets(){

        if(debug)Log.i(DEBUG, "getWidgets");

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_WIDGETS + ";";

            Cursor c = db.rawQuery(query, null);

            return c;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public Cursor getWidgetsDownloaded(){

        if(debug)Log.i(DEBUG, "getWidgetsDownloaded");

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_WIDGETS + " WHERE " + COLUMN_WIDGET_DOWNLOADED + " = 1;";

            Cursor c = db.rawQuery(query, null);

            return c;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }


    public Cursor getWallpaper(String name){

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_WALLPAPERS + " WHERE " + COLUMN_WALLPAPER_NAME + " = '" + name + "';";

            if(debug)Log.i(DEBUG, "getWallpaper query: " + query);

            Cursor c = db.rawQuery(query, null);

            return c;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public Cursor getWallpapers(){

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_WALLPAPERS + ";";

            if(debug) Log.i(DEBUG, "getWallpapers query: " + query);

            Cursor c = db.rawQuery(query, null);

            if(debug)Log.i(DEBUG, "getWallpapers c: " + c.getCount());

            return c;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public Cursor getCustomWallpapers(){

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_CUSTOM_WALLPAPERS + ";";

            if(debug) Log.i(DEBUG, "getCustomWallpapers query: " + query);

            Cursor c = db.rawQuery(query, null);

            if(debug)Log.i(DEBUG, "getCustomWallpapers c: " + c.getCount());

            return c;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public Cursor getWallpapersDownloaded(){

        if(debug)Log.i(DEBUG, "getWallpapersDownloaded");

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_WALLPAPERS + " WHERE " + COLUMN_WALLPAPER_DOWNLOADED + " = 1;";

            Cursor c = db.rawQuery(query, null);

            return c;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public Cursor getImage(String image_name, String package_name){

        if(debug)Log.i(DEBUG, "getImage name: " + image_name + " from package: " + package_name);

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_IMAGES + " WHERE " + COLUMN_IMAGE_PACKAGE_NAME + " = '" + package_name + "' AND " + COLUMN_IMAGE_NAME + " = '" + image_name + "'";
            Log.i(DEBUG, "query getImage: " + query);
            Cursor c = db.rawQuery(query, null);

            if(c.getCount() > 0 && c.moveToFirst()) {
                Log.i(DEBUG, "query getImage resultado: " + c.getCount());
                return c;
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public Cursor getImagesPackage(String package_name){

        if(debug)Log.i(DEBUG, "getImagesPackage from package: " + package_name);

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_IMAGES + " WHERE " + COLUMN_IMAGE_PACKAGE_NAME + " = '" + package_name + "';";

            Cursor c = db.rawQuery(query, null);

            return c;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

/*

    private static final String DEBUG_TAG = "DEBUG";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pickapps.db";
    public static final String TABLE_PACKAGES = "packages";
    public static final String COLUMN_PACKAGE_ID = "_id";
    public static final String COLUMN_PACKAGE_NAME = "package_name";
    public static final String COLUMN_PACKAGE_THUMBNAIL = "package_thumbnail";
    public static final String TABLE_IMAGES = "images";
    public static final String COLUMN_IMAGE_ID = "_id";
    public static final String COLUMN_IMAGE_PACKAGE_NAME = "package_name";
    public static final String COLUMN_IMAGE_NAME = "image_name";
    public static final String COLUMN_IMAGE_DATA = "image_data";



    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        Log.i(DEBUG_TAG, "DBManager");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(DEBUG_TAG, "onCreate db");

        String query;
        try {
            query = "CREATE TABLE " + TABLE_PACKAGES + "(" +
             COLUMN_PACKAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
             COLUMN_PACKAGE_NAME + " TEXT, " +
             COLUMN_PACKAGE_THUMBNAIL + " BLOB " +
              ");";
            db.execSQL(query);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        try {
             query = "CREATE TABLE " + TABLE_IMAGES + "(" +
                COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_IMAGE_PACKAGE_NAME + " TEXT, " +
                COLUMN_IMAGE_NAME + " TEXT, " +
                COLUMN_IMAGE_DATA + " BLOB, " +
                " FOREIGN KEY (" + COLUMN_IMAGE_PACKAGE_NAME + ") REFERENCES " + TABLE_PACKAGES + "(" + COLUMN_PACKAGE_ID +"));";
            Log.i(DEBUG_TAG, query);
            db.execSQL(query);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(DEBUG_TAG, "onUpgrade db");

        String query;

        query = "DROP TABLE IF EXISTS " + TABLE_PACKAGES;
        db.execSQL(query);

        query = "DROP TABLE IF EXISTS " + TABLE_IMAGES;
        db.execSQL(query);

        onCreate(db);
    }

    public Long addPackage(Package new_package){

        Log.i(DEBUG_TAG, "addPackage db");

        ContentValues values = new ContentValues();
        Long id;

        // Insert package info
        values.put(COLUMN_PACKAGE_NAME, new_package.getPackageName());
        values.put(COLUMN_PACKAGE_THUMBNAIL, new_package.getPackageThumbnail());

        SQLiteDatabase db = getWritableDatabase();
        id = db.insert(TABLE_PACKAGES, null, values);

        return id;
    }

    public Long addImage(String package_name, String image_name, byte[] image){

        Log.i(DEBUG_TAG, "addImage db");

        ContentValues values = new ContentValues();
        Long id;
        Log.i(DEBUG_TAG, "contenido a insertar: package_name:" + package_name + " image_name: " + image_name + " image: " + String.valueOf(image.length));
        values.put(COLUMN_IMAGE_PACKAGE_NAME, package_name);
        values.put(COLUMN_IMAGE_NAME, image_name);
        values.put(COLUMN_IMAGE_DATA, image);

        SQLiteDatabase db = getWritableDatabase();
        id = db.insert(TABLE_IMAGES, null, values);

        return id;
    }

    public void deletePackage(String package_name){

        Log.i(DEBUG_TAG, "deletePackage db name: " + package_name);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PACKAGES + " WHERE " + COLUMN_PACKAGE_NAME + "='" + package_name + "';");
        db.execSQL("DELETE FROM " + TABLE_IMAGES + " WHERE " + COLUMN_IMAGE_PACKAGE_NAME + "='" + package_name + "';");
    }


    public Cursor getPackage(String package_name){

        Log.i(DEBUG_TAG, "getPackage by name db: " + package_name);

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_PACKAGES + " WHERE " + COLUMN_PACKAGE_NAME + " = '" + package_name + "'";

            Cursor c = db.rawQuery(query, null);

           return c;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }


    public Cursor getPackages(){

        Log.i(DEBUG_TAG, "get all Packages");

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_PACKAGES;

            Cursor c = db.rawQuery(query, null);

            return c;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }


    public Cursor getImage(String image_name, String package_name){

        Log.i(DEBUG_TAG, "getImage by name: " + image_name + " from package: " + package_name);

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_IMAGES + " WHERE " + COLUMN_IMAGE_PACKAGE_NAME + " = '" + package_name + "' AND " + COLUMN_IMAGE_NAME + " = '" + image_name + "'";
            Log.i(DEBUG_TAG, "query getImage: " + query);
            Cursor c = db.rawQuery(query, null);
            Log.i(DEBUG_TAG, "query getImage resultado: " + c.getCount());

            return c;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }*/
}
