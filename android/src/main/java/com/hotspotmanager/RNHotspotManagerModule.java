
package com.hotspotmanager;

// import com.facebook.react.bridge.ReactApplicationContext;
// import com.facebook.react.bridge.ReactContextBaseJavaModule;
// import com.facebook.react.bridge.ActivityEventListener;
// import com.facebook.react.bridge.ReactMethod;
// import com.facebook.react.bridge.Callback;

import com.facebook.react.bridge.*;

import android.util.Log;

import android.app.Activity;
import android.content.ComponentName;
import android.net.Uri;

import android.os.Bundle;
import android.provider.Settings;
import java.lang.reflect.Method;
import java.lang.Exception;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class RNHotspotManagerModule extends ReactContextBaseJavaModule implements ActivityEventListener {

	private static final int REQUEST_CODE = 78;
	private static final String ATTR_ACTION = "action";
	private static final String ATTR_TYPE = "type";
	private static final String ATTR_CATEGORY = "category";
	private static final String TAG_EXTRA = "extra";
	private static final String ATTR_DATA = "data";
	private static final String ATTR_FLAGS = "flags";
	private static final String ATTR_PACKAGE_NAME = "packageName";
	private static final String ATTR_CLASS_NAME = "className";
	Promise promise;
	String ssid;
	private final ReactApplicationContext reactContext;

	public RNHotspotManagerModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
		reactContext.addActivityEventListener(this);
	}

	@Override
	public String getName() {
		return "RNHotspotManager";
	}

	public void writePermission() {
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		// if (!Settings.System.canWrite(getReactApplicationContext())) {
		Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
				Uri.parse("package:" + getReactApplicationContext().getPackageName()));
		getReactApplicationContext().startActivityForResult(intent, REQUEST_CODE, null);
		// }
		// }
	}

	public void _fireHotSpot() {
		Boolean result = setWifiApEnabled(true, this.ssid);
		WritableMap params = Arguments.createMap();
		if (result == true) {
			params.putString("status", "Created Network!");
			// this.successCallback.invoke(true, params);
		} else {
			params.putString("status", "Failed");
			// this.errorCallback.invoke(false, params);
		}
		this.promise.resolve(params);
	}

	@ReactMethod
	public void createHotspot(String ssid, final Promise promise) {
		this.promise = promise;
		this.ssid = ssid;
		try {
			if (!Settings.System.canWrite(getReactApplicationContext())) {
				this.writePermission();
			} else {
				this._fireHotSpot();
			}
		} catch (Exception e) {
			this.promise.reject("Error");
			// errorCallback.invoke(false, e.getMessage());
		}
	}

	// Use the class below to change/check the Wifi hotspot setting:

	// check whether wifi hotspot on or off
	public static boolean isApOn(Context context) {
		WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
		try {
			Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
			method.setAccessible(true);
			return (Boolean) method.invoke(wifimanager);
		} catch (Throwable ignored) {
		}
		return false;
	}

	public boolean setWifiApEnabled(boolean enabled, String ssid) {
		WifiManager mWifiManager = (WifiManager) getReactApplicationContext()
				.getSystemService(getReactApplicationContext().WIFI_SERVICE);

		WifiConfiguration wificonfiguration = new WifiConfiguration();
		// WifiConfiguration wificonfiguration =mwifi
		wificonfiguration.SSID = ssid;
		// wificonfiguration.preSharedKey=

		if (enabled) { // disable WiFi in any case
			mWifiManager.setWifiEnabled(false);
		}

		try {

			// TODO comment from here
			/*
			 * Method getWifiApConfigurationMethod =
			 * mWifiManager.getClass().getMethod("getWifiApConfiguration"); Object config =
			 * getWifiApConfigurationMethod.invoke(mWifiManager);
			 */

			// configuration = null works for many devices
			Method setWifiApEnabledMethod = mWifiManager.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, boolean.class);
			return (Boolean) setWifiApEnabledMethod.invoke(mWifiManager, wificonfiguration, enabled);
		} catch (Exception e) {
			// System.out.println(e);
			return false;
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
	}

	@Override
	public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) { 
		if (requestCode == REQUEST_CODE) {
			this._fireHotSpot();
		}
	}

}
