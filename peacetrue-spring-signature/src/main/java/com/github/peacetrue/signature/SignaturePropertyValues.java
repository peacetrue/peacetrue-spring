package com.github.peacetrue.signature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

/**
 * 签名相关属性值。
 *
 * @author peace
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignaturePropertyValues implements Serializable {

    private static final long serialVersionUID = 0L;

    /** 客户端标志 */
    private String clientId;
    /** 唯一随机校验码，防止重放攻击 */
    private String nonce;
    /** 时间戳，用于控制客户端和服务端的时间差 */
    private Long timestamp;

    /**
     * 随机生成一个签名属性值。
     *
     * @param clientId 客户端标志
     * @return 签名属性值
     */
    public static SignaturePropertyValues random(String clientId) {
        return new SignaturePropertyValues(clientId, UUID.randomUUID().toString(), System.currentTimeMillis());
    }

    /**
     * 转换为表单参数。
     *
     * @param propertyNames 签名属性名
     * @return 表单参数
     */
    public Map<String, List<String>> toFormParams(SignaturePropertyNames propertyNames) {
        Map<String, List<String>> params = new LinkedHashMap<>(3);
        params.put(propertyNames.getClientId(), Collections.singletonList(getClientId()));
        params.put(propertyNames.getNonce(), Collections.singletonList(getNonce()));
        params.put(propertyNames.getTimestamp(), Collections.singletonList(String.valueOf(getTimestamp())));
        return params;
    }
}
