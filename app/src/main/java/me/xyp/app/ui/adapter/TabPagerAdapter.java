package me.xyp.app.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;


import java.util.List;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private final List<String> titleList;
    private final List<Fragment> fragmentsList;

    public TabPagerAdapter(final FragmentManager fm, List<Fragment> fragmentsList, List<String> titleList) {
        super(fm);
        this.fragmentsList = fragmentsList;
        this.titleList = titleList;
    }

    @Override
    public Fragment getItem(int position) {
        return (fragmentsList == null || fragmentsList.size() == 0) ? null : fragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentsList == null ? 0 : fragmentsList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (titleList.size() > position) ? titleList.get(position) : "";
    }
}
