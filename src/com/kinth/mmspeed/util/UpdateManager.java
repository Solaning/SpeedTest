package com.kinth.mmspeed.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.VersionInfo;
import com.kinth.mmspeed.constant.APPConstant;
import com.lidroid.xutils.util.LogUtils;

public class UpdateManager {
	public static final int MSG_SHOW_TOAST = 34;
	public static final int MSG_INSTALL_APK = 35;
	public static final int MSG_SET_PROGRESS = 36;
	public static final int MSG_SET_FILE_LENGTH = 37;
	
	private Context context;
	private VersionInfo versionInfo = null;
	private AlertDialog dialog = null;
	private ProgressDialog pd = null;
	private boolean isLowSpeedNetwork;//是否低速网络
	private Thread downloadAndInstallThread = null;
	private boolean run = false;
	private boolean hasSDCard = true;
	private File apkFile = null;

	public UpdateManager(Context context, VersionInfo versionInfo,boolean isLowSpeedNetwork) {
		this.context = context;
		this.versionInfo = versionInfo;
		this.isLowSpeedNetwork = isLowSpeedNetwork;
	}

	public void start(){
		if (versionInfo == null) {
			return;
		}
		showDownloadAlertDialog();//显示对话框
	}
	
	private Handler myHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_TOAST:
				String str = (String) msg.obj;
				if (TextUtils.isEmpty(str)) {
					LogUtils.e("handler消息参数错误：MSG_SHOW_TOAST缺少参数");
				} else {
					UtilFunc.showToastMsg(context, str);
				}
				break;
			case MSG_INSTALL_APK://安装apk
				if (apkFile != null) {
					installApk(apkFile);
				}
				break;
			case MSG_SET_PROGRESS:
				Integer value = (Integer) msg.obj;
				if (value != null && value != 0) {
					pd.setProgress(value);
				}
				break;
			default:
				break;
			}
			return false;
		}
	});

	private void showDownloadAlertDialog() {
		dialog = new AlertDialog.Builder(context).create();
		dialog.setTitle("版本升级");
		dialog.setMessage(versionInfo.getVersionDesc());
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(isLowSpeedNetwork);//低速网络可取消
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "下载安装",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						downloadAndInstallApk();//开始下载
					}
				});
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, isLowSpeedNetwork ? "取消" : versionInfo.getIsForce() == 1 ? "退出程序" : "取消",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (!isLowSpeedNetwork && versionInfo.getIsForce() == 1) {
							System.exit(0);
						}
					}
				});
		dialog.show();
	}

	private void downloadAndInstallApk() {
		pd = new ProgressDialog(context);
		pd.setCanceledOnTouchOutside(false);
		pd.setCancelable(isLowSpeedNetwork);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle("正在下载最新安装包");
		pd.setMax(100);
		pd.setButton(DialogInterface.BUTTON_NEGATIVE, isLowSpeedNetwork ? "取消" : versionInfo.getIsForce() == 1 ? "退出程序" : "取消",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (downloadAndInstallThread != null
								&& downloadAndInstallThread.isAlive()) {
							// 中断下载线程
							run = false;
						}
//						//调用cancel()方法停止下载
//						if(handler == null){
//							return;
//						}
//						handler.cancel();
//						UtilFunc.showToastMsg(context,"取消下载");
						if (!isLowSpeedNetwork && versionInfo.getIsForce() == 1) {
							System.exit(0);
						}
					}
				});
		pd.show();
		
		downloadAndInstallThread = new Thread(new Runnable() {

			@Override
			public void run() {
				run = true;
				File file = downloadApkFromServer();
				if (hasSDCard == false && file == null) {
					// 没有sdcard，下载失败
					String str = context.getResources().getString(
							R.string.error_download_for_no_sdcard);
					Message msg = myHandler.obtainMessage(
							MSG_SHOW_TOAST, str);
					myHandler.sendMessage(msg);
				}
				// else if (file == null && run == true) {
				// String str = "下载失败";
				// Message msg = myHandler.obtainMessage(
				// Global.MSG_SHOW_TOAST, str);
				// myHandler.sendMessage(msg);
				// }
				else if (file == null && run == false) {
					// 取消下载
					String str = context.getResources().getString(
							R.string.hint_cancel_downlaod);
					Message msg = myHandler.obtainMessage(
							MSG_SHOW_TOAST, str);
					myHandler.sendMessage(msg);
				} else if (file != null && hasSDCard == true) {
					// 下载成功
					apkFile = file;
					Message msg = myHandler
							.obtainMessage(MSG_INSTALL_APK);
					myHandler.sendMessage(msg);
				} else {//未知原因
					String str = context.getResources().getString(
							R.string.error_download_for_unkown_reason);
					Message msg = myHandler.obtainMessage(
							MSG_SHOW_TOAST, str);
					myHandler.sendMessage(msg);
				}
				pd.dismiss();
			}
		});
		downloadAndInstallThread.start();
	}
//		final String path = APPConstant.UPDATE_DIRECTORY + "/ydlly_"+ versionInfo.getVersionNo() +".apk";
//		HttpUtils http = new HttpUtils();
//		handler = http.download(versionInfo.getUrl(),
//			path,
//		    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
//		    false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
//		    new RequestCallBack<File>() {
//
//		        @Override
//		        public void onStart() {
//		        	
//		        }
//
//		        @Override
//		        public void onLoading(long total, long current, boolean isUploading) {
////		            testTextView.setText(current + "/" + total);
//		        	Log.e("current*100.0f / total",""+(current*100.0f / total));
//		        	pd.setProgress((int) (current*100.0f / total));
//		        }
//
//		        @Override
//		        public void onSuccess(ResponseInfo<File> responseInfo) {
////		            testTextView.setText("downloaded:" + responseInfo.result.getPath());
//		        	installApk(new File(path));
//		        }
//
//
//		        @Override
//		        public void onFailure(HttpException error, String msg) {
//		        	UtilFunc.showToastMsg(context,"下载失败:"+msg);
//		        }
//		});

	private File downloadApkFromServer() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			hasSDCard = true;
			URL url = null;
			HttpURLConnection conn = null;
			InputStream is = null;
			FileOutputStream fos = null;
			BufferedInputStream bis = null;
			File file = null;
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			boolean ok = true;
			try {
				url = new URL(versionInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				// 设置进度条的最大值
				int length = conn.getContentLength();
				if (length < -1) {//获取长度失败-->未知大小如何办
					length = Integer.MAX_VALUE;
				}
				is = conn.getInputStream();
				bis = new BufferedInputStream(is);
				file = new File(APPConstant.UPDATE_DIRECTORY + "/ydlly_"+ versionInfo.getVersionNo() +".apk");
				if(!file.exists()){
					FileUtil.createFile(file, true);
				}
				fos = new FileOutputStream(file, false);
				while ((len = bis.read(buffer)) != -1 && run == true) {
					fos.write(buffer, 0, len);
					total += len;
					// 显示下载进度
					Message msg2 = myHandler.obtainMessage(
							MSG_SET_PROGRESS, (int)(total*100.0f / length));
					myHandler.sendMessage(msg2);
				}
				// 中途退出下载线程
				if (run == false) {
					ok = false;
				}
			} catch (MalformedURLException e) {
				ok = false;
				e.printStackTrace();
				FileOperation.writeFileAppend(UtilFunc.getCurrentTime()
						+ UtilFunc.getExceptionString(e));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ok = false;
				e.printStackTrace();
				FileOperation.writeFileAppend(UtilFunc.getCurrentTime()
						+ UtilFunc.getExceptionString(e));
			} finally {
				try {
					if (is != null) {
						is.close();
						is = null;
					}
					if (bis != null) {
						bis.close();
						bis = null;
					}
					if (fos != null) {
						fos.close();
						fos = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
					FileOperation.writeFileAppend(UtilFunc.getCurrentTime()
							+ UtilFunc.getExceptionString(e));
				}
				// 若异常，并且文件存在，删除它
				if (ok == false && file != null && file.exists()) {
					file.delete();
					file = null;
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			}
			return file;
		} else {
			LogUtils.e("无法识别存储卡sdcard");
			hasSDCard = false;
			return null;
		}
	}

	private void installApk(File file) {
		if (file == null) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
}
