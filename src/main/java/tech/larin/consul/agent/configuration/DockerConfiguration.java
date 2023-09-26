package tech.larin.consul.agent.configuration;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DockerConfiguration {
  private final AgentConfigurationProperties configuration;

  @Bean
  public DockerClient dockerClient() {
    log.debug("Accessing docker on: {}", configuration.getDockerHost());

    DefaultDockerClientConfig config =
        DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(configuration.getDockerHost())
            .build();
    DockerHttpClient httpClient =
        new ZerodepDockerHttpClient.Builder().dockerHost(config.getDockerHost()).build();

    return DockerClientImpl.getInstance(config, httpClient);
  }
}
