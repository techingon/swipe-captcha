# swipe-captcha

1000行代码实现滑动验证码，性能也不差，欢迎体验，代码全部开放。

滑动验证码在很多网站流行，一方面对用户体验来说，比较新颖，操作简单，另一方面相对图形验证码来说，安全性并没有很大的降低。当然到目前为止，没有绝对的安全验证，只是不断增加攻击者的绕过成本。

## 滑动验证码的核心流程：
* 后端随机生成抠图和带有抠图阴影的背景图片，后台保存随机抠图位置坐标
* 前端实现滑动交互，将抠图拼在抠图阴影之上，获取到用户滑动距离值，比如上述示例
* 前端将用户滑动距离值传入后端，后端校验误差是否在容许范围内。

使用方法如下：
```
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
```

更多介绍请关注：https://blog.csdn.net/feng_zi0yhv/article/details/124695294
