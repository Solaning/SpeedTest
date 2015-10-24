package com.kinth.mmspeed.util;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeTrans {

//	public static void main(String []args)
//	{
//		System.out.println(TransTime("2013-11-16 22:12:09", true));
//	}
	
	@SuppressWarnings("deprecation")
	public static String TransTime(String inTime, boolean full)
	{
		String reDes = "";
		
		//当前时间
		Date fromDate = new Date();
		//传入的目标时间
		Date toDate = DateUtil.strToDateLong(inTime);
		
		int toHour = toDate.getHours();
		int toMin = toDate.getMinutes();
		int toDay = toDate.getDate();
		
		int toWeekDay = toDate.getDay();
		int toMon = toDate.getMonth()+1;
		int toYear = toDate.getYear()+1900;
		
		int fromHour = fromDate.getHours();
		int fromDay = fromDate.getDate();
		int fromMon = fromDate.getMonth()+1;
		int fromYear = fromDate.getYear()+1900;
	

	    if (toYear == fromYear && toMon == fromMon && toDay == fromDay) {
	        if (toHour<12 && fromHour > 12) {
	            reDes = String.format("上午 %02d:%02d",toHour,toMin);
	        }else{
	            reDes =  String.format("%02d:%02d",toHour,toMin);
	        }
	    }else{
	        Date tmpToDate = new Date();
	        tmpToDate.setYear(toYear);
	        tmpToDate.setMonth(toMon);
	        tmpToDate.setDate(toDay);

	        Date tmpFromDate = new Date();
	        tmpFromDate.setYear(fromYear);
	        tmpFromDate.setMonth(fromMon);
	        tmpFromDate.setDate(fromDay);
	        
	        long diffSeconds = (fromDate.getTime() - toDate.getTime())/(1000);
	        long daySeconds = 24 * 60 * 60;
	        long WeekSeconds = 7 * daySeconds;
	        
	        if (diffSeconds <= daySeconds) {
	            if (full) {
	                reDes= String.format("昨天 %02d:%02d",toHour,toMin);
	            }else{
	                if (toHour<12) {
	                    reDes = "昨天上午";
	                }else{
	                    reDes = String.format("昨天 %02d:%02d",toHour,toMin);
	                }
	            }
	        }else if (diffSeconds < WeekSeconds)
	        {
	            reDes = weekDayStringWithWeekDay(toWeekDay);
	            if (full) {
	                reDes = String.format("%s %02d:%02d",reDes,toHour,toMin);
	            }
	        }else if (toMon == fromMon && toYear == fromYear){
	            if (full) {
	                reDes =  String.format("%d月%d日 %02d:%02d",toMon,toDay,toHour,toMin);
	            }else{
	                reDes =  String.format("%d月%d日",toMon,toDay);
	            }
	        }else{
	        	
				SimpleDateFormat otherFormatter = null;//设置日期格式
	            if (full) {
	            	otherFormatter = new SimpleDateFormat("yy-M-d H:mm");
	            }else{
	            	otherFormatter = new SimpleDateFormat("yy-M-d");
	            }
	            reDes = otherFormatter.format(toDate);
	        }
	    }
		
		return reDes;
	}
	
	private static String weekDayStringWithWeekDay(int weekDay)
	{
	    String returnString="";
	    switch (weekDay) {
	        case 1:
	            returnString = "星期一";
	            break;
	        case 2:
	            returnString = "星期二";
	            break;
	        case 3:
	            returnString = "星期三";
	            break;
	        case 4:
	            returnString = "星期四";
	            break;
	        case 5:
	            returnString = "星期五";
	            break;
	        case 6:
	            returnString = "星期六";
	            break;
	        case 7:
	            returnString = "星期天";
	            break;
	        default:
	            returnString = "";
	            break;
	    }
	    return returnString;
	}
}
