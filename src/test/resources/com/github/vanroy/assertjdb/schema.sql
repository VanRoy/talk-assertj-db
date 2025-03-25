drop table user if exists;
drop sequence if exists user_seq;
create sequence user_seq start with 1 increment by 1;
create table user
(
  id    bigint not null,
  name  varchar(255),
  email varchar(255),
  primary key (id)
);
