package tech.larin.consul.agent.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "agent")
@Getter
@Setter
public class AgentConfigurationProperties {
  private int pollingInterval;
  private String consulHost;
  private String consulPrefix;
  private String dockerHost;
  private String bindIp;
}
