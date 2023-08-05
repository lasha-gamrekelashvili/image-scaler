package com.example.imagescaler.upscale.apiprovider;

import com.example.imagescaler.upscale.configuration.ApiProperties;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
public class UpscaleApiProviderImpl implements UpscaleApiProvider {
  private final ApiProperties apiProperties;
  private final WebClient webClient;

  public UpscaleApiProviderImpl(
    ApiProperties apiProperties,
    WebClient.Builder webClientBuilder
  ) {
    this.apiProperties = apiProperties;
    this.webClient = initializeWebClient(webClientBuilder);
  }

  private WebClient initializeWebClient(WebClient.Builder webClientBuilder) {
    HttpClient httpClient = HttpClient
      .create()
      .responseTimeout(Duration.ofMinutes(2));

    ReactorClientHttpConnector connector = new ReactorClientHttpConnector(
      httpClient
    );

    return webClientBuilder
      .baseUrl(apiProperties.getUrl())
      .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .defaultHeader(HttpHeaders.AUTHORIZATION, apiProperties.getKey())
      .clientConnector(connector)
      .build();
  }

  @Override
  public Mono<String> upscale(String jsonPayload) {
    return webClient
      .post()
      .body(BodyInserters.fromValue(jsonPayload))
      .retrieve()
      .bodyToMono(String.class);
  }
}
