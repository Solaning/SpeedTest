package com.kinth.mmspeed.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kinth.mmspeed.R;

/**
 * 确定按钮，只有一个按钮
 * @author BJ
 * @version 1.0
 */
public class ConfirmDialog extends AlertDialog implements android.view.View.OnClickListener {
	int layoutRes;// 布局文件
	Context context;
	/** 确定按钮 **/
	private Button confirmBtn;
	private TextView contentTv;
	private TextView tittleTv;
	/** 取消按钮 **/
//	private Button cancelBtn;

	public ConfirmDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public void setTitle(CharSequence title) {
		// TODO Auto-generated method stub
		tittleTv.setText(title+"");
	}

	@Override
	public void setMessage(CharSequence message) {
		// TODO Auto-generated method stub
		contentTv.setText(message);
	}

	/**
	 * 自定义布局的构造方法
	 * 
	 * @param context
	 * @param resLayout
	 */
	public ConfirmDialog(Context context, int resLayout) {
		super(context);
		this.context = context;
		this.layoutRes = resLayout;
	}

	/**
	 * 自定义主题及布局的构造方法
	 * 
	 * @param context
	 * @param theme
	 * @param resLayout
	 */
	public ConfirmDialog(Context context, int theme, int resLayout) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(layoutRes);
		
		// 根据id在布局中找到控件对象
		confirmBtn = (Button) findViewById(R.id.btn_dialog_confirm);
		contentTv =(TextView) findViewById(R.id.tv_dialog_content); 
		tittleTv =(TextView) findViewById(R.id.tv_dialog_tittle); 
//		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		
		// 设置按钮的文本颜色
		confirmBtn.setTextColor(0xff1E90FF);
//		cancelBtn.setTextColor(0xff1E90FF);
		
		// 为按钮绑定点击事件监听器
		confirmBtn.setOnClickListener(this);
//		cancelBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_dialog_confirm:
			super.dismiss();
			break;
		}
	}
	
}