plugins {
    id("java")
}

group = "ru.teamscore"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    //hibernate
    implementation("org.hibernate:hibernate-core:7.2.0.Final")

    //postgresql
    implementation("org.postgresql:postgresql:42.7.8")

    //jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")

    //lombok
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}