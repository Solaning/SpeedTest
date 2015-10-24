package com.kinth.mmspeed.test;

import java.util.List;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.R.layout;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Demo描述: 判断应用从后台回到前台 即按下Home键后重新进入到应用
 */
public class TestActivity extends Activity {
	private final String TAG = "TestActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.whats6);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		boolean isCurrentRunningForeground = prefs.getBoolean(
				"isCurrentRunningForeground", false);
		if (!isCurrentRunningForeground) {
			Toast.makeText(TestActivity.this, "从后台回来", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			Thread.sleep(1000);
			new Thread() {
				public void run() {
					boolean isCurrentRunningForeground = isRunningForeground();
					SharedPreferences prefs = PreferenceManager
							.getDefaultSharedPreferences(getApplicationContext());
					Editor editor = prefs.edit();
					editor.putBoolean("isCurrentRunningForeground",
							isCurrentRunningForeground);
					editor.commit();
				};
			}.start();
		} catch (Exception e) {
		}
	}

	public boolean isRunningForeground() {
		String packageName = getPackageName(this);
		String topActivityClassName = getTopActivityName(this);
		System.out.println("packageName=" + packageName
				+ ",topActivityClassName=" + topActivityClassName);
		if (packageName != null && topActivityClassName != null
				&& topActivityClassName.startsWith(packageName)) {
			System.out.println("---> isRunningForeGround");
			return true;
		} else {
			System.out.println("---> isRunningBackGround");
			return false;
		}
	}

	public String getTopActivityName(Context context) {
		String topActivityClassName = null;
		ActivityManager activityManager = (ActivityManager) (context
				.getSystemService(android.content.Context.ACTIVITY_SERVICE));
		// android.app.ActivityManager.getRunningTasks(int maxNum)
		// int maxNum--->The maximum number of entries to return in the list
		// 即最多取得的运行中的任务信息(RunningTaskInfo)数量

		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

		List runningTaskInfos = activityManager.getRunningTasks(1);

		if (runningTaskInfos != null) {
			ComponentName f = activityManager.getRunningTasks(1).get(0).topActivity;
			topActivityClassName = f.getClassName();
		}
		// 按下Home键盘后 topActivityClassName=com.android.launcher2.Launcher
		return topActivityClassName;
	}

	public String getPackageName(Context context) {
		String packageName = context.getPackageName();
		return packageName;
	}
}
