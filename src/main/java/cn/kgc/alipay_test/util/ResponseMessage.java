package cn.kgc.alipay_test.util;

import lombok.Data;

@Data
public class ResponseMessage {
    private String code;
    private String msg ;
    private Object data;
}
