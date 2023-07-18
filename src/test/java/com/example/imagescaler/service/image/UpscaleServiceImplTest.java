package com.example.imagescaler.service.image;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertArrayEquals((byte[]) result.getBody(),Base64.getDecoder().decode(VALID_BASE64));
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

        var result = upscaleService.upscale(image);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Max image pixels for input is 1048576 (width * height), but got 1918800 (1599 * 1200)", result.getBody());
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

        var result = upscaleService.upscale(image);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("API Error: No exception information found in the response.", result.getBody());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testUpscaleInvalidImageSize() {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(11 * 1024 * 1024L); // Image size > MAX_FILE_SIZE

        var result = upscaleService.upscale(image);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("File size exceeds the maximum allowed limit of 10mb.", result.getBody());
        verify(restTemplate, never()).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testUpscaleInvalidBase64Data() throws IOException {
        var image = mock(MultipartFile.class);
        when(image.getSize()).thenReturn(1024L);
        when(image.getBytes()).thenReturn("invalid-base64-data".getBytes());

        var result = upscaleService.upscale(image);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid base64 encoded data.", result.getBody());
        verify(restTemplate, never()).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

}