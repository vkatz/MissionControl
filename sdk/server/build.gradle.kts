plugins {
    id("com.plugin.kmpmodule")
    alias(libs.plugins.compose)
}

kmmModule {
    androidName = "com.vkatz.missioncontrol.server"
    commonMainDependencies { kmm ->
        implementation(project(":sdk:common"))

        implementation(kmm.compose.foundation)
        implementation(kmm.compose.animation)
        implementation(kmm.compose.materialIconsExtended)
        implementation(kmm.compose.material3)

        implementation(libs.ktor.core)
        implementation(libs.ktor.network)
        implementation(libs.ktor.client.okhttp)
        implementation(libs.kotlinx.coroutines.core)
    }
}