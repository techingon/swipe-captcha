package com.echx.captcha.main;

import com.echx.captcha.bean.CaptchaResult;
import com.echx.captcha.bean.SwipeCaptcha;
import com.echx.captcha.bean.UserCaptcha;
import com.echx.captcha.core.ImageHelper;
import com.echx.captcha.core.SwipCaptchaBuilder;
import com.echx.captcha.service.SwipeCaptchaService;
import com.echx.captcha.service.SwipeCaptchaServiceImpl;
import org.slf4j.Logger;
import org.slf4j.impl.Log4jLoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class AppMain {
    private static final Logger log = new Log4jLoggerFactory().getLogger(AppMain.class.getName());

    public static void main(String[] args) throws InterruptedException {
        log.info("Go");
        SwipeCaptchaService scs = new SwipeCaptchaServiceImpl();

        String channel = "user-login";
        String uid = "u123456";
        UserCaptcha uc = scs.getCaptcha(channel, uid, System.currentTimeMillis());

//        Thread.sleep(1000);
        Thread.sleep(6000);
        CaptchaResult cr = scs.verifyCaptcha(channel, uid, uc.getUuid(), "200");

        log.info("验证结果,code:{},msg:{}",cr.getCode(),cr.getMsg());

        cr = scs.verifyCaptcha(channel, uid, uc.getUuid(), "200");

        log.info("重复验证结果,code:{},msg:{}",cr.getCode(),cr.getMsg());
    }

    private static void testGenerateCaptcha() {
        try {
            SwipCaptchaBuilder caF = SwipCaptchaBuilder.getInstance();

            for (int i = 0; i < 100; i++) {
                long b = System.currentTimeMillis();
                SwipeCaptcha ca = caF.cutImage();
                System.out.println(ca.getReceptImage().length / 1024 + "K,time:" + (System.currentTimeMillis() - b));
                ImageIO.write(ImageHelper.fromByte(ca.getLostImage()), ca.getType(),
                        new File("C:\\Users\\Desktop\\captcha\\", "lost_" + i + "." + ca.getType()));

                FileOutputStream fos = new FileOutputStream(
                        new File("C:\\Users\\Desktop\\captcha\\", "receive_" + i + "." + ca.getReceptType()));
                fos.write(ca.getReceptImage());
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
