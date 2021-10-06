plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
}

version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("de.baleipzig.pdfextraction.client.Launcher")
}

javafx {
    modules("javafx.controls", "javafx.fxml")
    version = "17"
}

dependencies {
    implementation(project(":API"))

    // https://mvnrepository.com/artifact/org.springframework/spring-webflux
    implementation("org.springframework:spring-webflux:5.3.10")
    // https://mvnrepository.com/artifact/io.projectreactor.netty/reactor-netty
    runtimeOnly("io.projectreactor.netty:reactor-netty:1.0.11")

    // https://mvnrepository.com/artifact/jakarta.inject/jakarta.inject-api
    implementation("jakarta.inject:jakarta.inject-api:2.0.0")
    // https://mvnrepository.com/artifact/jakarta.annotation/jakarta.annotation-api
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.0-B1")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")

    compileOnly("org.slf4j:slf4j-api:2.0.0-alpha5")
    runtimeOnly("ch.qos.logback:logback-classic:1.3.0-alpha10")


    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.testfx:testfx-junit5:4.0.16-alpha")
    // https://mvnrepository.com/artifact/io.projectreactor/reactor-test
    testImplementation("io.projectreactor:reactor-test:3.4.10")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    testImplementation("com.squareup.okhttp3:okhttp:4.9.2")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/mockwebserver
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.2")
}

tasks.test {
    useJUnitPlatform()
}

application {
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}

