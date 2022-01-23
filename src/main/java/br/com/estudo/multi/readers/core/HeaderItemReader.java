package br.com.estudo.multi.readers.core;

import br.com.estudo.multi.files.mapping.PropertyFieldLineMapper;
import lombok.Builder;
import lombok.Setter;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.core.io.Resource;

import static java.util.Objects.isNull;

@Setter
public class HeaderItemReader<T> extends AbstractLineItemReader<T> {

    private final PropertyFieldLineMapper<T> propertyFieldLineMapper;

    @Builder
    public HeaderItemReader(final Resource resource, final LineCallbackHandler lineCallbackHandler,
                            final PropertyFieldLineMapper<T> propertyFieldLineMapper) {
        super(resource, lineCallbackHandler);
        this.propertyFieldLineMapper = propertyFieldLineMapper;
    }

    @Override
    public T next() {
        final var line = nextLine();
        if (isNull(line)) {
            return null;
        }
        return propertyFieldLineMapper.mapFieldLine(line.split(delimiter));
    }
}
