package com.groqdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class MispApplication {
    private static final Logger logger = LoggerFactory.getLogger(MispApplication.class);

    public static void main(String[] args) {
	SpringApplication.run(MispApplication.class, args);
	logger.info("""

		 (♥◠‿◠)ﾉﾞ  MISP启动成功   ლ(´ڡ`ლ)ﾞ
		 __  __    ___     ___      ___
		|  \\/  |  |_ _|   / __|    | _ \\
		| |\\/| |   | |    \\__ \\    |  _/
		|_|  |_|  |___|   |___/   _|_|_
		_|"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_| \"\"\" |
		"\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'
		""");
    }
}
