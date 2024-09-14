package com.example.mymall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.mymall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {
    //在支付宝创建的应用的id
    private  String app_id = "9021000140671394";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCDwL+Me0hCpWQQCkb+oe64tKYMqKFIVcvHvIZT3N9U8sV1DTFau3w3kNE85p+kYvycC6dTi8jvrpFRvy0nmxVSOLvjwynYv/DRfFkFtlMGt6F6W9Qb+mjwumFn9mddlrQfO7oTYPxbfaFNcenFziH3JbzxCYFy8Gjim2gB80YXINY8uxkMi5CyXRWWb6Wc2OmC4CjsFDmFP3FyZcWszn36rX/WLbjuzppZIFcyv2uHCvmZGuqPOpLOgcQR29o/Nz10PtqpQLpd40hto742C1+f0zV87PSxrqH1+sKbM0ylWLWnvzm7Jc09PezOj+LEIL0tlauChn3/2L7Hb08vFZtXAgMBAAECggEAEqHLdN5qVaJ2CB6r846K5+wEH34pcMVRUCnFBU8S9jxu2CLJe/9PjbwZGveyU9j7yEWNoC8Ad4TEYD8c76Dq3vnO+GK9JD3gnH9tR3WFQEnvuBIMaPh8pq503/Lko0OQrWoQ4nSwu2+ExCL74FFKVF0t3xhtCggdjQ5VIxF4NATmksgmKGlZFhZuNsEwBUWSqwvQPVwfpqdjTe0gGlcFaf1vLqhB+xsrzkcIRaz8Pre1qCu6fnn545tCZtOzr3fwB8smpgibWxy3eqQiJ7aGby/qx0hHXrokLED/RoDOLiKuI69rlIxeWB75soskVgXrq9tGe5LBLJpm6vLfU/HSQQKBgQD2ad7zQI1cfDhuCPml1/S26CcebI+FOCskfLL13rYRLUwM6CYgZO1s/qZ3ZtiY6n5sdCYKuRN5uP7ILGBtw2t1hcqhrZ6wE9temK5txI7k3P7xv2kz1ZTPFngnWnx/4NVeh5EZFLLufw+DbZvdpqNVM1a4PCUSjH+m2d8RmZrrrwKBgQCI4O1blsBmY3TcOPRSA7cChm9djW4GwjkJ98kvR/zPVFFlVlh0iYQtM/KtZQC7+EFMgyoOSnu4se42SWpW2dTa1RB+x0hJKhkT8gb4Y4vHK9aH0qTTj0UqUCzhiWmpG/7bpUZYOfOCJAO2kVLZcLw6E3dYSuBZdXWAhcQbXYps2QKBgQCzduoU+he6mxIyOcEw4zbsm+oZw3FH0l/BhDyGy7yIiBgScmWKRFeTgWfWIIZx4J9Q98rZFnAzv+N4ALH+X0x9XIL2+89OaDfmDodDlNuJndbkVMcXJF/r7251Ivb339Umss+fl661+r5et07EwN/GZsyrA8rwVnmGGLnZRKlLKwKBgDwpSIoZ/2TBUAq/SbuGgHiup+IhmHuGAFX+P8H1TQu/jqsQwX4vNxY7H+3QV/tGSUUc3W48lJaANihMKl4UDjdEsKRYaNAnnbAjj6a68iPPg9Jc5i9KaGcJqEBVCkjW0jSuNvXuOrY3qB4lT5ncsA6CodD/0A3hz4x2vtQcUAw5AoGBAKWx1fd9FIYRvc0LhAK+0/axURYRdheupEqxDMdN5UOhyM+LqRH87QD0X7M14Zvfe6d92Dd+kelOEH53pzG5CaCf5zvhUmW+RA0dsu+Bi7TPusald/kfvrDkoQjUOprEpTEoKpjE0sBdfXq88bPOOQi71CCHjxq8WeAjsLPZIA0c";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl1dT7HoDJNF/adAt4uyV5Fa+SiG6IQ0rZnyygAxLB/F4mXoqByJ6y80gvGDtsjriVXclEvPdDjMNV4iUcjW3kTNm00Hn3CBzorU9AqezqVXWalF+RZh7mAW3COr5Ht8wMlRk6RKzTXSCCNk4WCGl/tBlYiTZ4YwkzcMzFlXv2Ng7hEvjTElhlqEzLvRf0OCohawqxGM5UnPLZ4noNmNAnvZsrUoqYpdeZWXGEA7OJM2jy8UkOrgQbZshynzjFg26y9Gzfiw0d4ZhkIijuNAyM49hjSOYJQ7NladheIR6QG2sywHN23n0DKb7ZmauhxvPNEmU4A+D4DP7J7j12WJz/QIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url="http://c4da574.r10.cpolar.top";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url="http://order.mymall.com/orderList.html";

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "UTF-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

//        String result = alipayClient.pageExecute(alipayRequest).getBody();
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
//        System.out.println("支付宝的响应："+result);

        return result;

    }
}
