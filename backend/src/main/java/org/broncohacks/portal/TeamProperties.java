package org.broncohacks.portal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "team")
public record TeamProperties(
        int minSize,
        int maxSize
) {
}