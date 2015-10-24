package com.kinth.mmspeed.friend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.kinth.mmspeed.R;

/**
 * 拍照工具类 调用该类须从在activity的onActivityResult获取返回数据
 * 
 */
public class PictureActivityUtil {
	private static final String TAG = "PictureActivityUtil";
	/** 用来标识请求照相功能的activity */
	public static final int CAMERA_WITH_DATA = 168;
	/** 用来标识请求gallery的activity */
	public static final int PHOTO_PICKED_WITH_DATA = CAMERA_WITH_DATA + 1;
	/** 图片裁剪 */
	public static final int PHOTO_CROP = PHOTO_PICKED_WITH_DATA + 1;
	/** 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera/");

	private static File mCurrentPhotoFile;// 照相机拍照得到的图片

	private static int cut_w, cut_h;// 裁剪图片宽度,高度

	/**
	 * 得到本地图片路径
	 * 
	 * @return
	 */
	public static File getmCurrentPhotoFile() {
		if (mCurrentPhotoFile == null) {
			if (!PHOTO_DIR.exists())
				PHOTO_DIR.mkdirs();// 创建照片的存储目录
			mCurrentPhotoFile = new File(PHOTO_DIR, "temp.jpg");
			if (!mCurrentPhotoFile.exists())
				try {
					mCurrentPhotoFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return mCurrentPhotoFile;
	}

	/**
	 * 设置裁剪图片尺寸
	 * 
	 * @param w
	 * @param h
	 */
	public static void InitCutSize(int w, int h) {
		cut_w = w;
		cut_h = h;
	}

	/**
	 * 开始启动照片选择框
	 * 
	 * @param context
	 * 
	 */
	public static void doPickPhotoAction(final Activity context) {
		final Context dialogContext = new ContextThemeWrapper(context,
				android.R.style.Theme_Light);
		String[] choices;
		choices = new String[2];
		choices[0] = context.getString(R.string.take_photo); // 拍照
		choices[1] = context.getString(R.string.pick_photo); // 从相册中选择
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle("添加图片");
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String status = Environment.getExternalStorageState();
						if (!status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
							Toast.makeText(dialogContext,
									"没有找到SD卡或者正在使用请关闭usb连接模式",
									Toast.LENGTH_LONG).show();
							return;
						}
						switch (which) {
						case 0:
							doTakePhoto(context);// 用户点击了从照相机获取
							break;
						case 1:
							doPickPhotoFromGallery(context);// 从相册中去获取
							break;
						}
					}
				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

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
	private static void doTakePhoto(Activity context) {
		try {
			if (!PHOTO_DIR.exists())
				PHOTO_DIR.mkdirs();// 创建照片的存储目录
			mCurrentPhotoFile = new File(PHOTO_DIR, "temp.jpg");// 给新照的照片文件命名
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			context.startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (Exception e) {
			Toast.makeText(context, R.string.photoPickerNotFoundText,
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 请求Gallery相册程序
	 * 
	 * @param context
	 * @param iscrop
	 */
	private static void doPickPhotoFromGallery(Context context) {
		try {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			((Activity) context).startActivityForResult(intent,
					PHOTO_PICKED_WITH_DATA);
		} catch (Exception e) {
			Toast.makeText(context, R.string.photoPickerNotFoundText1,
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 所有图片裁剪回调 如不需要裁剪及不用调用该方法
	 * 
	 * @param context
	 * @param uri
	 *            图片资源地址
	 */
	public static void doCropPhoto(Activity context, Intent data) {
		try {
			Intent intent = new Intent("com.android.camera.action.CROP");
			Uri uri = null;
			if (data.getData() != null) {
				uri = data.getData();
			} else {
				uri = Uri.fromFile(getCameraPath(data)); // 拍照返回数据
			}
			intent.setDataAndType(uri, "image/*");
			intent.putExtra("crop", true);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", cut_w != 0 ? cut_w : 600);
			intent.putExtra("outputY", cut_h != 0 ? cut_h : 600);
			context.startActivityForResult(intent, PHOTO_CROP);
		} catch (Exception e) {
			Toast.makeText(context, R.string.photoPickerNotFoundText,
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 获取不裁剪时图片返回的路径
	 * 
	 * @param activity
	 * @param data
	 * @return path
	 */
	public static String getNoCropPath(Activity activity, Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			return getCameraPath(data).toString();// 获取拍照图片路径
		}
		String path = "";
		Uri imageuri = data.getData();
		if (imageuri != null) {
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = activity.managedQuery(imageuri, proj, null, null,
					null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.moveToFirst()) {
				path = cursor.getString(column_index);
			}
		}
		return path;
	}

	/**
	 * 裁剪后的图片路径
	 * 
	 * @return
	 */
	public static String getCropPath(Activity activity, Intent data) {

		String path = "";
		Uri imageuri = data.getData();
		if (imageuri == null) {
			String str = data.getAction();
			imageuri = Uri.parse(str);
		}
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.managedQuery(imageuri, proj, null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.moveToFirst()) {
				path = cursor.getString(column_index);
			}
		}
		if (path == "") {
			path = getCameraPath(data).toString();
		}
		return path;
	}

	/**
	 * 拍照图片路径
	 * 
	 * @return
	 */
	private static File getCameraPath(Intent data) {
		Bundle extras = data.getExtras();
		Bitmap myBitmap = (Bitmap) extras.get("data");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(
					PictureActivityUtil.getmCurrentPhotoFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		return PictureActivityUtil.getmCurrentPhotoFile();
	}
}
