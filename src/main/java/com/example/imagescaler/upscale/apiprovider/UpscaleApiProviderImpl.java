package com.example.imagescaler.upscale.apiprovider;

import com.example.imagescaler.upscale.configuration.UpscaleApiConfig;
import java.time.Duration;
import lombok.val;
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
  private final WebClient webClient;

  public UpscaleApiProviderImpl(
    UpscaleApiConfig.Configuration apiConfig,
    WebClient.Builder webClientBuilder
  ) {
    val httpClient = HttpClient.create().responseTimeout(Duration.ofMinutes(2));

    val connector = new ReactorClientHttpConnector(httpClient);

    this.webClient =
      webClientBuilder
        .baseUrl(apiConfig.getUrl())
        .defaultHeader(
          HttpHeaders.CONTENT_TYPE,
          MediaType.APPLICATION_JSON_VALUE
        )
        .defaultHeader(HttpHeaders.AUTHORIZATION, apiConfig.getKey())
        .clientConnector(connector)
        .build();
  }

  @Override
  public Mono<String> upscale(String jsonPayload) {
    return this.webClient.post()
      .body(BodyInserters.fromValue(jsonPayload))
      .retrieve()
      .bodyToMono(String.class);
  }
}
