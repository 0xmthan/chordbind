<h1 align="center">
  <img src="src/main/resources/assets/chordbind/icon.png" alt="ChordBind logo" width="64" height="64" align="center" />
  &nbsp;CHORDBIND
</h1>

<p align="center">
  <strong>Key combos for your chat commands.</strong>
</p>

<p align="center">
  Client-side Fabric mod that runs commands or messages with custom key combo binds ("chords").<br />
  Supports Minecraft 25.2, with optional Mod Menu integration.
</p>

<p align="center">
  <a href="https://github.com/0xmthan/chordbind/actions/workflows/release.yml">
    <img src="https://github.com/0xmthan/chordbind/actions/workflows/release.yml/badge.svg" alt="Release status" />
  </a>
  <a href="https://github.com/0xmthan/chordbind/releases/latest">
    <img src="https://img.shields.io/github/v/release/0xmthan/chordbind?label=version&amp;color=5865f2" alt="Latest version" />
  </a>
</p>

<p align="center">
  <a href="https://github.com/0xmthan/chordbind/issues">Report a bug</a>
  &nbsp;·&nbsp;
  <a href="https://github.com/0xmthan/chordbind/issues/new">Request a feature</a>
  &nbsp;·&nbsp;
  <a href="https://modrinth.com/mod/chordbind">Modrinth</a>
</p>

## Build / run

Requires Java 25 (point `JAVA_HOME` at a JDK 25).

```sh
./gradlew build       # jar in build/libs/
./gradlew runClient   # dev client
```

Bindings are stored in `config/chordbind.json`.
