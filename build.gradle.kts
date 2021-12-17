plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

project.group = "com.convallyria"
project.version = "1.0.0-SNAPSHOT"

java.targetCompatibility = JavaVersion.VERSION_16
java.sourceCompatibility = JavaVersion.VERSION_16

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

    maven { url = uri("https://repo.lucko.me/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.dmulloy2.net/repository/public/") }
}

dependencies {
    implementation("net.kyori:adventure-api:4.9.3")
    implementation("net.kyori:adventure-platform-bukkit:4.0.0")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.3")
    implementation("me.lucko:helper:5.6.8")

    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        relocate("com.github.stefvanschie.inventoryframework", "com.convallyria.wanderingmarket.libs.inventoryframework")
        relocate("me.lucko.helper", "com.convallyria.wanderingmarket.libs.helper")
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }
}