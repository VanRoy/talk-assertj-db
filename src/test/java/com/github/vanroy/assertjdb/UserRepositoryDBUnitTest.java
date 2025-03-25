package com.github.vanroy.assertjdb;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest(properties = {
  "spring.jpa.hibernate.ddl-auto=none",
  "spring.jpa.properties.hibernate.query.mutation_strategy.global_temporary.create_tables=false",
  "spring.jpa.properties.hibernate.query.mutation_strategy.global_temporary.drop_tables=false",
})
@Transactional(propagation = Propagation.SUPPORTS)
@Sql("schema.sql")
@TestExecutionListeners(value = {
  DbUnitTestExecutionListener.class},
  mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
class UserRepositoryDBUnitTest {

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
