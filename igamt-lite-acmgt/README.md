# ONC-NIST ehr-Randomizer Application 

## Overview

-


## Prerequisites

### Java
The system runs with Java version -

## Running the application

### Configuring the environment


### Running the app during development

### Running front-end unit tests - TODO Verify it is valid


### Running front-end end to end tests

To run the e2e tests:

### Running the back-end tests

-

## Application Directory Layout

## Security auth schemas

	create table acmgtauth.users(
      username varchar(50) not null primary key,
      password varchar(256) not null,
      enabled boolean not null,
      accountNonExpired boolean not null,
      accountNonLocked boolean not null,
      credentialsNonExpired boolean not null
      );

	create table acmgtauth.authorities (
      username varchar(50) not null,
      authority varchar(50) not null,
      constraint fk_authorities_users foreign key(username) references acmgtauth.users(username));

	create unique index ix_auth_username on acmgtauth.authorities (username,authority);   

## Contact

