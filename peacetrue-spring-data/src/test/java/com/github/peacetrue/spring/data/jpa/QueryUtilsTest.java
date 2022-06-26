package com.github.peacetrue.spring.data.jpa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.Query;

/**
 * @author peace
 **/
class QueryUtilsTest {

    @Test
    void setPageable() {
        Query query = Mockito.mock(Query.class);
        PageRequest pageable = PageRequest.of(1, 10);
        QueryUtils.setPageable(query, pageable);
        Mockito.verify(query).setFirstResult((int) pageable.getOffset());
        Mockito.verify(query).setMaxResults(pageable.getPageSize());
    }
}
