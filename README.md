# ridexapi
###Run this command to run the development server
`clean spring-boot:run -Dspring-boot.run.arguments=--spring.config.location=config.properties,classpath:/application.properties -Dspring-boot.run.fork=false`

###Run tests
`./mvnw test`

###API documentation - 
* local - http://localhost:8080/swagger-ui.html
* hosted - http://ridex.ml/api/swagger-ui.html

** Need to create unique index in User collection  db.user.createIndex({"phone":1}, {unique:true}); **
