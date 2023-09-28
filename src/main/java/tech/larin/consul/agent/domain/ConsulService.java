package tech.larin.consul.agent.domain;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = {"name", "ip"})
@RequiredArgsConstructor
public class ConsulService {
  private final String name;
  private final String ip;
  private final List<Integer> ports;
  private final List<String> tags;
}
