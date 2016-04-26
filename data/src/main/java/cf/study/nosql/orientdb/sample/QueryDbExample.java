package cf.study.nosql.orientdb.sample;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import cf.study.nosql.orientdb.domain.Account;
import cf.study.nosql.orientdb.domain.Address;
import cf.study.nosql.orientdb.domain.Person;

/**
 * @author Decebal Suiu
 */
public class QueryDbExample {

	private static final Logger log = LoggerFactory.getLogger(QueryDbExample.class);

	public static void main(String[] args) {
		long time = System.currentTimeMillis();

		OObjectDatabaseTx db = getDatabase();

		long personCount = db.countClass(Person.class);
		if (personCount == 0) {
			createPerson(db);
		}
		queryPersons(db);

		db.close();

		time = System.currentTimeMillis() - time;
		log.info("Executed in {} ms", time);
	}

	public static OObjectDatabaseTx getDatabase() {
		// create a database in memory
		// OObjectDatabaseTx db = new OObjectDatabaseTx("memory:data").create();

		// create a database on disk
		String iURL = "plocal:/data/orientdb/databases/sample";
		OObjectDatabaseTx db = new OObjectDatabaseTx(iURL);
		if (db.exists()) {
			db = new OObjectDatabaseTx(iURL).open("admin", "admin");
		} else {
			db.create();
		}

		// register all classes from a package
		db.getEntityManager().registerEntityClasses(Person.class.getPackage().getName());

		// register class by class
		// db.getEntityManager().registerEntityClass(Person.class);
		// db.getEntityManager().registerEntityClass(Address.class);
		// db.getEntityManager().registerEntityClass(Account.class);

		return db;
	}

	public static void createPerson(OObjectDatabaseTx db) {
		log.info("Create person");

		// create an object
		Person person = new Person();
		// Person person = db.newInstance(Person.class);
		person.setFirstName("Decebal");
		person.setLastName("Suiu");
		// person.setAge(23);

		Address address = new Address();
		// Address address = db.newInstance(Address.class);
		address.setStreet("London Bridge");
		address.setPostcode("123");
		person.getAddresses().add(address);
		person.setDefaultAddress(address);

		Account account = new Account();
		// Account account = db.newInstance(Account.class);
		account.setPassword("decebal");
		account.setUsername("decebals");
		person.setAccount(account);

		// save object in database
		log.info("Save person in database");
		db.save(person);
	}

	public static void queryPersons(OObjectDatabaseTx db) {
		// make some queries
		log.info("Get all persons");
		for (Person p : db.browseClass(Person.class)) {
			log.info(p.toString());
		}

		log.info("Query persons by firstName");
		String sql = "select * from Person where firstName like 'D%'";
		log.info("sql = {}", sql);
		List<Person> persons = db.query(new OSQLSynchQuery<Person>(sql));
		for (Person p : persons) {
			log.info(p.toString());
		}

		log.info("Query persons by lastName");
		sql = "select * from Person where lastName like 'S%'";
		log.info("sql = {}", sql);
		persons = db.query(new OSQLSynchQuery<Person>(sql));
		for (Person p : persons) {
			log.info(p.toString());
		}
		
		log.info("iArg");
		// ? or :arg_name
		persons = db.query(new OSQLSynchQuery<Person>("select * from Person where lastName = ?"), "Suiu");
		for (Person p : persons) {
			log.info(p.toString());
		}
		
		log.info("in");
		persons = db.query(new OSQLSynchQuery<Person>("select * from Person where lastName in [?, ?]"), "123", "abc");
		log.info(StringUtils.join(persons, "\n"));
		
		log.info("in again");
		persons = db.query(new OSQLSynchQuery<Person>("select * from Person where lastName in ?"), new String[] { "Suiu", "abc"});
		log.info(StringUtils.join(persons, "\n"));
	}

}