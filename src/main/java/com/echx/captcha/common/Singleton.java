package com.echx.captcha.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Singleton {

	/**
	 * Only For demo, please use redis on product env.
	 */
	public final static ConcurrentHashMap<String, Map> CaptchaSession = new ConcurrentHashMap();
	
}
