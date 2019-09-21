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
