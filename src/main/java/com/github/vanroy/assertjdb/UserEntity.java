package com.github.vanroy.assertjdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.time.LocalDateTime;

// tag::class[]
@Entity(name = "user")
@Data
// end::class[]
@AllArgsConstructor
@NoArgsConstructor
// tag::class[]
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;
    @Transient
    private String name;
    private String email;
    // end::class[]
    private LocalDateTime registrationDate;

    public UserEntity(String name, String email) {
        this.name = name;
        this.email = email;
    }
    // tag::class[]
}
// end::class[]
