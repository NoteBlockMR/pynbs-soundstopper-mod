# WirelessNBS Sound Control — Minecraft Java 1.21.1

컴파일된 설치용 JAR은 [Releases](https://github.com/NoteBlockMR/pynbs-soundstopper-mod/releases)에서 받을 수 있습니다. `-sources.jar`나 GitHub의 Source code ZIP은 설치용이 아닙니다.

같은 사운드를 사용하는 음표도 개별 인스턴스로 추적하여 지정한 곡·레이어·노트만 중단합니다. 기존 OGG 및 sounds.json은 그대로 사용합니다.

## 설치

1. Minecraft Java **1.21.1**, Java 21, **Fabric Loader 0.16.14 이상**을 사용하세요. Forge/NeoForge용이 아닙니다.
2. `wirelessnbs-fabric-1.0.1.jar`와 **Fabric API 0.116.15+1.21.1**을 게임 프로필의 `mods` 폴더에 넣으세요.
   - Fabric 설치: https://fabricmc.net/use/installer/
   - Fabric API: https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/0.116.15+1.21.1/fabric-api-0.116.15+1.21.1.jar
3. 기존 사운드팩을 활성화하세요. OGG 및 sounds.json 교체나 추가 사운드팩은 필요하지 않습니다.
4. 새 `song.zip`으로 월드 `datapacks` 폴더의 기존 song 데이터팩을 교체하세요. 게임을 다시 실행하고 `/reload`를 실행하세요.
5. `/wirelessnbs status @s`가 `WirelessNBS ready`인지 확인한 뒤 `/function song:play`로 재생하세요. 중단은 `/function song:stop`입니다.

멀티플레이에서는 **서버와 음악을 듣는 모든 클라이언트**에 이 모드와 Fabric API가 필요합니다. 모드가 없는 클라이언트에는 음악을 전송하지 않습니다. 기존 블럭 효과에 필요한 `noteblock-extension` 모드는 계속 필요합니다. 볼륨은 마스터와 주크박스/음표 블록 설정을 따릅니다.

## 명령어

권한 레벨 2 이상이 필요합니다. 명령 블록과 데이터팩 함수에서도 사용할 수 있습니다.

```mcfunction
wirelessnbs play <플레이어> <곡ID> <레이어> <노트ID> <사운드ID> <볼륨> <피치>
wirelessnbs stop <플레이어> <곡ID> <시작레이어> <끝레이어>
wirelessnbs stopnote <플레이어> <곡ID> <노트ID>
wirelessnbs stopall <플레이어> <곡ID>
wirelessnbs status <플레이어>
```

레이어는 1부터 시작하며 양 끝을 포함합니다. 시작이 0이면 해당 곡 전체를 중단합니다. 끝이 시작보다 작으면 시작 레이어만 중단합니다. 노트 ID는 음수가 아닌 정수이며 같은 곡에서 같은 ID를 다시 재생하면 이전 음표를 교체합니다. 볼륨은 0~1, 피치는 0.5~2입니다.

바닐라의 긴 소리로 개별 중단을 확인하는 예시입니다. 두 소리를 재생한 뒤 세 번째 명령으로 첫 소리만 중단하세요.

```mcfunction
/wirelessnbs play @s demo 1 101 minecraft:music_disc.13 1 1
/wirelessnbs play @s demo 2 102 minecraft:music_disc.13 1 0.75
/wirelessnbs stop @s demo 1 1
/wirelessnbs stopnote @s demo 102
```

## 변환기 및 빌드

이 저장소에는 Fabric 모드 소스가 들어 있습니다. 곡 데이터팩은 별도의 모드 대응 `WirelessNBS-ApproachCircle.py` 변환기로 생성해야 합니다. 노트 ID는 `NBS 틱 × 65536 + 0부터 시작하는 레이어`입니다. 해당 변환기는 sounds.json을 이벤트 이름 검사에만 사용하고 수정하지 않으며, `_sounds.zip`도 생성하지 않습니다.

play는 처음부터 다시 시작합니다. pause는 진행을 멈추면서 남아 있는 소리도 끕니다. 소리는 청취자 기준이며 거리 감쇠가 없습니다. 기존 변환기의 지원 음역 등 다른 제한은 그대로입니다.

Java 21을 설정한 뒤 `gradlew.bat build`를 실행하세요. 설치 파일은 `build/libs/wirelessnbs-fabric-1.0.1.jar`입니다. `-sources.jar`는 소스 파일입니다.

JUnit은 다른 레이어/노트/곡 분리, 범위 중단, 같은 틱의 순서, 종료된 소리 정리 및 등록 한도를 검사합니다. `gradlew.bat test`로 실행할 수 있습니다. 실제 게임에서 청취 검증은 별도로 필요합니다.

## 1.0.1 의존성 수정

Fabric API 0.116.15 기준으로 빌드하고 최소 버전을 낮췄습니다. Forgified Fabric API 0.116.15+2.3.5+1.21.1을 사용하는 NeoForge/Connector 환경에서는 기존 Forgified Fabric API를 유지하세요. 일반 Fabric API를 추가하지 마세요. Connector 환경의 게임 내 실행은 검증하지 않았습니다. 기존 1.0.0 JAR를 제거하고 1.0.1만 설치하세요. 데이터팩과 사운드팩은 변경할 필요가 없습니다.
