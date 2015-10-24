package com.kinth.mmspeed.util;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

/**
 * �̳߳ع�����
 * 
 * @author carrey
 * 
 */
public class ThreadPoolManager {

	private static final String TAG = "ThreadPoolManager";

	/** �̳߳صĴ�С */
	private int poolSize;
	private static final int MIN_POOL_SIZE = 1;
	private static final int MAX_POOL_SIZE = 10;

	/** �̳߳� */
	private ExecutorService threadPool;

	/** ������� */
	private LinkedList<ThreadPoolTask> asyncTasks;

	/** ������ʽ */
	private int type;
	public static final int TYPE_FIFO = 0;
	public static final int TYPE_LIFO = 1;

	/** ��ѯ�߳� */
	private Thread poolThread;
	/** ��ѯʱ�� */
	private static final int SLEEP_TIME = 200;

	public ThreadPoolManager(int type, int poolSize) {
		this.type = (type == TYPE_FIFO) ? TYPE_FIFO : TYPE_LIFO;

		if (poolSize < MIN_POOL_SIZE)
			poolSize = MIN_POOL_SIZE;
		if (poolSize > MAX_POOL_SIZE)
			poolSize = MAX_POOL_SIZE;
		this.poolSize = poolSize;

		threadPool = Executors.newFixedThreadPool(this.poolSize);

		asyncTasks = new LinkedList<ThreadPoolTask>();
	}

	/**
	 * ������������������
	 * 
	 * @param task
	 */
	public void addAsyncTask(ThreadPoolTask task) {
		synchronized (asyncTasks) {
			Log.i(TAG, "add task: " + task.getURL());
			asyncTasks.addLast(task);
		}
	}

	/**
	 * �������������ȡ����
	 * 
	 * @return
	 */
	private ThreadPoolTask getAsyncTask() {
		synchronized (asyncTasks) {
			if (asyncTasks.size() > 0) {
				ThreadPoolTask task = (this.type == TYPE_FIFO) ? asyncTasks
						.removeFirst() : asyncTasks.removeLast();
				Log.i(TAG, "remove task: " + task.getURL());
				return task;
			}
		}
		return null;
	}

	/**
	 * �����̳߳���ѯ
	 * 
	 * @return
	 */
	public void start() {
		if (poolThread == null) {
			poolThread = new Thread(new PoolRunnable());
			poolThread.start();
		}
	}

	/**
	 * ������ѯ���ر��̳߳�
	 */
	public void stop() {
		poolThread.interrupt();
		poolThread = null;
	}

	/**
	 * ʵ����ѯ��Runnable
	 * 
	 * @author carrey
	 * 
	 */
	private class PoolRunnable implements Runnable {

		@Override
		public void run() {
			Log.i(TAG, "��ʼ��ѯ");

			try {
				while (!Thread.currentThread().isInterrupted()) {
					ThreadPoolTask task = getAsyncTask();
					if (task == null) {
						try {
							Thread.sleep(SLEEP_TIME);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
						continue;
					}
					threadPool.execute(task);
				}
			} finally {
				threadPool.shutdown();
			}

			Log.i(TAG, "������ѯ");
		}

	}
}
