# Stage 1: Build the native application using GraalVM and Gradle
FROM gradle:8.5.0-jdk21-graal AS builder

WORKDIR /app

# Copy only necessary files for dependency resolution
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy the application source code
COPY src/ src/

# Build the native executable using GraalVM
RUN gradle --no-daemon nativeCompile

# Run the gradle check command to test the application
RUN gradle --no-daemon nativeTest

# Stage 2: Create a minimal image with only the native executable
FROM ubuntu:jammy as final

WORKDIR /app

# Copy the native executable from the builder stage
COPY --from=builder /app/build/native/nativeCompile/consul-agent /app/consul-agent
COPY --from=builder /app/build/native/nativeCompile/consul-agent.debug /app/consul-agent.debug
COPY --from=builder /app/build/native/nativeCompile/sources /app/sources

EXPOSE 5005

# Install gdbserver for debugging
RUN apt-get update && apt-get install -y gdbserver

# Set the entry point to run gdbserver with the native executable for remote debugging
ENTRYPOINT ["gdbserver", "0.0.0.0:5005", "/app/consul-agent"]