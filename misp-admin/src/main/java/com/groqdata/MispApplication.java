package com.groqdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class MispApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(MispApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  MISP启动成功   ლ(´ڡ`ლ)ﾞ \r\n"
        		+ "\r\n"
        		+ " __  __    ___     ___      ___  \r\n"
        		+ "|  \\/  |  |_ _|   / __|    | _ \\ \r\n"
        		+ "| |\\/| |   | |    \\__ \\    |  _/ \r\n"
        		+ "|_|  |_|  |___|   |___/   _|_|_  \r\n"
        		+ "_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_| \"\"\" | \r\n"
        		+ "\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-' \r\n"
        		+ "\r\n");
    }
}
