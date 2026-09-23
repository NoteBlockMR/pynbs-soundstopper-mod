package dev.noteblock.wirelessnbs.client;

import dev.noteblock.wirelessnbs.SoundBook;
import dev.noteblock.wirelessnbs.SoundPayload;
import dev.noteblock.wirelessnbs.WirelessNbs;
import java.util.HashSet;
import java.util.Set;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public final class WirelessNbsClient implements ClientModInitializer {
    private SoundBook<SoundInstance> playing;
    private long ticks;
    private Object previousWorld;
    private final Set<String> missing = new HashSet<>();

    @Override public void onInitializeClient() {
        var client = MinecraftClient.getInstance();
        playing = new SoundBook<>(sound -> client.getSoundManager().stop(sound), 16384);
        ClientPlayNetworking.registerGlobalReceiver(SoundPayload.ID, (packet, context) ->
            context.client().execute(() -> handle(context.client(), packet)));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> {
            playing.clear();
            missing.clear();
            previousWorld = null;
        });
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            ticks++;
            if (previousWorld != null && previousWorld != mc.world) playing.clear();
            previousWorld = mc.world;
            playing.prune(ticks, sound -> mc.getSoundManager().isPlaying(sound));
        });
    }

    private void handle(MinecraftClient client, SoundPayload packet) {
        if (client.world == null || client.player == null) return;
        if (previousWorld != client.world) {
            playing.clear();
            previousWorld = client.world;
        }
        switch (packet.action()) {
            case SoundPayload.PLAY -> {
                Identifier id = Identifier.of(packet.sound());
                if (client.getSoundManager().get(id) == null) {
                    if (missing.add(packet.sound())) {
                        WirelessNbs.LOGGER.warn("Sound not in loaded resource packs: {}", id);
                        client.player.sendMessage(net.minecraft.text.Text.literal(
                            "[WirelessNBS] Missing sound: " + id + " (check your sound pack)"), false);
                    }
                    return;
                }
                // Relative, non-attenuating audio matches the generator's listener-centred playback.
                SoundInstance sound = new PositionedSoundInstance(id, SoundCategory.RECORDS,
                    packet.volume(), packet.pitch(), Random.create(), false, 0,
                    SoundInstance.AttenuationType.NONE, 0, 0, 0, true);
                playing.add(packet.song(), packet.note(), packet.layer(), sound, ticks);
                client.getSoundManager().play(sound);
            }
            case SoundPayload.STOP_RANGE -> playing.stopRange(packet.song(), packet.layer(), packet.endLayer());
            case SoundPayload.STOP_NOTE -> playing.stopNote(packet.song(), packet.note());
            default -> throw new IllegalArgumentException("Unknown sound action");
        }
    }
}
