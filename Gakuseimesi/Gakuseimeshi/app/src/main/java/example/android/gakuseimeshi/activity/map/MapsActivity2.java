package example.android.gakuseimeshi.activity.map;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.text.Line;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import example.android.gakuseimeshi.R;
import example.android.gakuseimeshi.database.basicData.MapData;
import example.android.gakuseimeshi.database.dao.ShopLocationDao;
import example.android.gakuseimeshi.database.dao.ShopMapViewDao;
import example.android.gakuseimeshi.gurunavi.ImageAsyncTask;

/**
 * Created by riku on 2017/12/19.
 */

public class MapsActivity2 extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    SupportMapFragment mapFragment;

    private GoogleApiClient googleApiClient;

    protected static GoogleMap mMap;
    //ロケーションアクセスの度合い(0～3)
    static int gpsStatus = 0;

    //現在地及び目的地
    private double present_location_latitude;
    private double present_location_longitude;
    private static double destination_latitude;
    private static double destination_longitude;

    //現在位置及び目的地
    private static LatLng present;
    private static LatLng destination;

    private static String destination_name;
    //所要時間を格納
    protected static String time;
    //目的地までの距離を格納
    protected static String distance;

    protected static MapTypeFragment mapTypeFragment;
    protected static DetailFragment detailFragment;
    private TransportationTab transportationTab;
    //map_typeを配置するFrameLayout
    protected static FrameLayout fragment_container;
    //detail_fragmentを配置するFrameLayout
    protected static FrameLayout detail_fragment_container;
    protected static FrameLayout tab_fragment;
    //map_typeへのタッチリスナー登録に使用
    protected static View map_type_view;
    //detail_fragmentへのタッチリスナー登録に使用
    protected static View detail_fragment_view;
    protected static View transportationTab_view;
    //map_typeのアニメーション
    protected static MapTypeAnimation mapTypeAnimation;

    //経路の詳細を入れるリスト
    protected static ArrayList<String> routeList = new ArrayList<>();

    //画面の大きさを格納
    protected static int maps_view_width;
    protected static int maps_view_height;

    private FusedLocationProviderApi fusedLocationProviderApi;
    private LocationRequest request;

    private static DirectionsResult result_walk;
    private static DirectionsResult result_car;
    //0:result_walk, 1:result_car
    private static int result_mode = 0;
    //各ステップのポリラインを格納
    private static ArrayList<Polyline> step_polyline;

    private boolean showRouteStatus = false;

    private final int REQUEST_PERMISSION = 10;

    ImageButton map_type_button;

    ImageButton myLocation_button;

    private String imageURL = "{}";
    private LruCache<String, Bitmap> mMemoryCache;
    ImageView shopImage;

    protected static View info_window;
    protected static TextView shopName;

    private static Marker start_marker;
    private static Marker end_marker;

    protected static String address;
    protected static TextView addressText;

    //現在地マーカーのbitmap
    static Bitmap start_marker_icon;

    private static List<LatLng> Path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        showRouteStatus = false;

        step_polyline = new ArrayList<>();

        start_marker_icon = BitmapFactory.decodeResource(getResources(), R.drawable.start_marker_icon);

        info_window = getLayoutInflater().inflate(R.layout.info_window, null);
        imageURL = MapsActivity.imageURL;
        mMemoryCache = MapsActivity.mMemoryCache;
        shopImage = info_window.findViewById(R.id.shopImage);
        shopName = info_window.findViewById(R.id.shopName);
        addressText = info_window.findViewById(R.id.addressText);
        address = MapsActivity.address;

        if(!imageURL.equals("{}")) {
            Bitmap bitmap = mMemoryCache.get(imageURL);

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

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        //位置情報のリクエスト情報を取得
        request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1);

        setLocation();
        setDestinationName();
        makeFragment();
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
            try{
                Thread.sleep(1000); //3000ミリ秒Sleepする
                CheckLocationStatus();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }

        Log.d("MapsActivity2", "onCreate");
        map_type_view = findViewById(R.id.fragment_container);
        fragment_container = findViewById(R.id.fragment_container);
        map_type_view.setOnTouchListener((View.OnTouchListener) mapTypeFragment);
        detail_fragment_view = findViewById(R.id.detail_fragment_container);
        detail_fragment_container = findViewById(R.id.detail_fragment_container);
        detail_fragment_view.setOnTouchListener((View.OnTouchListener) detailFragment);
        tab_fragment = findViewById(R.id.tab_fragment);
        transportationTab_view = findViewById(R.id.tab_fragment);

        setMapTypeButton();
        setMyLocationButton();

        mapFragment.getMapAsync(this);
    }


    private void setMapTypeButton(){
        map_type_button = findViewById(R.id.mapType);
        map_type_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapTypeAnimation = new MapTypeAnimation(map_type_view, -maps_view_width, (maps_view_width/3)*2, 1000);
                mapTypeAnimation.setAnimation();
            }
        });
    }

    private void setMyLocationButton(){
        myLocation_button = findViewById(R.id.myLocation);
        myLocation_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    CameraUpdate cUpdata = CameraUpdateFactory.newLatLngZoom(present, 16);
                    mMap.animateCamera(cUpdata, 1000, null);
                }catch (NullPointerException e){
                    Toast.makeText(getApplicationContext(), "現在地を取得できません", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //mapTypeFragment及びdetailFragmentを生成
    private void makeFragment(){
        mapTypeFragment = new MapTypeFragment();
        final android.app.FragmentTransaction mapTypeTransaction = getFragmentManager().beginTransaction();
        mapTypeTransaction.replace(R.id.fragment_container, mapTypeFragment);
        mapTypeTransaction.show(mapTypeFragment);
        mapTypeTransaction.commit();


        detailFragment = new DetailFragment();
        final android.app.FragmentTransaction detailTransaction = getFragmentManager().beginTransaction();
        detailTransaction.replace(R.id.detail_fragment_container, detailFragment);
        detailTransaction.show(detailFragment);
        detailTransaction.commit();

        transportationTab = new TransportationTab();
        final android.app.FragmentTransaction TransportTransaction = getFragmentManager().beginTransaction();
        TransportTransaction.replace(R.id.tab_fragment, transportationTab);
        TransportTransaction.show(transportationTab);
        TransportTransaction.commit();

    }

    //目的地をセット
    private void setLocation() {
        //present_location_latitude = 36.578268;
        //present_location_longitude = 136.662819;

        //destination_latitude = 36.5310338;
        //destination_longitude = 136.6284361;

        //イオンモールかほく
        //destination_latitude = 36.709294;
        //destination_longitude = 136.695049;

        destination_latitude = MapsActivity.destination_latitude;
        destination_longitude = MapsActivity.destination_longitude;

        destination = new LatLng(destination_latitude, destination_longitude);
    }

    //目的地の名前をセット
    private void setDestinationName() {
        destination_name = MapsActivity.destination_name;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            return;
        }
        //mMap.setMyLocationEnabled(true);

        CameraUpdate cUpdata = CameraUpdateFactory.newLatLngZoom(destination, 16);
        mMap.moveCamera(cUpdata);
        //二回呼ばれるのを防ぐ
        if(!showRouteStatus) {
            showRoute(mMap, present, destination);
            showRouteStatus = true;
        }
    }

    private void showRoute(final GoogleMap map, final LatLng presentLatLng, final LatLng destinationLatLng) {

        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;

            DateTime now = new DateTime();

            Log.d("MapsActivity", "debug");

            //徒歩経路リクエスト
            result_walk = DirectionsApi.newRequest(getGeoContext(bundle))
                    .mode(TravelMode.WALKING)
                    .origin(presentLatLng.latitude + "," + presentLatLng.longitude)
                    .destination(destinationLatLng.latitude + "," + destinationLatLng.longitude)
                    .departureTime(now)
                    .language("ja")
                    .await();
            //自動車経路リクエスト
            result_car = DirectionsApi.newRequest(getGeoContext(bundle))
                    .mode(TravelMode.DRIVING)
                    //.mode(TravelMode.WALKING)
                    .origin(presentLatLng.latitude + "," + presentLatLng.longitude)
                    .destination(destinationLatLng.latitude + "," + destinationLatLng.longitude)
                    .departureTime(now)
                    .language("ja")
                    .await();
            Log.i("MapsActivity2", "walk:"+String.valueOf(result_walk.routes[0].legs[0].steps.length)+ ", car:"+String.valueOf(result_car.routes[0].legs[0].steps.length));

            //デフォルトで徒歩経路表示
            mMap.clear();
            addMarkers(result_walk, map);
            addPolyline(result_walk, map);
            detailFragment.setTime_and_Distance(time + ", " + distance);
            setRouteList(result_walk);

            moveCamera(presentLatLng, destinationLatLng, 200);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d("MapsActivity2", "NameNotFound");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("MapsActivity2", "Interrupted");
        } catch (IOException e) {
            e.printStackTrace();
            //位置情報が得られなかった場合, 目的地にマーカー設置のみ行う
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(destination_latitude,
                            destination_longitude))
                    .title(destination_name));
            Log.d("MapsActivity2", "IOException");
        } catch (ApiException e) {
            e.printStackTrace();
            Log.d("MapsActivity2", "ApiException");
        } catch (NullPointerException e) {
            Log.d("MapsActivity2", "Null");
            //位置情報が得られなかった場合, 目的地にマーカー設置のみ行う
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(destination_latitude,
                            destination_longitude))
                    .title(destination_name));
            //位置情報が許可されているか確認
            CheckLocationStatus();
        }
    }

    public static void walk_or_car(int mode){
        //徒歩経路表示ボタン
        try {
            if (mode == 0) {
                mMap.clear();
                addMarkers(result_walk, mMap);
                reset_polyline(mode);
                detailFragment.setTime_and_Distance(time + ", " + distance);
                setRouteList(result_walk);
                result_mode = 0;
                moveCamera(present, destination, 200);
            } else if (mode == 1) {
                //自動車経路表示ボタン
                mMap.clear();
                addMarkers(result_car, mMap);
                reset_polyline(mode);
                detailFragment.setTime_and_Distance(time + ", " + distance);
                setRouteList(result_car);
                result_mode = 1;
                moveCamera(present, destination, 200);
            }
        }catch (NullPointerException e) {
            Log.d("MapsActivity2", "Null");
            //位置情報が得られなかった場合, 目的地にマーカー設置のみ行う
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(destination_latitude,
                            destination_longitude))
                    .title(destination_name));
            //位置情報が許可されているか確認
            //CheckLocationStatus();
        }
    }

    //ロケーションアクセスの度合いの確認
    private void CheckLocationStatus(){
        try {
            gpsStatus = Settings.Secure.getInt(
                    getContentResolver(), String.valueOf(Settings.Secure.LOCATION_MODE)
            );
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        /*
        Toast.makeText(getApplicationContext(), "GPSstatus:" + gpsStatus,
                Toast.LENGTH_LONG).show();
        */
        if(gpsStatus == Settings.Secure.LOCATION_MODE_OFF){
            LocationInfoDialog locationInfoDialog = new LocationInfoDialog();
            locationInfoDialog.show(getFragmentManager(), "LocationInfomation");
        }
    }

    private static void moveCamera(LatLng presentLatLng, LatLng destinationLatLng, int i){
        //カメラを経路全体が見える位置に移動
        LatLngBounds pre_des;
        if(presentLatLng.longitude >= destinationLatLng.longitude) {
            if(presentLatLng.latitude >= destinationLatLng.latitude) {
                pre_des = new LatLngBounds(destinationLatLng, presentLatLng);
            }else{
                LatLng lower_left = new LatLng(presentLatLng.latitude, destinationLatLng.longitude);
                LatLng upper_right = new LatLng(destinationLatLng.latitude, presentLatLng.longitude);
                pre_des = new LatLngBounds(lower_left, upper_right);
            }
        }else{
            if(presentLatLng.latitude >= destinationLatLng.latitude) {
                LatLng lower_left = new LatLng(destinationLatLng.latitude, presentLatLng.longitude);
                LatLng upper_right = new LatLng(presentLatLng.latitude, destinationLatLng.longitude);
                pre_des = new LatLngBounds(lower_left, upper_right);
            }else{
                pre_des = new LatLngBounds(presentLatLng, destinationLatLng);
            }
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(pre_des, 100));
        //presentLatLng, destinationLatLngが収まる位置に1秒かけて移動
        if(pre_des != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(pre_des, maps_view_width, maps_view_height, i), 1000, null);
        }
    }

    //クリックされた経路ステップにカメラを移動
    protected static void focusRouteStep(int num){
        if(result_mode == 0) {//route_displayに徒歩経路表示中
            LatLng start_walk = new LatLng(result_walk.routes[0].legs[0].steps[num].startLocation.lat, result_walk.routes[0].legs[0].steps[num].startLocation.lng);
            LatLng end_walk = new LatLng(result_walk.routes[0].legs[0].steps[num].endLocation.lat, result_walk.routes[0].legs[0].steps[num].endLocation.lng);
            moveCamera(start_walk, end_walk, 200);
            try {
                set_select_polyline(0, num);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }else if(result_mode == 1){//route_displayに自動車経路表示中
            LatLng start_car = new LatLng(result_car.routes[0].legs[0].steps[num].startLocation.lat, result_car.routes[0].legs[0].steps[num].startLocation.lng);
            LatLng end_car = new LatLng(result_car.routes[0].legs[0].steps[num].endLocation.lat, result_car.routes[0].legs[0].steps[num].endLocation.lng);
            moveCamera(start_car, end_car, 200);
            try {
                set_select_polyline(1, num);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    private static void reset_polyline(int mode){
        if(mode == 0) {

            for (int i = 0; i < result_car.routes[0].legs[0].steps.length; i++) {
                step_polyline.get(i).remove();
            }
            step_polyline.clear();
            addPolyline(result_walk, mMap);
        }else if(mode == 1){
            for (int i = 0; i < result_walk.routes[0].legs[0].steps.length; i++) {
                step_polyline.get(i).remove();
            }
            step_polyline.clear();
            addPolyline(result_car, mMap);
        }
    }

    private static void set_select_polyline(int mode, int num){
        if(mode == 0) {
            try {
                for (int i = 0; i < result_walk.routes[0].legs[0].steps.length; i++) {
                    step_polyline.get(i).remove();
                }
                step_polyline.clear();
                for (int i = 0; i < result_walk.routes[0].legs[0].steps.length; i++) {
                    if(i == num){
                        Path = PolyUtil.decode(result_walk.routes[0].legs[0].steps[i].polyline.getEncodedPath());
                        step_polyline.add(mMap.addPolyline(new PolylineOptions().addAll(Path).color(Color.argb(255, 255, 0, 0))));
                    }else {
                        Path = PolyUtil.decode(result_walk.routes[0].legs[0].steps[i].polyline.getEncodedPath());
                        step_polyline.add(mMap.addPolyline(new PolylineOptions().addAll(Path).color(Color.argb(255, 0, 0, 255))));
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }else if(mode == 1){
            try {
                for (int i = 0; i < result_car.routes[0].legs[0].steps.length; i++) {
                    step_polyline.get(i).remove();
                }
                step_polyline.clear();
                for (int i = 0; i < result_walk.routes[0].legs[0].steps.length; i++) {
                    if(i == num){
                        Path = PolyUtil.decode(result_car.routes[0].legs[0].steps[i].polyline.getEncodedPath());
                        step_polyline.add(mMap.addPolyline(new PolylineOptions().addAll(Path).color(Color.argb(255, 255, 0, 0))));
                    }else {
                        Path = PolyUtil.decode(result_car.routes[0].legs[0].steps[i].polyline.getEncodedPath());
                        step_polyline.add(mMap.addPolyline(new PolylineOptions().addAll(Path).color(Color.argb(255, 0, 0, 255))));
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    private GeoApiContext getGeoContext(Bundle bundle) {
        GeoApiContext geoApiContext = new GeoApiContext();
        geoApiContext.setQueryRateLimit(3).setApiKey(bundle.getString("com.google.android.geo.API_KEY"))
                .setConnectTimeout(1, TimeUnit.SECONDS).setReadTimeout(1, TimeUnit.SECONDS).setWriteTimeout(1, TimeUnit.SECONDS);
        return geoApiContext;
    }

    //マーカーの設置
    private static void addMarkers(DirectionsResult results, GoogleMap mMap) {
        try {
            //start_marker_iconの設定
            start_marker_icon = Bitmap.createScaledBitmap(start_marker_icon, maps_view_width/15, maps_view_width/15, false);
            start_marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(results.routes[0].legs[0]
                            .startLocation.lat, results.routes[0]
                            .legs[0].startLocation.lng))
                            .icon(BitmapDescriptorFactory.fromBitmap(start_marker_icon)));
            //.title(results.routes[0].legs[0].startAddress));
            start_marker.setTag("start_marker");
            end_marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(results.routes[0]
                            .legs[0].endLocation.lat, results.routes[0]
                            .legs[0].endLocation.lng))
                    //.title(results.routes[0].legs[0].startAddress)
                    .title(destination_name)
                    .snippet(getTime(results) + getDistance(results)));
            end_marker.setTag("end_marker");


            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    String id = marker.getId();
                    String tag = (String) marker.getTag();

                    shopName.setText(destination_name + "\n");
                    addressText.setText(address);
                    Log.d("MarkerID", "tag=" + tag);

                    if(tag.equals("end_marker")){
                        return info_window;
                    }

                    return null;
                }
            });

            end_marker.showInfoWindow();

        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    //所要時間の取得
    private static String getTime(DirectionsResult results) {
        int hour = (int) (results.routes[0].legs[0].duration.inSeconds / 3600);
        int minutes = (int) ((results.routes[0].legs[0].duration.inSeconds % (60 * 60)) / 60);

        if (hour != 0) {
            time = "所要時間 :" + hour + "時間" + minutes + "分";
            return "所要時間 :" + hour + "時間" + minutes + "分";
        } else {
            time = "所要時間 :" + minutes + "分";
            return "所要時間 :" + minutes + "分";
        }
    }

    //目的地までの距離取得
    private static String getDistance(DirectionsResult results) {
        distance = " 距離 :" + results.routes[0].legs[0].distance;
        return " 距離 :" + results.routes[0].legs[0].distance;
    }

    private static void setRouteList(DirectionsResult results){
        routeList.clear();
        for(int i = 0; i < results.routes[0].legs[0].steps.length; i++) {
            //各ステップの距離を格納
            String step_distance = results.routes[0].legs[0].steps[i].distance.toString();
            //経路の詳細を格納
            String route_detail = results.routes[0].legs[0].steps[i].htmlInstructions
                    .replaceAll("<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "")
                    .toString();

            routeList.add("[" + i + "] " + step_distance + "先 " + route_detail);
            //Log.d("MapsActivity2", i+"番目:"+step_distance + "先 " + route_detail);
        }
        detailFragment.makeListView();
    }

    //地図上に経路の表示
    private static void addPolyline(DirectionsResult results, GoogleMap mMap) {
        try {
            for(int i = 0; i < results.routes[0].legs[0].steps.length; i++) {
                Path = PolyUtil.decode(results.routes[0].legs[0].steps[i].polyline.getEncodedPath());
                step_polyline.add(mMap.addPolyline(new PolylineOptions().addAll(Path).color(Color.argb(255, 0, 0, 255))));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Google Playへの接続
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 位置情報リクエストの解除、及び、Google Playからの切断
        if (googleApiClient != null && googleApiClient.isConnected()) {
            fusedLocationProviderApi.removeLocationUpdates(googleApiClient, (LocationListener) this);
        }
        googleApiClient.disconnect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        //result_modeを徒歩に戻す
        result_mode = 0;
        Log.d("MarkerID", "stop");
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // ACCESS_FINE_LOCATIONへのパーミッションを確認

        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, android.Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // 位置情報の監視を開始
        fusedLocationProviderApi.requestLocationUpdates(googleApiClient, request, this);
    }

    // 位置情報許可の確認
    protected void checkPermission() {
        // 既に許可している場合
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        }
        // 拒否していた場合
        else {
            requestLocationPermission();
        }
    }

    // 許可を求める
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);
        } else {
            Toast toast = Toast.makeText(this,
                    "許可がないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}


    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        RelativeLayout activity_map2_layout = findViewById(R.id.MapsActivity2);
        //画面サイズを取得
        int layout_width = activity_map2_layout.getWidth();
        int layout_height = activity_map2_layout.getHeight();

        maps_view_width = layout_width;
        maps_view_height = layout_height;

        LinearLayout line1 = findViewById(R.id.line1);
        ViewGroup.LayoutParams line1_lp = line1.getLayoutParams();
        //line1.setGravity();

        int map_type_bitmap_size = layout_width/15;
        //map_type_buttonの設定
        Bitmap map_type_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_type_button);
        map_type_bitmap = Bitmap.createScaledBitmap(map_type_bitmap, map_type_bitmap_size, map_type_bitmap_size, false);
        map_type_button.setImageBitmap(map_type_bitmap);
        ViewGroup.LayoutParams map_type_button_lp = map_type_button.getLayoutParams();
        ViewGroup.MarginLayoutParams map_type_button_mlp = (ViewGroup.MarginLayoutParams) map_type_button_lp;
        map_type_button_mlp.setMargins(maps_view_width/37, maps_view_height/12, map_type_button_mlp.rightMargin, map_type_button_mlp.bottomMargin);

        int my_location_bitmap_size = layout_width/15;
        //my_location_buttonの設定
        Bitmap my_location_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.my_location_button);
        my_location_bitmap = Bitmap.createScaledBitmap(my_location_bitmap, my_location_bitmap_size, my_location_bitmap_size, false);
        myLocation_button.setImageBitmap(my_location_bitmap);
        ViewGroup.LayoutParams my_location_button_lp = myLocation_button.getLayoutParams();
        ViewGroup.MarginLayoutParams my_location_button_mlp = (ViewGroup.MarginLayoutParams) my_location_button_lp;
        my_location_button_mlp.setMargins(my_location_button_mlp.leftMargin, maps_view_height/12, maps_view_width/37, my_location_button_mlp.bottomMargin);

        //start_marker_iconの設定
        start_marker_icon = BitmapFactory.decodeResource(getResources(), R.drawable.start_marker_icon);
        start_marker_icon = Bitmap.createScaledBitmap(start_marker_icon, my_location_bitmap_size, my_location_bitmap_size, false);

        //map_typeの初期位置の変更
        RelativeLayout.LayoutParams f_lp = (RelativeLayout.LayoutParams) MapsActivity2.fragment_container.getLayoutParams();
        ViewGroup.MarginLayoutParams f_mlp = f_lp;
        f_mlp.setMargins(-maps_view_width, maps_view_height/15, maps_view_width, f_mlp.bottomMargin);
        MapsActivity2.fragment_container.setLayoutParams(f_mlp);

        //info_windowのサイズ
        LinearLayout info_indow_layout = info_window.findViewById(R.id.info_window_layout);
        ViewGroup.MarginLayoutParams info_window_mlp = (ViewGroup.MarginLayoutParams) info_indow_layout.getLayoutParams();
        info_window_mlp.height = maps_view_width/7;
        info_window_mlp.width = (maps_view_width/8)*3;
        info_indow_layout.setLayoutParams(info_window_mlp);
        ViewGroup.LayoutParams shopImageLayoutParams = shopImage.getLayoutParams();
        shopImageLayoutParams.width = maps_view_width/8;
        shopImage.setLayoutParams(shopImageLayoutParams);
        ViewGroup.LayoutParams shopNameLayoutParams = shopName.getLayoutParams();
        shopNameLayoutParams.width = maps_view_width/4;
        shopName.setLayoutParams(shopNameLayoutParams);
        ViewGroup.LayoutParams addressTextLayoutParams = addressText.getLayoutParams();
        addressTextLayoutParams.width = maps_view_width/4;
        addressText.setLayoutParams(addressTextLayoutParams);

        detailFragment.setTextViewParam();

        //detail_fragmentの初期位置変更
        RelativeLayout.LayoutParams f_lp2 = (RelativeLayout.LayoutParams) MapsActivity2.detail_fragment_container.getLayoutParams();
        ViewGroup.MarginLayoutParams f_mlp2 = f_lp2;
        f_mlp2.setMargins(f_mlp2.leftMargin, maps_view_height - maps_view_height/10,
                f_mlp2.rightMargin, -maps_view_height + maps_view_height/10);
        MapsActivity2.detail_fragment_container.setLayoutParams(f_mlp2);

        //transportationのサイズ変更
        ViewGroup.LayoutParams params = transportationTab_view.getLayoutParams();
        params.height = maps_view_height/15;
        transportationTab_view.setLayoutParams(params);

        //Log.d("MapsActivity2", "width=" + layout_width +
        //       ", height=" + layout_height);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("MapsActivity2", "Lat:"+location.getLatitude()
                +", Lng:"+location.getLongitude());
        present = new LatLng(location.getLatitude(), location.getLongitude());
        //map, 現在地, 目的地を引数に指定
        showRoute(mMap, present, destination);
    }
}