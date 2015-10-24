package com.kinth.mmspeed.map;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.View;
import android.view.View.MeasureSpec;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

public class MapUtils {
	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI = 6.28318530712; // 2*PI
	static double DEF_PI180 = 0.01745329252; // PI/180.0
	static double DEF_R = 6370693.5; // radius of earth

	public static LatLngBounds getExtendedBounds(MapView map, LatLngBounds bounds,
			Integer gridSize) {
		LatLngBounds tbounds = cutBoundsInRange(bounds);

		Projection projection = map.getMap().getProjection();
		Point pixelNE = projection.toScreenLocation(tbounds.northeast);
		Point pixelSW = projection.toScreenLocation(tbounds.southwest);
		pixelNE.x += gridSize;
		pixelNE.y += gridSize;
		pixelSW.x -= gridSize;
		pixelSW.y -= gridSize;
		
		LatLng newNE = projection.fromScreenLocation(new Point(pixelNE.x,pixelNE.y));
		LatLng newSW = projection.fromScreenLocation(new Point(pixelSW.x, pixelSW.y));
		return new LatLngBounds.Builder().include(newNE).include(newSW).build();
	}

	public static LatLngBounds cutBoundsInRange(LatLngBounds bounds) {
		double maxX = getRange(bounds.northeast.latitude,-74, 74);
		double minX = getRange(bounds.southwest.latitude,-74, 74);
		double maxY = getRange(bounds.northeast.longitude,-180,180);
		double minY = getRange(bounds.northeast.longitude, -180,180);
		LatLng southwest = new LatLng(minX, minY);
		LatLng northeast = new LatLng(maxX, maxY);
		return new LatLngBounds.Builder().include(northeast)
				.include(southwest).build();
	}

	public static double getRange(double i, int mix, int max) {
		i = Math.max(i, mix);
		i = Math.min(i, max);
		return i;
	}

	public static double GetShortDistance(double lon1, double lat1, double lon2,
			double lat2) {
		double ew1, ns1, ew2, ns2;
		double dx, dy, dew;
		double distance;

		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;

		dew = ew1 - ew2;

		if (dew > DEF_PI)
			dew = DEF_2PI - dew;
		else if (dew < -DEF_PI)
			dew = DEF_2PI + dew;
		dx = DEF_R * Math.cos(ns1) * dew; 
		dy = DEF_R * (ns1 - ns2); 

		distance = Math.sqrt(dx * dx + dy * dy);
		return distance;
	}

	public static double GetLongDistance(double lon1, double lat1, double lon2,
			double lat2) {
		double ew1, ns1, ew2, ns2;
		double distance;

		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;

		distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1)
				* Math.cos(ns2) * Math.cos(ew1 - ew2);

		if (distance > 1.0)
			distance = 1.0;
		else if (distance < -1.0)
			distance = -1.0;

		distance = DEF_R * Math.acos(distance);
		return distance;
	}

	public static Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		return bitmap;
	}
}
