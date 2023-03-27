package cn.kgc.alipay_test.ServiceImpl;

import cn.kgc.alipay_test.service.AlipayService;
import cn.kgc.alipay_test.util.ResponseMessage;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AlipayServiceImpl implements AlipayService {

    @Value("${alipay.openapi}")
    private String openapi;
    @Value("${alipay.appid}")
    private String appid;
    @Value("${alipay.privatekey}")
    private String privatekey;
    @Value("${alipay.publickey}")
    private String publickey;

    public ResponseMessage alipay(double price){
        AlipayClient alipayClient = new DefaultAlipayClient(openapi,appid,privatekey,"json","utf-8",publickey,"RSA2");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //异步接收地址，仅支持http/https，公网可访问
        request.setNotifyUrl("http://v9nnus.natappfree.cc/alipayNotify");
        //同步跳转地址，仅支持http/https
        request.setReturnUrl("http://localhost/Spring-Boot/adduser.html");
        /******必传参数******/
        JSONObject bizContent = new JSONObject();
        //商户订单号，商家自定义，保持唯一性
        // 生成订单号
        String tradeNo = UUID.randomUUID().toString();
        bizContent.put("out_trade_no", tradeNo);
        //支付金额，最小值0.01元
        bizContent.put("total_amount", price);
        //订单标题，不可使用特殊符号
        bizContent.put("subject", "测试商品");
        //电脑网站支付场景固定传值FAST_INSTANT_TRADE_PAY
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        /******可选参数******/
        //bizContent.put("time_expire", "2022-08-01 22:00:00");

        //// 商品明细信息，按需传入
        //JSONArray goodsDetail = new JSONArray();
        //JSONObject goods1 = new JSONObject();
        //goods1.put("goods_id", "goodsNo1");
        //goods1.put("goods_name", "子商品1");
        //goods1.put("quantity", 1);
        //goods1.put("price", 0.01);
        //goodsDetail.add(goods1);
        //bizContent.put("goods_detail", goodsDetail);

        //// 扩展信息，按需传入
        //JSONObject extendParams = new JSONObject();
        //extendParams.put("sys_service_provider_id", "2088511833207846");
        //bizContent.put("extend_params", extendParams);

        // 返回调用结果
        ResponseMessage responseMessage = new ResponseMessage();
        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = null;
        try {
            response = alipayClient.pageExecute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            responseMessage.setCode("200");
            responseMessage.setData(response.getBody());
            responseMessage.setMsg("调用成功");
        } else {
            responseMessage.setCode("500");
            responseMessage.setData(response);
            responseMessage.setMsg("调用失败");
        }
        return responseMessage;
    }

    /**
     * 通知
     * @return
     */
    public ResponseMessage alipayNotify(Map map){
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            boolean flag = AlipaySignature.rsaCheckV2(map,publickey,"utf-8","RSA2");
            if(flag){
                responseMessage.setCode("200");
                responseMessage.setMsg("验签成功！");
                responseMessage.setData("success");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return responseMessage;
    };

}
