package io.github.clfundev.better.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "security.ignored")
public class IgnoredUrls {
    private List<String> urls = new ArrayList<>();
}
