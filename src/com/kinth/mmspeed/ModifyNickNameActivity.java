package com.kinth.mmspeed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.mmspeed.db.UserPhoneDBService;

public class ModifyNickNameActivity extends BaseActivity implements OnClickListener{
	private EditText editText;
	private TextView tv_tittle;
	private ImageButton btnButton;
	
	private UserPhoneDBService userPhoneDBService;
	private String phoneStr = "";
	private String nickName;
	public static final int NickName_RequestCode = 100;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_phones);
		editText= (EditText)findViewById(R.id.et_modify_nickName);
		tv_tittle = (TextView)findViewById(R.id.nav_tittle);
		btnButton = (ImageButton)findViewById(R.id.nav_right);
		
		tv_tittle.setText("修改名称");
		userPhoneDBService = new UserPhoneDBService(ModifyNickNameActivity.this);
		phoneStr = getIntent().getStringExtra("phone");
		nickName = getIntent().getStringExtra("name");
		if (nickName!=null) {
			editText.setText(nickName);
			editText.setSelection(nickName.length());
		}
		btnButton.setOnClickListener(this);
	}
	public void leftBtnOnClick(View v) {
		Intent intent = new Intent();
		intent.putExtra("name", "");
		setResult(RESULT_OK, intent);
		ModifyNickNameActivity.this.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0) {
        	Intent intent = new Intent();
    		intent.putExtra("name", "");
    		setResult(RESULT_OK, intent);
    		ModifyNickNameActivity.this.finish();
    		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
             return true;
         }
         return super.onKeyDown(keyCode, event);
     }
	public void clearOnClick(View v) {
		editText.setText("");
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.nav_right:
			if ("".equals(editText.getText().toString())) {
				Toast.makeText(ModifyNickNameActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
			}else {
				userPhoneDBService.updateNickName(phoneStr, editText.getText().toString());
				Intent intent = new Intent();
				intent.putExtra("name", editText.getText().toString());
				setResult(RESULT_OK, intent);
				ModifyNickNameActivity.this.finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			}
			break;

		default:
			break;
		}
	}
}
