package com.kinth.mmspeed.friend;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.mmspeed.BaseActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.bean.Active;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ActiveType;
import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.network.AsyncFileUpload;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncFileUpload.FileCallback;
import com.kinth.mmspeed.network.AsyncNetworkManager.genActiveTimeLineCallBack;
import com.kinth.mmspeed.util.FileUtil;
import com.kinth.mmspeed.util.Md5Util;
import com.kinth.mmspeed.util.DateUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sola.code.ydlly_assist.bean.KeyValue;

/**
 * 发布动态--图片与文字
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_moments_publish)
public class MomentsPublishActivity extends BaseActivity {
	private static final int REQUEST_CODE_PICK_PHOTO = 9979;//选图片的请求码
	private static final int REQUEST_CODE_PREVIEW = 9980;//预览图片请求码
	public static final String INTENT_PUBLISH_ACTIVE_FOR_RESULT = "INTENT_PUBLISH_ACTIVE_FOR_RESULT";//发动态后的结果返回
	public static final String INTENT_PUBLISH_ACTIVE_FOR_RESULT_ACTIVE = "INTENT_PUBLISH_ACTIVE_FOR_RESULT_ACTIVE";//发动态后的该动态
	private static final int UPLOAD_RETRY_LIMIT = 3;//失败重试次数
	private Context mContext;
	private MomentsPublishAdapter adapter;
	private boolean uploading =false;//是否正在上传

	private ArrayList<PickedImage> pickedImages = null;

	@ViewInject(R.id.nav_left)
	private View back;// 返回

	@ViewInject(R.id.nav_tittle)
	private TextView title;// 标题

	@ViewInject(R.id.nav_right_btn)
	private Button right;// 右侧完成按钮
	
	@ViewInject(R.id.et_thought_of_this_time)
	private EditText thought;

	@ViewInject(R.id.gridGallery)
	private GridView gridGallery;

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {// 退出
		dialog();
	}
	
	@SuppressWarnings("unchecked")
	@OnClick(R.id.nav_right_btn)
	public void fun_2(View v){//发布动态
		//条件检查，排除只有一个“+”的情况
		if(pickedImages == null || pickedImages.size() == 0 || (pickedImages.size() == 1 && pickedImages.get(0).getPath().equals(MomentsPublishAdapter.THIS_IS_PLUS))){
			return;
		}
		uploading = true;//正在上传
		CustomProgressDialogUtil.startProgressDialog(
				mContext, "正在发送，请稍后...", true);
		//压缩图片生成要上传的图片数据
		new CompressBitmapTask().execute(pickedImages);
	}
	
	/**
	 * 异步压缩图片
	 * @author Sola
	 *
	 */
	class CompressBitmapTask extends AsyncTask<ArrayList<PickedImage>,Void,Boolean>{

		@Override
		protected Boolean doInBackground(ArrayList<PickedImage>... arg0) {
			for(PickedImage item : pickedImages){//更新上传图片路径为压缩后的图片路径
				if(!item.getPath().equals(MomentsPublishAdapter.THIS_IS_PLUS)){//舍弃加号
					try {
						item.setPath(PictureUtil.getThumbUploadPath(item.getPath(),getResources().getDimensionPixelSize(R.dimen.BitmapMaxWidth)));
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(!result){
				if (CustomProgressDialogUtil
						.stopProgressDialog()) {
					Toast.makeText(mContext, "发送失败：压缩图片失败", Toast.LENGTH_LONG).show();
				}
				return;
			}
			Map<String, UploadImage> uploadMap = new LinkedHashMap<String,UploadImage>();//服务器上的url
			for(final PickedImage item : pickedImages){
				if(!item.getPath().equals(MomentsPublishAdapter.THIS_IS_PLUS)){//舍弃加号
					uploadMap.put(item.getPath(), new UploadImage(item.getPath()));
//					uploadAction(uploadMap,item,creatParams(item.getPath()));
					upload2(uploadMap,item);
				}
			}
		}
	}
	
	private void upload2(final Map<String, UploadImage> uploadMap,final PickedImage item){
		AsyncFileUpload.asynFileUploadToServer(item.getPath(), new FileCallback() {
			@Override
			public void fileUploadCallback(String fileUrl) {
				
				if (TextUtils.isEmpty(fileUrl)) {   //上传失败
		        	if(!uploading){
		        		return;
		        	}
		        	uploadMap.get(item.getPath()).setSucceed(false);
		        	uploadMap.get(item.getPath()).autoAddFailTime();//失败次数自动加1
		        	if(uploadMap.get(item.getPath()).getFailTime() < UPLOAD_RETRY_LIMIT){//重试
		        		upload2(uploadMap,item);
		        	}else{
		        		uploading = false;//失败了
		        		onUploadFailed();
		        	}
				}else {
		        	if(!uploading){
		        		return;
		        	}
		        	uploadMap.get(item.getPath()).setUrl(fileUrl);
		        	uploadMap.get(item.getPath()).setSucceed(true);
		        	boolean hasDone = true;//判断图片是否都上传完成
		        	for (Map.Entry<String, UploadImage> entry : uploadMap.entrySet()) {
		        		if(entry.getValue().isSucceed()){
		        			continue;
		        		}else{//还有一个没有传完
		        			hasDone = false;
		        			break;
		        		}
		        	}
		        	if(hasDone){//上传完成，发动态
		        		final String mobile ;
		        		if(MainAccountUtil.getCurrentAccount(mContext) != null && !TextUtils.isEmpty(mobile = MainAccountUtil.getCurrentAccount(mContext).getMobile())){
		        			ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
		        			keyValue.add(new KeyValue("mobile",mobile));
		        			String content = null;
		        			try {
								content = java.net.URLEncoder.encode(thought.getText().toString(),"UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
		        			keyValue.add(new KeyValue("content",content));
		        			keyValue.add(new KeyValue("activeType",ActiveType.PersonalActive.getValue()));
		        			final StringBuffer sb = new StringBuffer();
		        			for(Map.Entry<String, UploadImage> entry : uploadMap.entrySet()){
		        				sb.append(entry.getValue().getUrl()).append(",");
		        			}
		        			keyValue.add(new KeyValue("picUrl",sb.toString()));//TODO
		        			/**
		        			 * 发动态
		        			 */
		        			new AsyncNetworkManager().genActiveTimeLine(mContext, keyValue, new genActiveTimeLineCallBack() {
								
								@Override
								public void onGenActiveTimeLineCallBack(int rtn, int activeId) {
									if (CustomProgressDialogUtil
											.stopProgressDialog()) {
										if(rtn == 1){//把图片重命名拷贝
											Active thisActive = new Active();
											thisActive.setActiveId(activeId);
											thisActive.setMobile(mobile);
											thisActive.setContent(thought.getText().toString());
											thisActive.setActiveType(ActiveType.PersonalActive.getValue());
											thisActive.setCreateTime(DateUtil.getStringDateAfterSecond(15));//时间延后15s
											thisActive.setIconUrl(MainAccountUtil.getCurrentAccount(mContext).getIconURL());
											thisActive.setNickName(MainAccountUtil.getCurrentAccount(mContext).getNickName());
											thisActive.setPicUrl(sb.toString());
											for(Map.Entry<String, UploadImage> entry : uploadMap.entrySet()){
						        				File source = new File(entry.getValue().getRawPath());
						        				File target = new File(APPConstant.IMAGE_PERSISTENT_CACHE + File.separator + Md5Util.md5s(entry.getValue().getUrl()));
						        				if(source.exists()){
						        					FileUtil.nioTransferCopy(source, target);
						        				}
						        			}
											
											Intent data = new Intent();
							        		data.putExtra(INTENT_PUBLISH_ACTIVE_FOR_RESULT, true);//发送成功
							        		data.putExtra(INTENT_PUBLISH_ACTIVE_FOR_RESULT_ACTIVE, thisActive);//成功后的动态id，可以直接插入数据库
							        		setResult(RESULT_OK, data);
							        		finish();
							        		rightOutFinishAnimation();
										}else{//发送失败
											onUploadFailed();
										}
									}
								}
							});
		        		}else{
		        			onUploadFailed();
		        		}
		        	}
		        
				}
			}
		});
	}
	
	/**
	 * 生成请求参数
	 * @param path
	 * @return
	 */
	@Deprecated
	private RequestParams creatParams(String path){
		RequestParams params = new RequestParams();
//		params.addHeader("connection", "Keep-Alive");
//		params.addHeader("user-agent",
//				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
//		params.addHeader("Charsert", "UTF-8");
//		params.addHeader("Content-Type", "multipart/form-data;");
//		params.addQueryStringParameter("name", "value");

		// 只包含字符串参数时默认使用BodyParamsEntity，
		// 类似于UrlEncodedFormEntity（"application/x-www-form-urlencoded"）。
//		params.addBodyParameter("name", "value");

		// 加入文件参数后默认使用MultipartEntity（"multipart/form-data"），
		// 如需"multipart/related"，xUtils中提供的MultipartEntity支持设置subType为"related"。
		// 使用params.setBodyEntity(httpEntity)可设置更多类型的HttpEntity（如：
		// MultipartEntity,BodyParamsEntity,FileUploadEntity,InputStreamUploadEntity,StringEntity）。
		// 例如发送json参数：params.setBodyEntity(new StringEntity(jsonStr,charset));
		String content = PictureUtil.bitmapToString(path);
		params.addBodyParameter("file", content);
//		params.addBodyParameter("attach[" + i + "]", new File(
//			       filePaths[i]), "image/jpeg");
		return params;
	}
	
	/**
	 * 上传图片
	 * @param uploadMap
	 * @param item
	 * @param params
	 */
	@Deprecated
	private void uploadAction(final Map<String, UploadImage> uploadMap,final PickedImage item,RequestParams params){
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST,
				APPConstant.UPLOAD_URL,params,
		    new RequestCallBack<String>() {

		        @Override
		        public void onStart() {
//		            Log.e("onStart", "conn...");
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
//		            if (isUploading) {
//		                Log.e("onLoading isUploading","upload: " + current + "/" + total);
//		            } else {
//		                Log.e("onLoading isUploading","reply: " + current + "/" + total);
//		            }
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	if(!uploading){
		        		return;
		        	}
		        	uploadMap.get(item.getPath()).setUrl(responseInfo.result);
		        	uploadMap.get(item.getPath()).setSucceed(true);
		        	boolean hasDone = true;//判断图片是否都上传完成
		        	for (Map.Entry<String, UploadImage> entry : uploadMap.entrySet()) {
		        		if(entry.getValue().isSucceed()){
		        			continue;
		        		}else{//还有一个没有传完
		        			hasDone = false;
		        			break;
		        		}
		        	}
		        	if(hasDone){//上传完成，发动态
		        		final String mobile ;
		        		if(MainAccountUtil.getCurrentAccount(mContext) != null && !TextUtils.isEmpty(mobile = MainAccountUtil.getCurrentAccount(mContext).getMobile())){
		        			ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
		        			keyValue.add(new KeyValue("mobile",mobile));
		        			keyValue.add(new KeyValue("content",thought.getText().toString()));
		        			keyValue.add(new KeyValue("activeType",ActiveType.PersonalActive.getValue()));
		        			StringBuffer sb = new StringBuffer();
		        			for(Map.Entry<String, UploadImage> entry : uploadMap.entrySet()){
		        				sb.append(entry.getValue().getUrl()).append(",");
		        			}
		        			keyValue.add(new KeyValue("picUrl",sb.toString()));//TODO
		        			/**
		        			 * 发动态
		        			 */
		        			new AsyncNetworkManager().genActiveTimeLine(mContext, keyValue, new genActiveTimeLineCallBack() {
								
								@Override
								public void onGenActiveTimeLineCallBack(int rtn, int activeId) {
									Log.e("rtn",""+rtn);
									if (CustomProgressDialogUtil
											.stopProgressDialog()) {
										Log.e("rtn","fuckkkk"+rtn);
										if(rtn == 1){
											Intent data = new Intent();
							        		data.putExtra(INTENT_PUBLISH_ACTIVE_FOR_RESULT, true);//发送成功
							        		setResult(RESULT_OK, data);
							        		finish();
							        		rightOutFinishAnimation();
										}else{//发送失败
											onUploadFailed();
										}
									}else{
										Log.e("rtn","else fuck"+rtn);
									}
								}
							});
		        		}else{
		        			onUploadFailed();
		        		}
		        	}
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	Log.e("onFailure",error.getExceptionCode() + ":" + msg);
		        	if(!uploading){
		        		return;
		        	}
		        	uploadMap.get(item.getPath()).setSucceed(false);
		        	uploadMap.get(item.getPath()).autoAddFailTime();//失败次数自动加1
		        	if(uploadMap.get(item.getPath()).getFailTime() < UPLOAD_RETRY_LIMIT){//重试
		        		uploadAction(uploadMap,item,creatParams(item.getPath()));
		        	}else{
		        		uploading = false;//失败了
		        		onUploadFailed();
		        	}
		        }
		});
	}
	
	private void onUploadFailed(){
		CustomProgressDialogUtil.stopProgressDialog();
		Toast.makeText(mContext, "发送失败", Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null);
		mContext = this;
		ViewUtils.inject(this);
		right.setEnabled(false);
		right.setText("发送");
		gridGallery.setFastScrollEnabled(false);

		pickedImages = getIntent().getParcelableArrayListExtra(
				CustomGalleryActivity.INTENT_IMG_PATH_ARRAY);
		if (pickedImages == null || pickedImages.size() == 0) {// 没有数据
			return;
		}
		
		adapter = new MomentsPublishAdapter(mContext, pickedImages);
		gridGallery.setAdapter(adapter);
		right.setEnabled(true);//有选择图片
		gridGallery.setOnItemClickListener(new OnItemClickListener() {//点击图片或加号

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				String path = adapter.getItem(position);
				if(MomentsPublishAdapter.THIS_IS_PLUS.equals(path)){//加号
					Intent intent = new Intent(mContext,CustomGalleryActivity.class);
					intent.putExtra(CustomGalleryActivity.INTENT_PICKED_IMG_CAPACITY, adapter.getCount() - 1);//已选图片张数
					startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
				}else{//发动态点图片预览
					//预览前把加号“+”去掉
					Intent intent = new Intent(mContext,MomentsPreviewActivity.class);
					int size = pickedImages.size();
					if(size <= 9 && pickedImages.get(size - 1).getPath().equals(MomentsPublishAdapter.THIS_IS_PLUS)){//小于或者刚好为9个，并且最后一个是“+”
						ArrayList<PickedImage> subList = new ArrayList<PickedImage>();
						for (int i = 0; i < size - 1; i++) {// 数量不多，无关性能
							PickedImage item = new PickedImage(pickedImages.get(i).getPosition(), pickedImages.get(i).getPath());
							subList.add(item);
						}
						intent.putParcelableArrayListExtra(MomentsPreviewActivity.INTENT_IMAGES_TO_PREVIEW, subList);//传递需要预览的图片列表
					}else{
						intent.putParcelableArrayListExtra(MomentsPreviewActivity.INTENT_IMAGES_TO_PREVIEW, pickedImages);//传递需要预览的图片列表
					}
					intent.putExtra(MomentsPreviewActivity.INTENT_IMAGE_POSITION, position);
					startActivityForResult(intent, REQUEST_CODE_PREVIEW);
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(resultCode != RESULT_OK){
			return;
		}
		if (requestCode == REQUEST_CODE_PICK_PHOTO) {//加号“+”，新加入图片
			ArrayList<PickedImage> addImages = intent.getParcelableArrayListExtra(CustomGalleryActivity.INTENT_IMG_PATH_ARRAY);
			if(addImages == null || addImages.size() == 0){
				return;
			}
			right.setEnabled(true);
			adapter.addPickedImage(addImages);
			adapter.notifyDataSetChanged();
			return;
		}
		if(requestCode == REQUEST_CODE_PREVIEW){//预览图片后返回
			ArrayList<PickedImage> imageResult = intent.getParcelableArrayListExtra(MomentsPreviewActivity.INTENT_PREVIEW_IMAGES_FOR_RESULT);
			if(imageResult == null){
				return;
			}
			if(imageResult.size() ==0 ){//选择的图片都删完
				right.setEnabled(false);
				pickedImages.clear();
				pickedImages.add(new PickedImage(Integer.MAX_VALUE, MomentsPublishAdapter.THIS_IS_PLUS));
				adapter.notifyDataSetChanged();
				return;
			}else {
				pickedImages.clear();
				pickedImages = imageResult;
				if (pickedImages.size() < 9 && !pickedImages.get(pickedImages.size() - 1).getPath().equals(MomentsPublishAdapter.THIS_IS_PLUS)) {// 小于9张图片，添加“+”
					pickedImages.add(new PickedImage(Integer.MAX_VALUE, MomentsPublishAdapter.THIS_IS_PLUS));
				}
				adapter.setPickedImage(pickedImages);
				adapter.notifyDataSetChanged();
				return;
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void dialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("朋友圈");
		builder.setMessage("退出此次编辑？");
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setPositiveButton("退出",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
						rightOutFinishAnimation();
					}
				});
		builder.create().show();
	}

}
