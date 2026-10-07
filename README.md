# ChordBind

Client-side Fabric mod that runs commands with custom key combo binds ("chords").

- Minecraft 26.2, Fabric Loader 0.19.5, Fabric API 0.161.0+26.2, optional Mod Menu
- Java 25 (Gradle 9.7 cannot run on newer JDKs; point `JAVA_HOME` at a JDK 25)

## Build / run

```sh
./gradlew build       # jar in build/libs/
./gradlew runClient   # dev client
```

Bindings are stored in `config/chordbind.json`.
