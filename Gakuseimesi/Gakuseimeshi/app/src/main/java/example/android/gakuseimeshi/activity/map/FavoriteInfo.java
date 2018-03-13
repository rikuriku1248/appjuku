package example.android.gakuseimeshi.activity.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by riku on 2018/03/13.
 */

public class FavoriteInfo {
    int id;
    LatLng latLng;
    public FavoriteInfo(int id, LatLng latLng){
        this.id = id;
        this.latLng = latLng;
    }
}
