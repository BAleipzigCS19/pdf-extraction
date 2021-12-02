plugins {
    application
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.openjfx.javafxplugin") version "0.0.10"
}

version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("de.baleipzig.pdfextraction.client.Main")
}

application {
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}

javafx {
    modules("javafx.controls", "javafx.fxml")
    version = "17.0.1"
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":API"))

    // https://mvnrepository.com/artifact/org.springframework/spring-webflux
    implementation("org.springframework:spring-webflux:5.3.13")
    // https://mvnrepository.com/artifact/io.projectreactor.netty/reactor-netty
    runtimeOnly("io.projectreactor.netty:reactor-netty:1.0.13")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    // https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox
    implementation("org.apache.pdfbox:pdfbox:2.0.24") {
        exclude(group = "commons-logging", module = "commons-logging")
    }
    // https://mvnrepository.com/artifact/com.jfoenix/jfoenix
    implementation("com.jfoenix:jfoenix:9.0.10")

    // https://mvnrepository.com/artifact/jakarta.inject/jakarta.inject-api
    implementation("jakarta.inject:jakarta.inject-api:2.0.1.MR")

    implementation("org.reflections:reflections:0.10.2")

    compileOnly("org.slf4j:slf4j-api:2.0.0-alpha5")
    runtimeOnly("ch.qos.logback:logback-classic:1.3.0-alpha10")

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.testfx:testfx-junit5:4.0.16-alpha")
    // https://mvnrepository.com/artifact/io.projectreactor/reactor-test
    testImplementation("io.projectreactor:reactor-test:3.4.12")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    testImplementation("com.squareup.okhttp3:okhttp:4.9.2")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/mockwebserver
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.2")

    //Cross-platform
    runtimeOnly("org.openjfx:javafx-graphics:${javafx.version}:win")
    runtimeOnly("org.openjfx:javafx-graphics:${javafx.version}:linux")
    runtimeOnly("org.openjfx:javafx-graphics:${javafx.version}:mac")
}
