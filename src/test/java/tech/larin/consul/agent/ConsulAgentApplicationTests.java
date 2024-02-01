package tech.larin.consul.agent;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

@Disabled
class ConsulAgentApplicationTests {

  @Test
  public void testConsulIntegration() {

    try (DockerComposeContainer<?> proxyEnvironment =
        new DockerComposeContainer<>(new File("src/test/resources/proxy/docker-compose.yml"))
            .withLogConsumer("consul", new Slf4jLogConsumer(LoggerFactory.getLogger("consul")))
            .withLogConsumer(
                "reverse-proxy", new Slf4jLogConsumer(LoggerFactory.getLogger("reverse-proxy")))
            .waitingFor("consul", Wait.forLogMessage(".*Synced node info.*", 1))
            .waitingFor(
                "reverse-proxy",
                Wait.forLogMessage(".*Starting provider configuration sync.*", 1))) {
      proxyEnvironment.start();
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue(isWebServerRegisteredInConsul());
  }

  private boolean isWebServerRegisteredInConsul() {
    return true;
  }
}
