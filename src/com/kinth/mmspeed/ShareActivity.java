package com.kinth.mmspeed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_share)
public class ShareActivity extends BaseActivity implements OnClickListener{
	//C:\Users\admin\.android\debug.keystore
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
	}

	@OnClick(R.id.btn_email)
	public void shareEmailOnClick(View v){
		sendMail("分享啊");
	}
	@OnClick(R.id.btn_sms)
	public void shareSMSOnClick(View v){
		sendSMS();
	}
	@OnClick(R.id.btn_wechat)
	public void shareWeChatOnClick(View v){
		
	}
	
	
	@Override
	public void onClick(View v) {
		
	}
	  //发短信   
    private   void  sendSMS(){  
	   Uri smsToUri = Uri.parse( "smsto:" );  
	   Intent sendIntent =  new  Intent(Intent.ACTION_VIEW, smsToUri);  
	    //sendIntent.putExtra("address", "123456"); // 电话号码，这行去掉的话，默认就没有电话   
	   sendIntent.putExtra( "sms_body" ,  "我要共享这个软件" );  
	   sendIntent.setType( "vnd.android-dir/mms-sms" );  
	   startActivityForResult(sendIntent, 1002 );  
   }  
    //发邮件   
    private   void  sendMail(String emailBody){  
        Intent email =  new  Intent(android.content.Intent.ACTION_SEND);  
        email.setType( "plain/text" );  
        String  emailSubject =  "共享软件" ;  
         //设置邮件默认地址   
        // email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);   
         //设置邮件默认标题   
        email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);  
         //设置要默认发送的内容   
        email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);  
         //调用系统的邮件系统   
        startActivityForResult(Intent.createChooser(email,  "请选择邮件发送软件" ), 1001 );  
   }  
}
