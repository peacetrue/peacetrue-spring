package com.github.peacetrue.spring.beans;


import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;

import java.io.IOException;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author peace
 **/
public class BeanConverters {

    interface Converter {
        <S, T> T convert(S source, Class<T> targetType);
    }

    public enum ByManual implements Converter {
        INSTANCE;

        @Override
        @SuppressWarnings({"unchecked"})
        public <S, T> T convert(S source, Class<T> targetType) {
            if (!(source instanceof User) || !UserVO.class.isAssignableFrom(targetType)) {
                throw new UnsupportedOperationException();
            }
            User user = (User) source;
            UserVO target = new UserVO();
            target.setId(user.getId());
            target.setName(user.getName());
            target.setPassword(user.getPassword());
            target.setCreatorId(user.getCreatorId());
            target.setCreatedTime(user.getCreatedTime());
            return (T) target;
        }
    }

    private static final JsonMapper jsonMapper = new JsonMapper();

    static {
        jsonMapper.registerModule(new JavaTimeModule());
    }

    public enum ByJackson implements Converter {
        INSTANCE;

        @Override
        public <S, T> T convert(S source, Class<T> targetType) {
            try {
                return jsonMapper.readValue(jsonMapper.writeValueAsBytes(source), targetType);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public enum BySpringCopyProperties implements Converter {
        INSTANCE;

        @Override
        public <S, T> T convert(S source, Class<T> targetType) {
            T target = org.springframework.beans.BeanUtils.instantiateClass(targetType);
            org.springframework.beans.BeanUtils.copyProperties(source, target);
            return target;
        }
    }

    private static final BeanCopier BEAN_COPIER = BeanCopier.create(User.class, UserVO.class, false);

    public enum BySpringBeanCopierManual implements Converter {
        INSTANCE;

        @Override
        @SuppressWarnings("unchecked")
        public <S, T> T convert(S source, Class<T> targetType) {
//            T target = BeanUtils.instantiateClass(targetType);
            T target = (T) new UserVO();
            BEAN_COPIER.copy(source, new UserVO(), null);
            return target;
        }
    }

    private static final Map<String, BeanCopier> BEAN_COPIERS = new HashMap<>();

    public enum BySpringBeanCopierGeneric implements Converter {
        INSTANCE;

        @Override
        public <S, T> T convert(S source, Class<T> targetType) {
            T target = BeanUtils.instantiateClass(targetType);
            BeanCopier beanCopier = BEAN_COPIERS.computeIfAbsent(
                    source.getClass().getName() + targetType.getName(),
                    classes -> BeanCopier.create(source.getClass(), targetType, false)
            );
            beanCopier.copy(source, target, null);
            return target;
        }
    }

    public enum BySpringBeanCopierGenericUseConverter implements Converter {
        INSTANCE;

        @Override
        @SuppressWarnings("unchecked")
        public <S, T> T convert(S source, Class<T> targetType) {
            T target = BeanUtils.instantiateClass(targetType);
            BeanCopier beanCopier = BEAN_COPIERS.computeIfAbsent(
                    source.getClass().getName() + targetType.getName(),
                    classes -> BeanCopier.create(source.getClass(), targetType, true)
            );
            beanCopier.copy(source, target, (propertyValue, propertyType, setterName) -> {
                if (BeanUtils.isSimpleValueType(propertyType)
                        || Temporal.class.isAssignableFrom(propertyType)) {
                    return propertyValue;
                }
                return BySpringBeanCopierGeneric.INSTANCE.convert(propertyValue, propertyType);
            });
            return target;
        }
    }
}
