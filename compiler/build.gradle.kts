plugins {
  `java-library`
  kotlin("jvm")
}

version = "1.0.0"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("com.palantir.javapoet:javapoet:0.12.0")
	implementation("org.yaml:snakeyaml:2.2")
  implementation(project(":resources-runtime"))
  implementation(kotlin("stdlib-jdk8"))
  compileOnly("org.projectlombok:lombok:1.18.44")
  annotationProcessor("org.projectlombok:lombok:1.18.44")
  testCompileOnly("org.projectlombok:lombok:1.18.44")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.44")
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}
