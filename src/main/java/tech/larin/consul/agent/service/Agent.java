package tech.larin.consul.agent.service;

import static java.util.stream.Collectors.toSet;
import static tech.larin.consul.agent.domain.DockerService.Port.Protocol.TCP;
import static tech.larin.consul.agent.domain.DockerService.State.RUNNING;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
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

  private final UUID agentUid = UUID.randomUUID();

  @PreDestroy
  public void scheduleUnDiscovery() {
    registry.unregisterAllWith(agentUid);
  }

  @PostConstruct
  public void scheduleDiscovery() {
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
              String name = getName(service);
              List<String> tags = getTags(service);
              Set<Integer> ports = Sets.difference(getPorts(service), getIgnoredPorts(service));
              String ip = config.getBindIp();
              if (!tags.isEmpty()) {
                return new ConsulService(name, ip, ports, tags);
              } else {
                return null;
              }
            })
        .filter(Objects::nonNull)
        .peek(service -> log.info("Registered docker service: {}", service))
        .forEach(service -> registry.registerWith(service, agentUid));
  }

  private Set<Integer> getIgnoredPorts(DockerService service) {
    String ignoredPortsLabel = service.getLabel("consul.ignore.ports");
    return Arrays.stream(ignoredPortsLabel.split(","))
        .filter(Strings::isNotBlank)
        .map(Integer::parseInt)
        .collect(toSet());
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

  private static Set<Integer> getPorts(DockerService service) {
    return service.getPorts().stream()
        .filter(
            port ->
                !Objects.equals("::", port.getIp())
                    && Objects.equals(TCP, port.getProtocol())
                    && Objects.nonNull(port.getPort()))
        .map(DockerService.Port::getPort)
        .collect(toSet());
  }
}
