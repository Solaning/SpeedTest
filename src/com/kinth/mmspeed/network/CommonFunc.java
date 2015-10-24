package com.kinth.mmspeed.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.X509TrustManager;


public class CommonFunc {

	/**
	 * 设置信任所有的http证书（正常情况下访问https打头的网站会出现证书不信任相关错误，所以必须在访问前调用此方法）
	 * @throws Exception
	 */
	private static void trustAllHttpsCertificates() throws Exception
	{
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		trustAllCerts[0] = new X509TrustManager()
		{
			@Override
			public X509Certificate[] getAcceptedIssuers()
			{
				return null;
			}
			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException
			{}
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException
			{}
		};
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}
	
	/**
	 * 以POST方式请求一个URL
	 * @param url
	 * @param parameters
	 * @return
	 */
	public static String postUrl(String url,String parameters)
	{
		StringBuilder result = new StringBuilder();
		try
		{
			trustAllHttpsCertificates();//设置信任所有的http证书
			URLConnection conn = new URL(url).openConnection();
			conn.setDoOutput(true);// 这里是关键，表示我们要向链接里注入的参数
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());// 获得连接输出流
			out.write(parameters);
			out.flush();
			out.close();
			// 到这里已经完成了，开始打印返回的HTML代码
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			Map<String, List<String>> map = conn.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
            	if(key!=null && key.equals("Set-Cookie"))
            	{
            		String str = map.get(key).toString();
            		int startIdx = str.indexOf("validkey=")+"validkey=".length();
            		int endIdx = str.indexOf(";");
            	}
            }
			
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				result.append(line);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result.toString();
	}
	
	
	public static int getRandom1()
	{
		return (int) ((Math.random())*2);
	}
	
	/**
	 * 随机按一定比例获取
	 * @param truePercent
	 * @return
	 */
	public static boolean getRandomTrue(int truePercent)
	{
		int rand = (int) ((Math.random())*100);
		if(rand <= truePercent)
			return true;
		else 
			return false;
	}
	
	
	/**
	* 生成一个在开始日期和结束日期之间的随机日期 
	* @param beginDate 开始日期
	* @param endDate 结束日期
	* @return 返回一个在beginDate与endDate之间的随机日期
	*/
    public static Date randomDate(String beginDate,String endDate){
        try {
            //建立一个SimpleDateFormat对象，指定好时间格式
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            //把传进来的String类型的时间转化为Date类型 
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);

            //如果开始时间大于等于结束时间，啥也不干了，返回null
            if(start.getTime() >= end.getTime()){
                return null;
            }

            //调用random函数，生成代表特定日期的long类型的随机数
            //getTime函数得到的是long类型的数
            long date = random(start.getTime(),end.getTime());

            //根据这个随机数，实例化一个日期对象，也就是生成了一个随机日期
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
    * 生成一个long类型的随机数 
    * @param begin 代表开始日期的long类型数
    * @param end 代表结束日期的long类型数
    * @return 返回long类型的随机数
    */
    private static long random(long begin,long end){
        //Math.random()生成0到1之间的一个随机数
        //随机数接近0时，生成的日期接近开始日期，随机数接近1时，生成的日期接近结束日期
        long rtn = begin + (long)(Math.random() * (end - begin));
        if(rtn == begin || rtn == end){
            return random(begin,end);
        }
        return rtn;
    }
	
	public static String randomString(int length) {
		Random randGen = null;
		char[] numbersAndLetters = null;
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
               randGen = new Random();
//               numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
//                  "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
                 numbersAndLetters = ("0123456789").toCharArray();
                }
        char [] randBuffer = new char[length];
        for (int i=0; i<randBuffer.length; i++) {
//            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
         randBuffer[i] = numbersAndLetters[randGen.nextInt(9)];
        }
        return new String(randBuffer);
}
	
	public static String getNowTime()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		
		return df.format(new Date()).toString();
	}
	
	public Set<Integer> getRandomIntList(int idx, int count) {
		Set<Integer> lst = new HashSet<Integer>();
		Random rd = new Random();
		for (int i  = 0; i < 3*idx; i ++) {
			if (lst.size() == idx)
				break;
			lst.add((int)(rd.nextFloat() * count));
		}
		return lst;
	}
	
	/**
	 * 生成t个不重复的随机整数，范围是1-N
	 * @param n
	 * @param t
	 * @return
	 */
    public static Integer[] randomT(int n,int t){
        if(n < t){
//            return new Integer[0];
        	Set<Integer> set = new HashSet<Integer>();
            for(int i=1;i<=n;i++){   
                 set.add(i); 
            }
            int[] array = new int[n];
            Integer[] intArray = new Integer[n];
            intArray = set.toArray(intArray);
            return intArray;
        	
        }
        else{
        	Set<Integer> set = new HashSet<Integer>();
            while((set.size())< t){
                 int raNum=(int)(Math.random()*n)+1;
                 set.add(raNum); 
            }
            //将Set转化为数组
            int[] array = new int[t];
            Integer[] intArray = new Integer[t];
            intArray = set.toArray(intArray);
//            for(int i = 0;i < t;i++){
//             array[i] = intArray[i].intValue();
//            }
//            return array;
            return intArray;
        }
    }
    
    /** 
     * 获得一个UUID  
     * @return String UUID 
     */ 
    public static String getUUID(){ 
        String s = UUID.randomUUID().toString(); 
        //去掉“-”符号 
        return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
    } 
    /** 
     * 获得指定数目的UUID 
     * @param number int 需要获得的UUID数量 
     * @return String[] UUID数组 
     */ 
    public static String[] getUUID(int number){ 
        if(number < 1){ 
            return null; 
        } 
        String[] ss = new String[number]; 
        for(int i=0;i<number;i++){ 
            ss[i] = getUUID(); 
        } 
        return ss; 
    } 
    

    /**
    * 生成随机密码
    * @param passLenth 生成的密码长度
    * @return 随机密码
    */
    public static String getPass(int passLenth) {

       StringBuffer buffer = new StringBuffer(
         "0123456789abcdefghijklmnopqrstuvwxyz");
       StringBuffer sb = new StringBuffer();
       Random r = new Random();
       int range = buffer.length();
       for (int i = 0; i < passLenth; i++) {
        //生成指定范围类的随机数0—字符串长度(包括0、不包括字符串长度)
        sb.append(buffer.charAt(r.nextInt(range)));
       }
       return sb.toString();
    }
    
    
    public static String replace(String strSource, String strFrom, String strTo) {    
        if (strSource == null) {        
          return null;    
        }  
        int i = 0;
        if ((i = strSource.indexOf(strFrom, i)) >= 0) {
          char[] cSrc = strSource.toCharArray();
          char[] cTo = strTo.toCharArray();
          int len = strFrom.length();  
          StringBuffer buf = new StringBuffer(cSrc.length);  
          buf.append(cSrc, 0, i).append(cTo);
          i += len;    
          int j = i;       
          while ((i = strSource.indexOf(strFrom, i)) > 0) {  
            buf.append(cSrc, j, i - j).append(cTo);   
            i += len;  
            j = i;        
          }        
          buf.append(cSrc, j, cSrc.length - j); 
          return buf.toString(); 
        } 
        return strSource;
      } 

}
