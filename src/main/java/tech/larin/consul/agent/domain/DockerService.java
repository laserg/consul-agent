package tech.larin.consul.agent.domain;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class DockerService {
  private final String name;
  private final String ip;
  private final List<Port> ports;
  private final Map<String, String> labels;
  private final State state;

  @Getter
  @ToString
  @RequiredArgsConstructor
  public static class Port {
    private final Integer port;
    private final Protocol protocol;

    @ToString
    public enum Protocol {
      TCP,
      UDP
    }
  }

  public enum State {
    EXITED,
    RUNNING
  }
}
