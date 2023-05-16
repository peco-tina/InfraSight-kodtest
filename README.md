# kodtest-template

Template for InfraSight Labs code test. Includes kodtest-server which provides a JSON REST API with data for the tests. 

## Build & Test

Building is driven by Maven (https://maven.apache.org.) Use Java 11 (https://adoptium.net/temurin/releases/?version=11) when building with maven. Use JAVA_HOME to override which Java maven will use. Example in Windows:

> set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-11.0.17.8-hotspot

First run validate to install bundled kodtest-server into local repository:

> mvn validate

Then compile and run tests:

> mvn test

This will spin up kodtest-server on port 8080 (You may change this in AbstractKodTest.java) and execute tests located in src\test\java\com\infrasight\kodtest annotated with @Test.

## API documentation

To view the API documentation (generated by Swagger) you can run kodtest-server manually with a custom port from the kodtest-template folder:

> mvn exec:java -Dexec.args="--port=8080"

Access the API documentation by browsing to http://localhost:8080. If you run kodtest-server manually it may interfere with the server started by the test suite so make sure you run them on different ports.