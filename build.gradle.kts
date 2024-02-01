plugins {
    java
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.graalvm.buildtools.native") version "0.9.28"
    id("io.freefair.lombok") version "8.4"
    id("com.diffplug.spotless") version "6.23.3"
}

group = "tech.larin"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter")

    // Docker
    implementation("com.github.docker-java:docker-java-core:3.3.3")
    implementation("com.github.docker-java:docker-java-transport-zerodep:3.3.3")

    // Consul
    implementation("com.ecwid.consul:consul-api:1.4.5")

    // Logback
    implementation("ch.qos.logback:logback-core:1.4.14")
    implementation("org.slf4j:slf4j-api:2.0.10")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // Mapstruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Collections
    implementation("com.google.guava:guava:32.1.2-jre")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation("org.testcontainers:testcontainers:1.19.3")
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("consul-agent")
            mainClass.set("tech.larin.consul.agent.ConsulAgentApplication")
            buildArgs.add("-g")
            buildArgs.add("-O0")
        }
        named("test") {
            buildArgs.add("-O0")
        }
    }
    binaries.all {
        buildArgs.add("--verbose")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(
        listOf(
            "-Amapstruct.defaultComponentModel=spring",
            "-Amapstruct.unmappedTargetPolicy=ERROR"
        )
    )
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    java {
        targetExclude("build/generated/**")
        googleJavaFormat()
    }
}