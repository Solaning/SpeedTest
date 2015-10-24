package com.kinth.mmspeed.bean;

/**
 * 套餐没有规整前的详情
 * 
 * @author Sola
 * 
 */
public class SMSProdDetail {
	private String phone;// 手机号码
	private String prodName;// 套餐名称
	private double contain;// 套餐总流量
	private double relay;// 套餐剩余流量
	private double used;// 套餐已用流量

	public SMSProdDetail() {
		super();
	}

	public SMSProdDetail(String phone, String prodName, double contain,
			double relay, double used) {
		super();
		this.phone = phone;
		this.prodName = prodName;
		this.contain = contain;
		this.relay = relay;
		this.used = used;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public double getContain() {
		return contain;
	}

	public void setContain(double contain) {
		this.contain = contain;
	}

	public double getRelay() {
		return relay;
	}

	public void setRelay(double relay) {
		this.relay = relay;
	}

	public double getUsed() {
		return used;
	}

	public void setUsed(double used) {
		this.used = used;
	}

}
