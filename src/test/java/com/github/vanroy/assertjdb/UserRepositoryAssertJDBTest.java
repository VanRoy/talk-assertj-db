package com.github.vanroy.assertjdb;

import org.assertj.core.api.Condition;
import org.assertj.db.api.SoftAssertions;
import org.assertj.db.output.Outputs;
import org.assertj.db.type.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.time.LocalDate;

import static org.assertj.db.api.Assertions.assertThat;

@DataJpaTest(properties = {
  "spring.jpa.hibernate.ddl-auto=none",
  "spring.jpa.properties.hibernate.query.mutation_strategy.global_temporary.create_tables=false",
  "spring.jpa.properties.hibernate.query.mutation_strategy.global_temporary.drop_tables=false"
})
@Transactional(propagation = Propagation.SUPPORTS)
@Sql("schema.sql")
// tag::assertDbConnection[]
class UserRepositoryAssertJDBTest {
  // end::assertDbConnection[]
  @Autowired
  private UserRepository repository;
  // tag::assertDbConnection[]
  @Autowired
  private DataSource dataSource;
  private AssertDbConnection assertDb;

  @BeforeEach
  void initAssertDbConnection() {
    assertDb = AssertDbConnectionFactory.of(dataSource).create();
  }
  // end::assertDbConnection[]

  @AfterEach
  void printTables() {
    // tag::outputConsole[]
    Table table = assertDb.table("user").build();

    // Output the content of the table in the console
    Outputs.output(table).toConsole();
    // end::outputConsole[]
  }

  // tag::assertThatTable[]
  @Test
  void testSaveUser_Table() {
    repository.saveAndFlush(
      new UserEntity("Homer Simpson", "homer@simpson.net")
    );

    Table table = assertDb.table("user").build();

    assertThat(table)
      .hasNumberOfRows(1)
      .row(0)
      .value("id").isEqualTo(1)
      .value("name").isEqualTo("Homer Simpson")
      .value("email").isEqualTo("homer@simpson.net");
  }
  // end::assertThatTable[]

  // tag::assertThatRequest[]
  @Test
  void testSaveUser_Request() {
    repository.saveAndFlush(
      new UserEntity("Homer Simpson", "homer@simpson.net")
    );

    Request request = assertDb.request(
      "select * from user where email like ?;"
    ).parameters("%@simpson.net").build();

    assertThat(request).hasNumberOfRows(1)
      .row(0)
      .value("id").isEqualTo(1)
      .value("name").isEqualTo("Homer Simpson")
      .value("email").isEqualTo("homer@simpson.net");
  }
  // end::assertThatRequest[]

  // tag::assertThatChanges[]
  @Test
  void testSaveUser_Changes() {
    Changes changes = assertDb.changes().build();
    changes.setStartPointNow();
    repository.saveAndFlush(
      new UserEntity("Homer Simpson", "homer@simpson.net")
    );
    changes.setEndPointNow();

    assertThat(changes)
      .hasNumberOfChanges(1)
      .ofCreationOnTable("user")
      .change(0)
      .rowAtEndPoint()
      .value("name").isEqualTo("Homer Simpson");
  }
  // end::assertThatChanges[]

  // tag::conditions[]
  @Test
  void testSaveUser_Conditions() {
    repository.saveAndFlush(
      new UserEntity("Homer Simpson", "homer@simpson.net")
    );

    Condition<String> aSimpson = new Condition<>(
      s -> s != null && s.endsWith("Simpson"),
      "is simpson"
    );

    assertThat(assertDb.table("user").build())
      .row(0)
      .value("name").is(aSimpson);
  }
  // end::conditions[]

  // tag::softAssertions[]
  @Test
  void testSaveUser_SoftAssertions() {
    repository.saveAndFlush(new UserEntity(
      "Lisa Simpson",
      "lisa@simpson.net"));

    SoftAssertions soft = new SoftAssertions();

    soft.assertThat(assertDb.table("user").build())
      .row(0).value("name").isEqualTo("Lisa Simpson");

    soft.assertThat(assertDb.table("family").build())
      .row(0).value("name").isEqualTo("Simpson");

    soft.assertAll();
  }
  // end::softAssertions[]

  // tag::localDate[]
  @Test
  void testSaveUser_LocalDate() {
    repository.saveAndFlush(
      new UserEntity("Homer Simpson", "homer@simpson.net", "1956-05-12")
    );

    assertThat(assertDb.table("user").build())
      .row(0)
      .value("birthdate").isEqualTo(LocalDate.of(1956, 5, 12));
  }
  // end::localDate[]

}
