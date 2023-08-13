plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

project.group = "com.convallyria"
project.version = "1.0.0-SNAPSHOT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "convallyria"
        url = uri("https://repo.convallyria.com/snapshots")
    }

    maven("https://repo.lucko.me/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.11")
    implementation("me.lucko:helper:5.6.9")

    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")

        relocate("com.github.stefvanschie.inventoryframework", "com.convallyria.wanderingmarket.libs.inventoryframework")
        relocate("me.lucko.helper", "com.convallyria.wanderingmarket.libs.helper")
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }
}