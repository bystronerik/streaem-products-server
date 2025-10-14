import io.micronaut.gradle.docker.MicronautDockerfile

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.allopen") version "2.2.0"

    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("com.gradleup.shadow") version "8.3.7"

    id("io.micronaut.application") version "4.5.4"
    id("io.micronaut.aot") version "4.5.4"
}

version = "1.0"
group = "com.streaem"

val kotlinVersion=project.properties.get("kotlinVersion")

repositories {
    mavenCentral()
}

dependencies {
    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    ksp("io.micronaut.openapi:micronaut-openapi")

    implementation("io.github.oshai:kotlin-logging-jvm:5.1.4")

    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.openapi:micronaut-openapi")
    implementation("io.micronaut:micronaut-http-client")

    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")

    implementation("com.jsoniter:jsoniter:0.9.23")
    implementation("net.openhft:chronicle-map:3.27ea1")
    implementation("com.esotericsoftware:kryo:5.6.2")

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("io.micronaut:micronaut-http-client")

    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")

    testImplementation("io.mockk:mockk:1.13.8")
}

application {
    mainClass = "com.streaem.products.ApplicationKt"
}

java {
    sourceCompatibility = JavaVersion.toVersion("21")
}


graalvmNative.toolchainDetection = false

micronaut {
    runtime("netty")
    testRuntime("kotest5")
    processing {
        incremental(true)
        annotations("com.streaem.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}

tasks.test {
    jvmArgs = listOf(
        "--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED",
        "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
        "--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens=java.base/java.io=ALL-UNNAMED",
        "--add-opens=java.base/java.util=ALL-UNNAMED"
    )
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
}

tasks.named<MicronautDockerfile>("dockerfile") {
    baseImage.set("eclipse-temurin:21-jdk-jammy")
    instruction("ENV MICRONAUT_ENVIRONMENTS=docker")
    instruction("""ENTRYPOINT [ \
        "java", \
        "--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED", \
        "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED", \
        "--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED", \
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED", \
        "--add-opens=jdk.compiler/com.sun.tools.javac=ALL-UNNAMED", \
        "--add-opens=java.base/java.lang=ALL-UNNAMED", \
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED", \
        "--add-opens=java.base/java.io=ALL-UNNAMED", \
        "--add-opens=java.base/java.util=ALL-UNNAMED", \
        "-jar", \
        "/home/app/application.jar" \
    ]""")
}



