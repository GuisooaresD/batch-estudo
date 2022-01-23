package br.com.estudo.multi;

import br.com.estudo.multi.configs.CustomerContext;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@EnableBatchProcessing
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application {

    public static void main(final String[] args) {
        final var start = LocalDateTime.now();
        final var context = SpringApplication.run(Application.class, args);
        final var end = LocalDateTime.now();
        System.out.println(ChronoUnit.SECONDS.between(start, end));
        final var customerContext = context.getBean(CustomerContext.class);
        System.out.println("file.size " + customerContext.getFileSize());
        System.out.println("customer.total " + customerContext.sizeCustomer());
        System.out.println("files.lines.total " + customerContext.getLineSize());
        SpringApplication.exit(context, () -> 1);
    }

}
