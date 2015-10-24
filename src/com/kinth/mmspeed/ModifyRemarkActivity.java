package com.kinth.mmspeed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kinth.mmspeed.db.UserPhoneDBService;

public class ModifyRemarkActivity extends Activity{
	private EditText editText;
	private UserPhoneDBService userPhoneDBService;
	private String phoneStr = "";
	private TextView tv_tittle;
	private String remark;
	public static final int Remark_RequestCode = 200;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_phones);
		tv_tittle = (TextView)findViewById(R.id.nav_tittle);
		tv_tittle.setText("修改备注");
		editText= (EditText)findViewById(R.id.et_modify_nickName);
		userPhoneDBService = new UserPhoneDBService(ModifyRemarkActivity.this);
		phoneStr = getIntent().getStringExtra("phone");
		remark =  getIntent().getStringExtra("remark");
		if (remark!=null) {
			editText.setText(remark);
			editText.setSelection(remark.length());
		}
	}
	public void confirmOnClick(View v) {
			userPhoneDBService.updateRemark(phoneStr, editText.getText().toString());
			Intent intent = new Intent();
			intent.putExtra("remark", editText.getText().toString());
			setResult(RESULT_OK, intent);
			ModifyRemarkActivity.this.finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	public void leftBtnOnClick(View v) {
		Intent intent = new Intent();
		intent.putExtra("remark", "");
		setResult(RESULT_OK, intent);
		ModifyRemarkActivity.this.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0) {
        	Intent intent = new Intent();
    		intent.putExtra("remark", "");
    		setResult(RESULT_OK, intent);
    		ModifyRemarkActivity.this.finish();
    		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
             return true;
         }
         return super.onKeyDown(keyCode, event);
     }
	public void clearOnClick(View v) {
		editText.setText("");
	}
}
