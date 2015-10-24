package com.kinth.mmspeed.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.adapter.FragmentAdapter;
import com.kinth.mmspeed.bj.FlowSingleton;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 流量查询结果界面
 * 
 * @author BJ
 */
public class FlowQueryFragment extends Fragment {
	public static final int TAB_SMART_BOARD = 0;
	public static final int TAB_FLOW_CATEGORY = 1;
	public static final int TAB_PACKAGE_DETAIL = 2;
	public static final int TAB_FLOW_ANALYZE = 3;
	private Context mContext;

	@ViewInject(R.id.viewpager)
	private ViewPager viewPager;
	
	@ViewInject(R.id.rbt_smart_kanban)
	private RadioButton rbtSmartBoard; // 智能看板

	@ViewInject(R.id.rbt_flow_classify)
	private RadioButton rbtFlowClassify; // 流量分类

	@ViewInject(R.id.rbt_flow_detail)
	private RadioButton rbtFlowDetail; // 套餐详情

	@ViewInject(R.id.rbt_flow_analyze)
	private RadioButton rbtFlowAnalyze;// 流量分析

	@ViewInject(R.id.rbt_above)
	private RadioGroup radioGroup;

	private UserPhoneDBService userPhoneDBService;

	public static Fragment newInstance() {
		FlowQueryFragment flowQueryFragment = new FlowQueryFragment();
		return flowQueryFragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userPhoneDBService = new UserPhoneDBService(mContext);
		userPhoneDBService.createTable();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_flowquery, container,
				false);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// mFragments = new Fragment[3];
		//
		// if (mFragments[0]==null) {
		// mFragments[0] = SmartKanBanFragment.newInstance();
		// }
		// if (mFragments[1] ==null) {
		// mFragments[1] = FlowCategoryFragment.newInstance();
		// }
		// if (mFragments[2]==null) {
		// mFragments[2] = PackageDetailFragment.newInstance();
		// }
		FragmentAdapter adapter = new FragmentAdapter(getChildFragmentManager());
		viewPager.setAdapter(adapter);
		addListener();
		setCurrentPage();
	}

	private void setCurrentPage() {
		int position = FlowSingleton.getInstance().getCurrentPosition();
		viewPager.setCurrentItem(position);
	}

	private void addListener() {
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int id) {
				FlowSingleton.getInstance().setCurrentPosition(id);
				switch (id) {
				case TAB_SMART_BOARD:
					rbtSmartBoard.setChecked(true);
					break;
				case TAB_FLOW_CATEGORY:
					rbtFlowClassify.setChecked(true);
					break;
				case TAB_PACKAGE_DETAIL:
					rbtFlowDetail.setChecked(true);
					break;
				case TAB_FLOW_ANALYZE:
					rbtFlowAnalyze.setChecked(true);
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@OnClick(R.id.rbt_smart_kanban)//点击智能看板
	public void fun_1(View v) {
		FlowSingleton.getInstance().setCurrentPosition(0); // 设置当前位置，刷新的时候位置不变
		viewPager.setCurrentItem(TAB_SMART_BOARD, true);
	}

	@OnClick(R.id.rbt_flow_classify)//点击流量分类
	public void fun_2(View v) {
		FlowSingleton.getInstance().setCurrentPosition(1);
		viewPager.setCurrentItem(TAB_FLOW_CATEGORY, true);

	}

	@OnClick(R.id.rbt_flow_detail)//点击流量详情
	public void fun_3(View v) {
		FlowSingleton.getInstance().setCurrentPosition(2);
		viewPager.setCurrentItem(TAB_PACKAGE_DETAIL, true);
	}

	@OnClick(R.id.rbt_flow_analyze)//点击流量分析
	public void fun_4(View v) {
		FlowSingleton.getInstance().setCurrentPosition(3);
		viewPager.setCurrentItem(TAB_FLOW_ANALYZE, true);
	}
}
