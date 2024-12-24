plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.security:spring-security-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-logging")
	implementation("com.nimbusds:nimbus-jose-jwt:9.31")
	implementation("org.bouncycastle:bcprov-jdk15on:1.70")
	implementation ("io.netty:netty-resolver-dns-native-macos:4.1.115.Final")

//	implementation("org.springframework.boot:spring-boot-starter-security")

//	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
	implementation("org.webjars:jquery:3.4.1")
	implementation("org.webjars:bootstrap:4.3.1")
	implementation("org.webjars:webjars-locator-core")
	implementation("org.webjars:js-cookie:2.1.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
