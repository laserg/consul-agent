package tech.larin.consul.agent.service;

import static java.util.function.Predicate.not;
import static tech.larin.consul.agent.domain.ConsulService.Protocol.TCP;
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
    List<DockerService> dockerServices =
        discovery.services().stream()
            .filter(service -> Objects.equals(RUNNING, service.getState()))
            .peek(service -> log.info("Discovered docker service: {}", service))
            .toList();
    List<ConsulService> consulServices =
        consulServiceMapper.map(dockerServices).stream()
            .filter(service -> Objects.equals(TCP, service.getProtocol()))
            .map(service -> service.filterTagsBy(config.getConsulPrefix()))
            .filter(not(service -> service.getTags().isEmpty()))
            .peek(service -> log.info("Registered consul service: {}", service))
            .toList();
    consulServices.forEach(registry::register);
  }
}
