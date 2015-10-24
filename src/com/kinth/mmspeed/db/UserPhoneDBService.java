package com.kinth.mmspeed.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kinth.mmspeed.bean.UserPhone;

public class UserPhoneDBService {
	private UserPhoneDbHelper userPhoneDbHelper; // sqlite数据库 UserDbHelper
	private SQLiteDatabase db; // sqlite数据库
	private Context context; // 上下文

	// 实例化
	public UserPhoneDBService(Context context) {
		this.context = context;
		userPhoneDbHelper = new UserPhoneDbHelper(context);
	}
	
	public UserPhone getUserPhoneByPhone(String phoneStr) {
		UserPhone userPhone = new UserPhone();
		String nickName = "";
		String remark = "";
		SQLiteDatabase db = userPhoneDbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from USERPHONE where phoneStr=?",
				new String[] { phoneStr });
		if (cursor.moveToFirst()) {
			nickName = cursor.getString(cursor.getColumnIndex("nickName"));
			remark = cursor.getString(cursor.getColumnIndex("remark"));
			userPhone = new UserPhone(phoneStr, nickName, remark);
			db.close();
			return userPhone;
		}
		db.close();
		return null;
	}

	public String getNickNameByPhone(String phone) {
		String name = "";
		SQLiteDatabase db = userPhoneDbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from USERPHONE where phoneStr=?",
				new String[] { phone });
		if (cursor.moveToFirst()) {
			name = cursor.getString(cursor.getColumnIndex("nickName"));
			return name;
		}
		return name;
	}

	public void updateNickName(String phone, String nickName) {
		SQLiteDatabase db = userPhoneDbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("nickName", nickName);
		// 第一个参数是要更新的表名 ss
		// 第二个参数是一个ContentValeus对象
		// 第三个参数是where子句
		db.update("USERPHONE", values, "phoneStr = ?", new String[] { phone
				+ "" });
		db.close();
	}

	public void updateRemark(String phone, String remark) {
		SQLiteDatabase db = userPhoneDbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("remark", remark);
		// 第一个参数是要更新的表名 ss
		// 第二个参数是一个ContentValeus对象
		// 第三个参数是where子句
		db.update("USERPHONE", values, "phoneStr = ?", new String[] { phone
				+ "" });
		db.close();
	}

	public ArrayList<UserPhone> getAllUserPhones() {
		ArrayList<UserPhone> userPhones = new ArrayList<UserPhone>();
		String sql = "select * from USERPHONE order by phoneStr"; // sql语句
		SQLiteDatabase db = userPhoneDbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			String phoneString = cursor.getString(cursor
					.getColumnIndex("phoneStr")); //
			String nikeName = cursor.getString(cursor
					.getColumnIndex("nickName"));
			String remark = cursor.getString(cursor.getColumnIndex("remark"));
			UserPhone smsProdInfo = new UserPhone(phoneString, nikeName, remark);
			userPhones.add(smsProdInfo);
		}
		cursor.close();
		db.close();
		userPhoneDbHelper.close();
		return userPhones;
	}

	/**
	 * 获取所有的号码
	 * @return
	 */
	public ArrayList<String> getAllPhoneNumber(){
		ArrayList<String> allPhone = new ArrayList<String>();
		String sql = "select phoneStr from USERPHONE"; // sql语句
		SQLiteDatabase db = userPhoneDbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			String phoneString = cursor.getString(cursor
					.getColumnIndex("phoneStr")); //
			allPhone.add(phoneString);
		}
		cursor.close();
		db.close();
		userPhoneDbHelper.close();
		return allPhone;
	}
	
	/**
	 * 获取所有号码的个数
	 * @return
	 */
	public int getAllPhoneAmount(){
		int amount = 0;
		String sql = "select phoneStr from USERPHONE"; // sql语句
		SQLiteDatabase db = userPhoneDbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		amount = cursor.getCount();
		cursor.close();
		db.close();
		userPhoneDbHelper.close();
		return amount;
	}
	
	/**
	 * 插入一个消息
	 */
	public boolean insertOrReplaceDetetion(UserPhone userPhone) {
		db = userPhoneDbHelper.getWritableDatabase();
		boolean result = false;
		try {
			db.execSQL(
					"insert or replace into USERPHONE (phoneStr,nickName,remark) values(?,?,?)",
					new Object[] { userPhone.getPhoneStr(),
							userPhone.getNickName(), userPhone.getRemark() });
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
			result = false;
		} finally {
			db.close();
		}
		return result;
	}

	public boolean deleteOnPhone(String userPhone) {
		db = userPhoneDbHelper.getWritableDatabase();
		boolean result = false;
		try {
			db.execSQL("delete from USERPHONE where phoneStr = ?",
					new Object[] { userPhone });
			result = true;
		} catch (Exception e) {
			System.out.println(e.toString());
			result = false;
		} finally {
			db.close();
		}
		return result;
	}

	public void dropTable() {
		db = userPhoneDbHelper.getWritableDatabase();
		try {
			db.execSQL("DROP TABLE IF EXISTS USERPHONE");
			// db.execSQL(create_table);
		} catch (Exception e) {
			// TODO: handle exception
			// db.close();
		} finally {
			db.close();
		}
	}

	public void deleteAll() {
		db = userPhoneDbHelper.getWritableDatabase();
		try {
			db.execSQL("delete from USERPHONE");
			// db.execSQL(create_table);
		} catch (Exception e) {
			// db.close();
		} finally {
			db.close();
		}
	}

	public void createTable() {
		db = userPhoneDbHelper.getWritableDatabase();
		try {
			String create_table = "CREATE TABLE IF NOT EXISTS USERPHONE (phoneStr varchar(11)  PRIMARY KEY,nickName varchar(20),remark varchar(50))";
			db.execSQL(create_table);
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			db.close();
		}
	}
}
