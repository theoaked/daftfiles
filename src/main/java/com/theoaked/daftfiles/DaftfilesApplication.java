package com.theoaked.daftfiles;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.theoaked.daftfiles.dto.User;

@SpringBootApplication
@EntityScan(basePackages = { "com.theoaked.daftfiles.dto" })
@EnableJpaRepositories(basePackages = { "com.theoaked.daftfiles.repository" })
@ComponentScan(basePackages = { "com.theoaked.daftfiles" })
public class DaftfilesApplication extends SpringBootServletInitializer {

	@Value("${users.credentials}")
	private String users;

	public static void main(String[] args) {
		SpringApplication.run(DaftfilesApplication.class, args);
	}

	public User checkLogin(String login, String senha) {
		String[] usersArr = users.split(";");
		for (int i = 0; i < usersArr.length; i++) {
			User user = new User();
			String[] auxUs = usersArr[i].split("|");
			user.setLogin(auxUs[0]);
			user.setSenha(auxUs[1]);
			System.out.println(user.getLogin()+" - "+user.getSenha());
			if (user.getLogin().equals(login) && user.getSenha().equals(senha)) {
				return user;
			}
		}
		return null;
	}

}
