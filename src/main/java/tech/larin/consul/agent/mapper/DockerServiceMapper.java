package tech.larin.consul.agent.mapper;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.larin.consul.agent.domain.DockerService;

@Mapper
public interface DockerServiceMapper {
  @Mapping(target = "name", expression = "java(mapContainerName(container))")
  @Mapping(target = "ip", source = "bindIp")
  @Mapping(target = "ports", expression = "java(mapContainerPorts(container, bindPorts))")
  @Mapping(target = "labels", source = "container.labels")
  DockerService map(Container container, String bindIp, List<Integer> bindPorts);

  default String mapContainerName(Container container) {
    return container.getNames()[0];
  }

  default List<Integer> mapContainerPorts(Container container, List<Integer> bindPorts) {
    return Arrays.stream(container.ports)
        .map(ContainerPort::getPublicPort)
        .filter(bindPorts::contains)
        .collect(Collectors.toList());
  }
}
