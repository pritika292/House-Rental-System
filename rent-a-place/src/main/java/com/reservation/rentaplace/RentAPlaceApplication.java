package com.reservation.rentaplace;

import com.reservation.rentaplace.Domain.Customer;
import com.reservation.rentaplace.Domain.Property;
import com.reservation.rentaplace.mapper.CustomerRowMapper;
import com.reservation.rentaplace.mapper.PropertyRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.reservation.rentaplace.Controller.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableSwagger2
public class RentAPlaceApplication implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {

//		String sql = ("SELECT * FROM Customer");
//		List<Customer> c =jdbcTemplate.query(sql, new CustomerRowMapper());
//		for (Customer x: c)
//			System.out.println(x.getCust_name() + "\t" + x.getCust_id() + "\t" + x.getEmail() + "\t" + x.getPhone_num() + "\t" + x.getCart_id());
//
//		String sql2 = ("SELECT * FROM Property");
//		List<Property> p = jdbcTemplate.query(sql2, new PropertyRowMapper());
//		for(Property y: p)
//			System.out.println(y.getProperty_id() + "\t" + y.getProperty_name() + "\t" + y.getProperty_type() + "\t" + y.getDesc() +
//					"\t" + y.getNum_baths() + "\t" + y.getNum_bedrooms() + "\t" + y.getNum_baths() + "\t" + y.getCarpet_area()
//					+ "\t" + y.getPet_friendly()+ "\t" + y.getWifi_avail()+ "\t" + y.getAverage_rating() + "\t" + y.getOwner_id());

	}

	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.SWAGGER_2)
				.enable(true)
				.apiInfo(new ApiInfoBuilder()
						.title("Swagger Super")
						.description("Swagger Description details")
						.version("1.0").build())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.reservation.rentaplace.Controller"))
				.paths(PathSelectors.any()).build();
	}
	public static void main(String[] args)
	{
		SpringApplication.run(RentAPlaceApplication.class, args);
	}

}
