package br.com.estudo.multi.readers.core;

import lombok.Setter;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.core.io.Resource;

@Setter
public class LineItemReader extends AbstractLineItemReader<String> {

    public LineItemReader(final Resource resource, final LineCallbackHandler lineCallbackHandler) {
        super(resource, lineCallbackHandler);
    }

    @Override
    public String next() {
        return nextLine();
    }

}
