plugins {
  application
}

version = "1.0.0"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  implementation(project(":resources-runtime"))
	implementation(project(":resources-compiler"))
	annotationProcessor(project(":resources-compiler"))
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

application {
  mainClass = "com.srilakshmikanthanp.resources.Main"
}

tasks.withType<JavaCompile>().configureEach {
  options.sourcepath = sourceSets.main.get().resources.sourceDirectories
}
