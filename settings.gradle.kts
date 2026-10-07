pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9.8"
}

stonecutter {
	create(rootProject) {
		// One jar per Minecraft version; per-version settings live in stonecutter.properties.toml
		versions("26.1", "26.2", "26.3")
		vcsVersion = "26.2"
	}
}

rootProject.name = "chordbind"
