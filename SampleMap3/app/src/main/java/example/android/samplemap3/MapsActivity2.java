package example.android.samplemap3;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private GoogleMap mMap;
    private final int REQUEST_PERMISSION = 10;

    //private LatLng present_place;

    private double present_location_latitude;
    private double present_location_longitude;
    private double destination_latitude;
    private double destination_longitude;

    private LatLng present;
    private LatLng destination;

    private String destination_name;


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

        Button back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mapFragment.getMapAsync(this);
    }

    //現在位置及び目的地をセット
    private void setLocation() {
        present_location_latitude = 36.562173;
        present_location_longitude = 136.662819;

        destination_latitude = 36.5310338;
        destination_longitude = 136.6284361;

        //present = new LatLng(present_location_latitude, present_location_longitude);
        try {
            //present = present_place.getLatLng();
        }catch (NullPointerException e){

        }
        destination = new LatLng(destination_latitude, destination_longitude);
    }

    //目的地の名前をセット
    private void setDestinationName() {
        destination_name = "目的地";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final LatLng center = new LatLng(35.690579, 139.778770);
        final LatLng tokyoTower = new LatLng(35.6584279, 139.7455992);
        final LatLng skyTree = new LatLng(35.7098861, 139.8107656);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12.5f));

        //LatLng KIT = new LatLng(36.5310338 , 136.6284361);

        //mMap.addMarker(new MarkerOptions().position(KIT).title("KIT"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(KIT));

        CameraUpdate cUpdata = CameraUpdateFactory.newLatLngZoom(destination, 16);
        mMap.moveCamera(cUpdata);


        //showRoute(mMap, present, destination);
    }

    private void showRoute(final GoogleMap map, LatLng presentLatLng, LatLng destinationLatLng) {

        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;

            DateTime now = new DateTime();

            //Log.d("Debug", "1");

            //徒歩経路リクエスト
            final DirectionsResult result_walk = DirectionsApi.newRequest(getGeoContext(bundle))
                    .mode(TravelMode.WALKING)
                    //.mode(TravelMode.WALKING)
                    .origin(presentLatLng.latitude + "," + presentLatLng.longitude)
                    .destination(destinationLatLng.latitude + "," + destinationLatLng.longitude)
                    .departureTime(now)
                    .await();
            //自転車経路リクエスト
            final DirectionsResult result_bike = DirectionsApi.newRequest(getGeoContext(bundle))
                    .mode(TravelMode.BICYCLING)
                    //.mode(TravelMode.WALKING)
                    .origin(presentLatLng.latitude + "," + presentLatLng.longitude)
                    .destination(destinationLatLng.latitude + "," + destinationLatLng.longitude)
                    .departureTime(now)
                    .await();
            //自動車経路リクエスト
            final DirectionsResult result_car = DirectionsApi.newRequest(getGeoContext(bundle))
                    .mode(TravelMode.DRIVING)
                    //.mode(TravelMode.WALKING)
                    .origin(presentLatLng.latitude + "," + presentLatLng.longitude)
                    .destination(destinationLatLng.latitude + "," + destinationLatLng.longitude)
                    .departureTime(now)
                    .await();

            //Log.d("Debug", "2");

            //デフォルトで徒歩経路表示
            mMap.clear();
            addMarkers(result_walk, map);
            addPolyline(result_walk, map);

            //徒歩経路表示ボタン
            Button walk_button = (Button) findViewById(R.id.walk_button);
            walk_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMap.clear();
                    addMarkers(result_walk, map);
                    addPolyline(result_walk, map);
                }
            });

            //自転車経路表示ボタン
            Button bike_button = (Button) findViewById(R.id.bike_button);
            bike_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMap.clear();
                    addMarkers(result_bike, map);
                    addPolyline(result_bike, map);

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
                }
            });


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private GeoApiContext getGeoContext(Bundle bundle) {
        GeoApiContext geoApiContext = new GeoApiContext();
        geoApiContext.setQueryRateLimit(3).setApiKey(bundle.getString("com.google.android.geo.API_KEY"))
                .setConnectTimeout(1, TimeUnit.SECONDS).setReadTimeout(1, TimeUnit.SECONDS).setWriteTimeout(1, TimeUnit.SECONDS);
        return geoApiContext;
    }

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

    private String getTime(DirectionsResult results) {
        int hour = (int) (results.routes[0].legs[0].duration.inSeconds / 3600);
        int minutes = (int) ((results.routes[0].legs[0].duration.inSeconds % (60 * 60)) / 60);

        if (hour != 0) {
            return "所要時間 :" + hour + "時間" + minutes + "分";
        } else {
            return "所要時間 :" + minutes + "分";
        }
    }

    private String getDistance(DirectionsResult results) {
        return " 距離 :" + results.routes[0].legs[0].distance;
    }

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
                        Log.i("aaa", String.format( "place '%s'",
                                present));
                    }
                }
                likelyPlaces.release();
                showRoute(mMap, present, destination);
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
