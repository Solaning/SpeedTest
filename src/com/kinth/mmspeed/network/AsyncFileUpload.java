package com.kinth.mmspeed.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kinth.mmspeed.constant.APPConstant;

public class AsyncFileUpload {

	public static String uploadFile(String filepath) {
		String rsltURL = "";
		try {
			URL url = new URL(APPConstant.UPLOAD_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; ");
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			File file = new File(filepath);
			StringBuilder sb = new StringBuilder();
			byte[] data = sb.toString().getBytes();
			out.write(data);
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();
			out.flush();
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String photourl = "";
			String line = "";
			while ((line = reader.readLine()) != null) {
				photourl += line;
			}
			if (photourl.equals("")) {
			} else {
				rsltURL = photourl;
				Log.i("AsyncFileUpload", photourl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			rsltURL = "";
		}
		return rsltURL;
	}

	public static void asynFileUploadToServer(final String fileName,
			final FileCallback fileCallback) {
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				fileCallback.fileUploadCallback((String) message.obj);
			}
		};
		new Thread() {
			@Override
			public void run() {
				String url = uploadFile(fileName);
//				Log.i("AsyncFileUpload", url);
				Message message = handler.obtainMessage(0, url);
				handler.sendMessage(message);
			}
		}.start();
	}

	public interface FileCallback {
		public void fileUploadCallback(String fileUrl);
	}
}
