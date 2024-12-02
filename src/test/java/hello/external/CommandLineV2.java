package hello.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;

import java.util.List;
import java.util.Set;

@Slf4j
public class CommandLineV2 {

    public static void main(String[] args) {
        for (String arg : args) {
            log.info("arg {}", arg);
        }

        ApplicationArguments arguments = new DefaultApplicationArguments(args);
        log.info("SourceArgs = {}", List.of(arguments.getSourceArgs()));
        log.info("NonOptionArgs = {}", arguments.getNonOptionArgs());
        log.info("OptionNames = {}", arguments.getOptionNames());

        Set<String> optionNames = arguments.getOptionNames();
        for (String optionName : optionNames) {
            log.info("optionName {} = {}", optionName, arguments.getOptionValues(optionName));
        }
    }
}
