package com.kinth.mmspeed.network;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JSONUtils;

public class Message {

	private int msgid;
	private int msglen;
	private byte[] data;

	private int readOffset = 0; // The next read point

	private Map<String, Object> dataMap = new HashMap<String, Object>();
	private JSONObject jsonObj = null;

	public void putJSONObject(String name, Object obj) {
		if (dataMap == null)
			dataMap = new HashMap<String, Object>();
		dataMap.put(name, obj);
	}

	public void makeJSONPkgWithoutCommonInfo() {
		JSONObject jobj = new JSONObject();
		try {
			// jobj.put("map", dataMap);

			for (Entry<String, Object> en : dataMap.entrySet()) {
				jobj.put(en.getKey(), en.getValue());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.putString(jobj.toString());
		// System.out.println("String msg:" + jobj.toString());
		// dataMap = null;
	}

	public Object getObj(String key) {
		if (jsonObj != null) {
			try {
				return jsonObj.get(key);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public Object getJSONObject(String key) {
		if (jsonObj != null) {
			try {
				return jsonObj.get(key);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public void putCommonMsgInfo() {
		dataMap.put("dev", "Android,"
				+ ApplicationController.getInstance().getANDROID_API_VERSION() + ","
				+ ApplicationController.getInstance().getDev());
		dataMap.put("resolution", ApplicationController.getInstance().getResolution());
		dataMap.put("mac", ApplicationController.getInstance().getMac());
		dataMap.put("version", ApplicationController.getInstance().getVersion());
		dataMap.put("uid", ApplicationController.getInstance().getUid());//
		dataMap.put("api", ApplicationController.getInstance().getInterfaceApi());//接口的版本号
		dataMap.put("password",ApplicationController.getInstance().getPassword());
	}

	public String getJSONString() {
		JSONObject jobj = new JSONObject();
		try {
			for (Entry<String, Object> en : dataMap.entrySet()) {
				jobj.put(en.getKey(), en.getValue());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// dataMap = null;
		return jobj.toString();
	}
	
	public String getStandardJSONString() {
//		String result = "";
//		try {
//			result =  net.sf.json.JSONObject.fromObject(dataMap).toString();
//		} catch (Exception e) {
//			System.out.println(e.toString());
//			result = "";
//		}
		Gson g = new Gson();
		String jsonString = g.toJson(dataMap);
		return jsonString;
	}
	
	public String getJSONTrimString() {
		JSONObject jobj = new JSONObject();
		try {
			for (Entry<String, Object> en : dataMap.entrySet()) {
				jobj.put(en.getKey(), en.getValue());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// dataMap = null;
		return (jobj.toString().trim());
	}
//	public String getSfJSONString() {
//		
//		return net.sf.json.JSONObject.fromObject(dataMap).toString();
//
//	}
	public <T> List<T> getClassObjArrayFromJson(String key, Class<T> t) {
		ArrayList<T> list = new ArrayList<T>();
		if (jsonObj != null) {
			try {
				String jsonStr = jsonObj.getString(key);
				JSONArray jsonArray = new JSONArray(jsonStr);
				int len = jsonArray.length();
				for (int i = 0; i < len; i++) {
					T temp = getClassObjFromJson(jsonArray.getString(i), t);
					list.add(temp);
				}
				return list;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public <T> T getClassObjFromJson(String json, Class<T> t) {
		if (jsonObj != null) {
			return JSONUtils.fromJson(json, t);
		}
		return null;
	}
	
	public <T> T getClassObjFromKey(String key, Class<T> t) {
		if (jsonObj != null) {
			String jsonStr = null;
			try {
				jsonStr = jsonObj.getString(key);
				return JSONUtils.fromJson(jsonStr, t);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	// //--------------------��Ϣ����--------------/////
	public Message() {
		msglen = 0;
		msgid = -1;
		data = null;
		dataMap = new HashMap<String, Object>();
	}

	public Message(int msglen, int msgid, float test, byte[] data) {
		this.msglen = msglen;
		this.msgid = msgid;
		this.data = data;
	}

	public Message(int msgid) {
		this.msgid = msgid;
		this.msglen = 0;
	}

	public Message(String data) {
		try {
			if (!data.equals("")) {
				jsonObj = new JSONObject(data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void putString(String str) {
		if (str == null || str.length() <= 0)
			return;

		byte[] stbts = null, bts = null;
		try {
			stbts = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (stbts != null) {
			if (stbts[stbts.length - 1] != 0) {
				bts = new byte[stbts.length + 1];
				System.arraycopy(stbts, 0, bts, 0, stbts.length);
				bts[bts.length - 1] = 0;
			} else {
				bts = stbts;
			}
		}
		putBytes(bts);
	}

	private void putBytes(byte[] bts) {
		if (bts == null || bts.length <= 0)
			return;
		if (this.data == null) {
			this.data = new byte[bts.length];
			System.arraycopy(bts, 0, data, 0, bts.length);
		} else {
			byte[] old = this.data;
			this.data = new byte[old.length + bts.length];
			System.arraycopy(old, 0, data, 0, old.length);
			System.arraycopy(bts, 0, data, old.length, bts.length);
		}
		this.msglen = data.length;
	}

	public int size() {
		if (data == null)
			return 8;
		return data.length + 8;
	}

	public int getMsglen() {
		return msglen;
	}

	public int getMsgid() {
		return msgid;
	}

	public void setMsgid(int msgid) {
		this.msgid = msgid;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public static void main(String[] args) {
		try {
			// Message msg = new Message();
			// msg.putInt(1);
			// msg.putObject(new String("sdfsfsdfsfd"));
			// msg.putBool(true);
			// msg.putFloat(3.4f);
			// System.out.println(msg.readInt());
			// System.out.println(msg.readObj());
			// System.out.println(msg.readBool());
			// System.out.println(msg.readFloat());
			// msg.putObject(new String("wefwefs"));
			//
			// byte[] byt = TypeTrans.int2lowhighbytes(102001);
			// System.out.println(TypeTrans.lowhighbytes2int(byt, 0));
			//
			// msg.putInt(102001);
			// msg.setMsgid(102001);
			// byte[] btx = msg.getBytes();
			// ByteArrayInputStream bi = new ByteArrayInputStream(btx);
			//
			// msg.readMessge(bi);
			//
			// System.out.println(msg);

			// System.out.println(msg.getMsgid());
			// System.out.println(msg.readString());

			Message msg = new Message();
			msg.putString("sldjf����");
			msg.putString("sldjf����");

			/*
			 * msg.putJSONObject("a", 1); msg.putJSONObject("b", new
			 * String[]{"lto", "lop"}); Question qs = new Question();
			 * qs.setContent("qqqq"); Question qs2 = new Question();
			 * qs2.setContent("qqqq222");
			 * 
			 * msg.putJSONObject("arr", new Question[]{qs, qs2});
			 * msg.putJSONObject("it", new Integer[]{1, 2}); msg.makeJSONPkg();
			 * //System.out.println(msg.readString()); //Map<String, Object> mp
			 * = msg.getJSONMap();
			 * 
			 * JSONObject jo = JSONObject.fromObject(msg.readString()); Question
			 * pojoClass = new Question(); Question[] a =
			 * (Question[])JsonUtil.getObjectArray4Json(jo.getString("arr"),
			 * Question.class); //System.out.println(jo.getString("arr"));
			 * //JSONArray array=JSONArray.fromObject(jo.getString("arr"));
			 * //Question[] a = (Question[])JSONArray.toArray(array,
			 * Question.class);
			 * 
			 * Integer[] pp = JsonUtil.getIntegerArray4Json(jo.getString("it"));
			 * System.out.println(pp.length); System.out.println(pp[0]);
			 * System.out.println(pp[1]);
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
