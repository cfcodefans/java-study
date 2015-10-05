package cf.study.jdk7.jaxb.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name=Person.TAG, namespace="cf.study")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Person {
	
	public static final String TAG = "Person";
	
	public static enum Gender {
		MALE, FEMALE, UNKNOWN; 
	}

	public static class PersonName {
		
		private String firstName;
		private String lastName;
		private String nickname;
		
		public PersonName() {
			super();
		}
		
		public PersonName(String firstName, String lastName, String nickname) {
			super();
			this.firstName = firstName;
			this.lastName = lastName;
			this.nickname = nickname;
		}

		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
		public String getNickname() {
			return nickname;
		}
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		
		
	}
	
	private int id;
	
	private PersonName name;
	
	private String mail;
	
	private Gender gender; 
	
	private int age;
	
	private Date birthday;

	private List<String> tags;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PersonName getName() {
		return name;
	}

	public void setName(PersonName name) {
		this.name = name;
	}

	@XmlElementWrapper(name="tags")
	@XmlElement(name="tag")
	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
		Calendar now = Calendar.getInstance();
		Calendar then = Calendar.getInstance();
		
		then.setTime(birthday);
		setAge(now.get(Calendar.YEAR) - then.get(Calendar.YEAR));
	}
	
}
