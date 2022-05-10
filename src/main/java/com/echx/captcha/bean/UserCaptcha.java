package com.echx.captcha.bean;

import java.io.Serializable;

public class UserCaptcha implements Serializable {
    private String uuid;
    private SwipeCaptcha captcha_swipe;
    public UserCaptcha(String uuid, SwipeCaptcha captcha_swipe) {
        this.uuid = uuid;
        this.captcha_swipe = captcha_swipe;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public SwipeCaptcha getCaptcha_swipe() {
        return captcha_swipe;
    }

    public void setCaptcha_swipe(SwipeCaptcha captcha_swipe) {
        this.captcha_swipe = captcha_swipe;
    }
}
