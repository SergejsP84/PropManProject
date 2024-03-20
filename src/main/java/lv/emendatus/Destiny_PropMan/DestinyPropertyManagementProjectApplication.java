package lv.emendatus.Destiny_PropMan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DestinyPropertyManagementProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(DestinyPropertyManagementProjectApplication.class, args);
	}

}

//	// Lloyd's Virtual Martini

//		Martini martini = new Martini();
//		martini.setGin("Premium Dry Gin");
//		martini.setVermouth("Extra Dry Vermouth");
//		martini.setGarnish("Twist of Lemon Peel");
//
//	// Enjoy your virtual martini, sir. The pleasure is all mine!