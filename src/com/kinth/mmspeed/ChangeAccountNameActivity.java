package com.kinth.mmspeed;

import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
@ContentView(R.layout.activity_modify_phones)
public class ChangeAccountNameActivity extends BaseActivity {
	private UserAccount userAccount;
	@ViewInject(R.id.et_modify_nickName)
	private EditText edtNickName;
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;
	private ProgressDialog mProgressDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		tvTittle.setText("修改昵称");
		
		userAccount =MainAccountUtil.getCurrentAccount(this);
		if (userAccount!=null&&!userAccount.getNickName().equals("")) {
			edtNickName.setText(userAccount.getNickName()+"");
			edtNickName.setSelection(userAccount.getNickName().length());
		}
	}
	public void clearOnClick(View v) {
		edtNickName.setText("");
	}
	@Override
	public void rightBtnOnClick(View v) {
		if ("".equals(edtNickName.getText().toString())) {
			JUtil.showMsg(ChangeAccountNameActivity.this, "昵称不能为空");
		}else {
			userAccount.setNickName(edtNickName.getText().toString());
			mProgressDialog = JUtil.showDialog(ChangeAccountNameActivity.this,"正在提交");
			AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
			asyncNetworkManager.updateInfo(ChangeAccountNameActivity.this, userAccount.getMobile(), userAccount.getNickName(), 
					userAccount.getIconURL()+"", "", new AsyncNetworkManager.UpdateInfoIml() {
					@Override
					/*
					 *   rtn 1设置成功
					 *    2 非法操作，修改失败（设备没有注册过）
					 */
						public void updateUserInfoCallBack(int rtn) {
						JUtil.dismissDialog(mProgressDialog);
							if (rtn==1) {
								MainAccountUtil.saveUserAccount(ChangeAccountNameActivity.this,userAccount);
								JUtil.showMsg(ChangeAccountNameActivity.this, "修改成功");
								Intent intent = new Intent();
								intent.setAction(AccountManageActivity.FINISH_ACTIVITY);
								sendBroadcast(intent);
								Intent intent2 = new Intent(ChangeAccountNameActivity.this,AccountManageActivity.class);
								startActivity(intent2);
								rightOutFinishAnimation();
								ChangeAccountNameActivity.this.finish();
								
							}else if (rtn==2) {
								JUtil.showMsg(ChangeAccountNameActivity.this, "修改失败");
							}else {
								JUtil.showMsg(ChangeAccountNameActivity.this, "修改失败");
							}
						}
					});
		}
	}
}
