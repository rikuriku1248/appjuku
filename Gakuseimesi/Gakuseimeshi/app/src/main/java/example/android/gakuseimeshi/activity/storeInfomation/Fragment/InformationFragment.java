package example.android.gakuseimeshi.activity.storeInfomation.Fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.android.gakuseimeshi.R;
import example.android.gakuseimeshi.activity.storeInfomation.StoreInfomationActivity;
import example.android.gakuseimeshi.database.basicData.MapData;
import example.android.gakuseimeshi.database.basicData.ReviewData;
import example.android.gakuseimeshi.database.dao.ShopMapSearchDao;
import example.android.gakuseimeshi.database.dao.ShopMapViewDao;
import example.android.gakuseimeshi.database.dao.ShopReviewDao;
import example.android.gakuseimeshi.gurunavi.ImageAsyncTask;

public class InformationFragment extends Fragment {
    private StoreInfomationActivity activity = new StoreInfomationActivity();
    private String genre;
    private String tel;
    private String address;
    private String access;
    private String opentime;
    private String holiday;
    private String imageURL = "{}";
    private Uri uri = Uri.parse("tel:" + tel);
    private LruCache<String, Bitmap> mMemoryCache;
    private int id;
    private int favorite_status;

    public InformationFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        ((TextView)view.findViewById(R.id.name)).setText(activity.name);
        ((TextView)view.findViewById(R.id.genre)).setText(genre);
        ((TextView)view.findViewById(R.id.tel)).setText(tel);
        ((TextView)view.findViewById(R.id.address)).setText(address);
        ((TextView)view.findViewById(R.id.access)).setText(access);
        ((TextView)view.findViewById(R.id.opentime)).setText(opentime);
        ((TextView)view.findViewById(R.id.holiday)).setText(holiday);
        // 画像をWeb上から取得しひょじ
        ImageView imageView = (ImageView)view.findViewById(R.id.storeImage);
        showImage(imageView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // 電話番号のクリック処理
        TextView tel = (TextView)getActivity().findViewById(R.id.tel);
        tel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent call = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(call);
            }
        });

        final Button favorite = (Button)getActivity().findViewById(R.id.favorite_button);
        getFavoriteStatus();
        if(favorite_status == 1) {
            favorite.setBackgroundResource(R.drawable.fav_on);
        }else if(favorite_status == 0){
            favorite.setBackgroundResource(R.drawable.ic_tab_fav);
        }else if(favorite_status == -1){
            Log.d("Error","Faild Update Favorite Info");
        }else{
            Log.d("Error","Unkwon");
        }


        // お気に入り情報のクリック処理
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorite_status = updateFavoriteStatus();
                if(favorite_status == 1) {
                    favorite.setBackgroundResource(R.drawable.fav_on);
                }else if(favorite_status == 0){
                    favorite.setBackgroundResource(R.drawable.ic_tab_fav);
                }else if(favorite_status == -1){
                    Log.d("Error","Faild Update Favorite Info");
                }else{
                    Log.d("Error","Unkwon");
                }
            }
        });



    }

    /**
     * 画像の表示
     * @param imageView
     */
    public void showImage(ImageView imageView){
        Log.d("url", imageURL);
        if(!imageURL.equals("{}")) {
            connectImage(imageView);
        }else{
            Log.d("name", activity.name);
            ShopReviewDao shopReviewDao= new ShopReviewDao(getContext());
            shopReviewDao.readDB();
            StringBuilder name = new StringBuilder(activity.name);
            name.delete(name.length() - 1, name.length());
            activity.name = name.toString();
            List<ReviewData> reviewDatas = shopReviewDao.searchName(activity.name);
            shopReviewDao.closeDB();
            for(int i = 0; i < reviewDatas.size(); i++){
                Log.d("review", String.valueOf(reviewDatas.get(i).getName()));
                imageURL = reviewDatas.get(i).getImage();
                connectImage(imageView);
            }

            imageView.setImageResource(R.drawable.no_image);
        }
    }

    /**
     * urlから画像の表示
     */
    private void connectImage(ImageView imageView){
        final Bitmap bitmap = mMemoryCache.get(imageURL);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setTag(imageURL);
            Uri uri = Uri.parse(imageURL);
            Uri.Builder builder = uri.buildUpon();
            ImageAsyncTask task = new ImageAsyncTask(imageView, mMemoryCache);
            task.execute(builder);
        }
    }

    /**
     * 初期化
     */
    private void init(){
        List<MapData> mapdata = getIdStatus();

        if(id != -1){
            Log.d("mapdata", String.valueOf(mapdata.get(0).getImage()));

            activity.name = mapdata.get(0).getName();
            genre = mapdata.get(0).getNameKana();
            tel = mapdata.get(0).getTel();
            address = mapdata.get(0).getAddress();
            access = "";
            opentime = mapdata.get(0).getOpentime();
            holiday = mapdata.get(0).getHoliiday();
            imageURL = mapdata.get(0).getImage();

            final int memClass = ((ActivityManager)getContext().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            final int cacheSize = 1024 * 1024 * memClass / 8;

            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount();
                }
            };
        }

    }

    /**
     * id情報から店舗情報データの入手
     * @return
     */
    public List<MapData> getIdStatus(){
        Bundle bundle = getArguments();
        id = bundle.getInt("id");
        ShopMapViewDao shopMapViewDao = new ShopMapViewDao(getContext());
        shopMapViewDao.readDB();
        List<MapData> mapdata = shopMapViewDao.searchId(id);
        shopMapViewDao.closeDB();
        return mapdata;
    }

    /**
     * お気に入り情報の入手
     */
    public void getFavoriteStatus(){
        ShopMapSearchDao shopMapSearchDao = new ShopMapSearchDao(getContext());
        shopMapSearchDao.readDB();
        favorite_status = shopMapSearchDao.getFavorite(id);
        Log.d("favorite",String.valueOf(favorite_status));
        shopMapSearchDao.closeDB();
    }

    /**
     * お気に入り情報の更新
     */
    public int updateFavoriteStatus(){
        ShopMapSearchDao shopMapSearchDao = new ShopMapSearchDao(getContext());
        shopMapSearchDao.openDB();
        int updateStatus = shopMapSearchDao.updateFavorite(id, favorite_status);
        Log.d("favorite",String.valueOf(updateStatus));
        shopMapSearchDao.closeDB();
        return updateStatus;
    }
}
