plugins {
    id("com.plugin.kmpmodule")
}

kmmModule {
    androidName = "com.vkatz.missioncontrol.client.release"
    commonMainDependencies {
        implementation(project(":sdk:common"))
    }
}