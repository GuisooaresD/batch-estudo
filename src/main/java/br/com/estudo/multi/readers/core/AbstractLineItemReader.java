package br.com.estudo.multi.readers.core;

import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import static com.google.common.collect.Lists.newLinkedList;
import static java.util.Objects.isNull;
import static org.springframework.batch.core.ExitStatus.COMPLETED;

@Setter
public abstract class AbstractLineItemReader<T> implements ItemStreamReader<T>, StepExecutionListener {

    private final Resource resource;
    private final LineCallbackHandler lineCallbackHandler;
    private final LinkedList<String> bufferLines;
    protected String delimiter = ";";
    private ExecutionContext executionContext;
    private StepExecution stepExecution;
    private BufferedReader reader;
    private int bufferSize = 100;
    private int skipLines = 0;
    private int count;

    protected AbstractLineItemReader(final Resource resource, final LineCallbackHandler lineCallbackHandler) {
        this.resource = resource;
        this.lineCallbackHandler = lineCallbackHandler;
        this.bufferLines = newLinkedList();
    }

    @Override
    @SneakyThrows
    public void open(final ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
        doOpen();
    }

    @Override
    public void update(final ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {
        stepExecution.setExitStatus(COMPLETED);
        return COMPLETED;
    }

    @Override
    @SneakyThrows
    public void close() throws ItemStreamException {
//        reader.close();
    }

    protected void doOpen() throws IOException {
        readerOpen();
        bufferLines();
        skip();
    }

    protected void skip() {
        String line;
        int count = 1;
        while ((line = nextLine()) != null && count <= skipLines) {
            ++count;
            if (!isNull(lineCallbackHandler)) {
                lineCallbackHandler.handleLine(line);
            }
        }
    }

    public abstract T next();

    @Override
    public T read() {
        return next();
    }

    public String fileName() {
        return resource.getFilename();
    }

    @SneakyThrows
    protected String nextLine() {
        if (bufferLines.isEmpty()) {
            bufferLines();
        }
        return bufferLines.pollFirst();
    }

    protected void bufferLines() throws IOException {
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null && count < bufferSize) {
            bufferLines.add(line);
            count++;
        }
        this.count += count;
    }

    private void incrementCount(int size) {
        this.count += size;
        executionContext.put(stepExecution.getStepName() + ".count", this.count);
    }

    private void readerOpen() throws IOException {
        if (isNull(reader)) {
            final var fileReader = new FileReader(resource.getFile());
            reader = new BufferedReader(fileReader);
        }
    }

}
