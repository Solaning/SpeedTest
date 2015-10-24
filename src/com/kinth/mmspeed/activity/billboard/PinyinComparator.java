package com.kinth.mmspeed.activity.billboard;

import java.util.Comparator;

import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.util.PingYinUtil;

public class PinyinComparator implements Comparator<ContractInfo>{


	@Override
	public int compare(ContractInfo arg0, ContractInfo arg1) {
		if (arg0==null||arg1==null) {
			return 0;
		}
		String str1 ;
		 String str2;
		
		 String name1 = arg0.getContractName();
		 String name2 = arg1.getContractName();
		 if (name1==null||name1.equals("")) {
			 str1="#";
		}else {
			try {
				str1 = PingYinUtil.getPingYin(name1);
			} catch (Exception e) {
				str1="#";
			}
			
		}
		 if (name2==null||name2.equals("")) {
			 str2="#";
		}else {
			try {
				str2 = PingYinUtil.getPingYin(name2);
			} catch (Exception e) {
				str2="#";
			}
		}
	     return str1.compareTo(str2);
	}

}
