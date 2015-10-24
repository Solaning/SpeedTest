package com.kinth.mmspeed.map;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.MapViewLayoutParams.ELayoutMode;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.mmspeed.CommonWebViewActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.bean.ArticleInfo;
import com.kinth.mmspeed.bean.MarkerPoint;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.fragment.BaseFragment;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.CommonMsgInfo;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.Md5Util;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sola.code.ydlly_assist.bean.KeyValue;

/**
 * @author Sola
 * 
 * 流量地图
 */
public class FlowMapFragment extends BaseFragment {
	private static final String IS_CLUSTER_MARKER = "IS_CLUSTER_MARKER";// 是否聚合
	private static final String SPEED_ARRAY = "SPEED_ARRAY";// 速度数组
	private static final String SPEED_UPLOAD_ARRAY = "SPEED_UPLOAD_ARRAY";// 速度数组上传
	private static final String SINGLE_SPEED = "SINGLE_SPEED";// 单独的数组
	private static final String SINGLE_UPLOAD_SPEED = "SINGLE_UPLOAD_SPEED";// 单独的数组上传
	private static final String OPERATOR = "OPERATOR";// 运营商
	private Context mContext;
	private View popupViewInMap;// 地图上的弹窗:添加或者移除用

	// 定位相关
	private BaiduMap mBaiduMap;
	boolean isFirstLoc = true;// 是否首次定位
	boolean hasData = false;// 是否定位成功

	// private boolean isAverageCenter = true;// 获取单个聚合的落脚点是否是聚合内所有标记的平均中心。
	private int mMaxZoom = 16;// 最大放大级别
	private float currentZoom;// 当前的缩放级别
	private int ZoomState = 0;// 缩放状态，0代表初始或者没有缩放，1代表放大，2代表缩小
	private int mGridSize = 60;// 像素大小
	private List<MarkerOptionsWithPoint> mMarkers;// 聚合之前的覆盖物list
	private Cluster mCluster;// 聚合
	private double mDistance = 400000;// 距离

	private LatLng latLng = null;// 当前定位位置的坐标

	LocationClient mLocClient;
	public MyLocationListenner myLocationListener = new MyLocationListenner();
	private List<ArticleInfo> adList = null;// 广告内容
	private int adRtn = 0;// 广告
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.image_download_loading_icon)
			.showImageForEmptyUri(R.drawable.image_download_loading_icon)
			.showImageOnFail(R.drawable.image_download_fail_icon)
			.cacheInMemory(true).cacheOnDisk(true).build();

	@ViewInject(R.id.bd_mapView)
	private MapView mMapView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("mMapView.onCreateView();","mMapView.onCreateView();");
		View view = inflater.inflate(R.layout.fragment_flow_map, container,
				false);
		ViewUtils.inject(this, view);

		mCluster = new Cluster(mContext, mMapView, mGridSize, mDistance);

		// 地图初始化
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(mContext);
		mLocClient.registerLocationListener(myLocationListener);
		InitLocation();
		mLocClient.start();
		// 监听事件
		mBaiduMap.setOnMapStatusChangeListener(mapStatusChangeListener);
		mBaiduMap.setOnMarkerClickListener(markerClickListener);
		return view;
	}

	// add测试数据
	private void addFakeDate(List<MarkerPoint> speedInfo) {
		mMarkers = new ArrayList<MarkerOptionsWithPoint>();
		for (MarkerPoint item : speedInfo) {
			LatLng pt = new LatLng(item.getLatitude(), item.getLongitude());

			// 构建Marker图标
			BitmapDescriptor bitmap = BitmapDescriptorFactory
					.fromResource(R.drawable.icon_mark);
			MarkerOptions options = new MarkerOptions().position(pt) // 设置marker的位置
					.icon(bitmap) // 设置marker图标
					.zIndex(9) // 设置marker所在层级
					.draggable(true); // 设置手势拖拽

			mMarkers.add(new MarkerOptionsWithPoint(options, item));
		}
		// for (int i = 0; i < 50; i++) {
		// LatLng pt = new LatLng((Math.random() * 0.125 + 23),
		// Math.random() * 0.5 + 113);
		//
		// // 构建Marker图标
		// BitmapDescriptor bitmap = BitmapDescriptorFactory
		// .fromResource(R.drawable.icon_mark);
		// MarkerOptions options = new MarkerOptions().position(pt) //
		// 设置marker的位置
		// .icon(bitmap) // 设置marker图标
		// .zIndex(9) // 设置marker所在层级
		// .draggable(true); // 设置手势拖拽
		// mMarkers.add(options);
		// }
	}

	/**
	 * 在地图上显示覆盖物
	 * 
	 * @param list
	 */
	private void pinMarkers(List<MarkerOptionsWithPoint> list) {
		mMapView.removeView(popupViewInMap);
		mBaiduMap.clear();
		for (int i = 0; i < list.size(); i++) {
			Marker marker = (Marker) mBaiduMap.addOverlay(list.get(i)
					.getMarkerOptions());
			Bundle bundle = new Bundle();
			bundle.putBoolean(IS_CLUSTER_MARKER, false);
			bundle.putDouble(SINGLE_SPEED, list.get(i).getMarkerPoint()
					.getSpeed());
			bundle.putDouble(SINGLE_UPLOAD_SPEED, list.get(i).getMarkerPoint()
					.getUpspeed());
			bundle.putString(OPERATOR, list.get(i).getMarkerPoint()
					.getNetworkOperator());
			marker.setExtraInfo(bundle);
		}
		// TODO
		switch (ZoomState) {
		case 0:
			break;
		case 1:// 放大
			break;
		case 2:// 缩小
			if (currentZoom < 15) {
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);// zoomLevel
																				// 缩放级别15
				mBaiduMap.animateMapStatus(u);
			}
			break;
		}
	}

	/**
	 * 在地图上显示覆盖物，聚合后
	 * 
	 * @param list
	 */
	private void pinMarkersWithSpeed(List<MarkerOptionsWithSpeed> list) {
		mMapView.removeView(popupViewInMap);
		mBaiduMap.clear();
		for (int i = 0; i < list.size(); i++) {
			Marker marker = (Marker) mBaiduMap.addOverlay(list.get(i)
					.getMarkerOptions());
			Bundle bundle = new Bundle();
			bundle.putBoolean(IS_CLUSTER_MARKER, true);
			bundle.putDoubleArray(SPEED_ARRAY, list.get(i).getSpeed());
			bundle.putDoubleArray(SPEED_UPLOAD_ARRAY, list.get(i).getUpspeed());
			marker.setExtraInfo(bundle);
		}
		// TODO
		switch (ZoomState) {
		case 0:
			break;
		case 1:// 放大
			break;
		case 2:// 缩小
			if (currentZoom < 15) {
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);// zoomLevel
																				// 缩放级别15
				mBaiduMap.animateMapStatus(u);
			}
			break;
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					// .direction(100)
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			latLng = new LatLng(location.getLatitude(), location.getLongitude());// 当前定位位置的坐标
			if (isFirstLoc) {
				isFirstLoc = false;
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(
						latLng, 16);// zoomLevel 缩放级别16
				mBaiduMap.animateMapStatus(u);
				// 根据定位位置请求数据
				HashMap<String, String> params = CommonMsgInfo
						.getCommonMsgInfo();
				params.put("latitude", location.getLatitude() + "");// 纬度
				params.put("longitude", location.getLongitude() + "");// 经度
				params.put("mobile", MainAccountUtil.getAvailablePhone(mContext));
				params.put("networkType", UtilFunc.getNetworkType(mContext));

				JsonObjectRequest req = new JsonObjectRequest(
						APPConstant.MAP_GET_NEAR_SPEED, new JSONObject(params),
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {// 取到数据成功，更新界面
								int unicomRtn = 0;
								int telecomRtn = 0;
								int mobileRtn = 0;
								ArrayList<MarkerPoint> speedInfo = new ArrayList<MarkerPoint>();// 测速点
								Gson gson = new Gson();
								try {
									unicomRtn = response.getInt("unicomRtn");
									telecomRtn = response.getInt("telecomRtn");
									mobileRtn = response.getInt("mobileRtn");
									adRtn = response.getInt("adRtn");
								} catch (JSONException e2) {
									// TODO Auto-generated catch block
									e2.printStackTrace();
								}

								if (adRtn == 1) {
									try {
										adList = gson.fromJson(
												response.getString("ADList"),
												new TypeToken<List<ArticleInfo>>() {
												}.getType());
									} catch (JsonSyntaxException
											| JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}

								List<MarkerPoint> temp = new ArrayList<MarkerPoint>();
								try {
									temp = gson.fromJson(
											response.getString("telecomNearSpeed"),
											new TypeToken<ArrayList<MarkerPoint>>() {
											}.getType());
								} catch (JsonSyntaxException | JSONException e) {
									temp = null;
									e.printStackTrace();
								}
								if (temp != null) {
									for (MarkerPoint item : temp) {
										item.setNetworkOperator(APPConstant.OperatorType.TELECOM
												.getValue());
									}
									speedInfo.addAll(temp);
								}
								try {
									temp = gson.fromJson(
											response.getString("mobileNearSpeed"),
											new TypeToken<ArrayList<MarkerPoint>>() {
											}.getType());
								} catch (JsonSyntaxException | JSONException e1) {
									temp = null;
									e1.printStackTrace();
								}
								if (temp != null) {
									for (MarkerPoint item : temp) {
										item.setNetworkOperator(APPConstant.OperatorType.MOBILE
												.getValue());
									}
									speedInfo.addAll(temp);
								}
								try {
									temp = gson.fromJson(
											response.getString("unicomNearSpeed"),
											new TypeToken<ArrayList<MarkerPoint>>() {
											}.getType());
								} catch (JsonSyntaxException | JSONException e) {
									temp = null;
									e.printStackTrace();
								}
								if (temp != null) {
									for (MarkerPoint item : temp) {
										item.setNetworkOperator(APPConstant.OperatorType.UNICOM
												.getValue());
									}
									speedInfo.addAll(temp);
								}

								int rtn = (unicomRtn == 1 || telecomRtn == 1 || mobileRtn == 1) ? 1
										: 0;

								if (rtn == 1) {
									hasData = true;
									addFakeDate(speedInfo);
									if (currentZoom >= mMaxZoom) {
										pinMarkers(mMarkers);// 直接在上面加载覆盖物，不经过聚合
										// 显示最近点的速度
										showNearestPoint(latLng, speedInfo);// 只显示一次的
									} else {
										new CreateClusterTask().execute();
									}
								} else {
									Toast.makeText(mContext, "您附近暂时没有数据",
											Toast.LENGTH_LONG).show();
								}
							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								VolleyLog.e("Error: ", error.getMessage());
								Log.e("response error", "" + error.getMessage());
								Toast.makeText(mContext, "获取数据失败",
										Toast.LENGTH_LONG).show();
							}
						});
				ApplicationController.getInstance().addToRequestQueue(req);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {

		}
	}

	/**
	 * 寻找附近最近的点，并显示
	 * 
	 * @param myLL
	 * @param speedInfo
	 */
	private void showNearestPoint(LatLng myLL, List<MarkerPoint> speedInfo) {
		double minDistance = Double.MAX_VALUE, distance = 0;
		LatLng nearestLatLng = null;
		MarkerPoint nearestMarkerPoint = null;
		for (MarkerPoint item : speedInfo) {
			LatLng point = new LatLng(item.getLatitude(), item.getLongitude());
			distance = DistanceUtil.getDistance(myLL, point);
			if (distance < minDistance) {
				minDistance = distance;// 最小值
				nearestMarkerPoint = item;
				nearestLatLng = point;
			}
		}
		if (nearestMarkerPoint == null) {
			return;
		}

		final View v = View.inflate(mContext, R.layout.map_popup_single_style,
				null);
		mMapView.removeView(popupViewInMap);
		popupViewInMap = v;
		ImageView operatorIcon = (ImageView) v
				.findViewById(R.id.iv_map_operator_icon);
		TextView tvSpeed = (TextView) v.findViewById(R.id.tv_map_speed);
		TextView tvUploadSpeed = (TextView) v
				.findViewById(R.id.tv_map_speed_upload);

		final ImageView mapAd = (ImageView) v.findViewById(R.id.iv_map_ad);// 广告
		initAdImage(mapAd);

		switch (APPConstant.OperatorType.getEnumFromString(nearestMarkerPoint
				.getNetworkOperator())) {
		case MOBILE:
			operatorIcon.setImageResource(R.drawable.yidong);
			tvSpeed.setText("下载:"
					+ String.format("%.2f",
							nearestMarkerPoint.getSpeed() / 128.0) + "Mbps");
			tvUploadSpeed.setText("上传:"
					+ String.format("%.2f",
							nearestMarkerPoint.getUpspeed() / 128.0) + "Mbps");
			break;
		case UNICOM:
			operatorIcon.setImageResource(R.drawable.liantong);
			tvSpeed.setText("下载:"
					+ String.format("%.2f",
							nearestMarkerPoint.getSpeed() / 128.0) + "Mbps");
			tvUploadSpeed.setText("上传:"
					+ String.format("%.2f",
							nearestMarkerPoint.getUpspeed() / 128.0) + "Mbps");
			break;
		case TELECOM:
			operatorIcon.setImageResource(R.drawable.dianxin);
			tvSpeed.setText("下载:"
					+ String.format("%.2f",
							nearestMarkerPoint.getSpeed() / 128.0) + "Mbps");
			tvUploadSpeed.setText("上传:"
					+ String.format("%.2f",
							nearestMarkerPoint.getUpspeed() / 128.0) + "Mbps");
			break;
		}
		addMyViewToMap(v, nearestLatLng);
	}

	private void initAdImage(final ImageView mapAd) {
		if (adRtn == 1 && adList != null && adList.size() > 0) {
			final String adImageUrl = adList.get(0).getTitleURLString();
			final String md5Path = APPConstant.IMAGE_PERSISTENT_CACHE
					+ File.separator + Md5Util.md5s(adImageUrl);// 取md5后的图片路径
			File imageFile = new File(md5Path);
			if (imageFile.exists()) {// 存在
				ImageSize targetSize = new ImageSize(160, 40); // result Bitmap
																// will be fit
																// to this size
				ImageLoader.getInstance().loadImage("file:///" + md5Path,
						targetSize, options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								mapAd.setImageBitmap(loadedImage);
							}
						});
				// 加载图片
				// ImageLoader.getInstance().displayImage("file:///" + md5Path,
				// mapAd, options, null);
			} else {// 本地不存在，去下载，然后显示
				HttpUtils http = new HttpUtils();
				HttpHandler<File> handler = http.download(adImageUrl, md5Path,
						true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
						false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
						new RequestCallBack<File>() {

							@Override
							public void onStart() {
							}

							@Override
							public void onLoading(long total, long current,
									boolean isUploading) {
							}

							@Override
							public void onSuccess(
									ResponseInfo<File> responseInfo) {
								ImageSize targetSize = new ImageSize(160, 40); // result
																				// Bitmap
																				// will
																				// be
																				// fit
																				// to
																				// this
																				// size
								ImageLoader.getInstance().loadImage(
										"file:///" + md5Path, targetSize,
										options,
										new SimpleImageLoadingListener() {
											@Override
											public void onLoadingComplete(
													String imageUri, View view,
													Bitmap loadedImage) {
												mapAd.setImageBitmap(loadedImage);
											}
										});
								// ImageLoader.getInstance().displayImage("file:///"
								// + md5Path, mapAd, options, null);
							}

							@Override
							public void onFailure(HttpException error,
									String msg) {
								ImageLoader.getInstance().displayImage(
										adImageUrl, mapAd, options, null);
							}
						});
			}
			mapAd.setOnClickListener(new OnClickListener() {// 广告的点击

				@Override
				public void onClick(View arg0) {
					ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
					keyValue.add(new KeyValue("statType", "speedMapAD"));
					keyValue.add(new KeyValue("keyValue", adList.get(0).getId()
							+ ""));
					new AsyncNetworkManager().asynSendData(mContext, keyValue);
					if (TextUtils.isEmpty(adList.get(0).getClickURL())) {
						return;
					}
					Intent intent = new Intent(mContext,
							CommonWebViewActivity.class);
					intent.putExtra(CommonWebViewActivity.INTENT_URL, adList
							.get(0).getClickURL());
					startActivity(intent);
				}
			});
		} else {
			mapAd.setVisibility(View.GONE);
		}
	}

	/**
	 * 覆盖物被点击的监听
	 */
	private OnMarkerClickListener markerClickListener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(final Marker marker) {
			// TextView button = new Button(mContext);
			// button.setBackgroundResource(R.drawable.popup_baidu_info_window);
			// final LatLng ll = marker.getPosition();
			// Point p = mBaiduMap.getProjection().toScreenLocation(ll);
			// p.y -= 50;//往上挪50个像素
			// LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
			// OnInfoWindowClickListener listener = null;
			//
			// button.setText("弹窗");
			// listener = new OnInfoWindowClickListener() {
			// public void onInfoWindowClick() {
			// Toast.makeText(mContext, "触发事件", Toast.LENGTH_LONG).show();
			// // LatLng llNew = new LatLng(ll.latitude + 0.005,
			// // ll.longitude + 0.005);
			// // marker.setPosition(llNew);
			// mBaiduMap.hideInfoWindow();
			// }
			// };
			// InfoWindow mInfoWindow = new InfoWindow(button, llInfo,
			// listener);
			// mBaiduMap.showInfoWindow(mInfoWindow);

			Bundle bundle = marker.getExtraInfo();
			if (bundle == null) {
				return false;
			}
			boolean isClusterMaker = bundle.getBoolean(IS_CLUSTER_MARKER);
			if (isClusterMaker) {// 聚合的
				double[] speedArray = bundle.getDoubleArray(SPEED_ARRAY);
				double[] speedUploadArray = bundle
						.getDoubleArray(SPEED_UPLOAD_ARRAY);
				if (speedArray == null || speedUploadArray == null) {
					Toast.makeText(mContext, "错误:无数据", Toast.LENGTH_LONG)
							.show();
					return false;
				}
				View v = View.inflate(mContext, R.layout.map_popup_style, null);
				mMapView.removeView(popupViewInMap);
				popupViewInMap = v;
				RelativeLayout llMoblie = (RelativeLayout) v
						.findViewById(R.id.rtl_mobile);
				ImageView mobile = (ImageView) v
						.findViewById(R.id.iv_map_mobile_icon);
				TextView mobileSpeed = (TextView) v
						.findViewById(R.id.tv_map_mobile_speed);
				TextView mobileUploadSpeed = (TextView) v
						.findViewById(R.id.tv_map_mobile_speed_upload);

				RelativeLayout llUnicom = (RelativeLayout) v
						.findViewById(R.id.rtl_unicom);
				ImageView unicom = (ImageView) v
						.findViewById(R.id.iv_map_unicom_icon);
				TextView unicomSpeed = (TextView) v
						.findViewById(R.id.tv_map_unicom_speed);
				TextView unicomUplostSpeed = (TextView) v
						.findViewById(R.id.tv_map_unicom_speed_upload);

				RelativeLayout llTelecom = (RelativeLayout) v
						.findViewById(R.id.rtl_telecom);
				ImageView telecom = (ImageView) v
						.findViewById(R.id.iv_map_telecom_icon);
				TextView telecomSpeed = (TextView) v
						.findViewById(R.id.tv_map_telecom_speed);
				TextView telecomUploadSpeed = (TextView) v
						.findViewById(R.id.tv_map_telecom_speed_upload);

				ImageView mapAd = (ImageView) v.findViewById(R.id.iv_map_ad);
				initAdImage(mapAd);

				if (speedArray[0] == 0 && speedUploadArray[0] == 0) {
					llMoblie.setVisibility(View.GONE);
				} else {
					mobile.setImageResource(R.drawable.yidong);
					mobileSpeed.setText("下载:"
							+ String.format("%.2f", speedArray[0] / 128.0)
							+ "Mbps");
					mobileUploadSpeed
							.setText("上传:"
									+ String.format("%.2f",
											speedUploadArray[0] / 128.0)
									+ "Mbps");
				}
				if (speedArray[1] == 0 && speedUploadArray[1] == 0) {
					llUnicom.setVisibility(View.GONE);
				} else {
					unicom.setImageResource(R.drawable.liantong);
					unicomSpeed.setText("下载:"
							+ String.format("%.2f", speedArray[1] / 128.0)
							+ "Mbps");
					unicomUplostSpeed
							.setText("上传:"
									+ String.format("%.2f",
											speedUploadArray[1] / 128.0)
									+ "Mbps");
				}
				if (speedArray[2] == 0 && speedUploadArray[2] == 0) {
					llTelecom.setVisibility(View.GONE);
				} else {
					telecom.setImageResource(R.drawable.dianxin);
					telecomSpeed.setText("下载:"
							+ String.format("%.2f", speedArray[2] / 128.0)
							+ "Mbps");
					telecomUploadSpeed
							.setText("上传:"
									+ String.format("%.2f",
											speedUploadArray[2] / 128.0)
									+ "Mbps");
				}
				addMyViewToMap(v, marker.getPosition());
			} else {// 单点
				double speed = bundle.getDouble(SINGLE_SPEED);
				double upspeed = bundle.getDouble(SINGLE_UPLOAD_SPEED);
				String operator = bundle.getString(OPERATOR);
				View v = View.inflate(mContext,
						R.layout.map_popup_single_style, null);
				mMapView.removeView(popupViewInMap);
				popupViewInMap = v;
				ImageView operatorIcon = (ImageView) v
						.findViewById(R.id.iv_map_operator_icon);
				TextView tvSpeed = (TextView) v.findViewById(R.id.tv_map_speed);
				TextView tvUplostSpeed = (TextView) v
						.findViewById(R.id.tv_map_speed_upload);

				ImageView mapAd = (ImageView) v.findViewById(R.id.iv_map_ad);// 广告
				initAdImage(mapAd);

				switch (APPConstant.OperatorType.getEnumFromString(operator)) {
				case MOBILE:
					operatorIcon.setImageResource(R.drawable.yidong);
					tvSpeed.setText("下载:"
							+ String.format("%.2f", speed / 128.0) + "Mbps");
					tvUplostSpeed.setText("上传:"
							+ String.format("%.2f", upspeed / 128.0) + "Mbps");
					break;
				case UNICOM:
					operatorIcon.setImageResource(R.drawable.liantong);
					tvSpeed.setText("下载:"
							+ String.format("%.2f", speed / 128.0) + "Mbps");
					tvUplostSpeed.setText("上传:"
							+ String.format("%.2f", upspeed / 128.0) + "Mbps");
					break;
				case TELECOM:
					operatorIcon.setImageResource(R.drawable.dianxin);
					tvSpeed.setText("下载:"
							+ String.format("%.2f", speed / 128.0) + "Mbps");
					tvUplostSpeed.setText("上传:"
							+ String.format("%.2f", upspeed / 128.0) + "Mbps");
					break;
				}
				addMyViewToMap(v, marker.getPosition());
			}
			return true;
		}
	};

	public void addMyViewToMap(View v, LatLng ll) {
		Point p = mBaiduMap.getProjection().toScreenLocation(ll);
		p.y -= 52;

		LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
		MapViewLayoutParams.Builder llb = new MapViewLayoutParams.Builder();
		llb.layoutMode(ELayoutMode.mapMode);
		llb.position(llInfo);
		mMapView.addView(v, llb.build());
	}

	/**
	 * 地图状态改变的监听
	 */
	private OnMapStatusChangeListener mapStatusChangeListener = new OnMapStatusChangeListener() {

		@Override
		public void onMapStatusChangeStart(MapStatus arg0) {

		}

		@Override
		public void onMapStatusChangeFinish(MapStatus ms) {
			if (currentZoom > ms.zoom) {// 判断是否放大的操作
				ZoomState = 2;// 缩小
			} else if (currentZoom < ms.zoom) {
				ZoomState = 1;
			} else {
				ZoomState = 0;
			}
			currentZoom = ms.zoom;
			if (!hasData)
				return;
			if (ms.zoom >= mMaxZoom) {
				pinMarkers(mMarkers);// 直接在上面加载覆盖物，不经过聚合
			} else {
				new CreateClusterTask().execute();
			}
		}

		@Override
		public void onMapStatusChange(MapStatus arg0) {
			// TODO Auto-generated method stub
		}
	};

	/**
	 * 开线程进行聚合计算
	 * 
	 * @author Sola
	 *
	 */
	class CreateClusterTask extends
			AsyncTask<Void, Void, List<MarkerOptionsWithSpeed>> {

		@Override
		protected List<MarkerOptionsWithSpeed> doInBackground(Void... params) {
			return mCluster.createCluster(mMarkers);// 进行聚合，
		}

		@Override
		protected void onPostExecute(List<MarkerOptionsWithSpeed> result) {
			super.onPostExecute(result);
			pinMarkersWithSpeed(result);// 加载聚合后的覆盖物
		}

	}

	// 设置百度定位的参数
	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(10000);// 设置发起定位请求的间隔时间为10s
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(false);// 返回的定位结果包含手机机头的方向
		mLocClient.setLocOption(option);// 设置定位参数
	}

	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}
