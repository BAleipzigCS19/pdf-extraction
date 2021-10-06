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

    runtimeOnly("com.h2database:h2:1.4.200")

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}
