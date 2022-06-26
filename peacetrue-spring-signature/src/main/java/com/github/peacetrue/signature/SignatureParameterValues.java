package com.github.peacetrue.signature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

/**
 * 签名相关参数值。
 *
 * @author peace
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignatureParameterValues implements Serializable {

    private static final long serialVersionUID = 0L;

    /** 唯一随机校验码，防止重放攻击 */
    private String nonce;
    /** 时间戳，用于控制客户端和服务端的时间差 */
    private Long timestamp;

    /**
     * 随机生成一个签名参数值。
     *
     * @param clientId 客户端标志
     * @return 签名参数值
     */
    public static SignatureParameterValues random(String clientId) {
        return new SignatureParameterValues(UUID.randomUUID().toString(), System.currentTimeMillis());
    }

    /**
     * 转换为表单参数。
     *
     * @param propertyNames 签名参数名
     * @return 表单参数
     */
    public Map<String, List<String>> toFormParams(SignatureParameterNames propertyNames) {
        Map<String, List<String>> params = new LinkedHashMap<>(2);
        params.put(propertyNames.getNonce(), Collections.singletonList(getNonce()));
        params.put(propertyNames.getTimestamp(), Collections.singletonList(String.valueOf(getTimestamp())));
        return params;
    }
}
