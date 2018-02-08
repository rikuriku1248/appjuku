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
    private TextView time_and_distance;
    //フラグメントのY座標の上限値を格納
    private int limitY;

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
        //Log.d("DetailFragment", "onTouch");
        int newY = (int)motionEvent.getRawY();
        limitY = MapsActivity2.maps_view_height - (MapsActivity2.maps_view_height / 9);
        //int dx = 0;
        int dy = MapsActivity2.detail_fragment_view.getTop() + (newY - preY);
        //int imgW = MapsActivity2.detail_fragment_view.getWidth();
        //int imgH = dy + MapsActivity2.detail_fragment_view.getHeight();

        Log.d("DetailFragment", "dy:" + dy);
        Log.d("DetailFragment", "limitY:" + limitY);

        switch (motionEvent.getAction()) {
            // ドラッグ時の処理
            case MotionEvent.ACTION_MOVE:
                //レイアウトのパラメータを変更
                RelativeLayout.LayoutParams f_lp = (RelativeLayout.LayoutParams) MapsActivity2.detail_fragment_container.getLayoutParams();
                ViewGroup.MarginLayoutParams f_mlp = f_lp;
                //フラグメントの位置上限設定
                if(dy >= 0 && dy <= limitY) {
                    f_mlp.setMargins(f_mlp.leftMargin, dy, f_mlp.rightMargin, f_mlp.bottomMargin);
                }else if(dy < 0){
                    f_mlp.setMargins(f_mlp.leftMargin, 0, f_mlp.rightMargin, f_mlp.bottomMargin);
                }else if(dy > limitY){
                    f_mlp.setMargins(f_mlp.leftMargin, limitY, f_mlp.rightMargin, f_mlp.bottomMargin);
                }
                //パラメータ設定
                MapsActivity2.detail_fragment_container.setLayoutParams(f_mlp);
                break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
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
        String[] texts = {};
        /*
        for(){

        }
*/
        Log.d("DetailFragment", MapsActivity2.routeList.get(0));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, MapsActivity2.routeList);
        //ListViewのid取得

        route_display.setAdapter(arrayAdapter);
    }
}
