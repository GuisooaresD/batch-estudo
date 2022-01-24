package br.com.estudo.multi.writers;

import br.com.estudo.multi.configs.CustomerContext;
import br.com.estudo.multi.models.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class CustomerWriter implements ItemWriter<Customer>, StepExecutionListener {

    private final CustomerContext customerContext;
    private final FlatFileItemWriter<String> writer;
    private final ObjectMapper mapper = new ObjectMapper();
    private StepExecution stepExecution;

    public CustomerWriter(final CustomerContext customerContext) {
        this.customerContext = customerContext;
        final var lineAggregator = new DelimitedLineAggregator<String>();
        this.writer = new FlatFileItemWriterBuilder<String>()
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
        final var list = items.stream()
                .map(value -> {
                    //return parseJson(value);
                    return parseString(value);
                })
                .collect(toList());
        writer.write(list);
    }

    private String parseString(final Customer value) {
        return value.getDocument();
    }

    public String parseJson(final Customer value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

}
