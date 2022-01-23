package br.com.estudo.multi.writers;

import br.com.estudo.multi.configs.CustomerContext;
import br.com.estudo.multi.models.Customer;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerWriter implements ItemWriter<Customer>, StepExecutionListener {

    private final CustomerContext customerContext;
    private final FlatFileItemWriter<Customer> writer;
    private StepExecution stepExecution;

    public CustomerWriter(final CustomerContext customerContext) {
        this.customerContext = customerContext;
        final var fieldExtractor = new BeanWrapperFieldExtractor<Customer>();
        fieldExtractor.setNames(new String[]{"document"});
        final var lineAggregator = new DelimitedLineAggregator<Customer>();
        lineAggregator.setDelimiter(";");
        lineAggregator.setFieldExtractor(fieldExtractor);
        this.writer = new FlatFileItemWriterBuilder<Customer>()
                .name("customerWriter")
                .resource(new PathResource("c:/batch/files/multi/out/DOCUMENTS.csv"))
                .lineAggregator(lineAggregator)
                .append(true)
                .shouldDeleteIfEmpty(false)
                .shouldDeleteIfExists(true)
                .build();
    }

    @BeforeWrite
    public void beforeWrite() {
        writer.open(stepExecution.getExecutionContext());
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {
        System.out.println(customerContext.sizeCustomer());
        return null;
    }

    @Override
    public void write(final List<? extends Customer> items) throws Exception {
        writer.write(items);
    }

}
