package com.example.mymall.thirdparty.controller;

import com.example.common.utils.HttpUtils;
import com.example.common.utils.R;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("thirdParty/code")
public class CodeController {


    @RequestMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        String code = sendCodeYanVo(phone);

        return R.ok().put("data", code);
    }

    //国阳云短信
    public String sendCodeYanVo(String phone){
        String host = "https://gyytz.market.alicloudapi.com";
        String path = "/sms/smsSend";
        String method = "POST";
        String appcode = "6b08dd970d974bf0ae32964c071b8eaa";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        Integer code = RandomUtils.nextInt(999999);
        querys.put("param", "**code**:" + code + ",**minute**:5");
        System.out.println("手机号为：" + phone + "验证码为：" + code + "有效时间为：5分钟");

//smsSignId（短信前缀）和templateId（短信模板），可登录国阳云控制台自助申请。参考文档：http://help.guoyangyun.com/Problem/Qm.html

        querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
        querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从\r\n\t    \t* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java\r\n\t    \t* 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            //因为短信发送要钱,所以先停掉
//            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(code);
    }
}
