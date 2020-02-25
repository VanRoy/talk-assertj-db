package com.github.vanroy.assertjdb;

import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none"
})
@Transactional(propagation = Propagation.SUPPORTS)
@Sql("schema.sql")
public class UserRepositoryDBUnitTest {

    @Autowired
    private UserRepository repository;

    // tag::saveUser[]
    @Test
    @ExpectedDatabase("expected-users.xml")
    void testSaveUser() {
        repository.saveAndFlush(new UserEntity(
                "Homer Simpson",
                "homer@simpson.net")
        );
    }
    // end::saveUser[]
}
