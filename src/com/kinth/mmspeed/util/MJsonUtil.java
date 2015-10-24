package com.kinth.mmspeed.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONObject;


public class MJsonUtil {
 private static final String TAG = "JSONUTIL";
 public static JSONObject getJSON(String url) throws Exception {
  return new JSONObject(getRequest(url));
 }
 protected static String getRequest(String url) {
  return getRequest(url, new DefaultHttpClient(new BasicHttpParams()));
 }
 protected static String getRequest(String url, DefaultHttpClient client) {
  String result = null;
  int statusCode = 0;
  HttpGet httpGet = new HttpGet(url);
  try {
   HttpResponse httpResponse = client.execute(httpGet);
   statusCode = httpResponse.getStatusLine().getStatusCode();// statusCode为200时表示请求数据成功
   result = parseInputStream(httpResponse.getEntity());
  } catch (ClientProtocolException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  } finally {
   httpGet.abort();
  }
  return result;
 }
 private static String parseInputStream(HttpEntity entity) {
  StringBuilder sb = null;
  try {
   sb = new StringBuilder("");
   InputStream inputStream = entity.getContent();
   int length = 0;
   byte[] buffer = new byte[1024];
   while ((length = inputStream.read(buffer)) > -1) {
    sb.append(new String(buffer, 0, length));
   }
   return sb.toString();
  } catch (IllegalStateException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  }
  return sb.toString();
 }
}