version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()

    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")

    // https://github.com/Dansoftowner/PDFViewerFX
    implementation("com.github.Dansoftowner:PDFViewerFX:0.8")
}

tasks.test {
    useJUnitPlatform()
}
