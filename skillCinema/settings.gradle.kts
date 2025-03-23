pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
     //   maven { url = uri("https://plugins.gradle.org/m2/")}
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ("https://jitpack.io")
    }
}

rootProject.name = "SkillCinema"
include(":app")

//plugins {
//   // id ("com.android.application") version "8.8.1" apply false
//    id ("org.jetbrains.kotlin.android") version "2.1.10" apply false // here I updated the version to 1.9.24 latest version as of now
//}