package example.android.gakuseimeshi.activity.storeInfomation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import example.android.gakuseimeshi.activity.map.MapsActivity;
import example.android.gakuseimeshi.activity.storeInfomation.Fragment.InformationFragment;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"店舗情報", "地図"};
    private Context mContext;
    private int id;

    public SampleFragmentPagerAdapter(FragmentManager fm, int id, Context mContext) {
        super(fm);
        this.mContext = mContext;
        this.id = id;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        switch(position) {
            case 0:
                InformationFragment informationFragment = new InformationFragment();
                informationFragment.setArguments(bundle);
                return informationFragment;
            case 1:
                MapsActivity mapsActivity = new MapsActivity();
                mapsActivity.setArguments(bundle);
                return mapsActivity;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
