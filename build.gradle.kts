plugins {
    kotlin("jvm") version "1.9.0"
    war
}

group = "legu.dev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

    implementation("org.thymeleaf:thymeleaf:3.1.2.RELEASE")

    implementation("org.jboss.weld.servlet:weld-servlet-core:5.1.1.SP2")

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