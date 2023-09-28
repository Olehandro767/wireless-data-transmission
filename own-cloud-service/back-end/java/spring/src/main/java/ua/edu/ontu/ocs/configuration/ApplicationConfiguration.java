package ua.edu.ontu.ocs.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ua.edu.ontu.ocs.dto.ApplicationSettingsRecord;

@Configuration
public class ApplicationConfiguration {
	
	public ApplicationConfiguration() throws IOException {
		var configurationFile = new File("./wdt-ocs.conf");
		
		if (!configurationFile.exists()) {
			throw new FileNotFoundException("Can't find wdt-ocs.conf file");
		}
	}
	
	@Bean
	public ApplicationSettingsRecord buildApplicationSettings() {
		return new ApplicationSettingsRecord(null);
	}
}

class A {
	// TODO
}