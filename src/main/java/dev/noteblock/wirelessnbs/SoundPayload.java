package dev.noteblock.wirelessnbs;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SoundPayload(int action, String song, int layer, int endLayer,
                           long note, String sound, float volume, float pitch) implements CustomPayload {
    public static final int PLAY = 0, STOP_RANGE = 1, STOP_NOTE = 2;
    public static final Id<SoundPayload> ID = new Id<>(Identifier.of("wirelessnbs", "sound_v1"));
    public static final PacketCodec<RegistryByteBuf, SoundPayload> CODEC =
        PacketCodec.ofStatic(SoundPayload::write, SoundPayload::read);

    public SoundPayload {
        if (action < PLAY || action > STOP_NOTE || song.isEmpty() || song.length() > 128 || sound.length() > 256
            || layer < 0 || layer > 65535 || endLayer < 0 || endLayer > 65535 || note < 0
            || !Float.isFinite(volume) || volume < 0 || volume > 1
            || !Float.isFinite(pitch) || pitch < 0.5f || pitch > 2.0f
            || action == PLAY && (layer == 0 || Identifier.tryParse(sound) == null)) {
            throw new IllegalArgumentException("Invalid WirelessNBS sound payload");
        }
    }

    public static SoundPayload play(String song, int layer, long note, String sound, float volume, float pitch) {
        return new SoundPayload(PLAY, song, layer, layer, note, sound, volume, pitch);
    }
    public static SoundPayload stopRange(String song, int start, int end) {
        return new SoundPayload(STOP_RANGE, song, start, end, 0, "", 0, 1);
    }
    public static SoundPayload stopNote(String song, long note) {
        return new SoundPayload(STOP_NOTE, song, 0, 0, note, "", 0, 1);
    }

    private static void write(RegistryByteBuf buf, SoundPayload value) {
        buf.writeVarInt(value.action);
        buf.writeString(value.song, 128);
        buf.writeVarInt(value.layer);
        buf.writeVarInt(value.endLayer);
        buf.writeVarLong(value.note);
        buf.writeString(value.sound, 256);
        buf.writeFloat(value.volume);
        buf.writeFloat(value.pitch);
    }
    private static SoundPayload read(RegistryByteBuf buf) {
        return new SoundPayload(buf.readVarInt(), buf.readString(128), buf.readVarInt(),
            buf.readVarInt(), buf.readVarLong(), buf.readString(256), buf.readFloat(), buf.readFloat());
    }
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
