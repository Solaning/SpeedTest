package com.kinth.mmspeed.util;

/** 
 * ����Ԫ 
 * @author carrey 
 * 
 */  
public abstract class ThreadPoolTask implements Runnable {  
  
    protected String url;  
      
    public ThreadPoolTask(String url) {  
        this.url = url;  
    }  
      
    public abstract void run();  
      
    public String getURL() {  
        return this.url;  
    }  
}  
