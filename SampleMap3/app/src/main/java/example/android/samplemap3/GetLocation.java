package example.android.samplemap3;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by riku on 2018/02/10.
 */

public class GetLocation implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient client;
    private LocationRequest request;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private GoogleMap mMap;
    private Marker mMarker;
    private Context context;

    public GetLocation(GoogleApiClient client, FusedLocationProviderApi fusedLocationProviderApi, Context context) {
        this.client = client;
        this.fusedLocationProviderApi = fusedLocationProviderApi;
        this.context = context;
        Log.d("GetLocation", "start");
    }

    public void getLocationStart() {
        //位置情報のリクエスト情報を取得
        request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(15);

        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("GetLocation", "Lat:"+location.getLatitude()
                +"Lng:"+location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // ACCESS_FINE_LOCATIONへのパーミッションを確認
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // 位置情報の監視を開始
        fusedLocationProviderApi.requestLocationUpdates(client, request, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("GetLocation", "onConnectionFailed");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
