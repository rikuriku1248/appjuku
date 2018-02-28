package example.android.gakuseimeshi.activity.map;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import example.android.gakuseimeshi.R;
import example.android.gakuseimeshi.activity.storeInfomation.StoreInfomationActivity;
import example.android.gakuseimeshi.database.basicData.LocationInformation;
import example.android.gakuseimeshi.database.basicData.MapData;
import example.android.gakuseimeshi.database.dao.ShopLocationDao;
import example.android.gakuseimeshi.database.dao.ShopMapViewDao;
import example.android.gakuseimeshi.gurunavi.ImageAsyncTask;

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    protected static double destination_latitude;
    protected static double destination_longitude;

    private LatLng destination;

    protected static String destination_name;
    protected static String address;
    private int id;

    protected static String imageURL = "{}";
    protected static LruCache<String, Bitmap> mMemoryCache;
    static ImageView shopImage;
    List<LocationInformation> locationInformations;

    View info_window;

    View view;
    LinearLayout activity_map_layout;
    LinearLayout info_window_layout;
    ViewGroup.MarginLayoutParams info_window_mlp;
    //画面サイズ
    int width;

    protected static Bitmap bitmap;

    static TextView shopName;
    static TextView addressText;

    public MapsActivity(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Log.d("MapsActivity","onCreate,width=" + width);
        info_window = getActivity().getLayoutInflater().inflate(R.layout.info_window, null);

        shopImage = info_window.findViewById(R.id.shopImage);
        shopName = info_window.findViewById(R.id.shopName);
        addressText = info_window.findViewById(R.id.addressText);

        if(!imageURL.equals("{}")) {
            bitmap = mMemoryCache.get(imageURL);

            if (bitmap != null) {
                shopImage.setImageBitmap(bitmap);
            } else {
                shopImage.setTag(imageURL);
                // 画像の設定 "{}"ならno_imageをセットし，URLなら画像を取得してセット
                if (imageURL.equals("{}")) {
                    shopImage.setImageResource(R.drawable.no_image);
                } else {
                    Uri uri = Uri.parse(imageURL);
                    Uri.Builder builder = uri.buildUpon();
                    ImageAsyncTask task = new ImageAsyncTask(shopImage, mMemoryCache);
                    task.execute(builder);
                }
            }
        }else{
            shopImage.setImageResource(R.drawable.no_image);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_maps, container, false);
        //activity_map_layout = view.findViewById(R.id.MapsActivity);

        ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("MapsActivity","onCreateView,width=" + view.getWidth());
                width = view.getWidth();
                set_info_window();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        setLocation();
        setDestinationName();

        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("MapsActivity","onViewCreated,width=" + width);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    //Toast.makeText(getApplicationContext(), "タップ位置\n緯度：" + point.latitude + "\n経度:" + point.longitude, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), MapsActivity2.class);
                    startActivity(intent);
                }
            });
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(destination_latitude, destination_longitude)));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                String id = marker.getId();
                shopName.setText(destination_name + "\n");
                addressText.setText(address);
                Log.d("address", "address=" + address);
                return info_window;
            }
        });

        marker.showInfoWindow();


        CameraUpdate cUpdata = CameraUpdateFactory.newLatLngZoom(destination, 16);
        mMap.moveCamera(cUpdata);
    }

    /**
     * 初期化
     */
    private void init(){
        Bundle bundle = getArguments();
        id = bundle.getInt("id");
        Log.d("MapsActivity","id" + String.valueOf(id));
        ShopMapViewDao shopMapViewDao = new ShopMapViewDao(getContext());
        shopMapViewDao.readDB();
        List<MapData> mapdata = shopMapViewDao.searchId(id);

        if(id != -1) {
            ShopLocationDao shopLocationDao = new ShopLocationDao(getContext());
            shopLocationDao.readDB();
            locationInformations = shopLocationDao.searchId(id);
            Log.d("locationInfomation", String.valueOf(locationInformations.get(0).getLatitude()));
            shopLocationDao.closeDB();

            Log.d("mapdata", String.valueOf(mapdata.get(0).getImage()));
            shopMapViewDao.closeDB();

            imageURL = mapdata.get(0).getImage();
            address = mapdata.get(0).getAddress();

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

    //現在位置及び目的地をセット
    private void setLocation(){
        //destination_latitude = 36.5310338;
        //destination_longitude = 136.6284361;

        destination_latitude = locationInformations.get(0).getLatitude();
        destination_longitude = locationInformations.get(0).getLongitude();

        destination = new LatLng(destination_latitude, destination_longitude);
    }

    //目的地の名前をセット
    private void setDestinationName(){
        destination_name = locationInformations.get(0).getName();
    }

    private void set_info_window(){
        Log.d("MapsActivity","set_info_window");
        // Log.d("MapsActivity","width =" + width);
        info_window_layout = info_window.findViewById(R.id.info_window_layout);
        info_window_mlp = (ViewGroup.MarginLayoutParams) info_window_layout.getLayoutParams();
        info_window_mlp.height = width/7;
        info_window_mlp.width = (width/8)*3;
        info_window_layout.setLayoutParams(info_window_mlp);
        ViewGroup.LayoutParams shopImageLayoutParams = shopImage.getLayoutParams();
        shopImageLayoutParams.width = width/8;
        shopImage.setLayoutParams(shopImageLayoutParams);
        ViewGroup.LayoutParams shopNameLayoutParams = shopName.getLayoutParams();
        shopNameLayoutParams.width = width/4;
        shopName.setLayoutParams(shopNameLayoutParams);
        ViewGroup.LayoutParams addressTextLayoutParams = addressText.getLayoutParams();
        addressTextLayoutParams.width = width/4;
        addressText.setLayoutParams(addressTextLayoutParams);
    }

}