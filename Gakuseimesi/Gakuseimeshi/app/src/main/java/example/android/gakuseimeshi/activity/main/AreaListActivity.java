package example.android.gakuseimeshi.activity.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import example.android.gakuseimeshi.R;
import example.android.gakuseimeshi.activity.main.expandLayout.ImageUtils;

public class AreaListActivity extends AppCompatActivity {
    private int i  = 0;
    private ListView areaListView;
    private Intent intent;
    private static final String[] areaContents = {
            "未選択", "野々市","有松","押野","金沢"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_list);
        LinearLayout cardLinear = (LinearLayout)this.findViewById(R.id.cardLinear);
        cardLinear.removeAllViews();

        for(String name: areaContents) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.test_card, null);
            CardView cardView = (CardView) linearLayout.findViewById(R.id.cardView);
            final TextView areaName = (TextView) linearLayout.findViewById(R.id.areaName);
            ImageView areaImage = (ImageView)findViewById(R.id.area_image);
            areaName.setText(name);
            cardView.setTag(i);
            Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.car_icon2);
            Bitmap dst = ImageUtils.resizeBitmapToDisplaySize(this, src);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(AreaListActivity.this, MainActivity.class);
                    if(v.getTag().equals("0")) intent.putExtra("AREA_CONTENT", "");
                    else intent.putExtra("AREA_CONTENT", (String)areaName.getText().toString());

                    intent.putExtra("AREA", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            cardLinear.addView(linearLayout,i);
            i++;
        }
        init(i);
//        areaListView = (ListView) findViewById(R.id.area_list);
//        ArrayAdapter<String> arrayAdapter =
//                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, areaContents);
//        areaListView.setAdapter(arrayAdapter);
//
//        areaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                ListView areaListView = (ListView) parent;
//                intent = new Intent(AreaListActivity.this, MainActivity.class);
//                if(id == 0) intent.putExtra("AREA_CONTENT", "");
//                else intent.putExtra("AREA_CONTENT", (String)areaListView.getItemAtPosition(position));
//
//
//                intent.putExtra("AREA", true);
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });
    }

    private void init(int num){
        num = 0;
    }
}