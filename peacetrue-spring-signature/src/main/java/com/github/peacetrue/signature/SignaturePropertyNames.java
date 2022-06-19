package com.github.peacetrue.signature;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 签名相关属性名。
 *
 * @author peace
 **/
@Getter
@Setter
public class SignaturePropertyNames {

    /** 客户端标志 */
    private String clientId = "clientId";
    /** 唯一随机校验码，防止重放攻击 */
    private String nonce = "nonce";
    /** 时间戳，用于控制客户端和服务端的时间差 */
    private String timestamp = "timestamp";
    /** 请求参数的签名 */
    private String signature = "signature";

}
