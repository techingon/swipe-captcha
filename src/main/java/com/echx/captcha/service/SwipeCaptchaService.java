package com.echx.captcha.service;

import com.echx.captcha.bean.UserCaptcha;
import com.echx.captcha.bean.CaptchaResult;

/**
 * 图片验证码
 * @author Echx
 *
 */
public interface SwipeCaptchaService {

	/**
	 * 生成验证图
	 * @param channel 场景定义
	 * @param uid 用户标识
	 * @param timestamp 时间戳
	 * @return
	 */
	UserCaptcha getCaptcha(String channel, String uid, long timestamp);

	/**
	 * 拿用户输入的距离和生成图的实际值对比做验证，需在一定误差范围内才通过
	 * @param channel
	 * @param uid
	 * @param uuid
	 * @param code 用户移动滑块的距离值，单位是px
	 * @return
	 */
	CaptchaResult verifyCaptcha(String channel, String uid, String uuid, String code);
}
