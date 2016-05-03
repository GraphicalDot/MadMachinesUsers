package com.sports.unity.common.viewhelper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by amandeep on 26/4/16.
 */
public class GenericFragmentViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<BasicVolleyRequestResponseViewHelper> listOfVolleyRequestResponseHandler = null;

    public GenericFragmentViewPagerAdapter(FragmentManager fm, ArrayList<BasicVolleyRequestResponseViewHelper> listOfVolleyRequestResponseHandler) {
        super(fm);

        this.listOfVolleyRequestResponseHandler = listOfVolleyRequestResponseHandler;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        BasicVolleyRequestResponseViewHelper basicVolleyRequestResponseViewHelper = listOfVolleyRequestResponseHandler.get(position);
        return basicVolleyRequestResponseViewHelper.getFragmentTitle();
    }

    @Override
    public Fragment getItem(int position) {
        BasicVolleyRequestResponseViewHelper basicVolleyRequestResponseViewHelper = listOfVolleyRequestResponseHandler.get(position);
        GenericVolleyRequestResponseFragment fragment = basicVolleyRequestResponseViewHelper.getFragment();
        fragment.setBasicVolleyRequestResponseViewHelper(basicVolleyRequestResponseViewHelper);
        return fragment;
    }

    @Override
    public int getCount() {
        return listOfVolleyRequestResponseHandler.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        Fragment fragment = getItem(position);
        if( fragment instanceof GenericVolleyRequestResponseFragment){
            GenericVolleyRequestResponseFragment genericVolleyRequestResponseFragment = (GenericVolleyRequestResponseFragment)fragment;
//            genericVolleyRequestResponseFragment.onComponentResume();
        } else {
            //nothing
        }
    }
}
