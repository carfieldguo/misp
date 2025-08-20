package com.groqdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author MISP TEAM
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class MispApplication {
	private static final Logger logger = LoggerFactory.getLogger(MispApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MispApplication.class, args);
        if (logger.isInfoEnabled()) {
            StringBuilder logo = new StringBuilder();
            logo.append("\n");
            logo.append(" (♥◠‿◠)ﾉﾞ  MISP启动成功   ლ(´ڡ`ლ)ﾞ\n");
            logo.append("  __  __     ___      ___       ___  \n");
            logo.append(" |  \\/  |   |_ _|    / __|     | _ \\ \n");
            logo.append(" | |\\/| |    | |     \\__ \\     |  _/ \n");
            logo.append(" |_|  |_|   |___|    |___/     |_|   \n");
            logo.append("_|\"\"\"\"\"\"|__|\"\"\"\"\"|__|\"\"\"\"\"|__|\"\"\"\"\"|_ \n");
            logo.append(" `-0-0-'\" \"'-0-0-'\"\"'-0-0-'\"\"'-0-0-' \n");

            logger.info(logo.toString());
        }
    }
}
