plugins {
	id("net.fabricmc.fabric-loom") version "1.18-SNAPSHOT"
}

val modId = sc.properties.get<String>("mod.id")
val modVersion = sc.properties.get<String>("mod.version")

version = "$modVersion+${sc.current.version}"
base.archivesName = modId

repositories {
	maven("https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
}

dependencies {
	minecraft("com.mojang:minecraft:${sc.current.version}")
	implementation("net.fabricmc:fabric-loader:${sc.properties.get<String>("deps.fabric_loader")}")
	implementation("net.fabricmc.fabric-api:fabric-api:${sc.properties.get<String>("deps.fabric_api")}")

	// Optional: adds a config button in the mods list. Not bundled.
	compileOnly("com.terraformersmc:modmenu:${sc.properties.get<String>("deps.modmenu")}")
}

loom {
	runConfigs.all {
		runDirectory = rootProject.file("run") // shared between versions
	}
}

java {
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks {
	withType<JavaCompile>().configureEach {
		options.release = 25
	}

	processResources {
		val props = mapOf(
			"version" to modVersion,
			"minecraft" to sc.properties.get<String>("mod.mc_compat"),
		)
		inputs.properties(props)
		filesMatching("fabric.mod.json") { expand(props) }
	}

	jar {
		val suffix = "_$modId"
		from(rootProject.file("LICENSE")) { rename { it + suffix } }
	}

	// Copies every version's jar to build/libs/<mod version>/
	register<Copy>("collectJars") {
		group = "build"
		from(jar.flatMap { it.archiveFile })
		into(rootProject.layout.buildDirectory.dir("libs/$modVersion"))
	}
}
