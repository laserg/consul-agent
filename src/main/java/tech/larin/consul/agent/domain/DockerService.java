package tech.larin.consul.agent.domain;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DockerService {
  private final String name;
  private final String ip;
  private final List<Integer> ports;
  private final Map<String, String> labels;
}
