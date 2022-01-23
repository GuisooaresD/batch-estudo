package br.com.estudo.multi.files.mapping.callback;

import org.springframework.batch.item.file.LineCallbackHandler;

public class CallbackLineHeader implements CallbackFileHeader, LineCallbackHandler {

    private String headerNames;

    @Override
    public String fileHeader() {
        return headerNames;
    }

    @Override
    public void handleLine(final String line) {
        headerNames = line;
    }
}
