My own a'la framework in Java, slightly similar to Spring Boot.

## Example of using
Application properties
src/main/resources/application.properties
```java
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/testdb
db.username=root
db.password=
```

> 
### Started class
src/main/java/test/MyApplication.java
```java
package test;

import com.framework.Application;

public class MyApplication {
 public static void main(String[] args) {
  Application.run();	
 }	
}
```

### Entity/Model
src/main/java/test/models/Person.java
```java
  
package test.models;

import com.framework.web.annotations.Entity;
import com.framework.web.annotations.GeneratedValue;
import com.framework.web.annotations.Id;
import com.framework.web.annotations.Seeder;
import com.framework.web.annotations.TableName;

@Entity
@TableName("mypersons")
@Seeder(5) // create random 5 persons after creating table, and send data to database
public class Person {
 @Id
 @GeneratedValue
 private long id;
 private String firstname;
 private Integer age;

 // getters and setters [VERY IMPORTANT]
}
```

### Repository
src/main/java/test/repositories/PersonRepository.java
```java
package test.repositories;

import com.framework.orm.CrudRepository;
import com.framework.web.annotations.Component;

import test.models.Person;
@Component
public class PersonRepository extends CrudRepository<Person, Long> {
}
```
