drop table user if exists;
drop sequence if exists hibernate_sequence;
create sequence hibernate_sequence start with 1 increment by 1;
create table user (id bigint not null, name varchar(255), email varchar(255), registration_date timestamp, primary key (id));
