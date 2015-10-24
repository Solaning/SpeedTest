package com.kinth.mmspeed.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kinth.mmspeed.fragment.FlowAnalyzeFragment;
import com.kinth.mmspeed.fragment.FlowCategoryFragment;
import com.kinth.mmspeed.fragment.FlowQueryFragment;
import com.kinth.mmspeed.fragment.PackageDetailFragment;
import com.kinth.mmspeed.fragment.SmartKanBanFragment;

public class FragmentAdapter extends FragmentPagerAdapter{
	public final static int TAB_COUNT = 4;
	public FragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int id) {
		switch (id) {
		case FlowQueryFragment.TAB_SMART_BOARD:
			SmartKanBanFragment homeFragment = new SmartKanBanFragment();
			return homeFragment;
		case FlowQueryFragment.TAB_FLOW_CATEGORY:
			FlowCategoryFragment flowCategoryFragment = new FlowCategoryFragment();
			return flowCategoryFragment;
		case FlowQueryFragment.TAB_PACKAGE_DETAIL:
			PackageDetailFragment packageDetailFragment = new PackageDetailFragment();
			return packageDetailFragment;
		case FlowQueryFragment.TAB_FLOW_ANALYZE:
			FlowAnalyzeFragment flowAnalyze = new FlowAnalyzeFragment();
			return flowAnalyze;
		}
		return null;
	}

	@Override
	public int getCount() {
		return TAB_COUNT;
	}
}
