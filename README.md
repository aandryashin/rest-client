# Rest Client

**Rest Client** simple web based rest client.

`mvn clean test` to build application and run integration tests.

`mvn jacoco:report` to generate test coverage report.

Report is placed to `${project.basedir}/rest-client-server/target/site/jacoco/index.html`

To run application use `mvn compile` then open `http://localhost:8080` in the browser.

Please note, that embedded jetty server starts in forked mode, so do not forget to stop it manually. 
