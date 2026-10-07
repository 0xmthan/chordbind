<h1 align="center">
  <img src="src/main/resources/assets/chordbind/icon.png" alt="ChordBind logo" width="64" height="64" align="center" />
  &nbsp;CHORDBIND
</h1>

<p align="center">
  <strong>Key bindings for your chat commands.</strong>
</p>

<p align="center">
  Bind any key combination to a command or chat message, like <code>Ctrl + Shift + H</code> → <code>/home</code>.<br />
  Client-side only: works on any server, with its normal permissions.<br />
  Supports Minecraft 26.1, 26.2 and 26.3.
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

## How to use

1. Open the bindings screen from **Mod Menu** (Mods → ChordBind → config), or bind
   **Open ChordBind bindings** under Options → Controls → Key Binds.
2. Click **Add Binding**, then click the key button and press the keys. It's recorded when you
   let go. Press Escape or click to cancel.
3. Type the command (like `/home`) or a chat message and click **Done**.

Bindings are saved to `config/chordbind.json` and carry over between Minecraft versions.

## Requirements

- Minecraft 26.1.x, 26.2.x or 26.3.x (download the jar matching your version)
- [Fabric Loader](https://fabricmc.net/use/) 0.19.5+ and [Fabric API](https://modrinth.com/mod/fabric-api)
- [Mod Menu](https://modrinth.com/mod/modmenu) (optional, adds the config button)

## Building from source

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

## License

[MIT](LICENSE)
