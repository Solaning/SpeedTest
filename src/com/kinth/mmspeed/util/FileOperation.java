package com.kinth.mmspeed.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.util.EncodingUtils;

import android.os.Environment;
import android.util.Log;


public class FileOperation {
	public static final String TAG = "FileOperation";
	public static final String FILE_FOLDER = "/" + "appLog"
			+ "/";
	public static final String FILE_NAME = "exception"
			+ UtilFunc.getCurrentDate() + ".log";
	public static final String SDCARD_DIRECTORY = Environment
			.getExternalStorageDirectory().getPath();
	public static String fileName;
	public static String fileFolder;

	public static boolean writeFileAppend(String message) {
		if (!ApplicationController.isTestingStage) {
			return false;
		}

		// �ж�sdcard�Ƿ�����򷵻�
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.w(TAG, "writeFileAppend:sdcard�޷���д");
			return false;
		}

		fileName = SDCARD_DIRECTORY + FILE_FOLDER + FILE_NAME;
		fileFolder = SDCARD_DIRECTORY + FILE_FOLDER;
		File folder = new File(fileFolder);
		FileOutputStream fout = null;
		if (folder != null && !folder.exists()) {
			if (!folder.mkdir() && !folder.isDirectory()) {
				Log.w(TAG, "writeFileAppend:make dire failed");
				return false;
			}
		}

		boolean res = true;
		try {
			fout = new FileOutputStream(fileName, true);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.w(TAG, "writeFileAppend:" + fileName
					+ " can't be opened for writing");
			res = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.w(TAG, "writeFileAppend:problem with writing data info "
					+ fileName);
			res = false;
		} finally {
			folder = null;
			fout = null;
		}
		return res;
	}

	public static boolean isSDCardAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static boolean isFileExist(String fileName) {
		if (!isSDCardAvailable()) {
			return false;
		}
		boolean rtn = true;
		File file = new File(fileName);
		if (!file.exists()) {
			rtn = false;
		}
		return rtn;
	}

	/**
	 * ��messageд��ָ�����ļ��ĺ���
	 * 
	 * @param fileFolder
	 *            �ļ����ڵ�Ŀ¼
	 * @param fileFullPath
	 *            �ļ������·��������Ŀ¼���ļ���
	 * @param message
	 * @return
	 */
	public static boolean writeFileAppend(String fileFolder,
			String fileFullPath, String message) {
		// �ж�sdcard�Ƿ�����򷵻�
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.w(TAG, "writeFileAppend:sdcard�޷���д");
			return false;
		}

		File folder = new File(fileFolder);
		FileOutputStream fout = null;
		if (folder != null && !folder.exists()) {
			if (!folder.mkdir() && !folder.isDirectory()) {
				Log.w(TAG, "writeFileAppend:make dire failed");
				return false;
			}
		}

		boolean res = true;
		try {
			fout = new FileOutputStream(fileFullPath, true);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.w(TAG, "writeFileAppend:" + fileFullPath
					+ " can't be opened for writing");
			res = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.w(TAG, "writeFileAppend:problem with writing data info "
					+ fileFullPath);
			res = false;
		} finally {
			folder = null;
			fout = null;
		}
		return res;
	}

	public static String readFileSdcard() {
		String res = "";
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				&& !Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED_READ_ONLY)) {
			Log.w(TAG, "writeFileAppend:sdcard�޷���ȡ");
			return res;
		}

		fileName = SDCARD_DIRECTORY + FILE_FOLDER + FILE_NAME;
		File file = null;
		FileInputStream fin = null;
		file = new File(fileName);
		if (!file.exists()) {
			return res;
		}
		try {
			fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			file = null;
			fin = null;
		}
		return res;
	}

	public static boolean deleteFileFromSdcard(String fileName) {
		boolean res = true;
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				&& !Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED_READ_ONLY)) {
			//�洢��������
			UtilFunc.logError(TAG, "sd卡不可用");
			res = false;
			return res;
		}

		File file = null;
		file = new File(fileName);
		if (file.exists()) {
			file.delete();
			if (file != null && file.exists()) {
				res = false;
			}
		}
		return res;
	}
}
