package co.edu.unimagdalena.RCU;

import org.springframework.boot.SpringApplication;

public class TestRcuApplication {

	public static void main(String[] args) {
		SpringApplication.from(RcuApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
