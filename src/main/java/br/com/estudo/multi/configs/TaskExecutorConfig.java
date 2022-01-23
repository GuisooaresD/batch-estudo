package br.com.estudo.multi.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static br.com.estudo.multi.configs.JobConfig.THREAD_SIZE;

@Configuration
public class TaskExecutorConfig {

    @Bean
    public TaskExecutor taskExecutorCustom() {
        final var taskExecutor = new SimpleAsyncTaskExecutor();
//        taskExecutor.setMaxPoolSize(THREAD_SIZE);
//        taskExecutor.setCorePoolSize(1);
//        taskExecutor.setQueueCapacity(THREAD_SIZE);
        taskExecutor.setConcurrencyLimit(THREAD_SIZE);
        return taskExecutor;
    }
}
