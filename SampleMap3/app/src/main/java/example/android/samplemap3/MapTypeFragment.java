package example.android.samplemap3;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by riku on 2018/02/01.
 */

public class MapTypeFragment extends Fragment implements View.OnTouchListener{

    //タッチの開始のX座標を格納
    private int preX;
    //フラグメントのX座標の上限値を格納
    private int limitX;
    private MapTypeAnimation mapTypeAnimation2;
    private MapTypeAnimation mapTypeAnimation3;


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
                MapsActivity2.mapTypeAnimation = new MapTypeAnimation(MapsActivity2.map_type_view,  (MapsActivity2.maps_view_width/3)*2,-MapsActivity2.maps_view_width, 1000);
                MapsActivity2.mapTypeAnimation.setAnimation();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.parseColor("#eeee0000"));
        RelativeLayout.LayoutParams f_lp = (RelativeLayout.LayoutParams) MapsActivity2.fragment_container.getLayoutParams();

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //Log.d("DetailFragment", "onTouch");
        int newX = (int)motionEvent.getRawX();
        limitX = MapsActivity2.maps_view_width + (MapsActivity2.maps_view_width/3)*2;

        int dx = MapsActivity2.map_type_view.getRight() + (newX - preX);
        int right_margin = MapsActivity2.maps_view_width - dx;
        int left_margin = dx - MapsActivity2.maps_view_width;
        int dy = 0;
        int imgW = MapsActivity2.map_type_view.getWidth();
        int imgH = dy + MapsActivity2.map_type_view.getHeight();
        int s = MapsActivity2.maps_view_width - dx;
        Log.d("MapTypeFragment", "dx:" + s + ", limitX:" + limitX);
        Log.d("MapTypeFragment", "right_margin:" + right_margin);

        RelativeLayout.LayoutParams f_lp = (RelativeLayout.LayoutParams) MapsActivity2.fragment_container.getLayoutParams();
        ViewGroup.MarginLayoutParams f_mlp = f_lp;

        switch (motionEvent.getAction()) {
            // ドラッグ時の処理
            case MotionEvent.ACTION_MOVE:
                //MapsActivity2.map_type_view.layout(dx, dy, imgW, imgH);
                //フラグメントの位置上限設定
                //f_mlp.setMargins(left_margin, f_mlp.topMargin, right_margin, f_mlp.bottomMargin);
                Log.d("MapTypeFragment", "getLeft:" + MapsActivity2.map_type_view.getLeft() + ", " + MapsActivity2.maps_view_width);

                if(limitX >= right_margin && right_margin >= MapsActivity2.maps_view_width) {
                    f_mlp.setMargins(left_margin, f_mlp.topMargin, right_margin, f_mlp.bottomMargin);
                }else if(right_margin < MapsActivity2.maps_view_width){
                    f_mlp.setMargins(left_margin, f_mlp.topMargin, MapsActivity2.maps_view_width, f_mlp.bottomMargin);
                }else if(right_margin > limitX){
                    f_mlp.setMargins(left_margin, f_mlp.topMargin, limitX, f_mlp.bottomMargin);
                }

                //パラメータ設定
                MapsActivity2.fragment_container.setLayoutParams(f_mlp);
                break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                int x = MapsActivity2.map_type_view.getLeft() + MapsActivity2.maps_view_width + (MapsActivity2.maps_view_width/3)*2;
                f_mlp.setMargins(-MapsActivity2.maps_view_width, f_mlp.topMargin, MapsActivity2.maps_view_width, f_mlp.bottomMargin);
                MapsActivity2.fragment_container.setLayoutParams(f_mlp);
                if(right_margin <= MapsActivity2.maps_view_width + MapsActivity2.maps_view_width/3 &&
                        right_margin > MapsActivity2.maps_view_width){
                    mapTypeAnimation2 = new MapTypeAnimation(MapsActivity2.map_type_view, x, (MapsActivity2.maps_view_width/3)*2, 500);
                    mapTypeAnimation2.setAnimation();
                }else if(right_margin > MapsActivity2.maps_view_width + MapsActivity2.maps_view_width/3 &&
                        left_margin < limitX){
                    mapTypeAnimation3 = new MapTypeAnimation(MapsActivity2.map_type_view, x, -MapsActivity2.maps_view_width, 500);
                    mapTypeAnimation3.setAnimation();
                }
                //Log.d("MapTypeFragment", "width/3:" + MapsActivity2.maps_view_width/3);

                break;
            default:
                break;
        }
        // タッチした位置の更新
        preX = newX;
        return true;
    }
}
