package tech.larin.consul.agent.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import tech.larin.consul.agent.validation.AgentConfigurationValidator;

@Configuration
@EnableConfigurationProperties(AgentConfigurationProperties.class)
public class AgentConfiguration {

  @Bean
  public Validator agentConfigurationValidator() {
    return new AgentConfigurationValidator();
  }
}
