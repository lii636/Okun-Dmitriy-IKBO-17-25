plugins {
    java
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    maven {
        url = uri("${System.getProperty("user.home")}/.m2/repository")
        metadataSources {
            mavenPom()
        }
    }
    mavenCentral()
}

val lombok = "org.projectlombok:lombok:1.18.44"

dependencies {
    compileOnly(lombok)
    annotationProcessor(lombok)
    testCompileOnly(lombok)
    testAnnotationProcessor(lombok)

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.2")
}

application {
    mainClass.set("depgraph.Main")
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
    test {
        java.setSrcDirs(listOf("tests"))
        resources.setSrcDirs(listOf("test-resources"))
    }
}
