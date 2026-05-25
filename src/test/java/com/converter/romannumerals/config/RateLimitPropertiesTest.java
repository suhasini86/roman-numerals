package com.converter.romannumerals.config;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class RateLimitPropertiesTest {

    @Test
    void defaultValues_shouldBeSet() {
        RateLimitProperties props = new RateLimitProperties();

        assertThat(props.isEnabled()).isTrue();
        assertThat(props.getMaxRequests()).isEqualTo(50);
        assertThat(props.getTimeWindowSeconds()).isEqualTo(60);

    }

    @Test
    void settersAndGetters_shoulWork() {
        RateLimitProperties props = new RateLimitProperties();

        props.setEnabled(false);
        props.setMaxRequests(100);
        props.setTimeWindowSeconds(120);

        assertThat(props.isEnabled()).isFalse();
        assertThat(props.getMaxRequests()).isEqualTo(100);
        assertThat(props.getTimeWindowSeconds()).isEqualTo(120);

    }
}
