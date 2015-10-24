package com.kinth.mmspeed.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;

public class FileUtil extends Activity{
	private
	 /** 
     * 一、私有文件夹下的文件存取（/data/data/包名/files） 
     *  
     * @param fileName 
     * @param message 
     * can only set one of public / protected / private
     */  
	 void writeFileData(String fileName, String message) {  
        try {  
            FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);  
            byte[] bytes = message.getBytes();  
            fout.write(bytes);  
            fout.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * //读文件在./data/data/包名/files/下面 
     *  
     * @param fileName 
     * @return 
     */  
    public String readFileData(String fileName) {  
        String res = "";  
        try {  
            FileInputStream fin = openFileInput(fileName);  
            int length = fin.available();  
            byte[] buffer = new byte[length];  
            fin.read(buffer);  
            res = EncodingUtils.getString(buffer, "UTF-8");  
            fin.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return res;  
    }  
  
    /** 
     * 写， 读sdcard目录上的文件，要用FileOutputStream， 不能用openFileOutput 
     * 不同点：openFileOutput是在raw里编译过的，FileOutputStream是任何文件都可以 
     * @param fileName 
     * @param message 
     */  
    // 写在/mnt/sdcard/目录下面的文件   
    public void writeFileSdcard(String fileName, String message) {  
  
        try {  
  
            // FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);   
  
            FileOutputStream fout = new FileOutputStream(fileName);  
  
            byte[] bytes = message.getBytes();  
  
            fout.write(bytes);  
  
            fout.close();  
  
        }  
  
        catch (Exception e) {  
  
            e.printStackTrace();  
  
        }  
  
    }  
  
    // 读在/mnt/sdcard/目录下面的文件   
  
    public String readFileSdcard(String fileName) {  
  
        String res = "";  
  
        try {  
  
            FileInputStream fin = new FileInputStream(fileName);  
  
            int length = fin.available();  
  
            byte[] buffer = new byte[length];  
  
            fin.read(buffer);  
  
            res = EncodingUtils.getString(buffer, "UTF-8");  
  
            fin.close();  
  
        }  
  
        catch (Exception e) {  
  
            e.printStackTrace();  
  
        }  
  
        return res;  
  
    }  
  
  
    /** 
     * 二、从resource中的raw文件夹中获取文件并读取数据（资源文件只能读不能写） 
     * 
     * @param fileInRaw 
     * @return 
     */  
    public String readFromRaw(int fileInRaw) {  
        String res = "";  
        try {  
            InputStream in = getResources().openRawResource(fileInRaw);  
            int length = in.available();  
            byte[] buffer = new byte[length];  
            in.read(buffer);  
            res = EncodingUtils.getString(buffer, "GBK");  
            // res = new String(buffer,"GBK");   
            in.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return res;  
    }  
  
    /** 
     * 三、从asset中获取文件并读取数据（资源文件只能读不能写） 
     *  
     * @param fileName 
     * @return 
     */  
    public String readFromAsset(String fileName) {  
        String res = "";  
        try {  
            InputStream in = getResources().getAssets().open(fileName);  
            int length = in.available();  
            byte[] buffer = new byte[length];  
            in.read(buffer);  
            res = EncodingUtils.getString(buffer, "UTF-8");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return res;  
    }  
  
	/**
	 * 创建文件
	 * 
	 * @param file
	 * @param isFile
	 */
	public static void createFile(File file, boolean isFile) {
		if (!file.exists()) {// 如果文件不存在
			if (!file.getParentFile().exists()) {// 如果文件父目录不存在
				createFile(file.getParentFile(), false);
			} else {// 存在文件父目录
				if (isFile) {// 创建文件
					try {
						file.createNewFile();// 创建新文件
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					file.mkdir();// 创建目录
				}
			}
		}
	}

	/**
	 * 拷贝文件
	 * @param source
	 * @param target
	 */
	public static void nioTransferCopy(File source, File target) {  
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {  
            inStream = new FileInputStream(source);  
            outStream = new FileOutputStream(target);  
            in = inStream.getChannel();  
            out = outStream.getChannel();  
            in.transferTo(0, in.size(), out);  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {
        	try {
				inStream.close();
				in.close();
	        	outStream.close();
	        	out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }  
    }  
}
