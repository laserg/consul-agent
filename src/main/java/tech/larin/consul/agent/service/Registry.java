package tech.larin.consul.agent.service;

import com.ecwid.consul.v1.agent.model.NewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.larin.consul.agent.domain.ConsulService;

@Slf4j
@Component
@RequiredArgsConstructor
public class Registry {
  private final com.ecwid.consul.v1.ConsulClient consulClient;

  public void register(ConsulService service) {
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

    consulClient.agentServiceRegister(newService);
  }
}
