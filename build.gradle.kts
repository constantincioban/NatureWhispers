import org.jetbrains.kotlin.fir.declarations.builder.buildScript

// Top-level build file where you can add configuration options common to all sub-projects/modules.



plugins {

    id("com.android.application") version "7.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}