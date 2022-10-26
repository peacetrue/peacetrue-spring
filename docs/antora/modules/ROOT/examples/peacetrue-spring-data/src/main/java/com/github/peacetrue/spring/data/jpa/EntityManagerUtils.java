package com.github.peacetrue.spring.data.jpa;

import com.github.peacetrue.spring.data.jpa.criteria.CriteriaQueryUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

import static com.github.peacetrue.spring.data.jpa.QueryUtils.setPageable;

/**
 * 实体管理者工具类。
 *
 * @author peace
 **/
public class EntityManagerUtils {

    private EntityManagerUtils() {
    }

    /**
     * 获取领域对象列表。
     *
     * @param entityManager 实体管理者
     * @param domainClass   领域对象类
     * @param where         查询条件
     * @param pageable      分页对象
     * @param <T>           领域对象类型
     * @return 领域对象列表
     */
    public static <T> List<T> getResultList(EntityManager entityManager, Class<T> domainClass,
                                            Specification<T> where, Pageable pageable) {
        CriteriaQuery<T> criteriaQuery = CriteriaQueryUtils.buildCriteriaQuery(
                entityManager.getCriteriaBuilder(), domainClass, where, pageable.getSort()
        );
        return setPageable(entityManager.createQuery(criteriaQuery), pageable).getResultList();
    }

}
