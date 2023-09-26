package tech.larin.consul.agent.configuration;

import com.ecwid.consul.v1.ConsulClient;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ConsulConfiguration {
  private final AgentConfigurationProperties configuration;

  @Bean
  public ConsulClient consulClient() {
    log.debug("Accessing consul on: {}", configuration.getConsulHost());

    URI uri = URI.create(configuration.getConsulHost());
    return new ConsulClient(uri.getHost(), uri.getPort());
  }
}
