plugins {
    id("java")
}

group = "org.glavo"
version = "0.2.0"// + "-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.compileJava {
    options.release.set(17)
}

tasks.jar {
    manifest.attributes(
        "Main-Class" to "org.glavo.rfg.Main"
    )
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.create<JavaExec>("run") {
    dependsOn(tasks.jar)
    classpath = files(tasks.jar.get().archiveFile.get().asFile)
}