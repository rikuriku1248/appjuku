package example.android.samplemap3;

import android.app.Application;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

/**
 * Created by riku on 2018/02/03.
 */

public class DetailFragment extends Fragment implements View.OnTouchListener{

    //private View detail_fragment_view;
    //タッチの開始のY座標を格納
    private int preY;
    //所要時間及び目的地までの距離を表示
    public TextView time_and_distance;
    //フラグメントのY座標の上限値を格納
    private int limitY;

    //detail_fragmentのアニメーション
    private DetailAnimation detailAnimation;
    private DetailAnimation detailAnimation2;
    private DetailAnimation detailAnimation3;
    private DetailAnimation detailAnimation4;

    int i = 0;

    //経路の詳細表示
    private ListView route_display;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.detail_fragment, null);
        time_and_distance = view.findViewById(R.id.time_and_distance);
        route_display = view.findViewById(R.id.route_display);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.parseColor("#eeeeeeee"));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int newY = (int)motionEvent.getRawY();
        limitY = MapsActivity2.maps_view_height - time_and_distance.getHeight();
        //int dx = 0;
        //int imgW = MapsActivity2.detail_fragment_view.getWidth();
        //int imgH = dy + MapsActivity2.detail_fragment_view.getHeight();


        //レイアウトのパラメータを変更
        RelativeLayout.LayoutParams f_lp2 = (RelativeLayout.LayoutParams) MapsActivity2.detail_fragment_container.getLayoutParams();
        ViewGroup.MarginLayoutParams f_mlp2 = f_lp2;
        int dy = MapsActivity2.detail_fragment_view.getTop() + (newY - preY);

        Log.d("DetailFragment", "dy:" + dy);
        //Log.d("DetailFragment", "limitY:" + limitY);

        switch (motionEvent.getAction()) {
            // ドラッグ時の処理
            case MotionEvent.ACTION_MOVE:
                //f_mlp2.setMargins(f_mlp2.leftMargin, dy, f_mlp2.rightMargin, f_mlp2.bottomMargin);

                //フラグメントの位置上限設定
                if(dy - i >= 0 && dy - i <= limitY) {
                    f_mlp2.setMargins(f_mlp2.leftMargin, dy, f_mlp2.rightMargin, f_mlp2.bottomMargin);
                }else if(dy - i < 0){
                    f_mlp2.setMargins(f_mlp2.leftMargin, i, f_mlp2.rightMargin, f_mlp2.bottomMargin);
                }else if(dy - i > limitY){
                    f_mlp2.setMargins(f_mlp2.leftMargin, i + MapsActivity2.maps_view_height - time_and_distance.getHeight(), f_mlp2.rightMargin, f_mlp2.bottomMargin);
                }

                //パラメータ設定
                MapsActivity2.detail_fragment_container.setLayoutParams(f_mlp2);
                break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                int y = MapsActivity2.detail_fragment_view.getTop() - MapsActivity2.maps_view_height + time_and_distance.getHeight();
                f_mlp2.setMargins(f_mlp2.leftMargin, MapsActivity2.maps_view_height - MapsActivity2.detailFragment.time_and_distance.getHeight(),
                    f_mlp2.rightMargin, -MapsActivity2.maps_view_height + MapsActivity2.detailFragment.time_and_distance.getHeight());
                MapsActivity2.detail_fragment_container.setLayoutParams(f_mlp2);
                //dy = MapsActivity2.detail_fragment_view.getTop() + (newY - preY);
                //Log.d("DetailFragment", "dy2:" + dy);
                if(dy - i >= 0 && dy - i <= MapsActivity2.maps_view_height/4){
                    detailAnimation = new DetailAnimation(MapsActivity2.detail_fragment_view,
                            y - i,
                            -MapsActivity2.maps_view_height + time_and_distance.getHeight(), 500);
                    detailAnimation.setAnimation();
                    i = MapsActivity2.maps_view_height - time_and_distance.getHeight();
                    Log.d("DetailFragment", "1");
                }else if (dy - i > MapsActivity2.maps_view_height/4 && dy - i <= MapsActivity2.maps_view_height/2){
                    detailAnimation2 = new DetailAnimation(MapsActivity2.detail_fragment_view,
                            y - i,
                            -MapsActivity2.maps_view_height + time_and_distance.getHeight() + MapsActivity2.maps_view_height/2, 500);
                    detailAnimation2.setAnimation();
                    i = MapsActivity2.maps_view_height/2 - time_and_distance.getHeight();
                    Log.d("DetailFragment", "2");
                }else if(dy - i > MapsActivity2.maps_view_height/2 && dy - i <= (MapsActivity2.maps_view_height/4)*3){
                    detailAnimation3 = new DetailAnimation(MapsActivity2.detail_fragment_view,
                            y - i,
                            -MapsActivity2.maps_view_height + time_and_distance.getHeight() + MapsActivity2.maps_view_height/2, 500);
                    detailAnimation3.setAnimation();
                    i = MapsActivity2.maps_view_height/2 - time_and_distance.getHeight();
                    Log.d("DetailFragment", "3");
                }else if(dy - i > (MapsActivity2.maps_view_height/4)*3 && dy - i <= limitY){
                    detailAnimation4 = new DetailAnimation(MapsActivity2.detail_fragment_view,
                            y - i, 0, 500);
                    detailAnimation4.setAnimation();
                    i = 0;
                    Log.d("DetailFragment", "4");
                }
                int dyi = dy - i;
                Log.d("DetailFragment", "dy - i:" + dyi + " dy:" + dy + " i:" + i);
                break;
            default:
                break;
        }
        // タッチした位置の更新
        preY = newY;
        return true;
    }

    public void setTime_and_Distance(String text){
        time_and_distance.setText(text);
    }

    public void makeListView(){

        Log.d("DetailFragment", MapsActivity2.routeList.get(0));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, MapsActivity2.routeList);
        //ListViewのid取得

        route_display.setAdapter(arrayAdapter);
    }
}
