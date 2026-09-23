# SoundControl - Minecraft 1.21.1

English | [한국어](README_KO.md)

A Fabric mod for playing and stopping sounds by group, layer, or note ID. Sounds using the same sound event can be stopped independently.

## Installation

1. Use Minecraft Java **1.21.1**, **Java 21**, and **Fabric Loader 0.16.14 or later**.
2. Download the mod JAR from [Releases](https://github.com/NoteBlockMR/pynbs-soundstopper-mod/releases) and put it in your `mods` folder. The `-sources.jar` file is not for installation.
3. Install **Fabric API 0.116.15+1.21.1**.
4. In multiplayer, install the mod and Fabric API on the server and every listening client.

Vanilla sounds need no custom resource pack. Custom sound IDs require a resource pack that provides them.

## Commands

Commands require permission level **2** and can also run in command blocks and functions. The current release uses `/wirelessnbs` as its command prefix.

```text
/wirelessnbs play <players> <group> <layer> <note> <sound> <volume> <pitch>
/wirelessnbs stop <players> <group> <first_layer> <last_layer>
/wirelessnbs stopnote <players> <group> <note>
/wirelessnbs stopall <players> <group>
/wirelessnbs status <players>
```

| Command | Description |
| --- | --- |
| `play` | Play a sound with a group, layer, and note ID. |
| `stop` | Stop an inclusive range of layers in a group. |
| `stopnote` | Stop one note in a group. |
| `stopall` | Stop all sounds in a group. |
| `status` | Check client mod availability for selected players. |

- **players:** A player name or selector such as `@s` or `@a`.
- **group:** An identifier such as `demo:music` for grouping sounds.
- **layer:** **1–65535**. For `stop`, a first layer of **0** stops the entire group. An end below the start stops only the start layer.
- **note:** A nonnegative integer ID. Reusing an ID in the same group replaces its previous sound.
- **sound:** A sound event ID such as `minecraft:music_disc.13`.
- **volume:** **0–1**. **pitch:** **0.5–2**.

Sounds play relative to the listener without distance attenuation and use the Jukebox/Note Blocks volume category. Stop commands affect sounds started by this mod.

## Example

Play the same sound on two layers, then stop only the first layer:

```mcfunction
/wirelessnbs play @s demo:music 1 101 minecraft:music_disc.13 1 1
/wirelessnbs play @s demo:music 2 102 minecraft:music_disc.13 1 0.75
/wirelessnbs stop @s demo:music 1 1
```

Stop the remaining note or clear the group:

```mcfunction
/wirelessnbs stopnote @s demo:music 102
/wirelessnbs stopall @s demo:music
```
