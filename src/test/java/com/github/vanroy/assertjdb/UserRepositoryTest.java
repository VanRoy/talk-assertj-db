package com.github.vanroy.assertjdb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository repository;
    @Autowired
    private JdbcTemplate template;

    @AfterEach
    void printTables() {
        printTable("user");
    }

    // tag::saveUser[]
    @Test
    void testSaveUser() {
        UserEntity user = repository.save(new UserEntity(
                "Homer Simpson",
                "homer@simpson.net")
        );
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("Homer Simpson");
    }
    // end::saveUser[]

    // tag::saveUserWithFind[]
    @Test
    void testSaveUserWithFind() {
        UserEntity savedUser = repository.save(new UserEntity(
                "Lisa Simpson",
                "lisa@simpson.net")
        );
        UserEntity user = repository.getOne(savedUser.getId());
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("Lisa Simpson");
    }
    // end::saveUserWithFind[]

    void printTable(String table) {
        repository.flush();
        Map<String, Object> users = template.queryForMap("select * from " + table);
        log.info("\n\n------ USERS ----- \n" + users.toString() + "\n-------------\n\n");
    }
}
