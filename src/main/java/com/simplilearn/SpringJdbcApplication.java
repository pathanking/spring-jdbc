package com.simplilearn;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import com.simplilearn.model.Customer;

@SpringBootApplication
public class SpringJdbcApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(SpringJdbcApplication.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SpringJdbcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Creating Tables");
		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");

		jdbcTemplate.execute("CREATE TABLE customers(" + "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		List<Object[]> names = Arrays.asList("Sai Kiran", "Manoj Das", "Raza Khan", "Saif Ali").stream()
				.map(name -> name.split(" ")).collect(Collectors.toList());

		names.forEach(name -> logger.info(String.format("Inserting Customer record for %s %s", name[0], name[1])));

		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES(?,?)", names);

		logger.info("Fetching records for customer where first_name = 'Raza':");

		jdbcTemplate.query("SELECT id, first_name, last_name FROM customers WHERE first_name=?",
				new Object[] { "Raza" },
				(rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")))
				.forEach(customer -> logger.info(customer.toString()));
	}

}
