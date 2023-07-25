package com.example.imagescaler.service.image;

import com.example.imagescaler.ImageScalerApplication;
import com.example.imagescaler.exception.CustomImageProcessingException;
import com.example.imagescaler.provider.upscaleApi.UpscaleApiProviderImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static resources.TestConstants.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ImageScalerApplication.class)
class UpscaleServiceImplTest {


    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private UpscaleApiProviderImpl mockedProvider;

    @InjectMocks
    private UpscaleServiceImpl upscaleService;

    @BeforeEach
    void setUp() {
        upscaleService = new UpscaleServiceImpl(new ObjectMapper(), mockedProvider);
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
    public void testParseMessageErrorWithArgs() throws IOException {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(1024L);
        when(image.getBytes()).thenReturn(VALID_BASE64.getBytes());
        when(mockedProvider.upscale(anyString())).thenReturn(Mono.just(ERROR_WITH_ARGS));

        Mono<byte[]> resultMono = upscaleService.upscale(image);

        StepVerifier.create(resultMono)
                .expectError(CustomImageProcessingException.UpscaleApiException.class)
                .verify();
    }

    @Test
    public void testParseMessageErrorWithoutArgs() throws IOException {

        var mockedProvider = mock(UpscaleApiProviderImpl.class);
        var image = mock(MultipartFile.class);

        when(image.getSize()).thenReturn(1024L);
        when(image.getBytes()).thenReturn(VALID_BASE64.getBytes());
        when(mockedProvider.upscale(anyString())).thenReturn(Mono.just(ERROR_WITHOUT_ARGS));

        UpscaleService upscaleService = new UpscaleServiceImpl(new ObjectMapper(), mockedProvider);

        Mono<byte[]> resultMono = upscaleService.upscale(image);

        StepVerifier.create(resultMono)
                .expectError(CustomImageProcessingException.UpscaleApiUnexpectedException.class)
                .verify();
    }
}