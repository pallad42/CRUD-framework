My own a'la framework in Java, slightly similar to basic Spring Boot.

## Example of using
### Application properties
src/main/resources/application.properties
```java
db.driver=com.mysql.cj.jdbc.Driver // I tested only mysql
db.url=jdbc:mysql://localhost:3306/testdb
db.username=root
db.password=
```

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
@Seeder(5) // create 5 random persons after creating table and send data to database
public class Person {
 @Id
 @GeneratedValue
 private long id;
 private String firstname;
 private Integer age;

 // getters and setters (VERY IMPORTANT!)
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
 /*  *** WARNING ***
  here is a mistake
  personRepository.findById and any similar method should return Optional<T> ;/
  here is my implementation: https://github.com/pallad42/CRUD-framework/blob/master/com-framework/src/main/java/com/framework/orm/CrudRepository.java
 */
}
```

### RestController
src/main/java/test/controllers/PersonController.java
```java
package test.controllers;

import java.util.Collection;

import com.framework.web.annotations.Autowired;
import com.framework.web.annotations.PathVariable;
import com.framework.web.annotations.RequestBody;
import com.framework.web.annotations.RequestMapping;
import com.framework.web.annotations.RestController;
import com.framework.web.enums.RequestMethod;

import test.models.Person;
import test.repositories.PersonRepository;

@RestController
@RequestMapping("/persons")
public class PersonController {
 @Autowired
 private PersonRepository personRepository;

 @RequestMapping(value = "", method = RequestMethod.GET)
 public Collection<Person> personsFindAll() {
  return personRepository.findAll();
 }

 @RequestMapping(value = "/{id}", method = RequestMethod.GET)
 public Person personsFindById(@PathVariable("id") long id) {
  return personRepository.findById(id);
 }

 @RequestMapping(value = "/save", method = RequestMethod.GET)
 public Person personsSave(@RequestBody Person person) {
  return personRepository.save(person);
 }

 @RequestMapping(value = "", method = RequestMethod.DELETE)
 public Collection<Person> personsDelete(@RequestBody Person person) {
  personRepository.delete(person);
  return personRepository.findAll();
 }

 @RequestMapping(value = "/all", method = RequestMethod.DELETE)
 public Collection<Person> personsDeleteAll() {
  personRepository.deleteAll();
  return personRepository.findAll();
 }

 @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
 public Collection<Person> personsDeleteById(@PathVariable("id") long id) {
  personRepository.deleteById(id);
  return personRepository.findAll();
 }

 @RequestMapping(value = "/count", method = RequestMethod.GET)
 public long personsCount() {
  return personRepository.count();
 }

 @RequestMapping(value = "/exists/{id}", method = RequestMethod.GET)
 public boolean personsExists(@PathVariable("id") long id) {
  return personRepository.existsById(id);
 }
}
```

## Available built-in annotations
https://github.com/pallad42/CRUD-framework/tree/master/com-framework/src/main/java/com/framework/web/annotations
- Autowired
- Component
- Controller
- Entity
- GeneratedValue
- Id
- PathVariable
- Qualifier
- RequestBody
- RequestMapping
- RequestParam
- ResponseBody
- RestController
- Seeder
- TableName
