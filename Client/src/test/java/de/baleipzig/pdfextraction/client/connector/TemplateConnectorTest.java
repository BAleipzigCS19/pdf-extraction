package de.baleipzig.pdfextraction.client.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

class TemplateConnectorTest {

    private MockWebServer mock;
    private TemplateConnector connector;

    @BeforeEach
    void setUp() throws IOException {
        this.mock = new MockWebServer();
        this.mock.start();
        this.connector = new TemplateConnector("http://%s:%s".formatted(this.mock.getHostName(), this.mock.getPort()));
    }

    @Test
    void HasName() {
        this.mock.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setBody("[{\"name\":\"test\",\"content\":\"\"}]")
        );

        StepVerifier.create(this.connector.getAllNames())
                .expectNext(List.of(new TemplateDTO("test", "")))
                .verifyComplete();
    }

    @Test
    void CanSave() {
        this.mock.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.CREATED.value()));

        StepVerifier.create(this.connector.save(new TemplateDTO("test", "Hello World.")))
                .verifyComplete();
    }

    @Test
    void CanRetrieve()
            throws JsonProcessingException {
        final TemplateDTO toSave = new TemplateDTO("test", "Hello World.");

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
                .expectError();
    }
}
