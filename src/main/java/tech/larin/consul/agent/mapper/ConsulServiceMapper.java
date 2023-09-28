package tech.larin.consul.agent.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import tech.larin.consul.agent.domain.ConsulService;
import tech.larin.consul.agent.domain.DockerService;

@Mapper
public interface ConsulServiceMapper {

  default List<ConsulService> map(Set<DockerService> dockerServices) {
    return dockerServices.stream()
        .map(this::map)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  default List<ConsulService> map(DockerService dockerService) {
    return dockerService.getPorts().stream()
        .map(
            port -> {
              String name = consulServiceName(dockerService.getName(), port.getPort());
              ConsulService.Protocol protocol = mapProtocol(port.getProtocol());
              List<String> tags = convertLabelsToTags(dockerService.getLabels());
              return new ConsulService(name, dockerService.getIp(), port.getPort(), protocol, tags);
            })
        .collect(Collectors.toList());
  }

  ConsulService.Protocol mapProtocol(DockerService.Port.Protocol protocol);

  private List<String> convertLabelsToTags(Map<String, String> labels) {
    return labels.entrySet().stream()
        .map(e -> e.getKey() + "=" + e.getValue())
        .collect(Collectors.toList());
  }

  private String consulServiceName(String name, Integer port) {
    return name.replaceAll("[^a-zA-Z0-9-]", "-")
            .replaceAll("^[^a-zA-Z0-9]+", "")
            .replaceAll("[^a-zA-Z0-9]+$", "")
        + "-"
        + port;
  }
}
