package de.baleipzig.pdfextraction.client.connector.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.client.connector.api.TemplateConnector;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

class TemplateConnectorTest {

    private MockWebServer mock;
    private TemplateConnector connector;

    @BeforeEach
    void setUp() {
        this.mock = new MockWebServer();
        this.connector = new TemplateConnectorImpl(this.mock.url("").toString());
    }

    @Test
    void HasName()
            throws JsonProcessingException {
        this.mock.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setBody(new ObjectMapper().writeValueAsString(List.of("test", "test2", "test3")))
        );

        StepVerifier.create(this.connector.getAllNames())
                .expectNext("test", "test2", "test3")
                .verifyComplete();
    }

    @Test
    void CanSave() {
        this.mock.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.CREATED.value()));

        StepVerifier.create(this.connector.save(new TemplateDTO("test", "Hello World.", Collections.emptyList())))
                .verifyComplete();
    }

    @Test
    void CanRetrieve()
            throws JsonProcessingException {
        final TemplateDTO toSave = new TemplateDTO("test", "Hello World.", List.of());

        this.mock.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setBody(new ObjectMapper().writeValueAsString(toSave))
        );

        StepVerifier.create(this.connector.getForName(toSave.getName()))
                .expectNextMatches(toSave::equals)
                .verifyComplete();
    }

    @Test
    void NegativeTest() {
        this.mock.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        StepVerifier.create(this.connector.getAllNames())
                .expectError()
                .verify();
    }
}
