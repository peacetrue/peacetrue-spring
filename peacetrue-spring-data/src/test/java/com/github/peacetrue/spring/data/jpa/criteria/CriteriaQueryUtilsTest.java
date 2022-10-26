package com.github.peacetrue.spring.data.jpa.criteria;

import com.github.peacetrue.spring.data.jpa.EntityManagerUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * @author peace
 **/
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        TransactionAutoConfiguration.class,
        FlywayAutoConfiguration.class,
})
@EnableJpaRepositories(basePackageClasses = CriteriaQueryUtilsTest.class)
@EntityScan(basePackageClasses = CriteriaQueryUtilsTest.class)
@ActiveProfiles("test")
class CriteriaQueryUtilsTest {

    private final static EasyRandom EASY_RANDOM = new EasyRandom();

    @Autowired
    private EntityManager entityManager;

    @Test
    void getResultList() {
        List<Template> templates = EntityManagerUtils.getResultList(entityManager, Template.class,
                (Specification<Template>) (root, query, builder) -> builder.equal(root.get("id"), -1L),
                PageRequest.of(0, 10)
        );
        Assertions.assertEquals(0, templates.size());
    }

    @Test
    @Transactional
    void buildCriteriaQuery() {
        Template template = EASY_RANDOM.nextObject(Template.class);
        entityManager.merge(template);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Template> criteriaQuery = CriteriaQueryUtils.buildCriteriaQuery(
                criteriaBuilder, Template.class, (Specification<Template>) (root, query, builder) -> builder.equal(root.get("id"), 1L), Sort.by("id")
        );
        List<Template> templates = entityManager.createQuery(criteriaQuery).getResultList();

        Assertions.assertEquals(1, templates.size());
    }
}
