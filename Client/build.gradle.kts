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
    implementation(project(":Backend"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.testfx:testfx-core:4.0.16-alpha")
    testImplementation("org.testfx:testfx-junit5:4.0.16-alpha")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

tasks.compileJava {
    sourceCompatibility = "17"
    targetCompatibility = "17"
    options.encoding = "UTF-8"
}

application{
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}
