plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.20"
    war
}

group = "legu.dev"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

    implementation("org.thymeleaf:thymeleaf:3.1.2.RELEASE")

    compileOnly("jakarta.enterprise:jakarta.enterprise.cdi-api:4.0.1")
    implementation("org.jboss:jandex:3.1.5") // Speed up CDI discovery
    implementation("org.jboss.weld.servlet:weld-servlet-core:5.1.1.SP2")

    implementation("commons-io:commons-io:2.15.0")

    implementation("com.mysql:mysql-connector-j:8.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation(kotlin("stdlib-jdk8"))
}

sourceSets {
    main {
        kotlin {
            setSrcDirs(listOf("src"))
        }
        resources {
            setSrcDirs(listOf("resources"))
        }
    }
}

kotlin {
    jvmToolchain(8)
}