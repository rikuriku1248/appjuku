package example.android.samplemap3;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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

/**
 * Created by riku on 2017/12/19.
 */

public class MapsActivity2 extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    SupportMapFragment mapFragment;

    private GoogleApiClient googleApiClient;

    public static GoogleMap mMap;
    private final int REQUEST_PERMISSION = 10;

    //現在地及び目的地
    private double present_location_latitude;
    private double present_location_longitude;
    private double destination_latitude;
    private double destination_longitude;

    //現在位置及び目的地
    private LatLng present;
    private LatLng destination;

    private String destination_name;
    //所要時間を格納
    public static String time;
    //目的地までの距離を格納
    public static String distance;

    public static MapTypeFragment mapTypeFragment;
    public static DetailFragment detailFragment;
    //map_typeを配置するFrameLayout
    public static FrameLayout fragment_container;
    //detail_fragmentを配置するFrameLayout
    public static FrameLayout detail_fragment_container;
    //map_typeへのタッチリスナー登録に使用
    public static View map_type_view;
    //detail_fragmentへのタッチリスナー登録に使用
    public static View detail_fragment_view;
    //map_typeのアニメーション
    public static MapTypeAnimation mapTypeAnimation;

    //経路の詳細を入れるリスト
    public static ArrayList<String> routeList = new ArrayList<>();

    //画面の大きさを格納
    public static int maps_view_width;
    public static int maps_view_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }

        setLocation();
        setDestinationName();
        makeFragment();


        Log.d("MapsActivity2", "onCreate");
        map_type_view = findViewById(R.id.fragment_container);
        fragment_container = findViewById(R.id.fragment_container);
        map_type_view.setOnTouchListener((View.OnTouchListener) mapTypeFragment);
        detail_fragment_view = findViewById(R.id.detail_fragment_container);
        detail_fragment_container = findViewById(R.id.detail_fragment_container);;
        detail_fragment_view.setOnTouchListener((View.OnTouchListener) detailFragment);



        Button back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final Button map_type_button = (Button) findViewById(R.id.mapType);
        map_type_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapTypeAnimation = new MapTypeAnimation(map_type_view, -maps_view_width, (maps_view_width/3)*2, 1000);
                mapTypeAnimation.setAnimation();
            }
        });

        mapFragment.getMapAsync(this);
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
    }

    //目的地をセット
    private void setLocation() {
        //present_location_latitude = 36.578268;
        //present_location_longitude = 136.662819;

        destination_latitude = 36.5310338;
        destination_longitude = 136.6284361;

        destination = new LatLng(destination_latitude, destination_longitude);
    }

    //目的地の名前をセット
    private void setDestinationName() {
        destination_name = "目的地";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        CameraUpdate cUpdata = CameraUpdateFactory.newLatLngZoom(destination, 16);
        mMap.moveCamera(cUpdata);
    }

    private void showRoute(final GoogleMap map, LatLng presentLatLng, LatLng destinationLatLng) {

        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;

            DateTime now = new DateTime();

            //徒歩経路リクエスト
            final DirectionsResult result_walk = DirectionsApi.newRequest(getGeoContext(bundle))
                    .mode(TravelMode.WALKING)
                    .origin(presentLatLng.latitude + "," + presentLatLng.longitude)
                    .destination(destinationLatLng.latitude + "," + destinationLatLng.longitude)
                    .departureTime(now)
                    .language("ja")
                    .await();
            //自動車経路リクエスト
            final DirectionsResult result_car = DirectionsApi.newRequest(getGeoContext(bundle))
                    .mode(TravelMode.DRIVING)
                    //.mode(TravelMode.WALKING)
                    .origin(presentLatLng.latitude + "," + presentLatLng.longitude)
                    .destination(destinationLatLng.latitude + "," + destinationLatLng.longitude)
                    .departureTime(now)
                    .language("ja")
                    .await();

            Log.i("PickerTest", String.format( "place '%s'",
                    present));

            //デフォルトで徒歩経路表示
            mMap.clear();
            addMarkers(result_walk, map);
            addPolyline(result_walk, map);
            detailFragment.setTime_and_Distance(time + ", " + distance);
            setRouteList(result_walk);

            //徒歩経路表示ボタン
            Button walk_button = (Button) findViewById(R.id.walk_button);
            walk_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMap.clear();
                    addMarkers(result_walk, map);
                    addPolyline(result_walk, map);
                    detailFragment.setTime_and_Distance(time + ", " + distance);
                    setRouteList(result_walk);
                }
            });

            //自動車経路表示ボタン
            Button car_button = (Button) findViewById(R.id.car_button);
            car_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMap.clear();
                    addMarkers(result_car, map);
                    addPolyline(result_car, map);
                    detailFragment.setTime_and_Distance(time + ", " + distance);
                    setRouteList(result_car);
                }
            });


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
            Log.d("MapsActivity2", "Null" + e);
            //位置情報が得られなかった場合, 目的地にマーカー設置のみ行う
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(destination_latitude,
                            destination_longitude))
                    .title(destination_name));
        }
    }

    private GeoApiContext getGeoContext(Bundle bundle) {
        GeoApiContext geoApiContext = new GeoApiContext();
        geoApiContext.setQueryRateLimit(3).setApiKey(bundle.getString("com.google.android.geo.API_KEY"))
                .setConnectTimeout(1, TimeUnit.SECONDS).setReadTimeout(1, TimeUnit.SECONDS).setWriteTimeout(1, TimeUnit.SECONDS);
        return geoApiContext;
    }

    //マーカーの設置
    private void addMarkers(DirectionsResult results, GoogleMap mMap) {
        try {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(results.routes[0].legs[0]
                            .startLocation.lat, results.routes[0]
                            .legs[0].startLocation.lng)));
            //.title(results.routes[0].legs[0].startAddress));
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(results.routes[0]
                            .legs[0].endLocation.lat, results.routes[0]
                            .legs[0].endLocation.lng))
                    //.title(results.routes[0].legs[0].startAddress)
                    .title(destination_name)
                    .snippet(getTime(results) + getDistance(results)));
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    //所要時間の取得
    private String getTime(DirectionsResult results) {
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
    private String getDistance(DirectionsResult results) {
        distance = " 距離 :" + results.routes[0].legs[0].distance;
        return " 距離 :" + results.routes[0].legs[0].distance;
    }

    private void setRouteList(DirectionsResult results){
        for(int i = 0; i < results.routes[0].legs[0].steps.length; i++) {
            //各ステップの距離を格納
            String step_distance = results.routes[0].legs[0].steps[i].distance.toString();
            //経路の詳細を格納
            String route_detail = results.routes[0].legs[0].steps[i].htmlInstructions
                    .replaceAll("<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "")
                    .toString();

            routeList.add(step_distance + "先 " + route_detail);
        }
        detailFragment.makeListView();
    }

    //地図上に経路の表示
    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        try {
            List<LatLng> Path = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
            mMap.addPolyline(new PolylineOptions().addAll(Path));
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "ルートがありません。", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // 位置情報許可の確認
    public void checkPermission() {
        // 既に許可している場合
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
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
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    REQUEST_PERMISSION);
        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //locationActivity();
            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        com.google.android.gms.common.api.PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(googleApiClient, null);
        result.setResultCallback( new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult( PlaceLikelihoodBuffer likelyPlaces ) {
                PlaceLikelihood i = null;
                boolean flag = true;
                for ( PlaceLikelihood placeLikelihood : likelyPlaces ) {
                    Log.i("PickerTest", String.format( "Place '%s' has likelihood: %g place '%s'",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood(),
                            placeLikelihood.getPlace().getLatLng()));

                    if(flag) {
                        i = placeLikelihood;
                        present = placeLikelihood.getPlace().getLatLng();
                        flag = false;
                    }
                    if(placeLikelihood.getLikelihood() > i.getLikelihood()){
                        //present = placeLikelihood.getPlace().getLatLng();
                        i = placeLikelihood;
                        present = i.getPlace().getLatLng();
                    }
                    /*
                    Log.i("PickerTest", String.format( "place '%s'",
                            present));
                    */
                }
                likelyPlaces.release();
                //map, 現在地, 目的地を引数に指定
                showRoute(mMap, present, destination);
            }
        });
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

        //map_typeの初期位置の変更
        RelativeLayout.LayoutParams f_lp = (RelativeLayout.LayoutParams) MapsActivity2.fragment_container.getLayoutParams();
        ViewGroup.MarginLayoutParams f_mlp = f_lp;
        f_mlp.setMargins(-maps_view_width, f_mlp.topMargin, maps_view_width, f_mlp.bottomMargin);
        MapsActivity2.fragment_container.setLayoutParams(f_mlp);

        //detail_fragmentの初期位置変更

        RelativeLayout.LayoutParams f_lp2 = (RelativeLayout.LayoutParams) MapsActivity2.detail_fragment_container.getLayoutParams();
        ViewGroup.MarginLayoutParams f_mlp2 = f_lp2;
        f_mlp2.setMargins(f_mlp2.leftMargin, maps_view_height - detailFragment.time_and_distance.getHeight(),
                f_mlp2.rightMargin, -maps_view_height + detailFragment.time_and_distance.getHeight());
        MapsActivity2.detail_fragment_container.setLayoutParams(f_mlp2);

        //map_type_view.layout(100,100,map_type_view.getRight()+100,map_type_view.getBottom()+100);
        Log.d("MapsActivity2", "width=" + layout_width +
                ", height=" + layout_height);
    }

}
