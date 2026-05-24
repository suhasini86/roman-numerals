package com.converter.romannumerals.controller;

import com.converter.romannumerals.service.RomanNumeralConverterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class RomanNumeralControllerUnitTest {

    private RomanNumeralController controller;
    private RomanNumeralConverterService svc;

    @BeforeEach
    void setup() {
        svc = Mockito.mock(RomanNumeralConverterService.class);
        controller = new RomanNumeralController(svc);
    }

    private void invokeValidate(HttpServletRequest request) throws Exception {
        Method m = RomanNumeralController.class.getDeclaredMethod("validateExclusiveParams", HttpServletRequest.class);
        m.setAccessible(true);
        m.invoke(controller, request);
    }

    @Test
    void validateExclusiveParams_bothQueryAndMin_throws() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getParameter("query")).thenReturn("1");
        when(req.getParameter("min")).thenReturn("1");

        assertThatThrownBy(() -> invokeValidate(req))
                .satisfies(t -> {
                    Throwable root = t.getCause();
                    org.assertj.core.api.Assertions.assertThat(root).isInstanceOf(RuntimeException.class);
                    org.assertj.core.api.Assertions.assertThat(root.getMessage()).contains("Provide either 'query' OR 'min & max'");
                });
    }

    @Test
    void validateExclusiveParams_minAndMaxEmpty_throwsMinMaxMessage() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getParameter("query")).thenReturn(null);
        when(req.getParameter("min")).thenReturn("");
        when(req.getParameter("max")).thenReturn("");

        // parameterMap with empty arrays to exercise length == 0 branch
        when(req.getParameterMap()).thenReturn(Map.of(
                "min", new String[0],
                "max", new String[0]
        ));

        assertThatThrownBy(() -> invokeValidate(req))
                .satisfies(t -> {
                    Throwable root = t.getCause();
                    org.assertj.core.api.Assertions.assertThat(root).isInstanceOf(RuntimeException.class);
                    org.assertj.core.api.Assertions.assertThat(root.getMessage()).contains("min and max must not be empty");
                });
    }

    @Test
    void validateExclusiveParams_queryMissing_throwsQueryMessage() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getParameter("query")).thenReturn(null);
        when(req.getParameter("min")).thenReturn("1");
        when(req.getParameter("max")).thenReturn(null);

        when(req.getParameterMap()).thenReturn(Map.of(
                "min", new String[]{"1"},
                "max", new String[]{(String) null}
        ));

        assertThatThrownBy(() -> invokeValidate(req))
                .satisfies(t -> {
                    Throwable root = t.getCause();
                    org.assertj.core.api.Assertions.assertThat(root).isInstanceOf(RuntimeException.class);
                    org.assertj.core.api.Assertions.assertThat(root.getMessage()).contains("query must not be empty");
                });
    }
}

