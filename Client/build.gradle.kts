plugins {
    java
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
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    runtimeOnly("com.fasterxml.jackson.core:jackson-databind:2.13.0")

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    // https://mvnrepository.com/artifact/io.projectreactor/reactor-test
    testImplementation("io.projectreactor:reactor-test:3.4.10")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/mockwebserver
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.2")

}

tasks.compileJava {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}
