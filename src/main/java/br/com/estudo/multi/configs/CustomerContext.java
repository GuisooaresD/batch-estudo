package br.com.estudo.multi.configs;

import br.com.estudo.multi.models.Customer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.collect.Sets.newConcurrentHashSet;

@Component
public class CustomerContext extends ExecutionContext {

    private final AtomicReference<Set<Customer>> customerCache;
    private final AtomicLong lineSize;
    private final AtomicLong fileSize;

    public CustomerContext() {
        this.customerCache = new AtomicReference<>();
        this.customerCache.set(newConcurrentHashSet());
        fileSize = new AtomicLong(0);
        lineSize = new AtomicLong(0);
    }

    public void incrementLine() {
        lineSize.incrementAndGet();
    }

    public Long getLineSize() {
        return lineSize.get();
    }

    public void incrementFile() {
        fileSize.incrementAndGet();
    }

    public Long getFileSize() {
        return fileSize.get();
    }

    public void clearCustomer() {
        customerCache.get().clear();
    }

    public void addCustomer(final Customer customer) {
        customerCache.get().add(customer);
    }

    public boolean containsCustomer(final Customer customer) {
        return customerCache.get().contains(customer);
    }

    public int sizeCustomer() {
        return customerCache.get().size();
    }
}
