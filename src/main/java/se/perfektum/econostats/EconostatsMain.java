package se.perfektum.econostats;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@EnableConfigurationProperties
public class EconostatsMain {

    public static void main(String args[]) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        EconoStats econoStats = (EconoStats) context.getBean("econoStats");
        econoStats.start();
    }
}
