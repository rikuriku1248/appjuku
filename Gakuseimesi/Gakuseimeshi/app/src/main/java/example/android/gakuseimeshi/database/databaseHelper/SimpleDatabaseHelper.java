package example.android.gakuseimeshi.database.databaseHelper;

import android.content.Context;
import android.content.res.Resources;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import example.android.gakuseimeshi.R;
import example.android.gakuseimeshi.database.basicData.LocationInformation;
import example.android.gakuseimeshi.database.basicData.MapData;
import example.android.gakuseimeshi.database.basicData.MapOpenDay;
import example.android.gakuseimeshi.database.basicData.MapSearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SimpleDatabaseHelper extends SQLiteOpenHelper {
    static final private String DBNAME = "studentmealdb.sqlite";
    static final private int VERSION = 1;
    private Context mContext;


    public SimpleDatabaseHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Error", "helper01");
        CreateMapMenuTable(db);
        CreateMapLocationTable(db);
        CreateMapSearchTable(db);
        CreateMapOpentimeTable(db);
        CreateReviewTable(db);
        Log.d("Error", "helper02");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_v, int new_v) {
        db.execSQL("DROP TABLE IF EXISTS shopmapview");
        db.execSQL("DROP TABLE IF EXISTS shoplocation");
        db.execSQL("DROP TABLE IF EXITS shopsearch");
        db.execSQL("DROP TABLE IF EXITS shopopentime");
        onCreate(db);
    }

    /*
     * マップメニュー用テーブル作成
     */
    public void CreateMapMenuTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE shopmapview (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT , name_kana TEXT, address TEXT, tel TEXT, opentime TEXT, holiday TEXT, image TEXT, favorite INTEGER)");
        List <MapData> jsonMapList = new ArrayList<MapData>();
        try {
            Resources res = mContext.getResources();
            InputStream inputStream = res.openRawResource(R.raw.kit_food_shop_address);
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream));

            String str = bufferedReader.readLine();
            JSONObject json = new JSONObject(str);
            JSONArray words = json.getJSONArray("kit_food_area");

            for(int i = 0; i < words.length(); i++){
                MapData mapData = new MapData();
                JSONObject word = words.getJSONObject(i);
                //mapData.setId(i + 1);
                mapData.setName(word.getString("name"));
                mapData.setNameKana(word.getString("name_kana"));
                mapData.setAddress(word.getString("address"));
                mapData.setTel(word.getString("tel"));
                mapData.setOpentime((word.getString("opentime")));
                mapData.setHoliday(word.getString("holiday"));
                mapData.setImage(String.valueOf("{}"));
                mapData.setFavorite(0);
                jsonMapList.add(mapData);
            }

            db.beginTransaction();
            try {
                SQLiteStatement sql = db.compileStatement(
                        "INSERT INTO shopmapview(name,name_kana,address,tel,opentime,holiday,image, favorite) VALUES( ?, ?, ?, ?, ?, ?, ?, ?)");
                for (int i = 0; i < jsonMapList.size(); i++) {
                    //sql.bindLong(1, jsonMapList.get(i).getId());
                    sql.bindString(1, jsonMapList.get(i).getName() );
                    sql.bindString(2, jsonMapList.get(i).getNameKana());
                    sql.bindString(3, jsonMapList.get(i).getAddress());
                    sql.bindString(4, jsonMapList.get(i).getTel());
                    sql.bindString(5, jsonMapList.get(i).getOpentime());
                    sql.bindString(6, jsonMapList.get(i).getHoliiday());
                    sql.bindString(7, jsonMapList.get(i).getImage());
                    sql.bindLong(8, jsonMapList.get(i).getFavorite());
                    sql.executeInsert();
                }
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }

            inputStream.close();
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
     * 座標情報
     */
    public void CreateMapLocationTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE shoplocation (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT , latitude DOUBLE, longitude DOUBLE)");

        List <LocationInformation> jsonMapList = new ArrayList<LocationInformation>();

        try {
            Resources res = mContext.getResources();
            InputStream inputStream = res.openRawResource(R.raw.kit_food_shop_address);
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream));

            String str = bufferedReader.readLine();
            JSONObject json = new JSONObject(str);
            JSONArray words = json.getJSONArray("kit_food_area");

            for(int i = 0; i < words.length(); i++){
                LocationInformation locationInformation = new LocationInformation();
                JSONObject word = words.getJSONObject(i);
                locationInformation.setName(word.getString("name"));
                locationInformation.setLatitude(word.getDouble("latitude"));
                locationInformation.setLongitude(word.getDouble("longitude"));
                jsonMapList.add(locationInformation);
            }

            db.beginTransaction();
            try {
                SQLiteStatement sql = db.compileStatement(
                        "INSERT INTO shoplocation(name, latitude, longitude) VALUES(?, ?, ?)");
                for (int i = 0; i < jsonMapList.size(); i++) {
                    sql.bindString(1, jsonMapList.get(i).getName() );
                    sql.bindDouble(2, jsonMapList.get(i).getLatitude());
                    sql.bindDouble(3, jsonMapList.get(i).getLongitude());
                    sql.executeInsert();
                }
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }

            inputStream.close();
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * 検索用データベース
     */
    public void CreateMapSearchTable(SQLiteDatabase db){
        /*db.execSQL("CREATE TABLE shopsearch (" +
                "name TEXT , name_kana TEXT, opentime TEXT, holiday TEXT," +
                " category_name1 TEXT, category_name2 TEXT, budget TEXT," +
                " student_discount INTEGER, student_discount_info TEXT, favorite INTEGER)");*/
        db.execSQL("CREATE TABLE shopsearch (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT , name_kana TEXT, address TEXT, opentime TEXT, holiday TEXT," +
                " category_name1 TEXT, category_name2 TEXT, budget INTEGER," +
                " student_discount INTEGER, student_discount_info TEXT, favorite INTEGER," +
                " search_time1 TEXT, search_time2 TEXT, search_time3 TEXT," +
                " search_holiday_day TEXT, search_holiday_month TEXT)");

        List <MapSearch> jsonMapList = new ArrayList<MapSearch>();


        try {
            Resources res = mContext.getResources();
            InputStream inputStream = res.openRawResource(R.raw.kit_food_shop_address);
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream));

            String str = bufferedReader.readLine();
            JSONObject json = new JSONObject(str);
            JSONArray words = json.getJSONArray("kit_food_area");

            Log.d("Error2", words.length() + "a");
            try {
                for (int i = 0; i < words.length(); i++) {
                    Log.d("Error","count" + i);
                    MapSearch mapSearch = new MapSearch();
                    JSONObject word = words.getJSONObject(i);
                    mapSearch.setName(word.getString("name"));
                    mapSearch.setNameKana(word.getString("name_kana"));
                    mapSearch.setAddress(word.getString("address"));
                    mapSearch.setOpentime(word.getString("opentime"));
                    mapSearch.setHoliday(word.getString("holiday"));

                    if (word.get("category_names") != null) {
                        JSONArray array = word.getJSONArray("category_names");
                        for(int j = 0; j < array.length(); j++){
                            if(j == 0) {
                                mapSearch.setCategoryName1(array.get(0).toString());
                                mapSearch.setCategoryName2(String.valueOf("{}"));
                            }else {
                                mapSearch.setCategoryName2(array.get(1).toString());
                            }
                        }
                    }

                    mapSearch.setBudget(word.getInt("budget"));
                    // 初期では学割をなしと仮定する
                    int studentDiscount = 0;
                    mapSearch.setStudentDiscountInfo(String.valueOf("{}"));
                    mapSearch.setFavorite(0);
                    if (word.get("search_time") != null) {
                        Log.d("Error","search_time_start");
                        JSONArray array = word.getJSONArray("search_time");
                        for(int j = 0; j < array.length(); j++){
                            if(j == 0) {
                                mapSearch.setSearchTime1(array.get(0).toString());
                                mapSearch.setSearchTime2(String.valueOf("{}"));
                                mapSearch.setSearchTime3(String.valueOf("{}"));
                            }else if(j == 1){
                                mapSearch.setSearchTime2(array.get(1).toString());
                            }else{
                                mapSearch.setSearchTime3(array.get(2).toString());
                            }
                        }
                        Log.d("Error","search_time_finish");
                    }

                    if (word.get("search_holiday") != null) {
                        Log.d("Error","search_holiday_start");
                        //オブジェクト内のデータの取得
                        mapSearch.setSearchHolidayDay(word.getJSONObject("search_holiday").getString("days"));
                        mapSearch.setSearchHolidayMonth(word.getJSONObject("search_holiday").getString("month"));
                        Log.d("Error","search_holiday_finish");
                    }

                    jsonMapList.add(mapSearch);
                    Log.d("Error", i + "createMapSearch");
                }
                Log.d("Error", "finish");
            }catch (Exception e){
                Log.d("Error", "ErrorFinish");
            }
            db.beginTransaction();
            try {
                SQLiteStatement sql = db.compileStatement(
                        "INSERT INTO shopsearch(" +
                                "name, name_kana, address, opentime, holiday," +
                                " category_name1, category_name2, budget," +
                                " student_discount, student_discount_info, favorite," +
                                " search_time1, search_time2, search_time3," +
                                " search_holiday_day, search_holiday_month )" +
                                " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                for (int i = 0; i < jsonMapList.size(); i++) {
                    Log.d("Error2",jsonMapList.get(i).getOpentime());
                    sql.bindString(1, jsonMapList.get(i).getName() );
                    sql.bindString(2, jsonMapList.get(i).getNameKana());
                    sql.bindString(3, jsonMapList.get(i).getAddress());
                    sql.bindString(4, jsonMapList.get(i).getOpentime());
                    sql.bindString(5, jsonMapList.get(i).getHoliday());
                    sql.bindString(6, jsonMapList.get(i).getCategoryName1());
                    sql.bindString(7, jsonMapList.get(i).getCategoryName2());
                    sql.bindLong(8, jsonMapList.get(i).getBudget());
                    sql.bindLong(9, jsonMapList.get(i).getStudentDiscount());
                    sql.bindString(10, jsonMapList.get(i).getStudentDiscountInfo());
                    sql.bindLong(11, jsonMapList.get(i).getFavorite());
                    sql.bindString(12, jsonMapList.get(i).getSearchTime1());
                    sql.bindString(13, jsonMapList.get(i).getSearchTime2());
                    sql.bindString(14, jsonMapList.get(i).getSearchTime3());
                    sql.bindString(15, jsonMapList.get(i).getSearchHolidayDay());
                    sql.bindString(16, jsonMapList.get(i).getSearchHolidayMonth());

                    sql.executeInsert();
                }
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }

            inputStream.close();
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
 　　* 日付検索用テーブル
 　　*/
    public void CreateMapOpentimeTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE shopopentime (" +
                "name TEXT , opentime1 TEXT, opentime2 TEXT, opentime3 TEXT, holiday TEXT)");

        List <MapOpenDay> jsonMapList = new ArrayList<MapOpenDay>();

        try {
            Resources res = mContext.getResources();
            InputStream inputStream = res.openRawResource(R.raw.kit_food_shop_address);
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream));

            String str = bufferedReader.readLine();
            JSONObject json = new JSONObject(str);
            JSONArray words = json.getJSONArray("kit_food_area");

            for(int i = 0; i < words.length(); i++){
                MapOpenDay mapOpenDay = new MapOpenDay();
                JSONObject word = words.getJSONObject(i);
                mapOpenDay.setName(word.getString("name"));
                mapOpenDay.setOpentime1(word.getString("search_time"));
                mapOpenDay.setOpentime2("{}");
                mapOpenDay.setOpentime3("{}");
                mapOpenDay.setHoliday(word.getString("holiday"));
                jsonMapList.add(mapOpenDay);
            }

            db.beginTransaction();
            try {
                SQLiteStatement sql = db.compileStatement(
                        "INSERT INTO shopopentime(name, opentime1, opentime2, opentime3, holiday) VALUES(?, ?, ?, ? , ?)");
                for (int i = 0; i < jsonMapList.size(); i++) {
                    sql.bindString(1, jsonMapList.get(i).getName() );
                    sql.bindString(2, jsonMapList.get(i).getOpentime1());
                    sql.bindString(3, jsonMapList.get(i).getOpentime2());
                    sql.bindString(4, jsonMapList.get(i).getOpentime3());
                    sql.bindString(5, jsonMapList.get(i).getHoliday());
                    sql.executeInsert();
                }
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }

            inputStream.close();
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 口コミ用データベース
     * @param db
     */
    public void CreateReviewTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE shopreview (" +
                "id INTEGER PRIMARY KEY, name TEXT , comment TEXT, image TEXT)");
    }

}
