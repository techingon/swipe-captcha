package com.echx.captcha.bean;

import org.slf4j.Logger;
import org.slf4j.impl.Log4jLoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class CachedCaptcha {
    private static final Logger log = new Log4jLoggerFactory().getLogger(CachedCaptcha.class.getName());
    private String uuid;
    private String code;
    private int tryCount;
    private String sign;
    private long timestamp;
    private int type = 0;

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(6);
        map.put("uuid", uuid);
        map.put("code", code);
        map.put("tryCount", String.valueOf(tryCount));
        map.put("sign", sign);
        map.put("timestamp", Long.toString(timestamp));
        map.put("type", Integer.toString(type));
        return map;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CachedCaptcha fromMap(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
        } else {
            this.uuid = map.get("uuid");
            this.code = map.get("code");
//			this.tryCount = StringUtil.convertInt(map.get("tryCount"), 0);
            this.sign = map.get("sign");
//			this.timestamp = StringUtil.convertLong(map.get("timestamp"), -1);
//			this.type = StringUtil.convertInt(map.get("type"), -1);
        }
        return this;
    }

    public boolean varifyEx(String uuid, String code) {
        int swipeOver = Math.abs(Integer.valueOf(this.code) - Integer.valueOf(code));
        log.info("uuid:{},滑动误差值：{}", uuid, swipeOver);
        /**
         * 供本地测试，设置的是100，建议值5，单位是像素px
         */
        return this.tryCount < 1 && uuid.equals(this.uuid) && swipeOver < 100;
    }
}