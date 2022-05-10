package com.echx.captcha.service;


import com.echx.captcha.bean.CachedCaptcha;
import com.echx.captcha.bean.UserCaptcha;
import com.echx.captcha.bean.CaptchaResult;
import com.echx.captcha.bean.SwipeCaptcha;
import com.echx.captcha.common.ReturnValue;
import com.echx.captcha.common.Singleton;
import com.echx.captcha.core.SwipCaptchaBuilder;
import org.slf4j.Logger;
import org.slf4j.impl.Log4jLoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class SwipeCaptchaServiceImpl implements SwipeCaptchaService {
    private static final Logger log = new Log4jLoggerFactory().getLogger(SwipeCaptchaServiceImpl.class.getName());
    private static final SwipCaptchaBuilder caF = SwipCaptchaBuilder.getInstance();
    private static final String AUTH_KEY = "picAuth_";

    @Override
    public UserCaptcha getCaptcha(final String channel, final String uid, final long timestamp) {
        final String key = getKey(channel, uid);
        //验证当前uid用户channel渠道下是否缓存有验证信息
        Map<String, String> variInfo = Singleton.CaptchaSession.get(key);
        if (variInfo != null && variInfo.keySet().size() > 0) {
            CachedCaptcha captchaCodeOld = new CachedCaptcha();
            captchaCodeOld = captchaCodeOld.fromMap(variInfo);
            if (captchaCodeOld.getTimestamp() >= timestamp) {//时间戳小的时候，sign失效，不能访问验证码
                log.warn("时间戳无效：{},{}", channel, uid, timestamp);
                return null;
            }
        }
        SwipeCaptcha captcha_swipe;
        //生成滑动验证码
        try {
            captcha_swipe = caF.cutImage();
        } catch (IOException e) {
            throw new IllegalStateException("Fail to generate swipe picture");
        }
        String code = Integer.toString(captcha_swipe.getPosition()[0]);
        String uuid = UUID.randomUUID().toString();
        log.info("Generated captcha distance：{}, uid:{}, uuid:{}", code, uid, uuid);
        CachedCaptcha vCode = new CachedCaptcha();
        vCode.setCode(code);
        vCode.setUuid(uuid);
        vCode.setTimestamp(timestamp);
        final Map<String, String> vCodeMap = vCode.toMap();
        Singleton.CaptchaSession.put(key, vCodeMap);
        return new UserCaptcha(uuid, captcha_swipe);
    }

    @Override
    public CaptchaResult verifyCaptcha(String channel, String uid, String uuid, String code) {
        CaptchaResult ret = new CaptchaResult();
        ret.setCode(ReturnValue.ERR_WRONG);
        ret.setMsg("验证失败");
        String key = getKey(channel, uid);
        Map<String, String> variInfo = Singleton.CaptchaSession.get(key);
        boolean result = false;//验证结果
        long intervalTime = 0;//获取与验证的时间间隔
        if (variInfo != null && variInfo.keySet().size() > 0 && uuid != null && code != null) {
            CachedCaptcha vCode = new CachedCaptcha();
            vCode.fromMap(variInfo);
            result = vCode.varifyEx(uuid, code);
            intervalTime = System.currentTimeMillis() - vCode.getTimestamp();
//          Singleton.CaptchaSession.addTryCount(key, "tryCount");//校验次数加1
        }
        if (result) {
            boolean checkIntervalTime = checkIntervalTime(intervalTime);
            if (checkIntervalTime) {
                ret.setCode(ReturnValue.OK);
                ret.setMsg("验证通过");
            } else {
                ret.setCode(ReturnValue.ERR_EXPIRED);
                ret.setMsg("验证过期");
                log.warn("验证过期:{},{},{},{},{}", channel, uid, uuid, code, intervalTime);
            }

        }
        //定义为只能验证一次，可以扩展规则
        Singleton.CaptchaSession.remove(key);
        return ret;
    }

    /**
     * 验证有效性判断
     *
     * @param intervalTime 验证有效时间 单位毫秒
     * @return
     */
    private static boolean checkIntervalTime(long intervalTime) {
        /**
         * 供测试，设置有效期只有5s, 建议设置为60s
         */
        return intervalTime < 5000;
    }

    private static String getKey(String channel, String uid) {
        return channel + uid;
    }
}