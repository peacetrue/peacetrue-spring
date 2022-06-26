package com.github.peacetrue.spring.configuration.metadata;

import org.springframework.boot.configurationprocessor.metadata.ConfigurationMetadata;
import org.springframework.boot.configurationprocessor.metadata.ItemMetadata;
import org.springframework.boot.configurationprocessor.metadata.JsonMarshaller;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author peace
 **/
public class SpringConfigurationMetadataTest {

    void configuration() throws Exception {
        InputStream resource = getClass().getResourceAsStream("/META-INF/spring-configuration-metadata.json");
        ConfigurationMetadata metadata = new JsonMarshaller().read(resource);
        List<ItemMetadata> items = metadata.getItems().stream().sorted().collect(Collectors.toList());
        String rows = items.stream().map(this::toString).collect(Collectors.joining("\n"));
        System.out.println(rows);
    }

    private String toString(ItemMetadata item) {
        return "| " + String.join(" | ",
                item.getName(),
                Objects.toString(item.getType(), ""),
                Objects.toString(item.getDefaultValue(), ""),
                Objects.toString(item.getDescription(), "")
        );
    }


}
