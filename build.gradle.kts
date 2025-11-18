plugins {
  kotlin("jvm") version "2.2.21"
  kotlin("plugin.allopen") version "2.2.21"
  id("io.quarkus")
  id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
}

ktlint {
  android.set(false)
  ignoreFailures.set(false)
  filter {
    exclude("**/build/**")
  }
}

repositories {
  mavenCentral()
  mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
  implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
  implementation("io.quarkus:quarkus-logging-json")
  implementation("io.quarkus:quarkus-smallrye-health")
  implementation("io.quarkus:quarkus-hibernate-validator")
  implementation("io.quarkus:quarkus-arc")
  implementation("io.quarkus:quarkus-smallrye-jwt")
  implementation("io.quarkus:quarkus-smallrye-jwt-build")
  implementation("io.quarkus:quarkus-smallrye-openapi")
  implementation("io.quarkus:quarkus-rest")
  implementation("io.quarkus:quarkus-kotlin")
  implementation("io.quarkus:quarkus-rest-jackson")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("io.quarkus:quarkus-micrometer")
  implementation("io.quarkus:quarkus-micrometer-registry-prometheus")
  testImplementation("io.quarkus:quarkus-junit5")
  testImplementation("io.rest-assured:rest-assured:5.5.6")
  testImplementation("io.mockk:mockk:1.14.6")
  testImplementation("org.assertj:assertj-core:4.0.0-M1")
}

group = "com.serhii"
version = "1.0.0-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
  systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
  jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}
allOpen {
  annotation("jakarta.ws.rs.Path")
  annotation("jakarta.enterprise.context.ApplicationScoped")
  annotation("jakarta.persistence.Entity")
  annotation("io.quarkus.test.junit.QuarkusTest")
}

kotlin {
  compilerOptions {
    jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    javaParameters = true
  }
}
