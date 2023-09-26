package tech.larin.consul.agent.service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.larin.consul.agent.configuration.AgentConfigurationProperties;
import tech.larin.consul.agent.domain.ConsulService;
import tech.larin.consul.agent.domain.DockerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class Agent {

  private final AgentConfigurationProperties config;
  private final Discovery discovery;
  private final Registry registry;

  @PostConstruct
  public void run() {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleAtFixedRate(
        this::registerServicesWithConsul, 0, config.getPollingInterval(), TimeUnit.SECONDS);
  }

  private void registerServicesWithConsul() {
    discovery.services().stream()
        .flatMap(
            service -> service.getPorts().stream().map(port -> buildConsulService(service, port)))
        .filter(Objects::nonNull)
        .forEach(registry::register);
  }

  private ConsulService buildConsulService(DockerService container, Integer port) {
    List<String> tags =
        container.getLabels().entrySet().stream()
            .filter(e -> e.getKey().startsWith(config.getConsulPrefix()))
            .map(e -> e.getKey().replaceFirst(config.getConsulPrefix(), "") + "=" + e.getValue())
            .collect(Collectors.toList());

    if (!tags.isEmpty()) {
      String serviceName = consulServiceName(container.getName(), port);
      return new ConsulService(serviceName, container.getIp(), port, tags);
    }
    return null;
  }

  private static String consulServiceName(String name, Integer port) {
    return name.replaceAll("[^a-zA-Z0-9-]", "-")
            .replaceAll("^[^a-zA-Z0-9]+", "")
            .replaceAll("[^a-zA-Z0-9]+$", "")
        + "-"
        + port;
  }
}
