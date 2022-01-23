package br.com.estudo.multi.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.LineCallbackHandler;

@RequiredArgsConstructor
public class CallbackLineHeader implements CallbackFileHeader, LineCallbackHandler {

    private final String delimiter;
    private String[] headerNames;
    private String[] fileHeader;

    @Override
    public String[] headerNames() {
        return headerNames;
    }

    @Override
    public void handleLine(final String line) {
        headerNames = line.split(delimiter);
    }
}
