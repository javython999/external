package hello.external;

import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
public class JavaSystemProperties {

    public static void main(String[] args) {

        System.setProperty("helloKey", "helloValue");

        Properties properties = System.getProperties();

        for (String key : properties.stringPropertyNames()) {
            log.info("{} = {}", key, properties.getProperty(key));
        }

        String url = System.getProperty("url");
        String username = System.getProperty("username");
        String password = System.getProperty("password");

        log.info("url = {}", url);
        log.info("username = {}", username);
        log.info("password = {}", password);


        log.info("helloKey = {}", System.getProperty("helloKey"));


    }
}
