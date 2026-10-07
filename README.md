<h1 align="center">
  <img src="src/main/resources/assets/chordbind/icon.png" alt="ChordBind logo" width="64" height="64" align="center" />
  &nbsp;CHORDBIND
</h1>

<p align="center">
  <strong>Key combos for your chat commands.</strong>
</p>

<p align="center">
  Client-side Fabric mod that runs commands or messages with custom key combo binds ("chords").<br />
  Supports Minecraft 26.1, 26.2 and 26.3, with optional Mod Menu integration.
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

Built with [Stonecutter](https://stonecutter.kikugie.dev/): one source tree, one jar per Minecraft version.
Version-specific code is marked with `//? if` comments, and versions and dependencies live in
`stonecutter.properties.toml`.

```sh
./gradlew build collectJars             # all versions -> build/libs/<mod version>/
./gradlew :26.2:runClient               # dev client for one version
./gradlew "Set active project to 26.3"  # switch which version src/ is written against
./gradlew "Reset active project"        # switch back to 26.2 before committing
```

Bindings are stored in `config/chordbind.json`.
