package tech.larin.consul.agent.service;

import static tech.larin.consul.agent.domain.DockerService.Port.Protocol.TCP;
import static tech.larin.consul.agent.domain.DockerService.State.RUNNING;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
        .filter(service -> Objects.equals(RUNNING, service.getState()))
        .peek(service -> log.info("Discovered docker service: {}", service))
        .map(
            service -> {
              List<Integer> ports = getPorts(service);
              List<String> tags = getTags(service);
              String name = getName(service);
              String ip = config.getBindIp();
              return new ConsulService(name, ip, ports, tags);
            })
        .forEach(registry::register);
  }

  private static String getName(DockerService service) {
    return service
        .getName()
        .replaceAll("[^a-zA-Z0-9-]", "-")
        .replaceAll("^[^a-zA-Z0-9]+", "")
        .replaceAll("[^a-zA-Z0-9]+$", "");
  }

  private List<String> getTags(DockerService service) {
    return service.getLabels().entrySet().stream()
        .map(e -> e.getKey() + "=" + e.getValue())
        .filter(tag -> tag.startsWith(config.getConsulPrefix()))
        .map(tag -> tag.replaceFirst(config.getConsulPrefix(), ""))
        .toList();
  }

  private static List<Integer> getPorts(DockerService service) {
    return service.getPorts().stream()
        .filter(
            port ->
                !Objects.equals("::", port.getIp())
                    && Objects.equals(TCP, port.getProtocol())
                    && Objects.nonNull(port.getPort()))
        .map(DockerService.Port::getPort)
        .toList();
  }
}
