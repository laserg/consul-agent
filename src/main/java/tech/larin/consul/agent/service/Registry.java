package tech.larin.consul.agent.service;

import com.ecwid.consul.v1.agent.model.NewService;
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
    newService.setPort(service.getPort());
    newService.setTags(service.getTags());

    NewService.Check httpCheck = new NewService.Check();
    httpCheck.setTcp(service.getIp() + ":" + service.getPort());
    httpCheck.setInterval("10s");
    newService.setCheck(httpCheck);

    consulClient.agentServiceRegister(newService);
  }
}
