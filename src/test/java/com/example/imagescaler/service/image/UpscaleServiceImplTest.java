package com.example.imagescaler.service.image;

import com.example.imagescaler.exception.CustomImageProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

import static org.mockito.Mockito.*;
import static resources.TestConstants.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class UpscaleServiceImplTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private UpscaleServiceImpl upscaleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidResponse() throws IOException {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(1024L);
        when(image.getBytes()).thenReturn(VALID_BASE64.getBytes());


        ResponseEntity<String> mockResponse = new ResponseEntity<>(VALID_RESPONSE, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        var result = upscaleService.upscale(image);

        Assertions.assertArrayEquals( result,Base64.getDecoder().decode(VALID_BASE64));
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }
    @Test
    public void testParseMessageErrorWithArgs() throws IOException {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(1024L);
        when(image.getBytes()).thenReturn(VALID_BASE64.getBytes());


        ResponseEntity<String> mockResponse = new ResponseEntity<>(ERROR_WITH_ARGS, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        Assertions.assertThrows(CustomImageProcessingException.UpscaleApiException.class, () -> upscaleService.upscale(image));
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testParseMessageErrorWithoutArgs() throws IOException {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(1024L);
        when(image.getBytes()).thenReturn(VALID_BASE64.getBytes());

        ResponseEntity<String> mockResponse = new ResponseEntity<>(ERROR_WITHOUT_ARGS, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        Assertions.assertThrows(CustomImageProcessingException.UpscaleApiUnexpectedException.class, () -> upscaleService.upscale(image));
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testUpscaleInvalidImageSize() {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(11 * 1024 * 1024L); // Image size > MAX_FILE_SIZE

        Assertions.assertThrows(CustomImageProcessingException.InvalidImageSizeException.class, () -> upscaleService.upscale(image));
        verify(restTemplate, never()).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testUpscaleInvalidBase64Data() throws IOException {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(1024L);
        when(image.getBytes()).thenReturn("invalid-base64-data".getBytes());

        Assertions.assertThrows(CustomImageProcessingException.InvalidBase64DataException.class, () -> upscaleService.upscale(image));
        verify(restTemplate, never()).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

}