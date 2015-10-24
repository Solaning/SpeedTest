package com.kinth.mmspeed.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;

public class TransObject {

	/**
	 * Get the boolean value associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return The truth.
	 * @throws JSONException
	 *             if the value is not a Boolean or the String "true" or
	 *             "false".
	 */
	public static boolean getBoolean(Object o) throws Exception {
		if (o == null)
			return false;
		if (o.equals(Boolean.FALSE)
				|| (o instanceof String && ((String) o)
						.equalsIgnoreCase("false"))) {
			return false;
		} else if (o.equals(Boolean.TRUE)
				|| (o instanceof String && ((String) o)
						.equalsIgnoreCase("true"))) {
			return true;
		}
		throw new Exception("getObject[" + (o) + "] is not a Boolean.");
	}

	/**
	 * Get the double value associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return The numeric value.
	 * @throws JSONException
	 *             if the key is not found or if the value is not a Number
	 *             object and cannot be converted to a number.
	 */
	public static double getDouble(Object o) throws Exception {
		if (o == null)
			return 0;
		try {
			if (o instanceof Number) {
				return ((Number) o).doubleValue();
			} else if (o.toString().length() > 0) {
				return Double.valueOf((o.toString()));
			} else
				return 0;
		} catch (Exception e) {
			throw new Exception("getObject[" + (o) + "] is not a number.");
		}
	}

	/**
	 * Get the int value associated with a key. If the number value is too large
	 * for an int, it will be clipped.
	 * 
	 * @param key
	 *            A key string.
	 * @return The integer value.
	 * @throws Exception
	 *             if the key is not found or if the value cannot be converted
	 *             to an integer.
	 */
	public static int getInt(Object o) throws Exception {
		if (o == null)
			return 0;
		return o instanceof Number ? ((Number) o).intValue()
				: (int) getDouble(o);
	}

	/**
	 * Get the long value associated with a key. If the number value is too long
	 * for a long, it will be clipped.
	 * 
	 * @param key
	 *            A key string.
	 * @return The long value.
	 * @throws Exception
	 *             if the key is not found or if the value cannot be converted
	 *             to a long.
	 */
	public static long getLong(Object o) throws Exception {

		if (o == null)
			return 0;
		if (o instanceof String) {
			if (o.toString().length() > 0) {
				return Long.valueOf((o.toString()));
			} else
				return 0;
		}
		return o instanceof Number ? ((Number) o).longValue()
				: (long) getDouble(o);
	}

	/**
	 * Get the string associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return A string which is the value.
	 * @throws Exception
	 *             if the key is not found.
	 */
	public static String getString(Object o) throws Exception {
		if (o == null)
			return "";
		return o.toString();
	}
	
	/**
	 * Get the string associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return A string which is the value.
	 * @throws Exception
	 *             if the key is not found.
	 */
	public static String getDateString(Object o) throws Exception {
		if (o == null)
			return "";
		
		try{
			DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = TIMESTAMP_FORMAT.parse(o.toString());
			String d=TIMESTAMP_FORMAT.format(date);
			return d;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

}
