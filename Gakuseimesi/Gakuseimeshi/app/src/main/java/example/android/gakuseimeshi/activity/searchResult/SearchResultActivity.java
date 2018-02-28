package example.android.gakuseimeshi.activity.searchResult;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import example.android.gakuseimeshi.R;
import example.android.gakuseimeshi.activity.storeInfomation.StoreInfomationActivity;
import example.android.gakuseimeshi.database.basicData.MapSearch;
import example.android.gakuseimeshi.database.dao.ShopMapSearchDao;

/**
 * Created by Yusuke on 2018/02/09/.
 * 検索結果画面
 */

public class SearchResultActivity extends ListActivity implements View.OnFocusChangeListener, SearchView.OnQueryTextListener {

    private ShopMapSearchDao shopMapSearchDao;
    private SearchView mSearchView;           // 検索窓
    private ArrayList<MapSearch> resultArrayList = new ArrayList<MapSearch>();
    private LruCache<String, Bitmap> mMemoryCache;

    // 検索条件変数
    private String mealName;
    private String category;
    private int minPrice;
    private int maxPrice;
    private String area;
    private int isOpen;
    private int existsStudentDiscount;
    private int buttonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_sheet_table);
        shopMapSearchDao = new ShopMapSearchDao(this);
        findViews();
        // 検索窓を開いた状態にする(設定していない場合はアイコンをクリックしないと入力箇所が開かない)
        mSearchView.setIconified(false);
        // 検索窓のイベント処理
        mSearchView.setOnQueryTextListener(this);

        Intent intent = this.getIntent();
        mealName = intent.getStringExtra("mealName");
        category = intent.getStringExtra("Category");
        minPrice = intent.getIntExtra("MinPrice", 0);
        maxPrice = intent.getIntExtra("MaxPrice", 99999);
        area = intent.getStringExtra("Area");
        isOpen = intent.getIntExtra("OpenTime", 0);
        existsStudentDiscount = intent.getIntExtra("StudentDiscount", 0);
        buttonId = intent.getIntExtra("buttonId",R.id.search);

        final int memClass = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = 1024 * 1024 * memClass / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };

        mSearchView.setQuery(mealName, false);
        onQueryTextSubmit(mealName);
    }

    /**
     * 各部品の結びつけ処理
     * findViews()
     */
    private void findViews() {
        mSearchView = (SearchView)findViewById(R.id.searchView);// 検索窓
    }

    /**
     * SearchViewの各イベント処理
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        /* Do Nothing */
    }

    // 検索を始める時
    @Override
    public boolean onQueryTextSubmit(String query) {
        // ListViewの項目を全て消す
        if (resultArrayList.size() != 0) resultArrayList.clear();
        if (buttonId == R.id.search) {
            resultArrayList = nameSearch(query);
        } else if (buttonId == R.id.search_detail_button) {
            resultArrayList = detailedSearch(category, minPrice, maxPrice, area, isOpen, existsStudentDiscount);
        }
        SearchResultAdapter adapter = new SearchResultAdapter(this, resultArrayList, R.layout.custom_layout, mMemoryCache);
        this.setListAdapter(adapter);

        mSearchView.clearFocus();   // 検索窓のフォーカスを外す(=キーボードを非表示)

        return false;
    }

    // 検索窓のテキストが変わった時
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l,v,position,id);
        Intent intent = new Intent(this, StoreInfomationActivity.class);
        intent.putExtra("StoreId", resultArrayList.get(position).getId());
        Log.d("getId", String.valueOf(id));

        startActivity(intent);
    }

    // 詳細検索実行メソッド
    public ArrayList<MapSearch> detailedSearch(String category, int minPrice, int maxPrice, String area, int isOpen, int existsStudentDiscount){
        shopMapSearchDao.readDB();
        ArrayList<MapSearch> detailedSearchResult;
        detailedSearchResult = (ArrayList<MapSearch>)shopMapSearchDao.detailedSearch(category,minPrice,maxPrice,area,isOpen,existsStudentDiscount);
        shopMapSearchDao.closeDB();

        return detailedSearchResult;
    }

    public ArrayList<MapSearch> nameSearch(String name) {
        shopMapSearchDao.readDB();
        ArrayList<MapSearch> nameSearchResult;
        nameSearchResult = (ArrayList<MapSearch>)shopMapSearchDao.nameSearch(name);
        shopMapSearchDao.closeDB();

        return nameSearchResult;
    }
}