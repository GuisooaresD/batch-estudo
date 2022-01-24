package br.com.estudo.multi.readers.core;

import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.batch.core.StepExecution;
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

@Setter
public abstract class AbstractLineItemReader<T> implements ItemStreamReader<T> {

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
        while (count <= skipLines && (line = nextLine()) != null) {
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
        int bufferCount = 0;
        while (bufferCount < bufferSize && (line = reader.readLine()) != null) {
            bufferLines.add(line);
            bufferCount++;
        }
        incrementCount(bufferCount);
    }

    private void incrementCount(int size) {
        this.count += size;
    }

    private void readerOpen() throws IOException {
        if (isNull(reader)) {
            final var fileReader = new FileReader(resource.getFile());
            reader = new BufferedReader(fileReader);
        }
    }

}
