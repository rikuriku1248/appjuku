package example.android.gakuseimeshi.activity.main.expandLayout;

import android.app.ActivityManager;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import example.android.gakuseimeshi.R;
import example.android.gakuseimeshi.activity.searchResult.SearchResultAdapter;
import example.android.gakuseimeshi.activity.storeInfomation.StoreInfomationActivity;
import example.android.gakuseimeshi.database.basicData.MapSearch;
import example.android.gakuseimeshi.database.dao.ShopMapSearchDao;

/**
 * Created by Tomu on 2018/02/28.
 */

public class FavoritePageFragment extends Fragment{
    private View view;
    private ShopMapSearchDao shopMapSearchDao;
    private ArrayList<MapSearch> resultArrayList = new ArrayList<MapSearch>();
    private LruCache<String, Bitmap> mMemoryCache;

    public static Fragment newInstance(){
        return new FavoritePageFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.favorite_list, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        shopMapSearchDao = new ShopMapSearchDao(getActivity());

        final int memClass = ((ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = 1024 * 1024 * memClass / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };

        ListView listView = (ListView) view.findViewById(R.id.favorite_list);
        resultArrayList = favoriteSearch();
        SearchResultAdapter adapter = new SearchResultAdapter(getActivity(), resultArrayList, R.layout.custom_layout, mMemoryCache);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView listView = (ListView) parent;
                Intent intent = new Intent(getActivity(), StoreInfomationActivity.class);
                intent.putExtra("StoreId", resultArrayList.get(position).getId());
                Log.d("getId", String.valueOf(id));

                startActivity(intent);
            }
        });
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        getActivity().setContentView(R.layout.favorite_list);
        shopMapSearchDao = new ShopMapSearchDao(getActivity());

        final int memClass = ((ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = 1024 * 1024 * memClass / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };

        resultArrayList = favoriteSearch();
        SearchResultAdapter adapter = new SearchResultAdapter(getActivity(), resultArrayList, R.layout.custom_layout, mMemoryCache);
        this.setListAdapter(adapter);
    }*/



    public ArrayList<MapSearch> favoriteSearch() {
        shopMapSearchDao.readDB();
        ArrayList<MapSearch> favoriteSearchResult;
        favoriteSearchResult = (ArrayList<MapSearch>)shopMapSearchDao.searchFavorite(1);
        shopMapSearchDao.closeDB();

        return favoriteSearchResult;
    }
}
