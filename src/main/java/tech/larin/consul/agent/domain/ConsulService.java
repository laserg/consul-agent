package tech.larin.consul.agent.domain;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ConsulService {
  private final String name;
  private final String ip;
  private final Integer port;
  private final List<String> tags;

  @Getter
  @RequiredArgsConstructor
  public static class Port {
    private final Integer port;
    private final DockerService.Port.Protocol protocol;

    public enum Protocol {
      TCP,
      UDP
    }
  }
}
