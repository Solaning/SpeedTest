package com.kinth.mmspeed.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.kinth.mmspeed.bean.InstancySpeedInfo;
import com.kinth.mmspeed.bean.SpeedAngleArray;
import com.kinth.mmspeed.bean.ThreadInfo;
import com.kinth.mmspeed.fragment.SpeedTestMainFragment;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;

/**
 * 下载管理线程
 * 
 * @author Sola
 * 
 */
public class DownloadManager extends Thread {
	private static final String TAG = "DownloadManager";
	private static final int NUMBER_OF_THREAD = 6;
	private Context context;
	private boolean running = false;// 下载是否继续的标志位
	private String downloadURL;// 下载地址
	private Handler handler; // UI线程的handler
	private float highestSpeed;// 下载的最高速度
	private int loopTimes = 0;// 临时量，循环次数
	private int countDownTimes = 0;// 倒计时的次数20s
	private long totalDownload;// 总的下载
	private long preStartTime;// 上次统计的的时间
	private long preSumSize = 0;// 上一次统计时下载的值
	private int retryLimit = 35; // 下载不正常时重新连接的最大次数
	private List<SpeedAngleArray> rateList = new ArrayList<SpeedAngleArray>();
	// 后面加的
	private SparseArray<DownloadThread> threadSparseArray;
	private List<ThreadInfo> threadList;

	public DownloadManager(Context context, Handler handler, String url) {
		this.context = context;
		this.handler = handler;
		this.downloadURL = url;
	}

	/**
	 * 获得允许下载线程的最大重新连接次数
	 * 
	 * @return 最大重新连接次数
	 */
	public int getRetryLimit() {
		return retryLimit;
	}

	/**
	 * 设置允许下载线程的最大重新连接次数
	 * 
	 * @param times
	 *            要设定的值
	 */
	public void setRetryLimit(int times) {
		this.retryLimit = times;
	}

	@Override
	public void run() {
		super.run();
		running = true;
		totalDownload = preSumSize = 0l;// 初始化
		this.threadSparseArray = new SparseArray<DownloadThread>();// 创建下载线程池
		this.threadList = new ArrayList<ThreadInfo>();
		for (int i = 0; i < NUMBER_OF_THREAD; i++) { // 开启线程进行下载
			Integer threadId = i;
			DownloadThread thread = new DownloadThread(this, threadId, false); // 初始化特定id的线程
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
				List<ThreadInfo> delList = new ArrayList<ThreadInfo>();// 需要删除的数据，不能在遍历的时候删除Collection的长度
				for (ThreadInfo threadInfo : this.threadList) {
					DownloadThread downloadThread = this.threadSparseArray
							.get(threadInfo.getThreadId());// 根据id找到下载实例
					if (downloadThread != null && !downloadThread.isFinished()
							&& !downloadThread.isWorking()) { // 如果发现该线程未完成下载且失败了,重新下载
						if (threadInfo.getThreadFailCountAutoPlus() <= this.retryLimit) {
							downloadThread = new DownloadThread(this,
									threadInfo.getThreadId(), true); // 重新开辟下载线程
							downloadThread.setPriority(7); // 设置下载的优先级
							downloadThread.start(); // 开始下载线程
							this.threadSparseArray.put(
									threadInfo.getThreadId(), downloadThread);
						} else { // 进入这里说明该下载线程不能正常工作且超过了重新连接的最大限制数，移除该下载线程
							downloadThread = null;
							this.threadSparseArray.remove(threadInfo
									.getThreadId());
							delList.add(threadInfo);
						}
					}
					if (downloadThread != null && downloadThread.isFinished()
							&& !downloadThread.isWorking()) {// 线程下载完成
						downloadThread = null;// 线程赋空
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
							.obtainMessage(SpeedTestMainFragment.DOWN_LOAD_COMPLETE);
					SingletonSharedPreferences.getInstance()
							.setValueOfDownload(getAverageSpeed());// 保存下载速度
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
									.obtainMessage(SpeedTestMainFragment.DOWN_LOAD_COMPLETE);
							SingletonSharedPreferences.getInstance()
									.setValueOfDownload(getAverageSpeed());// 保存下载速度
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
	 * 计算瞬时下载速度值
	 */
	public SpeedAngleArray getInstancySpeed() {
		long startTime = System.currentTimeMillis();
		long gapSize = totalDownload - preSumSize;// 跟上次比较的差值
		preSumSize = totalDownload;// 保存这次统计的值
		long gapTime = startTime - preStartTime;
		preStartTime = startTime;
		SpeedAngleArray speedAngleArray = new SpeedAngleArray();
		speedAngleArray.setSpeed(Math.abs(gapSize / (gapTime * 1.024f)));// 除以1.024，abs保证没有负值
																			// byte/ms
																			// --->kB/s
																			// 计算出来的是kB/s
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
	 * 完成后的平均下载速度，这里是所有下载完成后的大小，，不是定时的值
	 */
	public float getAverageSpeed() {
		return highestSpeed;
	}

	/**
	 * 更新指定线程在某时间片段里下载的文件大小和最后下载的位置
	 * 
	 * @param size
	 *            时间片段里下载的文件大小
	 */
	private synchronized void update(long size) throws IOException { // 使用同步关键字解决并发访问问题
		this.totalDownload += size; // 把实时下载的长度加入到总下载长度中
	}

	/**
	 * 下载线程
	 */
	class DownloadThread extends Thread {
		private boolean working; // 该线程有否正常工作的标志
		private boolean finished; // 该线程是否结束的标志
		private int threadId = -1; // 初始化线程id设置
		private boolean retry; // 该线程是否属于再次启动的
		private DownloadManager manager;// 管理线程实例

		/**
		 * 初始化DownloadThread对象
		 * 
		 * @param downloader
		 *            FileDownloader对象
		 * @param downloadedSize
		 *            整个文件目前已经下载的大小
		 * @param threadId
		 *            线程的ID
		 */
		public DownloadThread(DownloadManager manager, int threadId,
				boolean retry) {
			this.manager = manager;
			this.threadId = threadId;
			this.retry = retry;
		}

		@Override
		public void run() {// 启动去下载文件
			super.run();
			this.working = true;
			this.finished = false;
			downloadFileFromServer();
		}

		private void downloadFileFromServer() {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				URL url = null;
				HttpURLConnection conn = null;
				InputStream is = null;// 输入流
				FileOutputStream fos = null;// 输出流
				BufferedInputStream bis = null;
				File file = null;
				byte[] buffer = new byte[8192];// 下载缓冲区大小 1024*8
				int len = 0;// 设置每次读取的数据量
				try {
					url = new URL(downloadURL);
					conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET"); // 设置请求的方法为GET
					conn.setRequestProperty(
							"Accept",
							"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"); // 设置客户端可以接受的返回数据类型
					conn.setRequestProperty("Accept-Language", "zh-CN"); // 设置客户端使用的语言问中文
					conn.setRequestProperty("Charset", "UTF-8"); // 设置通信编码为UTF-8
					conn.setRequestProperty(
							"User-Agent",
							"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)"); // 客户端用户代理
					conn.setRequestProperty("Connection", "Keep-Alive"); // 使用长连接
					conn.getContentLength();// 文件总长度  没用
					is = conn.getInputStream();
					bis = new BufferedInputStream(is);
					file = new File(Environment.getExternalStorageDirectory()
							.getPath() + "/SpeedTest", "downloadfile"
							+ threadId);// 文件默认保存位置
					if (!file.exists()) {
						file.getParentFile().mkdirs();// 不存在就创建路径和文件
						file.createNewFile();
					}
					fos = new FileOutputStream(file, false);
					while (running == true && (len = bis.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
						this.manager.update(len);// 统计总的下载
					}
					// 下载完成退出下载线程
					if (running) {
						this.finished = true; // 设置完成标志为true，无论是下载完成还是用户主动中断下载
					}
					this.working = false; // 线程已经不需要工作了
				} catch (Exception e) {
					this.working = false; // 设置该线程已经没有正常工作
					e.printStackTrace();
				} finally {// 关闭没有关闭的流，删除下载的文件
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
					}
					if (conn != null) {
						conn.disconnect();
						conn = null;
					}
					// 文件存在，删除它
					if (file != null && file.exists()) {
						file.delete();
						file = null;
					}
					stopThread(this);
				}
			} else {
				this.working = false;
				Log.e(TAG, "无法识别存储卡sdcard");
			}
		}

		// 自动关闭线程
		private void stopThread(Thread theThread) {
			if (theThread != null) {
				theThread = null;
			}
		}

		/**
		 * 下载线程是否正常工作中
		 * 
		 * @return true为线程工作正常，否则为false
		 */
		public boolean isWorking() {
			return working;
		}

		/**
		 * 下载线程是否已经结束
		 * 
		 * @return true为线程已结束，否则为false
		 */
		public boolean isFinished() {
			return this.finished;
		}
	}
}
