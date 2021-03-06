plugins {
    java
    `java-library`
    `maven-publish`
}

//name = "database-commons"

dependencies {
    api("org.jooq", "jooq", "3.11.12")
    implementation("org.jooq", "jooq-meta", "3.11.12")
    implementation("org.jooq", "jooq-codegen", "3.11.12")

    implementation("com.zaxxer", "HikariCP", "3.4.0")

    implementation("org.slf4j", "slf4j-simple", "1.7.28")

    compile("org.flywaydb", "flyway-core", "6.0.3")

    implementation("org.postgresql", "postgresql", "42.2.8")
    implementation("com.h2database", "h2", "1.4.199")

    implementation("org.testcontainers", "testcontainers", "1.12.1")
    implementation("org.testcontainers", "postgresql", "1.12.1")
}

tasks {
    compileTestJava {
        dependsOn += publishToMavenLocal
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.sky"
            artifactId = "database-common"
            version = "1.0.0"

            from(components["java"])
        }
    }
}
