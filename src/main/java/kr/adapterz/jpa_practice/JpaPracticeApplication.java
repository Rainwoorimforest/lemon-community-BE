package kr.adapterz.jpa_practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import kr.adapterz.jpa_practice.repository.UserRepository;
import kr.adapterz.jpa_practice.repository.CommentRepository;
import kr.adapterz.jpa_practice.repository.PostRepository;
import kr.adapterz.jpa_practice.repository.LikeRepository;


@SpringBootApplication
public class JpaPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaPracticeApplication.class, args);
	}


}

