package nhan1303.watsong.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import nhan1303.watsong.fragment.FragmentHistory;
import nhan1303.watsong.fragment.FragmentMain;

/**
 * Created by NHAN on 13/11/2017.
 */

public class AdapterViewPager extends FragmentPagerAdapter {
    Context context;
    List<Fragment> fragmentList = new ArrayList<>();

    public AdapterViewPager(Context context, FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.context = context;
        this.fragmentList = fragmentList;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

}