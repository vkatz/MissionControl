plugins {
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.jvm).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}