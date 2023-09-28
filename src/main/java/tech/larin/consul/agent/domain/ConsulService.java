package tech.larin.consul.agent.domain;

import static java.util.stream.Collectors.toList;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = {"name", "ip", "port", "protocol"})
@RequiredArgsConstructor
public class ConsulService {
  private final String name;
  private final String ip;
  private final Integer port;
  private final Protocol protocol;
  private final List<String> tags;

  public ConsulService withTagsFilteredBy(String prefix) {
    return new ConsulService(
        name,
        ip,
        port,
        protocol,
        tags.stream()
            .filter(tag -> tag.startsWith(prefix))
            .map(tag -> tag.replaceFirst(prefix, ""))
            .collect(toList()));
  }

  public ConsulService withIp(String ip) {
    return new ConsulService(
        name,
        ip,
        port,
        protocol,
        tags);
  }

  @ToString
  public enum Protocol {
    TCP,
    UDP
  }
}
