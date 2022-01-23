package br.com.estudo.multi.readers;

import br.com.estudo.multi.configs.CustomerContext;
import br.com.estudo.multi.models.Customer;
import lombok.SneakyThrows;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

import static com.google.common.collect.Lists.newLinkedList;
import static java.lang.Integer.valueOf;
import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;

@StepScope
@Component
public class CustomReader implements ItemReader<Customer>, StepExecutionListener {

    private final CustomerContext customerContext;
    private final LinkedList<String> lines = newLinkedList();
    private File readFile;
    private String currentFileName;
    private BufferedReader bufferedReader;
    private StepExecution stepExecution;

    public CustomReader(final CustomerContext customerContext) {
        this.customerContext = customerContext;
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {
        final var context = stepExecution.getExecutionContext();
        final var contextFileName = context.getString("fileName");
        if (!contextFileName.equals(currentFileName) && isNotEmpty(contextFileName)) {
            currentFileName = contextFileName;
            readFile = new File("c:/batch/files/multi/in/" + currentFileName);
            bufferedReader = null;
            System.out.println("read file " + currentFileName);
            customerContext.incrementFile();
        }
        System.out.println(currentFileName + " " + stepExecution.getStepName());
        this.stepExecution = stepExecution;
    }

    @Override
    @SneakyThrows
    public ExitStatus afterStep(final StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }

    @BeforeRead
    @SneakyThrows
    public void beforeRead() {
        if (bufferedReader == null) {
            final var fileReader = new FileReader(readFile);
            bufferedReader = new BufferedReader(fileReader);
            bufferedReader.readLine();
        }
    }

    @SneakyThrows
    private void newBuffer() {
        String line;
        int count = 0;
        while ((line = bufferedReader.readLine()) != null && count < 100) {
            lines.add(line);
            count++;
        }
    }

    private Customer next() {
        if (lines.isEmpty()) {
            newBuffer();
        }
        final var line = lines.pollFirst();
        if (isNull(line)) {
            return null;
        }
        final var splited = line.split(";");
        final var customer = new Customer();
        customer.setDocument(splited[0]);
        customer.setCount(valueOf(splited[2]));
        return customer;
    }

    @Override
    public Customer read() {
        if (customerContext.sizeCustomer() >= 60000) {
            customerContext.clearCustomer();
        }
        Customer customer;
        while ((customer = next()) != null && customerContext.containsCustomer(customer)) {
            // ignored
        }
        if (customer != null) {
            customerContext.addCustomer(customer);
        }
        return customer;
    }
}
