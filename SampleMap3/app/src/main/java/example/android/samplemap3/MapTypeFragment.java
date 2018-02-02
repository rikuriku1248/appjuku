package example.android.samplemap3;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by riku on 2018/02/01.
 */

public class MapTypeFragment extends Fragment {

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.map_type, null);

        Button mapType_normal_button = (Button)view.findViewById(R.id.mapType_normal);
        mapType_normal_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                MapsActivity2.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        Button mapType_satellite_button = (Button)view.findViewById(R.id.mapType_satellite);
        mapType_satellite_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                MapsActivity2.mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        Button mapType_terrain_button = (Button)view.findViewById(R.id.mapType_terrain);
        mapType_terrain_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                MapsActivity2.mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });

        Button back_button = (Button)view.findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.hide(MapsActivity2.mapTypeFragment);
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.parseColor("#eeeeeeee"));

    }
}
