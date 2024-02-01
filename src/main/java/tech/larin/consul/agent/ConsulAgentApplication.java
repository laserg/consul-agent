package tech.larin.consul.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import tech.larin.consul.agent.hint.AppHintsRegistrar;

@SpringBootApplication
@ImportRuntimeHints(AppHintsRegistrar.class)
public class ConsulAgentApplication {

  public static void main(String[] args) {
    SpringApplication.run(ConsulAgentApplication.class, args);
  }
}
