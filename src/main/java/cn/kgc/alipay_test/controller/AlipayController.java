package cn.kgc.alipay_test.controller;

import cn.kgc.alipay_test.ServiceImpl.AlipayServiceImpl;
import cn.kgc.alipay_test.util.ResponseMessage;
import com.alibaba.fastjson.JSON;
import com.alipay.api.kms.aliyun.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AlipayController {

    @Autowired
    private AlipayServiceImpl alipayServiceImpl;
    /**
     * 支付
     */
    @RequestMapping("alipay")
    public void alipay(HttpServletResponse response, Double price){
       ResponseMessage responseMessage = alipayServiceImpl.alipay(price);
       response.setContentType("text/html");
       response.setCharacterEncoding("utf-8");
        try {
            PrintWriter pw =response.getWriter();
            if("200".equals(responseMessage.getCode())){
                pw.print(responseMessage.getData());
                pw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知
     */

    @RequestMapping("alipayNotify")
    public String alipayNotify(HttpServletRequest request){
        Map param = new HashMap();
        Map<String,String[]> map = request.getParameterMap();


        String name = new String();
        //循环参数
        for(Map.Entry<String, String[]> entry : map.entrySet()){
            String values = "";
            String[] params = entry.getValue();
            name = entry.getKey();
            for (int i = 0 ; i<params.length ; i++){
                if(i==params.length-1){
                    values += params[i];
                }else {
                    values += params[i]+",";
                }
            }
            param.put(name,values);
        }
//        try {
//            values = new String(values.getBytes("iso-8859-1"),"utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }


        // remove sign
        param.remove("sign_type");
        ResponseMessage responseMessage = alipayServiceImpl.alipayNotify(param);
        return responseMessage.getData().toString();
    }
}
