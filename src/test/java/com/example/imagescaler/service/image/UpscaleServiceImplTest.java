package com.example.imagescaler.service.image;

import com.example.imagescaler.ImageScalerApplication;
import com.example.imagescaler.exception.CustomImageProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Base64;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import static resources.TestConstants.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ImageScalerApplication.class)
class UpscaleServiceImplTest {

    @MockBean
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private UpscaleServiceImpl upscaleService;

    private WireMockServer wireMockServer;

    @Value("${api.url}")
    private String API_URL;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();

        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();

        doReturn(webClient).when(webClientBuilder).build();
    }

    @AfterEach
    public void cleanup() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    public void testUpscaleInvalidImageSize() {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(11 * 1024 * 1024L); // Image size > MAX_FILE_SIZE

        Assertions.assertThrows(CustomImageProcessingException.InvalidImageSizeException.class, () -> upscaleService.upscale(image).block());
        verify(webClientBuilder, never()).build();
    }

    @Test
    public void testUpscaleInvalidBase64Data() throws IOException {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(1024L);
        when(image.getBytes()).thenReturn("invalid-base64-data".getBytes());

        Assertions.assertThrows(CustomImageProcessingException.InvalidBase64DataException.class, () -> upscaleService.upscale(image).block());
        verify(webClientBuilder, never()).build();
    }

    @Test
    public void testValidResponse() throws IOException {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(1024L);
        when(image.getBytes()).thenReturn(VALID_BASE64.getBytes());

        wireMockServer.stubFor(post(urlEqualTo(API_URL))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                        .withBody(VALID_RESPONSE)));

        Mono<byte[]> resultMono = upscaleService.upscale(image);

        StepVerifier.create(resultMono)
                .expectNext(Base64.getDecoder().decode(VALID_RESPONSE))
                .verifyComplete();
    }

    @Test
    public void testParseMessageErrorWithArgs() throws IOException {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(1024L);
        when(image.getBytes()).thenReturn(VALID_BASE64.getBytes());

        wireMockServer.stubFor(post(urlEqualTo(API_URL))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                        .withBody(ERROR_WITH_ARGS)));

        Mono<byte[]> resultMono = upscaleService.upscale(image);

        StepVerifier.create(resultMono)
                .expectError(CustomImageProcessingException.UpscaleApiException.class)
                .verify();
    }

    @Test
    public void testParseMessageErrorWithoutArgs() throws IOException {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(1024L);
        when(image.getBytes()).thenReturn(VALID_BASE64.getBytes());

        wireMockServer.stubFor(post(urlEqualTo(API_URL))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                        .withBody(ERROR_WITHOUT_ARGS)));

        Mono<byte[]> resultMono = upscaleService.upscale(image);

        StepVerifier.create(resultMono)
                .expectError(CustomImageProcessingException.UpscaleApiUnexpectedException.class)
                .verify();
    }

}