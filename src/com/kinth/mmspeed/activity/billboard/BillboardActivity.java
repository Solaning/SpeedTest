package com.kinth.mmspeed.activity.billboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.activity.billboard.FTDBillboardFragment.On4GRefresh;
import com.kinth.mmspeed.activity.billboard.FlowBillboardFragment.OnFlowRefresh;
import com.kinth.mmspeed.ui.CircularProgressView;
import com.kinth.mmspeed.util.AnimationUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 好友排行榜  -- tab3
 */
@ContentView(R.layout.aty_billboard)
public class BillboardActivity extends FragmentActivity{
	private Context mContext;
	private FTDBillboardFragment ftdBillboardFragment;
	private FlowBillboardFragment flowBillboardFragment;
	private Fragment[] fragments;
	private int currentTabIndex;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;
	
	@ViewInject(R.id.nav_right_progress)
	private CircularProgressView progress;
	
	@ViewInject(R.id.rbt_one)
	private Button rbtOne;
	
	@ViewInject(R.id.rbt_two)
	private Button rbtTwo;
	
	/**
	 * 左边返回按钮事件
	 */
	@OnClick(R.id.nav_left)
	public void leftBtnOnClick(View v) {
		this.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	
	@OnClick(R.id.rbt_one)
	public void fun_1(View v){
		if (currentTabIndex != 0) {//非0，切
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[0].isAdded()) {
				trx.add(R.id.fragment_container, fragments[0]);
			}
			trx.show(fragments[0]).commit();
		}
		rbtTwo.setSelected(false);
		//把当前tab设为选中状态
		rbtOne.setSelected(true);
		currentTabIndex = 0;
	}
	
	@OnClick(R.id.rbt_two)
	public void fun_2(View v){
		if (currentTabIndex != 1) {//非1，切
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[1].isAdded()) {
				trx.add(R.id.fragment_container, fragments[1]);
			}
			trx.show(fragments[1]).commit();
		}
		rbtOne.setSelected(false);
		//把当前tab设为选中状态
		rbtTwo.setSelected(true);
		currentTabIndex = 1;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		
		rbtOne.setSelected(true);
		tvTittle.setText("好友排行榜");
		initFragment();
	}
	
	private void initFragment(){
		ftdBillboardFragment = new FTDBillboardFragment(new On4GRefresh(){

			@Override
			public void onStart() {
				progress.setVisibility(View.VISIBLE);
			}

			@Override
			public void onStop() {
				progress.setVisibility(View.GONE);				
			}}
		);
		flowBillboardFragment = new FlowBillboardFragment(new OnFlowRefresh(){

			@Override
			public void onStart() {
				progress.setVisibility(View.VISIBLE);
			}

			@Override
			public void onStop() {
				progress.setVisibility(View.GONE);				
			}});
		fragments = new Fragment[] {ftdBillboardFragment, flowBillboardFragment};
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ftdBillboardFragment).
			add(R.id.fragment_container, flowBillboardFragment).hide(flowBillboardFragment).show(ftdBillboardFragment).commit();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return false;
		}
		return false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (AnimationUtil.ANIM_IN != 0 && AnimationUtil.ANIM_OUT != 0) {
			super.overridePendingTransition(AnimationUtil.ANIM_IN,
					AnimationUtil.ANIM_OUT);
			AnimationUtil.clear();
		}
	}
}
