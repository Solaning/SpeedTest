package com.kinth.mmspeed;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.kinth.mmspeed.bean.User;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.modify_phone)
public class ModifyAcountNameActivity extends BaseActivity {
	@ViewInject(R.id.edt_name)
	private EditText edtNickName;
	@ViewInject(R.id.nav_right)
	private ImageButton btnButton;
	
	private DbUtils mDbUtils;
	private User userAccount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mDbUtils = DbUtils.create(this,APPConstant.DATABASE_NAME);
		try {
			userAccount = mDbUtils.findFirst(User.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void rightBtnOnClick(View v) {
		if ("".equals(edtNickName.getText().toString())) {
			JUtil.showMsg(ModifyAcountNameActivity.this, "昵称不能为空!");
		}
		userAccount.setNickName(edtNickName.getText().toString());
		try {
			mDbUtils.replace(userAccount);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
}
