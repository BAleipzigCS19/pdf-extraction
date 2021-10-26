plugins {
    application
}

version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":API"))

    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot
    implementation("org.springframework.boot:spring-boot:2.5.5")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-autoconfigure
    implementation("org.springframework.boot:spring-boot-autoconfigure:2.5.5")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-logging
    implementation("org.springframework.boot:spring-boot-starter-logging:2.5.5")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web
    implementation("org.springframework.boot:spring-boot-starter-web:2.5.5")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.5.5")

    // https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox
    implementation("org.apache.pdfbox:pdfbox:2.0.24")

    // Im moment brauchen wir durch Spring die 2.x Versionen, NICHT die 3.x
    // https://mvnrepository.com/artifact/org.glassfish.jaxb/jaxb-runtime
    runtimeOnly("org.glassfish.jaxb:jaxb-runtime:2.3.5")

    runtimeOnly("com.h2database:h2:1.4.200")

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

application {
    mainClass.set("de.baleipzig.pdfextraction.backend.Starter")
}

tasks.test {
    useJUnitPlatform()
}
