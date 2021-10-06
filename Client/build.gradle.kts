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
    implementation(project(":Backend"))

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.compileJava {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}
