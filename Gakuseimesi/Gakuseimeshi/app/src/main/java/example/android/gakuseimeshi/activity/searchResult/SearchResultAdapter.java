package example.android.gakuseimeshi.activity.searchResult;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.android.gakuseimeshi.R;
import example.android.gakuseimeshi.database.basicData.MapData;
import example.android.gakuseimeshi.database.basicData.MapSearch;
import example.android.gakuseimeshi.database.basicData.ReviewData;
import example.android.gakuseimeshi.database.dao.ShopMapViewDao;
import example.android.gakuseimeshi.database.dao.ShopReviewDao;
import example.android.gakuseimeshi.gurunavi.ImageAsyncTask;

import example.android.gakuseimeshi.activity.searchResult.SearchResultActivity;

/**
 * Created by Yusuke on 2018/02/06.
 * 検索結果画面のリストビューのアダプター
 */
public class SearchResultAdapter extends BaseAdapter {
    private Context context = null;
    private ArrayList<MapSearch> researchResult = null;
    private int resource = 0;
    private Typeface shopNameFont;
    private Typeface shopHourFont;
    // 2018/02/07 中山 翔夢
    private List<MapData> ImageURLList = new ArrayList<MapData>();
    private List<ReviewData> reviewDatas = new ArrayList<ReviewData>();
    private LruCache<String, Bitmap> mMemoryCache;

    // コンストラクター(コンテキスト、データソース、レイアウトファイルを設定)
    public SearchResultAdapter(Context context,
                               ArrayList<MapSearch> researchResult, int resource, LruCache<String, Bitmap> memoryCache) {
        this.context = context;
        this.researchResult = researchResult;
        this.resource = resource;
        this.mMemoryCache = memoryCache;


        Log.d("Error",String.valueOf(getCount()));
        for (int i = 0; i < getCount(); i++) {
            ShopMapViewDao shopMapViewDao = new ShopMapViewDao(context);
            shopMapViewDao.readDB();
            List<MapData> id = shopMapViewDao.searchId(researchResult.get(i).getId());
            Log.d("Error", String.valueOf(i) + String.valueOf(id.get(0).getId()));
            ImageURLList.add(id.get(0));
            Log.d("Error", String.valueOf(i) + ImageURLList.get(i).getImage());
            shopMapViewDao.closeDB();
        }

        setReviewDatas();

        shopNameFont = Typeface.createFromAsset(context.getAssets(),"ipaexm.ttf");
        shopHourFont = Typeface.createFromAsset(context.getAssets(),"GenEiAntiqueTN-M.ttf");
    }

    // データ項目の個数を取得
    @Override
    public int getCount() {
        return researchResult.size();
    }

    // 指定された項目を取得
    @Override
    public Object getItem(int index) {
        return researchResult.get(index);
    }

    // 指定された項目を識別するためのid値を取得
    @Override
    public long getItemId(int index){
        return researchResult.get(index).getId();
    }

    // リスト項目を表示するためのViewを取得
    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        Activity activity = (Activity)context;
        MapSearch item = (MapSearch)getItem(index);
        String imageURL = ImageURLList.get(index).getImage();
        if (convertView == null){
            convertView = activity.getLayoutInflater().inflate(resource, null);
        }

        ImageView imageView = (ImageView)convertView.findViewById(R.id.shopImage);

        final Bitmap bitmap = mMemoryCache.get(imageURL);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setTag(imageURL);
            // 画像の設定 "{}"ならno_imageをセットし，URLなら画像を取得してセット
            if (imageURL.equals("{}")) {
                imageView.setImageResource(R.drawable.no_image);
                List<String> images = setUserImageURL(ImageURLList.get(index).getName());

                if(images.size() != 0){
                    final Bitmap bitmap2 = mMemoryCache.get(images.get(0));
                    if(bitmap2 != null){
                        Log.d("bit2","aaaa");
                        imageView.setImageBitmap(bitmap2);
                    }else {
                        Log.d("no_iamge","aaaa");
                        Uri uri = Uri.parse(images.get(0));
                        Uri.Builder builder = uri.buildUpon();
                        ImageAsyncTask task = new ImageAsyncTask(imageView, mMemoryCache);
                        task.execute(builder);
                    }
                }
            } else {
                Uri uri = Uri.parse(imageURL);
                Uri.Builder builder = uri.buildUpon();
                ImageAsyncTask task = new ImageAsyncTask(imageView, mMemoryCache);
                task.execute(builder);
            }
        }
        /*
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setTag(imageURL);
            // 画像の設定 "{}"ならno_imageをセットし，URLなら画像を取得してセット
            if (imageURL.equals("{}")) {
                List<String> images = setUserImageURL(ImageURLList.get(index).getName());
                if(images.size() != 0){
                    Uri uri = Uri.parse(images.get(0));
                    Uri.Builder builder = uri.buildUpon();
                    ImageAsyncTask task = new ImageAsyncTask(imageView, mMemoryCache);
                    task.execute(builder);
                }
            } else {
                Uri uri = Uri.parse(imageURL);
                Uri.Builder builder = uri.buildUpon();
                ImageAsyncTask task = new ImageAsyncTask(imageView, mMemoryCache);
                task.execute(builder);
            }
        }*/

        ((TextView)convertView.findViewById(R.id.shopName)).setText(item.getName());
        ((TextView)convertView.findViewById(R.id.shopName)).setTypeface(shopNameFont);
        ((TextView)convertView.findViewById(R.id.shopHour)).setText(item.getAddress());
        ((TextView)convertView.findViewById(R.id.shopHour)).setTypeface(shopHourFont);

        return convertView;
    }

    /**
     * List<ReviewData>に値を格納
     */
    private void setReviewDatas(){
        ShopReviewDao shopReviewDao = new ShopReviewDao(context);
        shopReviewDao.readDB();
        reviewDatas = shopReviewDao.findAll();
        shopReviewDao.closeDB();
    }

    private List<String> setUserImageURL(String name){
        List<String> names = new ArrayList<String>();
        StringBuilder search_name = new StringBuilder(name);
        search_name.delete(name.length() - 1, name.length());
        name = search_name.toString();
        for(ReviewData reaviewData : reviewDatas){
            if(reaviewData.getName().equals(name)){
                names.add(reaviewData.getImage());
                Log.d("name",reaviewData.getName());
                Log.d("setUserImageURL", reaviewData.getImage());
            }
        }

        return names;
    }
}