package com.kinth.mmspeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kinth.mmspeed.bean.User;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.UserState;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_password)
public class PasswordActivity extends BaseActivity{
	
	@ViewInject(R.id.tv_phone)
	private TextView tvCurrentPhone ;
	@ViewInject(R.id.edt_psw)
	private EditText edtPassword ;
	@ViewInject(R.id.edt_psw_repeat)
	private EditText edtPasswordRepeat ;
	
	@ViewInject(R.id.nav_left_text)
	private TextView tvLeft ;
	@ViewInject(R.id.nav_right_text)
	private TextView tvRight ;
	
	private DbUtils mDbUtils;
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mDbUtils = DbUtils.create(this, APPConstant.DATABASE_NAME);
		tvRight.setVisibility(View.INVISIBLE);
		try {
			user = mDbUtils.findFirst(User.class);
		} catch (DbException e) {
			e.printStackTrace();
			user = null;
		}
		if(user!=null){
			tvCurrentPhone.setText(user.getMobile());
		}
	}
	
	@OnClick(R.id.btn_login)
	public void finishOnClick(View v) {
		if (edtPassword.getText().toString().equals("")) {
			JUtil.showMsg(PasswordActivity.this, "请输入密码");
		}else if(edtPasswordRepeat.getText().toString().equals("")){
			JUtil.showMsg(PasswordActivity.this, "请输入密码");
		}else if (!edtPassword.getText().toString().equals(edtPasswordRepeat.getText().toString())) {
			JUtil.showMsg(PasswordActivity.this, "两次密码不一致");
		}else {
			AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
			asyncNetworkManager.updateInfo(PasswordActivity.this, user.getMobile(), user.getNickName()+"",
					user.getIconUrl(), edtPassword.getText().toString(), new AsyncNetworkManager.UpdateInfoIml() {
						@Override
						public void updateUserInfoCallBack(int rtn) {
							if (rtn==1) {
								JUtil.showMsg(PasswordActivity.this, "设置成功！");
								user.setStatus(UserState.LOGOUT);    //设置状态为注销
								try {
									mDbUtils.replace(user);
								} catch (DbException e) {
									e.printStackTrace();
								}
								//发送广播到“我的账号“页面，结束那个页面
								Intent intent = new Intent();
								intent.setAction(AccountManageActivity.FINISH_ACTIVITY);
								sendBroadcast(intent);
								
								PasswordActivity.this.finish();
								rightOutFinishAnimation();
							}else if (rtn==2) {
								JUtil.showMsg(PasswordActivity.this, "非法操作！");
							}else {
								JUtil.showMsg(PasswordActivity.this, "修改失败，请稍后重试！");
							}
						}
					});
		}
	}
	@OnClick(R.id.nav_left_text)
	public void leftOnClick(View v) {
		PasswordActivity.this.finish();
		rightOutFinishAnimation();
	}
	
}
