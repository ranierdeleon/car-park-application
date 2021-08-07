package com.ranier.parking.config;

import com.ranier.parking.shell.InputReader;
import com.ranier.parking.shell.ShellHelper;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class SpringShellConfig {

    @Bean
    public ShellHelper shellHelper(@Lazy Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    public InputReader inputReader(@Lazy LineReader lineReader, ShellHelper shellHelper) {
        return new InputReader(lineReader, shellHelper);
    }
}
