package com.github.vanroy.assertjdb;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// tag::class[]
@Entity(name = "user")
// end::class[]
@Data
@AllArgsConstructor
@NoArgsConstructor
// tag::class[]
public class UserEntity {

  @Id
  @GeneratedValue(generator = "user_seq")
  // end::class[]
  @SequenceGenerator(name = "user_seq", allocationSize = 1)
  // tag::class[]
  private Long id;
  @Transient
  private String name;
  private String email;
  // end::class[]
  @Transient
  private String birthdate;
  // tag::class[]

  public UserEntity(String name, String email) {
    this.name = name;
    this.email = email;
  }
  // end::class[]
  public UserEntity(String name, String email, String birthdate) {
    this(name, email);
    this.birthdate = birthdate;
  }
  // tag::class[]
}
// end::class[]
