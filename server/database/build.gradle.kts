plugins {
    java
    `java-library`
    `maven-publish`
}

//name = "database-commons"

dependencies {
    api("org.jooq", "jooq", "3.16.23")
    implementation("org.jooq", "jooq-meta", "3.16.23")
    implementation("org.jooq", "jooq-codegen", "3.16.23")

    implementation("com.zaxxer", "HikariCP", "3.4.5")

    implementation("org.slf4j", "slf4j-simple", "2.0.10")

    compile("org.flywaydb", "flyway-core", "6.0.8")

    implementation("org.postgresql", "postgresql", "42.2.27")

    implementation("org.testcontainers", "testcontainers", "1.19.1")
    implementation("org.testcontainers", "postgresql", "1.19.1")
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
