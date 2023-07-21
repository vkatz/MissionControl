pluginManagement {
    includeBuild("gradlePlugins")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}


rootProject.name = "MissionControl"

include(":appAndroid")
include(":appDesktopClient")
include(":appDesktopServer")
include(":sdk:common")
include(":sdk:client")
include(":sdk:clientStub")
include(":sdk:server")
