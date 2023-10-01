package tech.larin.consul.agent.service;

import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.agent.model.Service;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.larin.consul.agent.configuration.AgentConfigurationProperties;
import tech.larin.consul.agent.domain.ConsulService;

@Slf4j
@Component
@RequiredArgsConstructor
public class Registry {
  private final AgentConfigurationProperties config;
  private final com.ecwid.consul.v1.ConsulClient consulClient;

  public void registerWith(ConsulService service, UUID agentId) {
    NewService newService = new NewService();
    newService.setName(service.getName());
    newService.setAddress(service.getIp());
    newService.setTags(service.getTags());

    List<NewService.Check> checks =
        service.getPorts().stream()
            .map(
                port -> {
                  NewService.Check check = new NewService.Check();
                  check.setTcp(service.getIp() + ":" + port);
                  check.setInterval("3s");
                  return check;
                })
            .toList();
    newService.setChecks(checks);

    newService.setMeta(Map.of("agentId", agentId.toString()));

    consulClient.agentServiceRegister(newService, config.getConsulToken());
  }

  public void unregisterAllWith(UUID agentId) {
    Collection<Service> services = consulClient.getAgentServices().getValue().values();
    services.stream()
        .filter(service -> Objects.equals(agentId.toString(), service.getMeta().get("agentId")))
        .forEach(
            service -> {
              consulClient.agentServiceDeregister(service.getId());
            });
  }
}
