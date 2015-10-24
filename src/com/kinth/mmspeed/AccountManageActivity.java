package com.kinth.mmspeed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.id;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.bean.Active;
import com.kinth.mmspeed.bean.Comments;
import com.kinth.mmspeed.bean.User;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.UserState;
import com.kinth.mmspeed.hk.Action;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.network.AsyncFileUpload;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncFileUpload.FileCallback;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.UserFriendUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class AccountManageActivity extends BaseActivity {
	/*用来标识请求照相功能的activity*/  
    private static final int CAMERA_WITH_DATA = 3023;  
    /*用来标识请求gallery的activity*/  
    private static final int PHOTO_PICKED_WITH_DATA = 3021;  
    /*拍照的照片存储位置*/  
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");  
    private File mCurrentPhotoFile;//照相机拍照得到的图片  
    private String fileName = "";
    private Context mContext;
	
	private UserAccount user;
	private ProgressDialog mProgressDialog = null;
	@ViewInject(R.id.tv_nickname)
	private TextView tvNickName;
	@ViewInject(R.id.tv_phone)
	private TextView tvPhone;
	@ViewInject(R.id.iv_head)
	private ImageView ivIcon;
	@ViewInject(R.id.nav_right)
	private ImageButton btnRight;
	
	@ViewInject(R.id.btn_loginout)
	private Button btnLogout;
	
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;
	
	private BitmapUtils bitmapUtils ;
	
	private MyBroadcastReceiver mBroadcastReceiver;
	
	public static final String FINISH_ACTIVITY = "FINISH_ACTIVITY";
	private Handler handle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			AccountManageActivity.this.finish();
			}
		};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_account_manage);
		ViewUtils.inject(this);
		initView(); 
		
		mBroadcastReceiver = new MyBroadcastReceiver();
		
	    IntentFilter myOwnFilter = new IntentFilter();
	    myOwnFilter.addAction(FINISH_ACTIVITY);
	    registerReceiver(mBroadcastReceiver, myOwnFilter);
	    
		user =MainAccountUtil.getCurrentAccount(this);
		if (user==null||user.getMobile().equals("")) {
			Intent intent = new Intent(this, FirstLoginActivity.class);
			startActivity(intent);
			this.finish();
		}else {
			bitmapUtils = new BitmapUtils(this);
			// 加载assets中的图片(路径以assets开头)
			if (user.getIconURL()==null||user.getIconURL().equals("")) {
				
			}else {
				bitmapUtils.display(ivIcon,user.getIconURL()+"");
			}
			tvNickName.setText(user.getNickName()+"");
			tvPhone.setText(user.getMobile()+"");
		}
		UserFriendUtil.checkUploadContract(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		user =MainAccountUtil.getCurrentAccount(AccountManageActivity.this);
	}

	/**
	 * 注销处理
	 * @param v
	 */
	public void logoutOnClick(View v) {
		//add by sola @2014-08-05
		DbUtils db = DbUtils.create(mContext);
		db.configAllowTransaction(true);// 开启事务
		db.configDebug(false);// debug，输出sql语句
		try {
			db.dropTable(Active.class);
			db.dropTable(Comments.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SingletonSharedPreferences.getInstance().setHasUploadContacts(false);
		ApplicationController.getInstance().setNumOfMoments(0);//朋友圈的消息条数为0
		//发广播通知
		mContext.sendBroadcast(new Intent().setAction(Action.HAS_NEW_MESSAGE));
		
		//----------end--------------
		MainAccountUtil.deleteUserAccount(AccountManageActivity.this);
		JUtil.showMsg(AccountManageActivity.this, "注销成功");
		AccountManageActivity.this.finish();
		rightOutFinishAnimation();
	}
	private void initView() {
		btnRight.setVisibility(View.INVISIBLE);
		tvTittle.setText("个人信息");
	}
	protected void onDestroy() {
		super.onDestroy();
		// 解除注册广播接收器
		unregisterReceiver(mBroadcastReceiver);
	}
	
	@OnClick(R.id.iv_head)
	public void checkBigIcon(View v){
		if (user!=null&&user.getIconURL()!=null&&!user.getIconURL().equals("")) {
			Intent intent = new Intent(AccountManageActivity.this,HeadIconActivity.class);
			intent.putExtra("url", user.getIconURL()+"");
			startActivity(intent);
		}
		
	}
	@OnClick(R.id.linearLayout3)
	public void changeNickName(View v){
		Intent intent = new Intent(AccountManageActivity.this,ChangeAccountNameActivity.class);
		startActivity(intent);
		rightInAnimation();
	}
	/**
	 * 选择照片
	 */
	@OnClick(R.id.linearLayout2)
	public void doPickPhotoAction(View v) {  
        Context context = AccountManageActivity.this;  
        // Wrap our context to inflate list items using correct theme  
        final Context dialogContext = new ContextThemeWrapper(context,  
                android.R.style.Theme_Light);  
        String cancel="返回";  
        String[] choices;  
        choices = new String[2];  
        choices[0] = getString(R.string.take_photo);  //拍照  
        choices[1] = getString(R.string.pick_photo);  //从相册中选择  
        final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,  
                android.R.layout.simple_list_item_1, choices);  
      
        final AlertDialog.Builder builder = new AlertDialog.Builder(  
                dialogContext);  
        builder.setTitle(R.string.attachToContact);  
        builder.setSingleChoiceItems(adapter, -1,  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) {  
                        dialog.dismiss();  
                        switch (which) {  
                        case 0:{  
                            String status=Environment.getExternalStorageState();  
                            if(status.equals(Environment.MEDIA_MOUNTED)){//判断是否有SD卡  
                                doTakePhoto();// 用户点击了从照相机获取  
                            }  
                            else{  
                                JUtil.showMsg(AccountManageActivity.this, "没有SD卡");
                            }  
                            break;  
                        }  
                        case 1:  
                            doPickPhotoFromGallery();// 从相册中去获取  
                            break;  
                        }  
                    }  
                });  
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
            }  
        });  
        builder.create().show();  
    }  
	
	/** 
	    * 拍照获取图片 
	    *  
	    */  
	    protected void doTakePhoto() {  
	        try {  
	            // Launch camera to take photo for selected contact  
	            PHOTO_DIR.mkdirs();// 创建照片的存储目录  
	            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名  
	            final Intent intent = getTakePickIntent(mCurrentPhotoFile);  
	            startActivityForResult(intent, CAMERA_WITH_DATA);  
	        } catch (ActivityNotFoundException e) {  
	            Toast.makeText(this,"出错",  
	                    Toast.LENGTH_LONG).show();  
	        }  
	    }  
	      
	    public static Intent getTakePickIntent(File f) {  
	        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);  
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));  
	        return intent;  
	    }  
	      
	    /** 
	    * 用当前时间给取得的图片命名 
	    */  
	    private String getPhotoFileName() {  
	        Date date = new Date(System.currentTimeMillis());  
	        SimpleDateFormat dateFormat = new SimpleDateFormat(  
	                "'IMG'_yyyy-MM-dd HH:mm:ss");  
	        return dateFormat.format(date) + "._jpg";  
	    }  
	 // 请求Gallery程序  
	    protected void doPickPhotoFromGallery() {  
	        try {  
	            // Launch picker to choose photo for selected contact  
	            final Intent intent = getPhotoPickIntent();  
	            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);  
	        } catch (ActivityNotFoundException e) {  
	            Toast.makeText(this, R.string.photoPickerNotFoundText1,  
	                    Toast.LENGTH_LONG).show();  
	        }  
	    }  
	      
	    // 封装请求Gallery的intent  
	    public static Intent getPhotoPickIntent() {  
	        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);  
	        intent.setType("image/*");  
	        intent.putExtra("crop", "true");  
	        intent.putExtra("aspectX", 1);  
	        intent.putExtra("aspectY", 1);  
	        intent.putExtra("outputX", 80);  
	        intent.putExtra("outputY", 80);  
	        intent.putExtra("return-data", true);  
	        return intent;  
	    }  
	      
	    // 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult  
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	        if (resultCode != RESULT_OK)  
	            return;  
	        switch (requestCode) {  
	            case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的  
	                final Bitmap photo = data.getParcelableExtra("data");  
	                if (photo==null) {
						JUtil.showMsg(AccountManageActivity.this, "更换头像失败");
						break;
					}
	                // 下面就是显示照片了  
	                //缓存用户选择的图片  
//	                img = getBitmapByte(photo);  
//	                mEditor.setPhotoBitmap(photo);  
	                ivIcon.setImageBitmap(photo);//
	                fileName = "head"+System.currentTimeMillis();
	                saveBitmap2file(photo, fileName);
	                
	                changeIcon(fileName);
	                
	                System.out.println("set new photo");  
	                break;  
	            }  
	            case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片  
	                doCropPhoto(mCurrentPhotoFile);  
	                break;  
	            }  
	        }  
	    }  
	      
	    protected void doCropPhoto(File f) {  
	        try {  
	            // 启动gallery去剪辑这个照片  
	            final Intent intent = getCropImageIntent(Uri.fromFile(f));  
	            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);  
	        } catch (Exception e) {  
	            Toast.makeText(this, R.string.photoPickerNotFoundText,  
	                    Toast.LENGTH_LONG).show();  
	        }  
	    }  
	      
	    /**  
	    * Constructs an intent for image cropping. 调用图片剪辑程序  
	    */  
	    public static Intent getCropImageIntent(Uri photoUri) {  
	        Intent intent = new Intent("com.android.camera.action.CROP");  
	        intent.setDataAndType(photoUri, "image/*");  
	        intent.putExtra("crop", "true");  
	        intent.putExtra("aspectX", 1);  
	        intent.putExtra("aspectY", 1);  
	        intent.putExtra("outputX", 80);  
	        intent.putExtra("outputY", 80);  
	        intent.putExtra("return-data", true);  
	        return intent;  
	    }  
	    static boolean  saveBitmap2file(Bitmap bmp,String filename){  
	    	File destDir = new File(Environment.getExternalStorageDirectory() +"/LiuLiangYi/data/avatar/");
	    	  if (!destDir.exists()) {
	    	   destDir.mkdirs();
	    	  }
	         CompressFormat format= Bitmap.CompressFormat.JPEG;  
	        int quality = 100;  
	        OutputStream stream = null;  
	        try {  
	                stream = new FileOutputStream(Environment.getExternalStorageDirectory() +"/LiuLiangYi/data/avatar/" + filename);  
	        } catch (FileNotFoundException e) {  
	                e.printStackTrace();  
	        }  
	        return bmp.compress(format, quality, stream);  
	        }  
	
	class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			//接收到一个消息，现在就是添加到聊天的list，然后更新界面
			if (actionStr.equals(FINISH_ACTIVITY)) {
				handle.obtainMessage();
				handle.sendEmptyMessage(0);
			}
		}
	}
	
	private void setUserInfo(final UserAccount userAccount ) {
		JUtil.dismissDialog(mProgressDialog);
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.updateInfo(AccountManageActivity.this, userAccount.getMobile(), userAccount.getNickName(), 
				userAccount.getIconURL()+"", "", new AsyncNetworkManager.UpdateInfoIml() {
				@Override
				/*
				 *   rtn 1设置成功
				 *    2 非法操作，修改失败（设备没有注册过）
				 */
					public void updateUserInfoCallBack(int rtn) {
					JUtil.dismissDialog(mProgressDialog);
						if (rtn==1) {
							MainAccountUtil.saveUserAccount(AccountManageActivity.this,userAccount);
							JUtil.showMsg(AccountManageActivity.this, "头像更新成功");
						}else if (rtn==2) {
							JUtil.showMsg(AccountManageActivity.this, "头像设置失败");
						}else {
							JUtil.showMsg(AccountManageActivity.this, "头像设置失败");
						}
					}
				});
	}
	
	private void changeIcon(String mFileName) {
		File file = new File(Environment.getExternalStorageDirectory() +"/LiuLiangYi/data/avatar/"+mFileName);
		if (!file.exists()) {
			JUtil.showMsg(AccountManageActivity.this, "设置头像失败！");
			return;
		}
		mProgressDialog = JUtil.showDialog(AccountManageActivity.this,"正在上传头像...");
		AsyncFileUpload.asynFileUploadToServer(Environment.getExternalStorageDirectory() +"/LiuLiangYi/data/avatar/"+mFileName, new FileCallback() {
			@Override
			public void fileUploadCallback(String fileUrl) {
				
				if (fileUrl==null||"".equals(fileUrl)) {   //上传失败
					JUtil.showMsg(AccountManageActivity.this, "上传失败！");
				}else {
					UserAccount userAccount = new UserAccount(user.getMobile(), user.getNickName(), fileUrl);
					
//					UserAccount user = new User(edtName.getText().toString(), fileUrl, userPhone, "", "", UserState.NORMAL);
					setUserInfo(userAccount);
				}
			}
		});
	}
	
	
}
