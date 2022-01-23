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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Component;

import static org.springframework.batch.core.ExitStatus.COMPLETED;

@StepScope
@Component
public class CustomerReader implements ItemReader<Customer>, StepExecutionListener {

    private final FlatFileItemReader<Customer> reader;
    private final String filename;
    private final CustomerContext customerContext;
    private StepExecution stepExecution;

    @SneakyThrows
    public CustomerReader(@Value("#{stepExecutionContext[fileName]}") final String filename,
                          final CustomerContext customerContext) {
        final var tokens = new String[]{"document", "createdAt"};
        reader = new FlatFileItemReader<>();
        reader.setResource(new PathResource("c:/batch/files/multi/in/" + filename));
        reader.setLinesToSkip(1);

        final var tokenizer = new DelimitedLineTokenizer(";");
        tokenizer.setNames(tokens);
        tokenizer.afterPropertiesSet();

        final var lineMapper = new DefaultLineMapper<Customer>();
        lineMapper.setLineTokenizer(tokenizer);
        final var mapper = new BeanWrapperFieldSetMapper<Customer>();
        mapper.setTargetType(Customer.class);
        lineMapper.setFieldSetMapper(mapper);
        lineMapper.afterPropertiesSet();

        reader.setLineMapper(lineMapper);
        reader.afterPropertiesSet();
        this.filename = filename;
        this.customerContext = customerContext;
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {
        System.out.println(filename + " " + stepExecution.getStepName());
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {
        return COMPLETED;
    }

    @BeforeRead
    public void beforeRead() {
        reader.open(stepExecution.getExecutionContext());
    }


    @Override
    public Customer read() throws Exception {
        if (customerContext.sizeCustomer() >= 60000) {
            customerContext.clearCustomer();
        }
        Customer customer = null;
        while ((customer = reader.read()) != null && customerContext.containsCustomer(customer)) {
            customerContext.incrementLine();
            // ignored
        }
        if (customer != null) {
            customerContext.addCustomer(customer);
        }
        return customer;
    }
}
