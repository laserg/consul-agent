package tech.larin.consul.agent.hint;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class AppHintsRegistrar implements RuntimeHintsRegistrar {
  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    hints.serialization().registerType(com.github.dockerjava.api.model.Container.class);
    hints.serialization().registerType(com.github.dockerjava.api.model.ContainerPort.class);
  }
}
