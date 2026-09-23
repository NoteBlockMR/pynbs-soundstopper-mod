# SoundControl - Minecraft 1.21.1

[English](README.md) | 한국어

그룹·레이어·노트 ID별로 소리를 재생하고 중단하는 Fabric 모드입니다. 같은 사운드를 사용하는 소리도 각각 중단할 수 있습니다.

## 설치

1. Minecraft Java **1.21.1**, **Java 21**, **Fabric Loader 0.16.14 이상**을 사용하세요.
2. [Releases](https://github.com/NoteBlockMR/pynbs-soundstopper-mod/releases)에서 모드 JAR을 받아 `mods` 폴더에 넣으세요. `-sources.jar`는 설치용이 아닙니다.
3. **Fabric API 0.116.15+1.21.1**을 설치하세요.
4. 멀티플레이에서는 서버와 소리를 듣는 모든 클라이언트에 모드와 Fabric API를 설치하세요.

기본 마인크래프트 사운드는 별도 리소스팩 없이 사용할 수 있습니다. 사용자 지정 사운드 ID는 해당 소리를 제공하는 리소스팩이 필요합니다.

## 명령어

권한 레벨 **2** 이상이 필요합니다. 명령 블록과 함수에서도 사용할 수 있습니다. 현재 배포 버전의 명령어 접두사는 `/wirelessnbs`입니다.

```text
/wirelessnbs play <플레이어> <그룹> <레이어> <노트> <사운드> <볼륨> <피치>
/wirelessnbs stop <플레이어> <그룹> <시작레이어> <끝레이어>
/wirelessnbs stopnote <플레이어> <그룹> <노트>
/wirelessnbs stopall <플레이어> <그룹>
/wirelessnbs status <플레이어>
```

| 명령어 | 설명 |
| --- | --- |
| `play` | 그룹·레이어·노트 ID를 지정해 소리를 재생합니다. |
| `stop` | 그룹 안에서 지정한 레이어 범위의 소리를 중단합니다. 양 끝을 포함합니다. |
| `stopnote` | 그룹 안의 특정 노트 하나를 중단합니다. |
| `stopall` | 그룹의 모든 소리를 중단합니다. |
| `status` | 선택한 플레이어의 클라이언트 모드 설치 상태를 확인합니다. |

- **플레이어:** 플레이어 이름 또는 `@s`, `@a` 등의 선택자입니다.
- **그룹:** 소리를 묶는 `demo:music` 같은 식별자입니다.
- **레이어:** **1~65535**입니다. `stop`의 시작이 **0**이면 그룹 전체를 중단합니다. 끝이 시작보다 작으면 시작 레이어만 중단합니다.
- **노트:** **0 이상**의 정수 ID입니다. 같은 그룹에서 ID를 다시 사용하면 이전 소리를 교체합니다.
- **사운드:** `minecraft:music_disc.13` 같은 사운드 이벤트 ID입니다.
- **볼륨:** **0~1**, **피치:** **0.5~2**입니다.

소리는 청취자 기준으로 재생되며 거리 감쇠가 없습니다. 주크박스/음표 블록 볼륨 설정을 따릅니다. 중단 명령은 이 모드로 재생한 소리에 적용됩니다.

## 예시

같은 사운드를 두 레이어에서 재생한 뒤 첫 번째 레이어만 중단합니다.

```mcfunction
/wirelessnbs play @s demo:music 1 101 minecraft:music_disc.13 1 1
/wirelessnbs play @s demo:music 2 102 minecraft:music_disc.13 1 0.75
/wirelessnbs stop @s demo:music 1 1
```

남은 노트 하나를 중단하거나 그룹 전체를 중단합니다.

```mcfunction
/wirelessnbs stopnote @s demo:music 102
/wirelessnbs stopall @s demo:music
```
