plugins {
    jacoco
    `java-library`
    `maven-publish`
    id ("org.sonarqube") version "3.3"
    id("io.freefair.lombok") version "6.6"
    id ("fr.brouillard.oss.gradle.jgitver") version "0.9.1"
}

group = "com.frog-development"

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {

    api("io.jsonwebtoken:jjwt:0.9.1")

    implementation("org.springframework.boot:spring-boot-starter-security:2.6.2")
    implementation("org.springframework.boot:spring-boot-actuator-autoconfigure:2.6.2")
    implementation("ch.qos.logback:logback-classic:1.2.10")
    implementation("javax.servlet:javax.servlet-api:4.0.1")
    implementation("javax.xml.bind:jaxb-api:2.3.1")

    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("org.springframework.security:spring-security-test:5.6.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.2")
    testImplementation("org.springframework.security:spring-security-test:5.5.3")
    testImplementation("org.junit.platform:junit-platform-runner:1.8.2")
}

tasks.named<Test>("test") {
    reports.html.required.set(false)

    useJUnitPlatform {
        includeTags("unitTest")
        excludeTags("integrationTest")
    }

    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("test"))
    reports {
        xml.required.set(true)
//        xml.destination file("${buildDir}/reports/jacoco/test.xml")
        csv.required.set(false)
        html.required.set(false)
    }
}

//tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
//    violationRules {
//        rule {
//            limit {
//                minimum = "0.9".toBigDecimal()
//            }
//        }
//    }
//}

sonarqube {
    properties {
//        property ("sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/reports/jacoco/test.xml")
        property ("sonar.projectKey", "FrogDevelopment_authorization-jwt-module")
        property ("sonar.organization", "frogdevelopment")
        property ("sonar.host.url", "https://sonarcloud.io")
    }
}

project.tasks["sonarqube"].dependsOn("jacocoTestReport")

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("Authorisation JWT")
                description.set("Handle Spring Security with JWT")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("legall.benoit")
                        name.set("Benoît Le Gall")
                        email.set("legall.benoit@gmail.com")
                    }
                }
            }
        }
    }
}

tasks.wrapper {
    gradleVersion = "7.2"
    distributionType = Wrapper.DistributionType.ALL
}

jgitver {
    strategy("PATTERN")
    versionPattern("\${v}-SNAPSHOT")
    tagVersionPattern("\${v}")
}
