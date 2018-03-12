package example.android.gakuseimeshi.activity.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;

import example.android.gakuseimeshi.R;
import example.android.gakuseimeshi.activity.main.expandLayout.FavoritePageFragment;
import example.android.gakuseimeshi.activity.main.expandLayout.FragmentPageOne;
import example.android.gakuseimeshi.activity.main.expandLayout.FavoritePageFragment;

public class MainActivity extends AppCompatActivity {
    private TabPagerAdapter pagerAdapter;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        init();
        toolbar.inflateMenu(R.menu.main);
        pagerAdapter = new TabPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        pagerSlidingTabStrip.setViewPager(viewPager);
    }

    //登録
    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.pager);
    }

    //初期化
    private void init() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class TabPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

        private final int[] ICONS = {R.drawable.ic_tab_home, R.drawable.ic_tab_fav};
        Context mContext;
        private Fragment f = null;

        public TabPagerAdapter(Context ctx, FragmentManager fm) {
            super(fm);
            mContext = ctx;
        }

        @Override
        public int getCount() {
            return ICONS.length;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    f = FragmentPageOne.newInstance();
                    break;
                case 1:
                    f = FavoritePageFragment.newInstance();
                    break;
            }
            return f;
        }

        @Override
        public View getCustomTabView(ViewGroup parent, int position) {
            LinearLayout customLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.custom_tab, parent, false);
            ImageView imageView = (ImageView) customLayout.findViewById(R.id.image);
            imageView.setImageResource(ICONS[position]);
            return customLayout;
        }

        @Override
        public void tabSelected(View view) {

        }

        @Override
        public void tabUnselected(View view) {

        }
    }
}