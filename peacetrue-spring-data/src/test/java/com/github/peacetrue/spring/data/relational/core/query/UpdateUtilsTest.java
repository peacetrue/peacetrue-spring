package com.github.peacetrue.spring.data.relational.core.query;

import com.github.peacetrue.beans.operator.OperatorCapable;
import com.github.peacetrue.beans.properties.modifiedtime.ModifiedTimeCapable;
import com.github.peacetrue.beans.properties.modifierid.ModifierIdCapable;
import org.junit.jupiter.api.Test;
import org.springframework.data.relational.core.query.Update;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author peace
 **/
class UpdateUtilsTest {

    @Test
    void setModify() {
    }

    @Test
    void selectiveUpdateFromExample() {
    }

    @Test
    void testSelectiveUpdateFromExample() {
    }

    /**
     * 设置修改属性。
     *
     * @param update   更新对象
     * @param operator 操作者
     * @param <T>      主键类型
     * @return 更新对象
     */
    public static <T> Update setModify(Update update, OperatorCapable<T> operator) {
        return update.set(ModifierIdCapable.PROPERTY_MODIFIER_ID, operator.getId())
                .set(ModifiedTimeCapable.PROPERTY_MODIFIED_TIME, LocalDateTime.now());
    }

}
