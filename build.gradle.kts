plugins {
	id("net.fabricmc.fabric-loom") version "1.18-SNAPSHOT"
	id("me.modmuss50.mod-publish-plugin") version "2.2.1"
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

publishMods {
	val modrinthId = sc.properties.get<String>("publish.modrinth_id")
	// CI passes an empty string when the secret is missing, so treat blank as unset
	val modrinthToken = providers.environmentVariable("MODRINTH_TOKEN").orNull.orEmpty()

	file = tasks.jar.flatMap { it.archiveFile }
	version = project.version.toString()
	displayName = "ChordBind $modVersion for ${sc.current.version}"
	changelog = providers.environmentVariable("CHANGELOG").orElse("")
	type = STABLE
	modLoaders.add("fabric")
	// Only uploads for real when both the project ID and the token are set (i.e. in CI)
	dryRun = modrinthId.isBlank() || modrinthToken.isBlank()

	modrinth {
		accessToken = modrinthToken
		projectId = modrinthId
		minecraftVersions.addAll(
			sc.properties.rawOrNull("mod", "mc_releases")?.asList().orEmpty().map { it.toString() }
		)
		requires("fabric-api")
		optional("modmenu")
	}
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
