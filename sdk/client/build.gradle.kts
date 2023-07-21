plugins {
    id("com.plugin.kmpmodule")
    alias(libs.plugins.compose)
}

kmmModule {
    androidName = "com.vkatz.missioncontrol.client.debug"
    commonMainDependencies { kmm ->
        implementation(project(":sdk:common"))
        implementation(libs.ktor.core)
        implementation(libs.ktor.network)
        implementation(libs.ktor.client.okhttp)
        implementation(libs.kotlinx.coroutines.core)
        implementation(kmm.compose.foundation)
    }
}