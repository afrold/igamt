# NIST MU3-IZ Application 

## Overview
 
## Prerequisites

###Setting up Apache Tomcat - MysqlDB
AAdd the following in context.xml bnefore </context> under $TOMCATHOME/conf folder
<Resource 
	name="jdbc/igl_jndi" 
	auth="Container" 
	type="javax.sql.DataSource" 
	maxActive="100" 
	maxIdle="30" 
	maxWait="10000"
	username="user" 
	password="secret" 
	driverClassName="com.mysql.jdbc.Driver" 
	url="jdbc:mysql://localhost:3306/igamt_lite"/>
Note : - The mysql server must have the schema name igamt_lite.


### Java
The system runs with Java version  1.7

## Running the application

### Configuring the environment

### Running the app during development
 

## Application Directory Layout

## Security auth schemas

## Contact

