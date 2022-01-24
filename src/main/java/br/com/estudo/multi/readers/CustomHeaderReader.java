package br.com.estudo.multi.readers;

import br.com.estudo.multi.configs.CustomerContext;
import br.com.estudo.multi.models.Customer;
import br.com.estudo.multi.readers.core.FieldLineItemReader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

@RequiredArgsConstructor
public class CustomHeaderReader implements ItemReader<Customer>, ItemStreamReader<Customer> {

    private final CustomerContext customerContext;
    private final FieldLineItemReader<Customer> reader;
    private int count = 0;

    @Override
    public Customer read() {
        if (customerContext.sizeCustomer() >= 60000) {
            customerContext.clearCustomer();
        }
        Customer customer = null;
        do {
            count++;
            customerContext.incrementLine();
            // ignored
        } while ((customer = reader.read()) != null && customerContext.containsCustomer(customer));
        if (customer != null) {
            customerContext.addCustomer(customer);
        } else {
            System.out.println("arquivo " + reader.fileName() + " finalizado, total " + count);
        }
        return reader.read();
    }

    @Override
    public void open(final ExecutionContext executionContext) throws ItemStreamException {
        reader.open(executionContext);
    }

    @Override
    public void update(final ExecutionContext executionContext) throws ItemStreamException {
        reader.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        reader.close();
    }
}
