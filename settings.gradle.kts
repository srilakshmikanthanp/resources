pluginManagement {
  plugins {
    kotlin("jvm") version "2.3.10"
  }
}
plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


rootProject.name = "resources"


include("compiler")
include("runtime")
include("sample")


project(":compiler").name = "resources-compiler"
project(":runtime").name = "resources-runtime"
project(":sample").name = "resources-sample"
