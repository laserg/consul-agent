package tech.larin.consul.agent.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.larin.consul.agent.configuration.AgentConfigurationProperties;
import tech.larin.consul.agent.domain.DockerService;
import tech.larin.consul.agent.mapper.DockerServiceMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class Discovery {
  private final com.github.dockerjava.api.DockerClient dockerClient;
  private final AgentConfigurationProperties configuration;
  private final DockerServiceMapper mapper;

  public List<DockerService> services() {

    try {
      return dockerClient.listContainersCmd().withShowAll(true).exec().stream()
          .map(
              container ->
                  mapper.map(container, configuration.getBindIp(), configuration.getBindPorts()))
          .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Error while containers scanning", e);
      return List.of();
    }
  }
}
