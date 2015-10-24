package com.kinth.mmspeed.bj;


public class SingleTonStatus {
	private static SingleTonStatus singleTonStatus;
	private static boolean autoGetPosition =true;
	private static boolean autoGetNetWorkStatus =true;
	public static SingleTonStatus getInstance() {
		   if (singleTonStatus == null){
			   singleTonStatus = new SingleTonStatus();
		   }
		   return singleTonStatus;
	}
	
}
