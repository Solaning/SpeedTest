package com.kinth.mmspeed.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import com.kinth.mmspeed.bean.InstancySpeedInfo;
import com.kinth.mmspeed.bean.SpeedAngleArray;
import com.kinth.mmspeed.bean.ThreadInfo;
import com.kinth.mmspeed.fragment.SpeedTestMainFragment;
import com.kinth.mmspeed.hk.FormFile;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;

public class UploadManager extends Thread {
	private final static int NUMBER_OF_THREAD = 5;
	private String url;// 定义上传的网址
	private String targetFile;// 指定上传文件的保存位置
	private long preStartTime;// 上次统计的的时间
	private long preSumSize;// 上一次统计时下载的值
	private long totalUpload = 0;// 定义该线程已经上传的字节数
	private float highestSpeed;
	private Context context;
	private Handler handler;// UI线程的handler
	private boolean running = false;
	private List<SpeedAngleArray> rateList = new ArrayList<SpeedAngleArray>();
	// 后面加的
	private int retryLimit = 35; // 下载不正常时重新连接的最大次数
	private int loopTimes = 0;// 临时量，循环次数
	private int countDownTimes = 0;// 倒计时的次数20s
	private SparseArray<UploadThread> threadSparseArray;
	private List<ThreadInfo> threadList;

	public UploadManager(Context context, Handler handler, String uploadURL,
			String targetFile) {
		this.context = context;
		this.handler = handler;
		this.url = uploadURL;// 上传的url
		this.targetFile = targetFile;// 要上传的文件
	}

	@Override
	public void run() {
		super.run();
		running = true;
		totalUpload = preSumSize = 0l;// 初始化
		this.threadSparseArray = new SparseArray<UploadThread>();// 创建下载线程池
		this.threadList = new ArrayList<ThreadInfo>();
		for (int i = 0; i < NUMBER_OF_THREAD; i++) { // 开启线程进行下载
			Integer threadId = i;
			UploadThread thread = new UploadThread(this, threadId, false); // 初始化特定id的线程
			thread.setPriority(7); // 设置线程的优先级，Thread.NORM_PRIORITY
									// = 5 Thread.MIN_PRIORITY =
									// 1 Thread.MAX_PRIORITY =
									// 10
			thread.start(); // 启动线程
			this.threadSparseArray.put(threadId, thread);
			ThreadInfo threadInfo = new ThreadInfo(threadId, true);
			this.threadList.add(threadInfo);
		}
		preStartTime = System.currentTimeMillis();// 初始化第一次的时间
		while (running) { // 循环判断所有线程是否完成下载
			try {
				Thread.sleep(500);
				List<ThreadInfo> delList = new ArrayList<ThreadInfo>();// 需要删除的数据
				for (ThreadInfo threadInfo : this.threadList) {
					UploadThread uploadThread = this.threadSparseArray
							.get(threadInfo.getThreadId());// 根据id找到下载实例
					if (uploadThread != null && !uploadThread.isFinished()
							&& !uploadThread.isWorking()) { // 如果发现该线程未完成下载且失败了,重新下载
						if (threadInfo.getThreadFailCountAutoPlus() <= this.retryLimit) {
							uploadThread = new UploadThread(this,
									threadInfo.getThreadId(), true); // 重新开辟下载线程
							uploadThread.setPriority(7); // 设置下载的优先级
							uploadThread.start(); // 开始下载线程
							this.threadSparseArray.put(
									threadInfo.getThreadId(), uploadThread);
						} else { // 进入这里说明该下载线程不能正常工作且超过了重新连接的最大限制数，移除该下载线程
							uploadThread = null;
							this.threadSparseArray.remove(threadInfo
									.getThreadId());
							delList.add(threadInfo);
						}
					}
					if (uploadThread != null && uploadThread.isFinished()
							&& !uploadThread.isWorking()) {// 线程下载完成
						uploadThread = null;// 线程赋空
						this.threadSparseArray.remove(threadInfo.getThreadId());
						delList.add(threadInfo);
					}
				}
				if (delList.size() > 0) {
					this.threadList.removeAll(delList);
				}
				if (this.threadList.size() == 0) {// 所有线程下载完成
					running = false;
					Message message3 = handler
							.obtainMessage(SpeedTestMainFragment.UP_LOAD_COMPLETE);
					SingletonSharedPreferences.getInstance().setValueOfUpload(
							getAverageSpeed());// 保存下载速度
					message3.obj = UtilFunc.getSpanResult(context,
							getAverageSpeed());
					handler.sendMessage(message3);// 开始上传测速，设置平均值
				} else {
					// 计算瞬时速度和最高速度
					rateList.add(getInstancySpeed());// 保存瞬时速度值
					float speed = calcRate();// 重新计算速度值--->测速稳定
					highestSpeed = highestSpeed > speed ? highestSpeed : speed;// 保存最高速度

					Message message = handler
							.obtainMessage(SpeedTestMainFragment.INSTANCY_SPEED);
					InstancySpeedInfo instancySpeed = UtilFunc.getInstancySpan(
							context, speed);
					if (instancySpeed.getAngle() >= ApplicationController.MaxDegree) {
						instancySpeed.setAngle(ApplicationController.MaxDegree);
					}
					if (instancySpeed.getAngle() <= ApplicationController.MinDegree) {
						instancySpeed.setAngle(ApplicationController.MinDegree);
					}
					message.obj = instancySpeed;
					handler.sendMessage(message);// 直接向UI线程发送Message 更新瞬时速度

					loopTimes++;
					// 每2次循环即1000ms更新一次进度条，20s后不论怎么样都终止下载
					if (loopTimes == 2) {// 循环2次更新一次进度条
						loopTimes = 0;
						if (++countDownTimes < 10) {
							// 发送消息通知界面更新进度条
							Message msg = handler.obtainMessage(
									SpeedTestMainFragment.UPDATE_PROGRESS_BAR,
									(100 * countDownTimes) / 10, 0);
							handler.sendMessage(msg);
						} else {// 超出时间，强制停止
							running = false;// 停止所有下载线程
							Message message2 = handler
									.obtainMessage(SpeedTestMainFragment.UP_LOAD_COMPLETE);
							SingletonSharedPreferences.getInstance()
									.setValueOfUpload(getAverageSpeed());// 保存下载速度
							message2.obj = UtilFunc.getSpanResult(context,
									getAverageSpeed());
							handler.sendMessage(message2);// 开始上传测速，设置平均值
						}
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 完成后的平均下载速度，这里是所有下载完成后的大小，，不是定时的值
	 */
	public float getAverageSpeed() {
		return highestSpeed;
	}

	/**
	 * 获取瞬时上传速度值
	 */
	public SpeedAngleArray getInstancySpeed() {
		long startTime = System.currentTimeMillis();
		long gapSize = totalUpload - preSumSize;// 跟上次比较的差值
		preSumSize = totalUpload;// 保存这次统计的值
		long gapTime = startTime - preStartTime;
		preStartTime = startTime;
		SpeedAngleArray speedAngleArray = new SpeedAngleArray();
		speedAngleArray.setSpeed(Math.abs(gapSize / (gapTime * 1.024f)));// 除以1.024，abs保证没有负值
		speedAngleArray.setAngle(SingletonSharedPreferences.getInstance()
				.getUnitMode() ? UtilFunc.Value2Angle2(speedAngleArray
				.getSpeed())
				: UtilFunc.Value2Angle1(speedAngleArray.getSpeed()));// 换算的角度值
		return speedAngleArray;
	}

	private float calcRate() {
		float totalRate = 0;// 速度
		int j = 0;
		for (int i = rateList.size() - 1; i > 0 && j < 10; i--, j++) {
			totalRate += rateList.get(i).getSpeed();
		}
		if (j == 0) {
			return totalRate;
		}
		return totalRate / 10;
	}

	/**
	 * 更新指定线程在某时间片段里下载的文件大小和最后下载的位置
	 * 
	 * @param size
	 *            时间片段里下载的文件大小
	 */
	private synchronized void update(long size) throws IOException { // 使用同步关键字解决并发访问问题
		this.totalUpload += size; // 把实时下载的长度加入到总下载长度中
	}

	private synchronized void copyFile(File target) {
		if (!target.exists()) {// 再一次判定
			target.getParentFile().mkdirs();
			UtilFunc.CopyAssets(context, "upload_file", targetFile);
			preStartTime = System.currentTimeMillis();// 重设时间
		}
	}

	/**
	 * 上传线程
	 * 
	 * @author Sola
	 * 
	 */
	private class UploadThread extends Thread {
		private boolean working; // 该线程有否正常工作的标志
		private boolean finished; // 该线程是否结束的标志
		private int threadId = -1; // 初始化线程id设置
		private boolean retry; // 该线程是否属于再次启动的
		private UploadManager manager;// 管理线程实例

		private File binaryFile;

		public UploadThread(UploadManager manager, int threadId, boolean retry) {
			this.manager = manager;
			this.threadId = threadId;
			this.retry = retry;
		}

		@Override
		public void run() {
			super.run();
			this.working = true;
			this.finished = false;
			binaryFile = new File(targetFile);
			if (!binaryFile.exists()) {// 文件不存在，拷贝过去
				manager.copyFile(binaryFile);
			}
			FormFile formFile = new FormFile(binaryFile.getName(), binaryFile,
					"document", "text/plain");// "document"为控件的名称,"text/plain"为文件的mimetype
			uploadFile(url, null,formFile);

			
//			String param = "value";
//			binaryFile = new File(targetFile);
//			if (!binaryFile.exists()) {// 文件不存在，拷贝过去
//				manager.copyFile(binaryFile);
//			}
//			binaryFile.length();// 上传文件的大小
//			String boundary = Long.toHexString(System.currentTimeMillis()); // Just
//			// generate some unique random value.
//			String CRLF = "\r\n"; // Line separator required by
//									// multipart/form-data.
//			HttpURLConnection connection = null;
//			PrintWriter writer = null;
//			try {
//				connection = (HttpURLConnection) new URL(url).openConnection();
//				connection.setRequestProperty("Content-Type",
//						"multipart/form-data; boundary=" + boundary);
//				connection.setUseCaches(false);
//				connection.setChunkedStreamingMode(1024); // 告诉HttpUrlConnection,我们需要采用流方式上传数据，无需本地缓存数据。HttpUrlConnection默认是将所有数据读到本地缓存，然后再发送给服务器，这样上传大文件时就会导致内存溢出。
//				connection.setDoOutput(true);
//				OutputStream output = connection.getOutputStream();
//				writer = new PrintWriter(
//						new OutputStreamWriter(output, "UTF-8"), true);
//				// true =autoFlush,important!
//				// Send normal param.
//				writer.append("--" + boundary).append(CRLF);
//				writer.append("Content-Disposition: form-data; name=\"param\"")
//						.append(CRLF);
//				writer.append("Content-Type: text/plain; charset=" + "UTF-8")
//						.append(CRLF);
//				writer.append(CRLF);
//				writer.append(param).append(CRLF).flush();
//				// Send binary file.
//				writer.append("--" + boundary).append(CRLF);
//				writer.append(
//						"Content-Disposition: form-data; name=\"binaryFile\"; filename=\""
//								+ binaryFile.getName() + "\"").append(CRLF);
//				writer.append(
//						"Content-Type: "
//								+ URLConnection
//										.guessContentTypeFromName(binaryFile
//												.getName())).append(CRLF);
//				writer.append("Content-Transfer-Encoding: binary").append(CRLF);
//				writer.append(CRLF).flush();
//				InputStream input = new FileInputStream(binaryFile);
//				byte[] buffer = new byte[5120];
//				int length = 0;
//				while (running == true && (length = input.read(buffer)) != -1) {
//					output.write(buffer, 0, length);
//					this.manager.update(length);// 统计总的下载
//				}
//				output.flush(); // Important! Output cannot be closed. Close
//								// of
//								// writer will close output as well.
//				if (running) {
//					this.finished = true; // 设置完成标志为true，无论是下载完成还是用户主动中断下载
//				}
//				this.working = false; // 线程已经不需要工作了
//				input.close();
//				writer.append(CRLF).flush(); // CRLF is important! It indicates
//												// end
//												// of binary boundary.
//				// End of multipart/form-data.
//				writer.append("--" + boundary + "--").append(CRLF);
//			} catch (Exception e2) {
//				this.working = false;
//				e2.printStackTrace();
//			} finally {
//				if (writer != null)
//					writer.close();
//				if (connection != null) {
//					connection.disconnect();
//					connection = null;
//				}
//				stopThread(this);
//			}
		}

		// 自动关闭线程
		private void stopThread(Thread theThread) {
			if (theThread != null) {
				theThread = null;
			}
		}

		/**
		 * 上传线程是否正常工作中
		 * 
		 * @return true为线程工作正常，否则为false
		 */
		public boolean isWorking() {
			return working;
		}

		/**
		 * 上传线程是否已经结束
		 * 
		 * @return true为线程已结束，否则为false
		 */
		public boolean isFinished() {
			return this.finished;
		}

		// ------------------------TODO----------------------------
		/**
		 * 直接通过HTTP协议提交数据到服务器,实现如下面表单提交功能: <FORM METHOD=POST
		 * ACTION="http://192.168.0.200:8080/ssi/fileload/test.do"
		 * enctype="multipart/form-data"> <INPUT TYPE="text" NAME="name"> <INPUT
		 * TYPE="text" NAME="id"> <input type="file" name="imagefile"/> <input
		 * type="file" name="zip"/> </FORM>
		 * 
		 * @param path
		 *            上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，
		 *            你可以使用http://www.itcast.cn或http://192.168.1.10:8080这样的路径测试)
		 * @param params
		 *            请求参数 key为参数名,value为参数值
		 * @param file
		 *            上传文件
		 */
		public boolean uploadFile(String path, Map<String, String> params,
				FormFile files) {
			final String BOUNDARY = "---------------------------7da2137580612"; // 数据分隔线
			final String endline = "--" + BOUNDARY + "--\r\n";// 数据结束标志

			int fileDataLength = 0;
			if (files != null) {
				// 得到文件类型数据的总长度
				StringBuilder fileExplain = new StringBuilder();
				fileExplain.append("--");
				fileExplain.append(BOUNDARY);
				fileExplain.append("\r\n");
				fileExplain.append("Content-Disposition: form-data;name=\""
						+ files.getParameterName() + "\";filename=\""
						+ files.getFilname() + "\"\r\n");
				fileExplain.append("Content-Type: " + files.getContentType()
						+ "\r\n\r\n");
				fileExplain.append("\r\n");
				fileDataLength += fileExplain.length();
				if (files.getInStream() != null) {
					fileDataLength += files.getFile().length();
				} else {
					fileDataLength += files.getData().length;
				}
			}
			StringBuilder textEntity = new StringBuilder();
			if (params != null && !params.isEmpty()) {
				for (Map.Entry<String, String> entry : params.entrySet()) {// 构造文本类型参数的实体数据
					textEntity.append("--");
					textEntity.append(BOUNDARY);
					textEntity.append("\r\n");
					textEntity.append("Content-Disposition: form-data; name=\""
							+ entry.getKey() + "\"\r\n\r\n");
					textEntity.append(entry.getValue());
					textEntity.append("\r\n");
				}
			}
			// 计算传输给服务器的实体数据总长度
			int dataLength = textEntity.toString().getBytes().length
					+ fileDataLength + endline.getBytes().length;
			try {
				URL url = new URL(path);
				int port = url.getPort() == -1 ? 80 : url.getPort();
				Socket socket = new Socket(
						InetAddress.getByName(url.getHost()), port);
				OutputStream outStream = socket.getOutputStream();
				// 下面完成HTTP请求头的发送
				String requestmethod = "POST " + url.getPath()
						+ " HTTP/1.1\r\n";
				outStream.write(requestmethod.getBytes());
				String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
				outStream.write(accept.getBytes());
				String language = "Accept-Language: zh-CN\r\n";
				outStream.write(language.getBytes());
				String contenttype = "Content-Type: multipart/form-data; boundary="
						+ BOUNDARY + "\r\n";
				outStream.write(contenttype.getBytes());
				String contentlength = "Content-Length: " + dataLength + "\r\n";
				outStream.write(contentlength.getBytes());
				String alive = "Connection: Keep-Alive\r\n";
				outStream.write(alive.getBytes());
				String host = "Host: " + url.getHost() + ":" + port + "\r\n";
				outStream.write(host.getBytes());
				// 写完HTTP请求头后根据HTTP协议再写一个回车换行
				outStream.write("\r\n".getBytes());
				// 把所有文本类型的实体数据发送出来
				outStream.write(textEntity.toString().getBytes());
				// 把所有文件类型的实体数据发送出来
				if (files != null) {
					StringBuilder fileEntity = new StringBuilder();
					fileEntity.append("--");
					fileEntity.append(BOUNDARY);
					fileEntity.append("\r\n");
					fileEntity.append("Content-Disposition: form-data;name=\""
							+ files.getParameterName() + "\";filename=\""
							+ files.getFilname() + "\"\r\n");
					fileEntity.append("Content-Type: " + files.getContentType()
							+ "\r\n\r\n");
					outStream.write(fileEntity.toString().getBytes());
//					this.manager.update(fileEntity.toString().getBytes().length);
					if (files.getInStream() != null) {
						byte[] buffer = new byte[1024];
						int len = 0;
						while (running
								&& (len = files.getInStream().read(buffer, 0,
										1024)) != -1) {
							outStream.write(buffer, 0, len);
							this.manager.update(len);
						}
						files.getInStream().close();
						if (running) {
							this.finished = true; // 设置完成标志为true，无论是下载完成还是用户主动中断下载
						}
						this.working = false; // 线程已经不需要工作了
					} else {
						outStream.write(files.getData(), 0,
								files.getData().length);
					}
					outStream.write("\r\n".getBytes());
				}
				// 下面发送数据结束标志，表示数据已经结束
				outStream.write(endline.getBytes());

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				if (reader.readLine().indexOf("200") == -1) {// 读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
					return false;
				}
				outStream.flush();
				outStream.close();
				reader.close();
				socket.close();
			} catch (MalformedURLException e) {
				this.working = false;
				e.printStackTrace();
			} catch (UnknownHostException e) {
				this.working = false;
				e.printStackTrace();
			} catch (IOException e) {
				this.working = false;
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				stopThread(this);
			}
			return true;
		}

	}

	/**
	 * 停止线程
	 */
	public void stopRun() {
		// 中断线程
		running = false;
	}
}
