package com.thinkmobiles.sudo.fragments.numbers;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.thinkmobiles.sudo.R;
import com.thinkmobiles.sudo.fragments.NumbersFragment;
import com.thinkmobiles.sudo.models.counties.Numbers;

/**
 * Created by njakawaii on 29.04.2015.
 */
public abstract class BaseNumbersFragment extends Fragment {
    protected Activity mActivity;
    protected FragmentManager mFragmentManager;
    private Numbers mCountryModel;

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        mActivity           = _activity;
        mFragmentManager    =  getChildFragmentManager();
    }

    protected void openCountryFragment() {
        Fragment newFragment = new NumbersFragment();
        mFragmentManager.beginTransaction().add(R.id.flContainer_FMN, newFragment).commit();
    }

    protected void openNumbersFragment(){
        changeFragment(new NumberListFragment());
    }

    private void changeFragment(final Fragment _fragment){
        mFragmentManager.beginTransaction().replace(R.id.flContainer_FMN, _fragment).addToBackStack(null).commit();
    }

    protected void goBack(){
        if (mFragmentManager.getBackStackEntryCount() > 0){
            mFragmentManager.popBackStack();
        }
    }

    protected Numbers getmCountryModel() {
        return mCountryModel;
    }

    protected void setmCountryModel(Numbers mCountryModel) {
        this.mCountryModel = mCountryModel;
    }


}
