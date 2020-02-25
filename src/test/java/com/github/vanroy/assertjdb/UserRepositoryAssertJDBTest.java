package com.github.vanroy.assertjdb;

import org.assertj.core.api.Condition;
import org.assertj.db.api.SoftAssertions;
import org.assertj.db.output.Outputs;
import org.assertj.db.type.Changes;
import org.assertj.db.type.Request;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static org.assertj.db.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none"
})
@Transactional(propagation = Propagation.SUPPORTS)
@Sql("schema.sql")
public class UserRepositoryAssertJDBTest {

    @Autowired
    private UserRepository repository;
    @Autowired
    private DataSource dataSource;

    @AfterEach
    void printTables() {
        // tag::outputConsole[]
        Table table = new Table(dataSource, "user");

        // Output the content of the table in the console
        Outputs.output(table).toConsole();
        // end::outputConsole[]
    }

    // tag::assertThatTable[]
    @Test
    void testSaveUser_Table() {
        repository.saveAndFlush(new UserEntity(
                "Homer Simpson",
                "homer@simpson.net")
        );
        assertThat(new Table(dataSource, "user"))
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
        repository.saveAndFlush(new UserEntity(
                "Homer Simpson",
                "homer@simpson.net")
        );
        Request request = new Request(
                dataSource,
                "select * from user where email like ?;",
                "%@simpson.net"
        );
        assertThat(request).hasNumberOfRows(1).row(0)
                .value("id").isEqualTo(1)
                .value("name").isEqualTo("Homer Simpson")
                .value("email").isEqualTo("homer@simpson.net");
    }
    // end::assertThatRequest[]

    // tag::assertThatChanges[]
    @Test
    void testSaveUser_Changes() {
        Changes changes = new Changes(dataSource);
        changes.setStartPointNow();
        repository.saveAndFlush(
                new UserEntity("Homer Simpson", "homer@simpson.net")
        );
        changes.setEndPointNow();

        assertThat(changes)
                .hasNumberOfChanges(1)
                .ofCreationOnTable("user")
                .change()
                .rowAtEndPoint()
                .column("name").hasValues("Homer Simpson");
    }
    // end::assertThatChanges[]

    // tag::conditions[]
    @Test
    void testSaveUser_Conditions() {
        repository.saveAndFlush(new UserEntity(
                "Homer Simpson",
                "homer@simpson.net")
        );
        Condition<String> aSimpson = new Condition<>(
                s -> s != null && s.endsWith("Simpson"),
                "is simpson"
        );
        assertThat(new Table(dataSource, "user"))
                .hasNumberOfRows(1)
                .row(0)
                .value("id").isEqualTo(1)
                .value("name").is(aSimpson);
    }
    // end::conditions[]

    // tag::softAssertions[]
    @Test
    void testSaveUser_SoftAssertions() {
        repository.saveAndFlush(new UserEntity(
                "Lisa Simpson",
                "lisa@simpson.net")
        );

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(new Table(dataSource, "user"))
                .hasNumberOfRows(1)
                .row(0)
                .value("id").isEqualTo(1)
                .value("name").isEqualTo("Lisa Simpson")
                .value("email").isEqualTo("lisa@simpson.net");
        soft.assertAll();
    }
    // end::softAssertions[]
}
