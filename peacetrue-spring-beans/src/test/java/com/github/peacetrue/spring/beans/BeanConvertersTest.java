package com.github.peacetrue.spring.beans;

import com.github.peacetrue.bean.StringBeanVisitor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author peace
 **/
@Slf4j
class BeanConvertersTest {
    private final User entity = new EasyRandom().nextObject(User.class);

    @Test
    void basic() {
        log.info("entity:\n{}", StringBeanVisitor.DEFAULT.visitRoot(entity));
        UserVO vo = BeanConverters.ByManual.INSTANCE.convert(entity, UserVO.class);
        Assertions.assertEquals(vo, BeanConverters.ByJackson.INSTANCE.convert(entity, UserVO.class));
        Assertions.assertEquals(vo, BeanConverters.BySpringCopyProperties.INSTANCE.convert(entity, UserVO.class));
        Assertions.assertEquals(vo, BeanConverters.BySpringBeanCopierManual.INSTANCE.convert(entity, UserVO.class));
        Assertions.assertEquals(vo, BeanConverters.BySpringBeanCopierGeneric.INSTANCE.convert(entity, UserVO.class));
        Assertions.assertEquals(vo, BeanConverters.BySpringBeanCopierGenericUseConverter.INSTANCE.convert(entity, UserVO.class));
    }

}
