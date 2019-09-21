package test.repositories;

import com.framework.orm.CrudRepository;
import com.framework.web.annotations.Component;

import test.models.Person;

@Component
public class PersonRepository extends CrudRepository<Person, Long> {

}
