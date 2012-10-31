package com.rarnu.findaround.common;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.telephony.TelephonyManager;

import com.rarnu.findaround.GlobalInstance;

/**
 * 设备操作基础工具类
 * 
 * @author rarnu
 */
public class DeviceUtils {

	private static final String BUILD_PROP = "/system/build.prop";

	public static final String RO_BUILD_ID = "ro.build.id";
	public static final String RO_BUILD_VERSION_SDK = "ro.build.version.sdk";
	public static final String RO_BUILD_VERSION_RELEASE = "ro.build.version.release";
	public static final String RO_PRODUCT_MODEL = "ro.product.model";
	public static final String RO_PRODUCT_BRAND = "ro.product.brand";
	public static final String RO_PRODUCT_NAME = "ro.product.name";
	public static final String RO_PRODUCT_DEVICE = "ro.product.device";
	public static final String RO_PRODUCT_BOARD = "ro.product.board";
	public static final String RO_PRODUCT_CPU_ABI = "ro.product.cpu.abi";
	public static final String RO_PRODUCT_CPU_ABI2 = "ro.product.cpu.abi2";
	public static final String RO_PRODUCT_MANUFACTURER = "ro.product.manufacturer";
	public static final String RO_BOARD_PLATFORM = "ro.board.platform";
	public static final String RO_BUILD_DESCRIPTION = "ro.build.description";
	public static final String RO_PRODUCT_VERSION = "ro.product.version";

	private static List<String> buildProp = null;

	public static DeviceInfo getDeviceInfo() {
		DeviceInfo info = null;
		if (buildProp == null) {
			try {
				buildProp = FileUtils.readFile(BUILD_PROP);
			} catch (IOException ioe) {
				return null;
			}
		}

		info = new DeviceInfo();
		info.roBuildId = findPropValue(RO_BUILD_ID);
		info.roBuildVersionSdk = findPropValue(RO_BUILD_VERSION_SDK);
		info.roBuildVersionRelease = findPropValue(RO_BUILD_VERSION_RELEASE);
		info.roProductModel = findPropValue(RO_PRODUCT_MODEL);
		info.roProductBrand = findPropValue(RO_PRODUCT_BRAND);
		info.roProductName = findPropValue(RO_PRODUCT_NAME);
		info.roProductDevice = findPropValue(RO_PRODUCT_DEVICE);
		info.roProductBoard = findPropValue(RO_PRODUCT_BOARD);
		info.roProductCpuAbi = findPropValue(RO_PRODUCT_CPU_ABI);
		info.roProductCpuAbi2 = findPropValue(RO_PRODUCT_CPU_ABI2);
		info.roProductManufacturer = findPropValue(RO_PRODUCT_MANUFACTURER);
		info.roBoardPlatform = findPropValue(RO_BOARD_PLATFORM);
		info.roBuildDescription = findPropValue(RO_BUILD_DESCRIPTION);
		info.roProductVersion = findPropValue(RO_PRODUCT_VERSION);

		return info;
	}

	public static String getBuildProp(String key) {

		if (buildProp == null) {
			try {
				buildProp = FileUtils.readFile(BUILD_PROP);
			} catch (IOException ioe) {
				return null;
			}
		}
		return findPropValue(key);
	}

	public static String getDeviceUniqueId(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId() + "_" + tm.getSubscriberId();
	}

	private static String findPropValue(String key) {
		String tmp;
		String val = null;
		int idx = -1;
		for (String s : buildProp) {
			idx = s.indexOf("=");
			if (idx < 0) {
				continue;
			}
			tmp = s.substring(0, idx);
			if (tmp.equals(key)) {
				val = s.substring(idx + 1);
				break;
			}
		}
		return val;
	}

	public static int getAppVersionCode(Context context) {
		int versionCode = 0;
		try {
			PackageInfo pi = GlobalInstance.pm.getPackageInfo(
					context.getPackageName(), 0);
			versionCode = pi.versionCode;

		} catch (Exception e) {

		}
		return versionCode;
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageInfo pi = GlobalInstance.pm.getPackageInfo(
					context.getPackageName(), 0);
			versionName = pi.versionName;

		} catch (Exception e) {

		}
		return versionName;
	}

}