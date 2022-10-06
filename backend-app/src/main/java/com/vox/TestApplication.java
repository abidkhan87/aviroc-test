package com.vox;

import com.vox.api.data.migration.DataSeeder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @author abidkhan
 * This class will avoid running data migration while running tests
 */
@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		value = { DataSeeder.class,
				VoxCinemasServiceApplication.class }))
public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

}
