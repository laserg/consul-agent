package tech.larin.consul.agent.domain;

import java.util.Map;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = {"name"})
@RequiredArgsConstructor
public class DockerService {
  private final String name;
  private final Set<Port> ports;
  private final Map<String, String> labels;
  private final State state;

  @Getter
  @ToString
  @EqualsAndHashCode(of = {"ip", "port", "protocol"})
  @RequiredArgsConstructor
  public static class Port {
    private final String ip;
    private final Integer port;
    private final Protocol protocol;

    @ToString
    public enum Protocol {
      TCP,
      UDP
    }
  }

  @ToString
  public enum State {
    EXITED,
    RUNNING
  }
}
