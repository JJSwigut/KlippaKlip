import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.jjswigut"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation(libs.sqlDelight.driver)
    implementation(libs.sqlDelight.coroutines)
    implementation(libs.jNativeHook)
    implementation(libs.kotlinJson)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            modules("java.sql", "java.naming")
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Deb,
                TargetFormat.Pkg,
                TargetFormat.Exe
            )
            packageName = "KlippaKlip"
            packageVersion = "1.0.0"

            macOS {
                bundleID = "com.jjswigut.klippaklip"
                iconFile.set(project.file("src/main/resources/klippaklip.icns"))

                signing {
                    sign.set(true)
                    identity.set("Joshua Swigut")
                }
            }
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.jjswigut.klippaklip")
        }
    }
}