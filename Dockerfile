FROM openjdk:17

WORKDIR /app

COPY build/libs/consul-agent.jar /app/consul-agent.jar

CMD ["java", "-jar", "consul-agent.jar"]
