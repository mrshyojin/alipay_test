package cn.kgc.alipay_test.service;

import cn.kgc.alipay_test.util.ResponseMessage;

import java.util.Map;

public interface AlipayService {

    public ResponseMessage alipay(double price);

    public ResponseMessage alipayNotify(Map map);
}
