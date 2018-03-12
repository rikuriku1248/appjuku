package example.android.gakuseimeshi.activity.main.expandLayout;

/**
 * Created by MAKOTO_KOBAYASHI on 2018/02/23.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import example.android.gakuseimeshi.R;
import example.android.gakuseimeshi.activity.main.AreaListActivity;
import example.android.gakuseimeshi.activity.main.GenreListActivity;
import example.android.gakuseimeshi.activity.searchResult.SearchResultActivity;
import example.android.gakuseimeshi.activity.storeInfomation.StoreInfomationActivity;
import example.android.gakuseimeshi.database.basicData.MapSearch;
import example.android.gakuseimeshi.database.dao.ShopMapSearchDao;
import example.android.gakuseimeshi.gurunavi.UploadAsyncTask;

import static android.app.Activity.RESULT_OK;

public class FragmentPageOne extends Fragment {

    View view;
    private ImageView searchBackground;
    static final int RESULT_CODE = 1000;
    private Button detailedButton;
    private CheckBox openCheck;
    private CheckBox studentDiscountCheck;
    private LinearLayout detailedContentsAreaLinear;
    private BootstrapEditText searchMeal;
    private EditText maxPrice;
    private EditText minPrice;
    private TextView categoryText;
    private TextView areaText;
    private Intent area;
    private Intent genre;
    private String areaContent = "";
    private String genreContent = "";
    private final static int DURATION = 200;
    private ShopMapSearchDao shopMapSearchDao;
    private SharedPreferences sharedPref;
    private UploadAsyncTask uploadAsyncTask;



    public static Fragment newInstance(){
        return new FragmentPageOne();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        findViews();
        UploadDatabase();
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.food_image2);
        Bitmap dst = ImageUtils.resizeBitmapToDisplaySize(getActivity(), src);
        searchBackground.setImageBitmap(dst);
        searchMeal.setPadding(10, 5, 5, 5 );


        getActivity().findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                searchButtonhClickEvent(v);
            }
        });
        getActivity().findViewById(R.id.area_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                areaButtonClickEvent(v);
            }
        });
        getActivity().findViewById(R.id.genre_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                genreButtonClickEvent(v);
            }
        });
        getActivity().findViewById(R.id.search_detail_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                searchDetailButtonClickEvent(v);
            }
        });
    }
    private void searchButtonhClickEvent(View v){
        searchMeal.selectAll();
        String mealName = searchMeal.getText().toString();
        if (!TextUtils.isEmpty(mealName)){
            Intent intent = new Intent(getActivity(), SearchResultActivity.class);
            intent.putExtra("mealName", mealName);
            intent.putExtra("buttonId", R.id.search);
            this.startActivity(intent);
        }
    }
    private void areaButtonClickEvent(View v){
        area = new Intent(getActivity(), AreaListActivity.class);
        startActivityForResult(area, RESULT_CODE);
    }
    private void genreButtonClickEvent(View v) {
        genre = new Intent(getActivity(), GenreListActivity.class);
        startActivityForResult(genre, RESULT_CODE);

    }
    private void searchDetailButtonClickEvent(View v){
        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        int min = minPrice.getText().toString().isEmpty() ? 0 : Integer.parseInt(minPrice.getText().toString());
        int max = maxPrice.getText().toString().isEmpty() ? 99999 : Integer.parseInt(maxPrice.getText().toString());
        int isOpen = openCheck.isChecked() ? 1 : 0;
        int existsStudentDiscount = studentDiscountCheck.isChecked() ? 1 : 0;

        intent.putExtra("Category", categoryText.getText().toString().replace("で検索", ""));
        intent.putExtra("MinPrice", min);
        intent.putExtra("MaxPrice", max);
        intent.putExtra("Area", areaText.getText().toString());
        intent.putExtra("OpenTime", isOpen);
        intent.putExtra("StudentDiscount", existsStudentDiscount);
        intent.putExtra("buttonId", R.id.search_detail_button);
        this.startActivity(intent);
    }

    private void findViews(){
        categoryText = getActivity().findViewById(R.id.category_text);
        areaText = getActivity().findViewById(R.id.area_text);
        searchMeal = getActivity().findViewById(R.id.search_meal);
        minPrice = getActivity().findViewById(R.id.min_price);
        maxPrice = getActivity().findViewById(R.id.max_price);
        detailedContentsAreaLinear = getActivity().findViewById(R.id.detailed_contents_area_linear);
        detailedButton = getActivity().findViewById(R.id.detailed_button);
        openCheck = getActivity().findViewById(R.id.open_check);
        studentDiscountCheck = getActivity().findViewById(R.id.student_discout_check);
        searchBackground = getActivity().findViewById(R.id.search_background);
    }

    public void UploadDatabase() {
        shopMapSearchDao = new ShopMapSearchDao(getActivity());

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int before_entry_year = sharedPref.getInt("before_entry_year", 0);
        int before_entry_month = sharedPref.getInt("before_entry_month", 0);
        int before_entry_day = sharedPref.getInt("before_entry_day", 0);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);

//        Log.d("Error", String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day));
//        Log.d("Error", before_entry_year + "/" + before_entry_month + "/" + before_entry_day);
        if ((year > before_entry_year) || (year == before_entry_year && month > before_entry_month)
                || (year == before_entry_year && month == before_entry_month && day > before_entry_day)) {
            uploadAsyncTask = new UploadAsyncTask(getActivity(), getActivity());
            uploadAsyncTask.execute();
            //searchKitFoodArea = new SearchKitFoodArea(this);
            //searchKitFoodArea.execute();
            //uploadAsyncTask.cancel(true);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("before_entry_year", year);
            editor.putInt("before_entry_month", month);
            editor.putInt("before_entry_day", day);
            editor.commit();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK && requestCode == RESULT_CODE && null != intent
                && intent.getBooleanExtra("AREA", false) == true) {
            areaContent = intent.getStringExtra("AREA_CONTENT");
            areaText.setText(areaContent);
        } else if (resultCode == RESULT_OK && requestCode == RESULT_CODE && null != intent
                && intent.getBooleanExtra("GENRE", false) == true) {
            genreContent = intent.getStringExtra("GenreItem");
            categoryText.setText(genreContent);
        }
    }

    //回転ボタン
    public void onWindowFocusChanged(boolean hasFocus) {
        // ExpandするViewの元のサイズを保持
        final int originalHeight = 552;
        final int btnWidth = detailedButton.getWidth();
        final int btnHeight = detailedButton.getLineHeight();
        //展開ボタン押下時
        detailedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detailedContentsAreaLinear.getHeight() > 0) {
                    detailedButton.setBackgroundResource(R.drawable.up);
                    startRotateAnim(-180, btnWidth / 2, btnHeight / 2);
                    // 内容エリアを閉じるアニメーション
                    ResizeAnimation closeAnimation = new ResizeAnimation(detailedContentsAreaLinear, -originalHeight, originalHeight);
                    closeAnimation.setDuration(DURATION);
                    detailedContentsAreaLinear.startAnimation(closeAnimation);
                } else {
                    detailedButton.setBackgroundResource(R.drawable.down);
                    // 展開ボタンのアニメーション(時計周りに180度回転)
                    startRotateAnim(180, btnWidth / 2, btnHeight / 2);
                    // 内容エリアを開くアニメーション
                    ResizeAnimation openAnimation = new ResizeAnimation(detailedContentsAreaLinear, originalHeight, 0);
                    openAnimation.setDuration(DURATION);    // アニメーションにかける時間(ミリ秒)
                    detailedContentsAreaLinear.startAnimation(openAnimation);   // アニメーション開始
                }
            }
        });
    }

    private void startRotateAnim(int toDegrees, int pivotX, int pivotY) {
        // 展開ボタンの180度回転アニメーションを生成
        RotateAnimation rotate = new RotateAnimation(0, toDegrees, pivotX, pivotY);
        rotate.setDuration(DURATION);       // アニメーションにかける時間(ミリ秒)
        rotate.setFillAfter(true);          // アニメーション表示後の状態を保持
        detailedButton.startAnimation(rotate);    // アニメーション開始
    }
}