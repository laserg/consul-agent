package tech.larin.consul.agent.domain;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ConsulService {
  private final String name;
  private final String ip;
  private final Integer port;
  private final Protocol protocol;
  private final List<String> tags;

  public ConsulService filterTagsBy(String prefix) {
    return new ConsulService(
        name,
        ip,
        port,
        protocol,
        tags.stream()
            .filter(tag -> tag.startsWith(prefix))
            .map(tag -> tag.replaceFirst(prefix, ""))
            .collect(Collectors.toList()));
  }

  @ToString
  public enum Protocol {
    TCP,
    UDP
  }
}
