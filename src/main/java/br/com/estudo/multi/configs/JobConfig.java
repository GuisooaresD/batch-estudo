package br.com.estudo.multi.configs;

import br.com.estudo.multi.files.mapping.HeaderFieldLineMapper;
import br.com.estudo.multi.files.mapping.callback.CallbackLineHeader;
import br.com.estudo.multi.files.mapping.types.FieldMapper;
import br.com.estudo.multi.models.Customer;
import br.com.estudo.multi.partitioners.CustomPartitioner;
import br.com.estudo.multi.readers.CustomHeaderReader;
import br.com.estudo.multi.readers.CustomReader;
import br.com.estudo.multi.readers.CustomerReader;
import br.com.estudo.multi.readers.core.FieldLineItemReader;
import br.com.estudo.multi.writers.CustomerWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class JobConfig {

    public static final int THREAD_SIZE = 5;

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobFactory;
    private final ResourcePatternResolver resolver;

    @Bean(name = "partitionerJob")
    public Job partitionerJob(final Step partitionerStep) {
        return jobFactory.get("partitionerJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobExecutionListenerSupport())
                .flow(partitionerStep)
                .end()
                .build();
    }

    @Bean
    public Step step1(final CustomerContext customerContext,
                      final CustomReader customReader,
                      final CustomHeaderReader headerItemReader,
                      final CustomerReader customerReader,
                      final CustomerWriter customerWriter) {
        return this.stepBuilderFactory.get("customerStep")
                .<Customer, Customer>chunk(100)
                .reader(headerItemReader)
                .writer(customerWriter)
                .build();
    }

    @Bean
    @StepScope
    public CustomHeaderReader headerItemReader(@Value("#{stepExecutionContext[fileName]}") final String filename,
                                               final List<FieldMapper<?>> mappers,
                                               final CustomerContext customerContext) {
        final var delimiter = ";";
        final var callbackHeader = new CallbackLineHeader();
        final var lineMapper = new HeaderFieldLineMapper<>(Customer.class, callbackHeader, mappers, delimiter);
        final var reader = FieldLineItemReader.<Customer>builder()
                .resource(resolver.getResource("file:C:/batch/files/multi/in/" + filename))
                .lineCallbackHandler(callbackHeader)
                .fieldLineMapper(lineMapper)
                .build();
        reader.setSkipLines(1);
        customerContext.incrementFile();
        return new CustomHeaderReader(customerContext, reader);

    }

    @Bean
    public Step partitionerStep(final TaskExecutor taskExecutorCustom,
                                final Step step1,
                                final PartitionHandler partitionHandler,
                                final Partitioner partitioner) {
        return stepBuilderFactory.get("step1.partition")
                .partitioner("partitioner", partitioner)
                .step(step1)
                .partitionHandler(partitionHandler)
                .taskExecutor(taskExecutorCustom)
                .build();
    }

    @Bean
    public PartitionHandler partitionHandler(final Step step1, final TaskExecutor taskExecutorCustom) {
        final var retVal = new TaskExecutorPartitionHandler();
        retVal.setTaskExecutor(taskExecutorCustom);
        retVal.setStep(step1);
        retVal.setGridSize(THREAD_SIZE);
        return retVal;
    }

    @Bean
    @SneakyThrows
    public Partitioner partitioner() {
        final var partitioner = new CustomPartitioner();
        final var resources = resolver.getResources("file:C:/batch/files/multi/in/*.csv");
        partitioner.setResources(resources);
        return partitioner;
    }
}
