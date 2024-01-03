FROM openjdk:21

WORKDIR /app

COPY build/libs/consul-agent.jar /app/consul-agent.jar

CMD ["java", "-jar", "consul-agent.jar"]
