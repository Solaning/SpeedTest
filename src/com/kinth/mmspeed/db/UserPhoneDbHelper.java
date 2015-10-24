package com.kinth.mmspeed.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kinth.mmspeed.constant.APPConstant;

public class UserPhoneDbHelper extends SQLiteOpenHelper {

	private static final int MESSAGE_DATABASE_VISION = 1; // 数据库版

	public UserPhoneDbHelper(Context context) {
		super(context, APPConstant.DATABASE_NAME, null, MESSAGE_DATABASE_VISION);
	}

	/**
	 * 创建table
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// String create_table =
		// "CREATE TABLE IF NOT EXISTS Supplier (SupplierNo varchar(20) PRIMARY KEY,SupplierName varchar(20))";
		// db.execSQL(create_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("DROP TABLE IF EXISTS "
//				+ LatestChatColumns.CHATLIST_TABLE_NAME);
//		onCreate(db);
	}

//	public static final class UPCColumns {
//		public static final String CHATLIST_TABLE_NAME = "Supplier"; // 数据库table名字
//		public static final String SupplierName = "SupplierName";
//		public static final String SupplierNo = "SupplierNo";
//	}
}
