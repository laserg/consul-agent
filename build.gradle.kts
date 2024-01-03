import com.diffplug.gradle.spotless.SpotlessTask

plugins {
	java
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.graalvm.buildtools.native") version "0.9.28"
	id("io.freefair.lombok") version "6.2.0"
	id("com.diffplug.spotless") version "6.13.0"
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
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// Docker
	implementation("com.github.docker-java:docker-java-core:3.3.3")
	implementation("com.github.docker-java:docker-java-transport-zerodep:3.3.3")

	// Consul
	implementation("com.ecwid.consul:consul-api:1.4.5")

	// Logback
	implementation("ch.qos.logback:logback-classic:1.2.6")

	// Lombok
	compileOnly("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")
	implementation("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	// Mapstruct
	implementation("org.mapstruct:mapstruct:1.4.2.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.4.2.Final")

	// Collections
	implementation("com.google.guava:guava:32.1.2-jre")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

spotless {
	java {
		googleJavaFormat()
	}
}