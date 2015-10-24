package com.kinth.mmspeed.hk;


import android.content.Context;
import android.graphics.Typeface;

import com.kinth.mmspeed.util.ApplicationController;

public class SingletonTypefaceClass {
	private static SingletonTypefaceClass instance = new SingletonTypefaceClass();
	private Context context = null;
	private Typeface font;
	
	private SingletonTypefaceClass() {
		context = ApplicationController.getInstance();
		font = Typeface.createFromAsset(context.getAssets(),
				"gothamrnd_bold.otf");
	}

	public static SingletonTypefaceClass getInstance() {
		if (instance == null)
			instance = new SingletonTypefaceClass();
		return instance;
	}

	public Typeface getFont() {
		return font;
	}
}
