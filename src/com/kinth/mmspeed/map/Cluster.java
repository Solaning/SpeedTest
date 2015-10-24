package com.kinth.mmspeed.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;
import com.kinth.mmspeed.R;

public class Cluster {
	private Context mContext;
	private MapView mMapView;
//	private Boolean isAverageCenter;
	private int mGridSize;
	private double mDistance;//距离400000
	
	//当前聚合后元素的集合
	private List<ClusterMarker> mMarkers;
	
	public Cluster(Context mContext,MapView mapView
//			,Boolean isAverageCenter
			,int mGridSize,double mDistance) {
		this.mContext = mContext;
		this.mMapView = mapView;
//		this.isAverageCenter = isAverageCenter;
		this.mGridSize = mGridSize;
		this.mDistance = mDistance;
		mMarkers = new ArrayList<ClusterMarker>();
	}

	public List<MarkerOptionsWithSpeed> createCluster(List<MarkerOptionsWithPoint> markerList){
		this.mMarkers.clear();
		for(MarkerOptionsWithPoint markerOptionsWithPoint : markerList){
			addCluster(markerOptionsWithPoint);
		}
		
		List<MarkerOptionsWithSpeed> itemList = new ArrayList<MarkerOptionsWithSpeed>();
		
		for(ClusterMarker cm : mMarkers){//对每一个聚合后的ClusterMarker进行操作
			MarkerOptions markerOptions = null;
			BitmapDescriptor bmDescriptor = setClusterDrawable(cm);
			markerOptions =  new MarkerOptions().position(cm.getmCenter()).icon(bmDescriptor)
						.draggable(true);
			itemList.add(new MarkerOptionsWithSpeed(markerOptions,cm.getSpeed(),cm.getUpspeed()));
		}
		return itemList;
	}
	
	private void addCluster(MarkerOptionsWithPoint marker){
		LatLng markGeo = marker.getMarkerOptions().getPosition();
		if(mMarkers.size() == 0){
			ClusterMarker clusterMarker = new ClusterMarker();
			clusterMarker.AddMarker(marker);
			
			LatLng southwest = new LatLng(markGeo.latitude, markGeo.longitude);
			LatLng northeast = new LatLng(markGeo.latitude, markGeo.longitude);
			LatLngBounds bound = new LatLngBounds.Builder().include(northeast)
					.include(southwest).build();
			bound = MapUtils.getExtendedBounds(mMapView, bound, mGridSize);
			
			clusterMarker.setmGridBounds(bound);
			mMarkers.add(clusterMarker);
		}else{
			ClusterMarker clusterContain = null;
			double distance = mDistance;
			for(ClusterMarker clusterMarker : mMarkers){
				LatLng center = clusterMarker.getmCenter();
				double d = DistanceUtil.getDistance(center, marker.getMarkerOptions().getPosition());
				if(d < distance){
					distance = d;
					clusterContain = clusterMarker;
				}
			}
			if(clusterContain == null || !isMarkersInCluster(markGeo, clusterContain.getmGridBounds())){
//				Log.e("一个全新的ClusterMarker点，无法归到现有的ClusterMarker中","一个全新的ClusterMarker点，无法归到现有的ClusterMarker中");
				ClusterMarker clusterMarker = new ClusterMarker();
				clusterMarker.AddMarker(marker);
				
				LatLng southwest = new LatLng(markGeo.latitude, markGeo.longitude);
				LatLng northeast = new LatLng(markGeo.latitude, markGeo.longitude);
				LatLngBounds bound = new LatLngBounds.Builder().include(northeast)
						.include(southwest).build();
				bound = MapUtils.getExtendedBounds(mMapView, bound, mGridSize);
				clusterMarker.setmGridBounds(bound);
				
				mMarkers.add(clusterMarker);
			}else{//该点归到了某个ClusterMarker中
				clusterContain.AddMarker(marker);
			}
		}
	}
	
	private BitmapDescriptor setClusterDrawable(ClusterMarker clusterMarker){
		View drawableView = LayoutInflater.from(mContext).inflate(
				R.layout.drawable_mark, null);
		TextView text = (TextView) drawableView.findViewById(R.id.drawble_mark);
		int markNum = clusterMarker.getmMarkers().size();
		if(markNum>=2){
			text.setText(markNum+"");
			if(markNum<11){
				text.setBackgroundResource(R.drawable.m0);
			}else if(markNum>10&&markNum<21){
				text.setBackgroundResource(R.drawable.m1);
			}else if(markNum>20&&markNum<31){
				text.setBackgroundResource(R.drawable.m2);
			}else if(markNum>30&&markNum<41){
				text.setBackgroundResource(R.drawable.m3);
			}else{
				text.setBackgroundResource(R.drawable.m4);
			}
			Bitmap bitmap = MapUtils.convertViewToBitmap(drawableView);
			return BitmapDescriptorFactory.fromBitmap(bitmap);
		}else{
			return BitmapDescriptorFactory.fromResource(R.drawable.icon_mark);
		}
	}
	
	private Boolean isMarkersInCluster(LatLng markerGeo,LatLngBounds bound){
		if(markerGeo.latitude > bound.southwest.latitude
				&&markerGeo.latitude < bound.northeast.latitude
				&&markerGeo.longitude < bound.northeast.longitude
				&&markerGeo.longitude > bound.southwest.longitude){
			return true;
		}
		return false;
	}
}
