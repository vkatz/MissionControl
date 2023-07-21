plugins {
    id("com.plugin.kmpmodule")
    alias(libs.plugins.serialization)
    alias(libs.plugins.compose)
}

kmmModule {
    androidName = "com.vkatz.missioncontrol.common"
    commonMainDependencies { kmm ->
        implementation(libs.ktor.network)
        implementation(libs.kotlinx.serialization)

        implementation(kmm.compose.foundation)
        implementation(kmm.compose.materialIconsExtended)
    }
}