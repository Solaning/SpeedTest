package com.kinth.mmspeed;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.adapter.ArticleInfoAdapter;
import com.kinth.mmspeed.bean.ArticleInfo;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 流量优惠Activity
 */
@ContentView(R.layout.activity_flow_favorable)
public class FlowFavorableActivity extends BaseActivity{
	private Context mContext;
	@ViewInject(R.id.lv_infos)
	private ListView mListView;
	
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;
	
	@ViewInject(R.id.nav_right)
	private ImageButton btnRight;
	
	private List<ArticleInfo>mArticleInfos;
	private ArticleInfoAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mContext = this;
		tvTittle.setText("消息中心");
		btnRight.setVisibility(View.INVISIBLE);
		mArticleInfos = new ArrayList<ArticleInfo>();
		adapter = new ArticleInfoAdapter(this, mArticleInfos);
		mListView.setAdapter(adapter);
		
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.getFlowPreferentialList(this, new AsyncNetworkManager.GetNewsCallback() {
			@Override
			public void getNewsResult(int rtn, List<ArticleInfo> articleInfos) {
				mArticleInfos = articleInfos;
				if (rtn==-1) {
					JUtil.showMsg(FlowFavorableActivity.this,"获取失败");
				}else if (rtn==1) {
					if (articleInfos!=null&&articleInfos.size()>0) {
						adapter = new ArticleInfoAdapter(FlowFavorableActivity.this, mArticleInfos);
						mListView.setAdapter(adapter);
						mListView.setOnItemClickListener(new OnItemClickListener() {
			            	@Override
							public void onItemClick(AdapterView<?> arg0, View arg1,
									int arg2, long arg3) {
								Intent intent = new Intent(FlowFavorableActivity.this,FlowFavorableDetailActivity.class);
								String clickUrl = mArticleInfos.get(arg2).getClickURL();
								if(clickUrl.contains(APPConstant.URL_WITH_MOBILE_GMCC_OPEN) || clickUrl.contains(APPConstant.URL_WITH_MOBILE_GDMOBILE)){
									String phone = MainAccountUtil.getAvailablePhone(mContext);
									if(!TextUtils.isEmpty(phone)){
										intent.putExtra(FlowFavorableDetailActivity.INTENT_URL, clickUrl+"&mobile="+phone);
									}else{
										intent.putExtra(FlowFavorableDetailActivity.INTENT_URL, clickUrl);
									}
								}else{
									intent.putExtra(FlowFavorableDetailActivity.INTENT_URL, clickUrl);
								}
								intent.putExtra(FlowFavorableDetailActivity.INTENT_TITTLE, mArticleInfos.get(arg2).getArticleName());
								AsyncNetworkManager asyncNetworkManager= new AsyncNetworkManager();
								asyncNetworkManager.statisticsClick(FlowFavorableActivity.this, "clickFlowPreferential", mArticleInfos.get(arg2).getId()+"", new AsyncNetworkManager.StatisticsClickImp() {
									@Override
									public void StatisticsClickImpCallBack(int rtn) {
										
									}
								});
								startActivity(intent);
								rightInAnimation();
							}
						});
					}
				}
			}
		});
	}
	
	public void leftBtnOnClick(View w) {
		FlowFavorableActivity.this.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0) {
        	FlowFavorableActivity.this.finish();
    		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
             return true;
         }
         return super.onKeyDown(keyCode, event);
     }
}
