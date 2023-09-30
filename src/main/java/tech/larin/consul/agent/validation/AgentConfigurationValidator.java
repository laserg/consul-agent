package tech.larin.consul.agent.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import tech.larin.consul.agent.configuration.AgentConfigurationProperties;

public class AgentConfigurationValidator implements Validator {
  @Override
  public boolean supports(Class<?> clazz) {
    return AgentConfigurationProperties.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    AgentConfigurationProperties config = (AgentConfigurationProperties) target;
    String consulPrefix = config.getConsulPrefix();

    if (consulPrefix == null || consulPrefix.isEmpty() || consulPrefix.startsWith("consul.")) {
      errors.rejectValue(
          "AGENT_CONSUL_PREFIX", "invalid.prefix", "Consul prefix should not be reserved");
    }
  }
}
