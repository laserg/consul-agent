# Consul agent

Consul agent for Docker services discovery.

## Table of Contents

- [Project Title](#project-title)
- [Description](#description)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [Usage](#usage)

## Description

This Java application acts as an agent, listening to the Docker socket and publishing services to Consul. It automatically registers Docker containers as Consul services, making them discoverable within your infrastructure.

## Getting Started

Just add consul-agent to you compose file and configure environment variables.

### Prerequisites

* Docker 
* Consul

### Installation

Add and configure consul agent using docker compose.

```yaml
version: '3'

services:
  consul-agent:
    image: laserg/core.cloud.catalog:latest
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
    environment:
      - AGENT_DOCKER_HOST=unix:///var/run/docker.sock
      - AGENT_CONSUL_HOST=consul://${REGISTRY_SERVER_IP}:8500
      - AGENT_POLLING_INTERVAL=60
      - AGENT_BIND_IP=${APP_SERVER_IP}
      - AGENT_CONSUL_PREFIX=consul.
    networks:
      - traefik_network
```

## Usage

These environment variables configure the behavior of the Consul agent when it runs inside a Docker container. They control how the agent connects to Consul servers, interacts with the Docker daemon, and how frequently it checks for updates in the Docker environment.

### AGENT_DOCKER_HOST

This environment variable specifies the Docker host the Consul agent should connect to. In this case, it is set to use the Unix socket at `/var/run/docker.sock`, which allows the Consul agent to interact with the Docker daemon on the host machine.

### AGENT_CONSUL_HOST

This environment variable specifies the address of the Consul server that the Consul agent should join. It uses the URL format and specifies the IP address or hostname of the Consul server along with the default Consul port, `8500`.

### AGENT_POLLING_INTERVAL

This environment variable specifies the time interval at which the Consul agent periodically scans for and discovers new Docker containers running on the host.

### AGENT_BIND_IP

This variable specifies the IP address that the Consul agent should bind services to. It typically binds to the IP of the host machine where the agent is running.

### AGENT_CONSUL_PREFIX

This environment variable specifies the prefix that will be used for detect labels should be converted to Consul tags.

