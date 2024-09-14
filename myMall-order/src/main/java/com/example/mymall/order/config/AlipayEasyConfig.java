//package com.example.mymall.order.config;
//
//import com.alipay.easysdk.factory.Factory;
//import com.alipay.easysdk.kernel.Config;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AlipayEasyConfig {
//
//    @Bean
//    public Config factoryConfig() {
//        Config config = new Config();
//        config.protocol = "https";
//        config.gatewayHost = "openapi-sandbox.dl.alipaydev.com/gateway.do";
//        config.signType = "RSA2";
//
//        config.appId = "9021000140671394";
//
//        // 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
//        config.merchantPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCQWgsNkTHqhMAGt5kIRYVg07HpovKWvKKA/O946xhnXpeZ5OgR6K80MNf/irCaZNuWNp5aM0rNL1HmEbgtcMcKuXvVrB7pR32nbFVpE2qbDwmzj3bYrRasSnIUTtDMnjouadcqEf+q5zHEOJ+4+IWSYwo6W1+uyddgCmbAQfUDPYk1N4MW+w0QGQbA7PJ4c7Zn/0grLAWN6P6mjQFV2eZ9L4KegWwerMdNHuQd1/8QZADWpBSdTcIJ4WpdJICaTbWCooME2210Qk6Ft4qTXIegEA8dHfrspSJJTJ6iRCEytg68BlbHaa8XZgwfDYoEQC/4sFKuOoo91lqKV08H5e5DAgMBAAECggEACIUH+fkD1S+YlczoP1C7GofkDEINx0/C15IWIDDf89nlYLPd7pCpJo9rcC2jVrl1LFx05CX62DlewBFzqVrqpHtmD6pk+Ke7htIiCF2ZASXWEL766Lbui6SpLwGn2SvNi0e750PArzmN+mLNKoGggI9P3zndX9eR2p1Ciz5ZU6OK+pNVOfNXjokSQp8cZXp3fXQ4I5wdqRWsfsR/pSLU0lKm2YYzlyLrNadBXUp0G7UN5RXV31quylQ/uzmDnP+ZtdsGVK4hbOWmk0+t9EXUVeXvRUB6OutQTtEfzTmw+A0pjA4Muo1K+0CsauD6RLUb9H9vuh9r8MuGSFE8DQECgQKBgQDrES5f4XUJa9Hn49kwIjaiSfLGR2xFbxjc8LNZO+ndpdgzqxH4eElrgiZT7qSDmz7kIBUrYPFJQ1sTRCCteyShzlg05mkHJDUBLS4QApPKSa2nPvegDUrCmAbbgKAqRu6ik7m9vLAorexrPLCai5RN2gcOt4b/AQq5rHu6864vMwKBgQCdNNNov/FlvBAAtDeQQ0C4hhwFeZKpRgm9WJjmvFJjCjSM4zeExO50YB58SwKtnU7Vn0eAUkwuUU9nSA458BblR29bHExnK9ggAySHzjeftHry6H3AkjZrurVOwbUG9F/OB3+j2zP/GH0XhAZKbdvBtzCH5dCHQImBX5xIN1uEsQKBgQDAvU2xAEPefbGW2ZPzi5oQZ2HQgi+cnwNb5WCpAh83xgwIEXSirr5EkGqs1ze9UUW39zZkYxB0ae37VhkVJatboL1iYqorWNy9IjZqmjJj4xWn1FFlwoBSHiVBRa4N/Cse0tGxaU1njXQUWabUkS4Ax4yR3Jmr7UbGB09McxGXUQKBgF4kt4IgiGDDadXyFuBG1ihq77b7oi/K6KRCvXZYSwJSpzvECohxItgAg+EtTpJIBWD6vOgE4OhP+zw/s31Q4XU2/WHTpZGXeRtapnLJtRfoDkUPo9dB+GM1ccSTTmS2zfTlGrQ23oGi2a7OerHx7kuo9+A6aVHF/VLDviSlOiJRAoGAU8YOyK6rHuz9ALttwEAEOo2H+On6EKorBEHnbyK9p2mGu9G6HTsFj/McLjOGUxTDXauII+fEjBwVfxY0jmkxgnv5eY6zZxEHEKUPx0O0RrgVRdbWEEBcRjiOnUUTfOWS3CFXc5XxT2pj1Hh5S8jyFRAgrG+ZWETfbUOXgPu0Bfo=";
//        //注：证书文件路径支持设置为文件系统中的路径或CLASS_PATH中的路径，优先从文件系统中加载，加载失败后会继续尝试从CLASS_PATH中加载
//        config.merchantCertPath = "static/appPublicCert.crt";
//        config.alipayCertPath = "static/alipayPublicCert.crt";
//        config.alipayRootCertPath = "static/alipayRootCert.crt";
//
//        //注：如果采用非证书模式，则无需赋值上面的三个证书路径，改为赋值如下的支付宝公钥字符串即可
//        // config.alipayPublicKey = "<-- 请填写您的支付宝公钥，例如：MIIBIjANBg... -->";
//
//        //可设置异步通知接收服务地址（可选）
//        config.notifyUrl = "<-- 请填写您的支付类接口异步通知接收服务地址，例如：https://www.test.com/callback -->";
//
//        return config;
//    }
//}
