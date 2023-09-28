package tech.larin.consul.agent.service;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import static tech.larin.consul.agent.domain.ConsulService.Protocol.TCP;
import static tech.larin.consul.agent.domain.DockerService.State.RUNNING;

import java.util.Objects;
import java.util.Set;
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
import tech.larin.consul.agent.mapper.ConsulServiceMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class Agent {

  private final AgentConfigurationProperties config;
  private final Discovery discovery;
  private final Registry registry;

  private final ConsulServiceMapper consulServiceMapper;

  @PostConstruct
  public void run() {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleAtFixedRate(
        this::registerServicesWithConsul, 0, config.getPollingInterval(), TimeUnit.SECONDS);
  }

  private void registerServicesWithConsul() {
    Set<DockerService> dockerServices =
        discovery.services().stream()
            .filter(service -> Objects.equals(RUNNING, service.getState()))
            .map(DockerService::filterPortsWithIps)
            .peek(service -> log.info("Discovered docker service: {}", service))
            .collect(toSet());
    Set<ConsulService> consulServices =
        consulServiceMapper.map(dockerServices).stream()
            .filter(service -> Objects.equals(TCP, service.getProtocol()))
            .filter(service -> Objects.nonNull(service.getPort()))
            .map(service -> service.filterTagsBy(config.getConsulPrefix()))
            .filter(not(service -> service.getTags().isEmpty()))
            .peek(service -> log.info("Registered consul service: {}", service))
            .collect(toSet());
    consulServices.forEach(registry::register);
  }
}
