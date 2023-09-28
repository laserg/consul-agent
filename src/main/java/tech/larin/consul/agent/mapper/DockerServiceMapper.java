package tech.larin.consul.agent.mapper;

import static java.util.stream.Collectors.toSet;
import static tech.larin.consul.agent.domain.DockerService.*;

import com.github.dockerjava.api.model.Container;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.larin.consul.agent.domain.DockerService;

@Mapper
public interface DockerServiceMapper {
  @Mapping(target = "name", expression = "java(mapContainerName(container))")
  @Mapping(target = "ports", expression = "java(mapContainerPorts(container))")
  @Mapping(target = "labels", source = "container.labels")
  @Mapping(target = "state", expression = "java(mapState(container))")
  DockerService map(Container container);

  default String mapContainerName(Container container) {
    return container.getNames()[0];
  }

  default Set<Port> mapContainerPorts(Container container) {
    return Arrays.stream(container.ports)
        .map(
            port -> {
              Port.Protocol protocol =
                  switch (Objects.requireNonNull(port.getType())) {
                    case "tcp" -> Port.Protocol.TCP;
                    case "udp" -> Port.Protocol.UDP;
                    default -> null;
                  };

              return new Port(port.getIp(), port.getPublicPort(), protocol);
            })
        .collect(toSet());
  }

  default State mapState(Container container) {
    return switch (Objects.requireNonNull(container.getState())) {
      case "exited" -> State.EXITED;
      case "running" -> State.RUNNING;
      default -> null;
    };
  }
}
