package example.android.gakuseimeshi.gurunavi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import example.android.gakuseimeshi.database.basicData.LocationInformation;
import example.android.gakuseimeshi.database.basicData.MapData;
import example.android.gakuseimeshi.database.basicData.MapOpenDay;
import example.android.gakuseimeshi.database.basicData.MapSearch;
import example.android.gakuseimeshi.database.dao.ShopLocationDao;
import example.android.gakuseimeshi.database.dao.ShopMapSearchDao;
import example.android.gakuseimeshi.database.dao.ShopMapViewDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomu on 2018/01/19.
 */

public class SearchKitFoodArea extends AsyncTask<Void, Void, String>{
    private int maxCount = 1;
    private List<LocationInformation> locationInformations = new ArrayList<LocationInformation>();
    private List<MapData> mapDatas = new ArrayList<MapData>();
    private List<MapOpenDay> mapOpenDays = new ArrayList<MapOpenDay>();
    private List<MapSearch> mapSearchs = new ArrayList<MapSearch>();
    private Context context;

    public SearchKitFoodArea(){

    }

    public SearchKitFoodArea(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // doInBackground前処理
        locationInformations.clear();
        mapDatas.clear();
        mapOpenDays.clear();
        mapSearchs.clear();
    }

    @Override
    protected String doInBackground(Void... params) {
        setSearchData();

        ShopLocationDao shopLocationDao;
        shopLocationDao = new ShopLocationDao(context);
        shopLocationDao.openDB();
        shopLocationDao.replaceDB(locationInformations);
        shopLocationDao.closeDB();

        ShopMapSearchDao shopMapSearchDao;
        shopMapSearchDao = new ShopMapSearchDao(context);
        shopMapSearchDao.openDB();
        shopMapSearchDao.replaceDB(mapSearchs);
        shopMapSearchDao.closeDB();

        ShopMapViewDao shopMapViewDao;
        shopMapViewDao = new ShopMapViewDao(context);
        shopMapViewDao.openDB();
        shopMapViewDao.replaceDB(mapDatas);
        shopMapViewDao.closeDB();
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // doInBackground後処理


    }

    /**
     * 検索条件を入れてその後検索する
     */
    private void setSearchData(){
        // アクセスキー

        String acckey = "APIkey";
        int count = 1;

        do {
            String lat = "36.5310338";
            String lon = "136.6284361";
            String range = "5";
            String hit_per_page = "100";
            String offset_page = String.valueOf(count);
            String format = "json";

            // エンドポイント
            String gnaviRestUri = "https://api.gnavi.co.jp/RestSearchAPI/20150630/";

            String prm_format = "?format=" + format;
            String prm_keyid = "&keyid=" + acckey;
            String prm_lat = "&latitude=" + lat;
            String prm_lon = "&longitude=" + lon;
            String prm_range = "&range=" + range;
            String prm_hit_per_page = "&hit_per_page=" + hit_per_page;
            String prm_offset_page = "&offset_page=" + offset_page;

            // URI組み立て
            StringBuffer uri = new StringBuffer();
            uri.append(gnaviRestUri);
            uri.append(prm_format);
            uri.append(prm_keyid);
            uri.append(prm_lat);
            uri.append(prm_lon);
            uri.append(prm_range);
            uri.append(prm_hit_per_page);
            uri.append(prm_offset_page);

            getObjectList(uri.toString());
            count++;
        }while(count <= maxCount);
    }

    /**
     * 検索条件urlから検索を行う
     * @param url
     */
    private void getObjectList(String url){
        try {
            URL restSearch = new URL(url);
            HttpURLConnection http = (HttpURLConnection)restSearch.openConnection();
            http.setRequestMethod("GET");
            //http.setInstanceFollowRedirects(false);
            http.setDoInput(true);
            http.connect();
            InputStream in = http.getInputStream();
            readInputStream(in);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得したデータを格納
     * @param in
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public void readInputStream(InputStream in) throws IOException, UnsupportedEncodingException {


        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str = br.readLine();
            JSONObject json = new JSONObject(str);
            Log.d("Error", json.toString());
            maxCount = Integer.parseInt(json.getString("total_hit_count")) / 100 + 1;
            JSONArray words = json.getJSONArray("rest");


            for(int i=0; i < words.length(); i++){
                JSONObject word = words.getJSONObject(i);
                storeMapData(word);
                storeMapSearch(word);
                storeLocalInfomation(word);
            }


            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * LocalInfoamation型のリストのデータを格納
     * @param word
     */
    private void storeLocalInfomation(JSONObject word){
        try {
            LocationInformation locationInformation = new LocationInformation();
            locationInformation.setName(word.getString("name"));
            locationInformation.setLongitude(word.getDouble("longitude"));
            locationInformation.setLatitude(word.getDouble("latitude"));
            Log.d("Error", String.valueOf(locationInformation.getLatitude()));
            locationInformations.add(locationInformation);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * MapData型リストにデータを格納
     * @param word
     */
    private void storeMapData(JSONObject word){
        try {
            MapData mapData = new MapData();
            //mapData.setId(0); // Idは設定しない
            mapData.setName(word.getString("name"));
            mapData.setNameKana(word.getString("name_kana"));
            mapData.setAddress(word.getString("address"));
            mapData.setTel(word.getString("tel"));
            mapData.setOpentime((word.getString("opentime")));
            mapData.setHoliday(word.getString("holiday"));
            mapData.setImage(word.getJSONObject("image_url").getString("shop_image1"));
            Log.d("Error", word.getJSONObject("image_url").getString("shop_image1"));
            mapData.setFavorite(0);
            mapDatas.add(mapData);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * MapSearch型のリストにデータを格納
     * @param word
     */
    private void storeMapSearch(JSONObject word){
        try{
            MapSearch mapSearch = new MapSearch();
            mapSearch.setName(word.getString("name"));
            mapSearch.setNameKana(word.getString("name_kana"));
            mapSearch.setAddress(word.getString("address"));
            mapSearch.setOpentime(word.getString("opentime"));
            mapSearch.setHoliday(word.getString("holiday"));

            for(int i = 0; i < word.getJSONObject("code").length(); i++){
                if(i == 0){
                    mapSearch.setCategoryName1(word.getJSONObject("code").getJSONArray("category_name_s").get(0).toString());
                    mapSearch.setCategoryName2("{}");
                } else{
                    mapSearch.setCategoryName2(word.getJSONObject("code").getJSONArray("category_name_s").get(1).toString());
                    break;
                }
            }
            String budget = word.getString("budget");
            budget = budget.replace("{}", "0");
            Log.d("Error", budget);
            mapSearch.setBudget(Integer.parseInt(budget));
            //mapSearch.setBudget(word.getInt("budget"));
            mapSearch.setStudentDiscountInfo(String.valueOf("{}"));
            Log.d("Error", mapSearch.getAddress());
            mapSearchs.add(mapSearch);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
