package com.kinth.mmspeed.hk;

import android.content.Context;

import com.kinth.mmspeed.ui.CustomProgressDialog;

/**
 * 菊花转
 * @author Sola
 *
 */
public class CustomProgressDialogUtil {
	private static CustomProgressDialog progressDialog = null;

	public static void startProgressDialog(Context mContext,String message,boolean isCancelable) {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(mContext);
		}
		progressDialog.setMessage(message);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(isCancelable);
		progressDialog.show();
	}

	/**
	 * 正常结束返回true，否则false
	 * @return
	 */
	public static boolean stopProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
			return true;
		}
		return false;
	}
}
