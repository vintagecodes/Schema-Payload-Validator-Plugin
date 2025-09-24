plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Compile plugin for Java 17 (compatible with IntelliJ)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile> {
    options.release.set(17)
}

intellij {
    version.set("2024.2")
    type.set("IC")
    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks.patchPluginXml {
    sinceBuild.set("233")
    untilBuild.set("243.*")
}

tasks.signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
}

tasks.publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
}