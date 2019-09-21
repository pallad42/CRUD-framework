package test.models;

import com.framework.web.annotations.Entity;
import com.framework.web.annotations.GeneratedValue;
import com.framework.web.annotations.Id;
import com.framework.web.annotations.Seeder;
import com.framework.web.annotations.TableName;

@Entity
@TableName("mypersons")
@Seeder(5)
public class Person {

	@Id
	@GeneratedValue
	private long id;
	private String firstname;
	private Integer age;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

}
