package br.com.estudo.multi.processors;

import br.com.estudo.multi.configs.CustomerContext;
import br.com.estudo.multi.models.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    private final CustomerContext context;

    @Override
    public Customer process(final Customer item) {
        if (context.containsCustomer(item)) {
            return null;
        }
        context.addCustomer(item);
        return item;
    }
}
