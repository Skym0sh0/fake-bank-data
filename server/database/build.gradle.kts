plugins {
    java
    `java-library`
    `maven-publish`
}

//name = "database-commons"
val jooq_version: String by rootProject.extra
val slf4j_version: String by rootProject.extra

dependencies {
    api("org.jooq", "jooq", jooq_version)
    implementation("org.jooq", "jooq-meta", jooq_version)
    implementation("org.jooq", "jooq-codegen", jooq_version)

    implementation("com.zaxxer", "HikariCP", "5.1.0")

    implementation("org.slf4j", "slf4j-api", slf4j_version)

    api("org.flywaydb", "flyway-core", "10.4.1")
    runtimeOnly("org.flywaydb", "flyway-database-postgresql", "10.4.1")

    implementation("org.postgresql", "postgresql", "42.7.1")

    testImplementation("org.testcontainers", "testcontainers", "1.19.3")
    testImplementation("org.testcontainers", "postgresql", "1.19.3")
    testImplementation("org.slf4j", "slf4j-simple", slf4j_version)
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
