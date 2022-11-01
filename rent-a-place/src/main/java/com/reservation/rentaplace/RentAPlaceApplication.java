package com.reservation.rentaplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.util.List;

@SpringBootApplication
public class RentAPlaceApplication implements CommandLineRunner {


	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... args) throws Exception {
		String sql = ("SELECT COUNT(*) FROM Customer");

		Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
		System.out.println(count);
//		int result = jdbcTemplate.update(sql, "Ravi Kumar", "ravi.kumar@gmail.com", "ravi2021");
//
//		if (result > 0) {
//			System.out.println("A new row has been inserted.");
//		}

	}
	public static void main(String[] args)
	{
		SpringApplication.run(RentAPlaceApplication.class, args);
	}

}
