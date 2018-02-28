package example.android.gakuseimeshi.gurunavi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

import example.android.gakuseimeshi.database.basicData.ReviewData;
import example.android.gakuseimeshi.database.dao.ShopReviewDao;

/**
 * Created by Tomu on 2018/02/23.
 */

public class SearchShopReview extends AsyncTask<Void, Void, String> {
    int count = 1;
    private int maxCount = 1;
    private List<ReviewData> reviewDatas = new ArrayList<ReviewData>();
    private Context context;


    public SearchShopReview(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // doInBackground前処理
        reviewDatas.clear();
    }

    @Override
    protected String doInBackground(Void... params) {
        setSearchData();

        ShopReviewDao shopReviewDao;
        shopReviewDao = new ShopReviewDao(context);
        shopReviewDao.openDB();
        shopReviewDao.replaceDB(reviewDatas);
        shopReviewDao.closeDB();

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

        do {
            String lat = "36.5310338";
            String lon = "136.6284361";
            String range = "5";
            String hit_per_page = "50";
            String offset_page = String.valueOf(count);
            String format = "json";

            // エンドポイント
            String gnaviRestUri = "https://api.gnavi.co.jp/PhotoSearchAPI/20150630/";

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
            JSONObject json_response = json.getJSONObject("response");
            Log.d("json_response", json_response.toString());
            maxCount = Integer.parseInt(json_response.getString("total_hit_count")) / 100 + 1;

            if(count == maxCount){
                for(int i  = 0; i < Integer.parseInt(json_response.getString("total_hit_count")) % 50; i++){
                    JSONObject number_object = json_response.getJSONObject(String.valueOf(i)).getJSONObject("photo");
                    Log.d("number_object", String.valueOf(number_object));
                    Log.d("count", String.valueOf(i));
                    storeReviewData(number_object);
                }
            }else{
                for(int i = 0; i < 50; i++){
                    JSONObject number_object = json_response.getJSONObject(String.valueOf(i));
                    storeReviewData(number_object);
                }
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
     * レビュー情報の格納
     * @param word
     */
    private void storeReviewData(JSONObject word){
        try{
            ReviewData reviewData = new ReviewData();
            reviewData.setId(word.getInt("vote_id"));
            reviewData.setName(word.getString("shop_name"));
            reviewData.setComment(word.getString("comment"));
            reviewData.setImage(word.getJSONObject("image_url").getString("url_320"));
            reviewDatas.add(reviewData);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }



}
