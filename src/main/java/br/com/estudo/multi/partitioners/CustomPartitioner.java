package br.com.estudo.multi.partitioners;

import lombok.Setter;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.Assert.state;

@Setter
public class CustomPartitioner implements Partitioner {

    private static final String DEFAULT_KEY_NAME = "fileName";
    private static final String PARTITION_KEY = "partition";

    private Resource[] resources;
    private String keyName = DEFAULT_KEY_NAME;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> map = new HashMap<>(gridSize);
        int i = 0, k = 1;
        for (var resource : resources) {
            final var context = new ExecutionContext();
            state(resource.exists(), "Resource does not exist: " + resource);
            context.putString(keyName, resource.getFilename());
            context.putString("opFileName", "output"+k+++".xml");
            map.put(PARTITION_KEY + i, context);
            i++;
        }
        return map;
    }

}