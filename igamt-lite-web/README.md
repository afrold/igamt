# NIST MU3-IZ Application 

## Overview

-


## Prerequisites

### Java
The system runs with Java version -

## Running the application

### Configuring the environment

### Running the app during development
 
1. run `mvn -Denv=dev tomcat7:run-war`
2. navigate your browser to `http://hostname:port/mu3-iz/index.html` to see the app running in your
   browser.

### Running front-end unit tests - TODO Verify it is valid

in a separate tab - `./scripts/test.sh`

### Running front-end end to end tests

To run the e2e tests:
in a separate tab - `mvn -Denv=dev tomcat7:run-war`
in a separate tab - `./scripts/e2e-test.sh`
or in the browser open `http://hostname:port/test/e2e/runner.html`

### Running the back-end tests

run `mvn test`

-

## Application Directory Layout

## Security auth schemas

## Contact

