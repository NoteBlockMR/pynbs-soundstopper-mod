package dev.noteblock.wirelessnbs;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.mojang.brigadier.arguments.FloatArgumentType.*;
import static com.mojang.brigadier.arguments.IntegerArgumentType.*;
import static com.mojang.brigadier.arguments.LongArgumentType.*;
import static net.minecraft.server.command.CommandManager.*;

public final class WirelessNbs implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("wirelessnbs");

    @Override public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(SoundPayload.ID, SoundPayload.CODEC);
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> {
            dispatcher.register(literal("wirelessnbs").requires(source -> source.hasPermissionLevel(2))
                .then(literal("play")
                    .then(argument("targets", EntityArgumentType.players())
                    .then(argument("song", IdentifierArgumentType.identifier())
                    .then(argument("layer", integer(1, 65535))
                    .then(argument("note", longArg(0))
                    .then(argument("sound", IdentifierArgumentType.identifier())
                    .then(argument("volume", floatArg(0, 1))
                    .then(argument("pitch", floatArg(0.5f, 2))
                        .executes(ctx -> send(ctx, SoundPayload.play(song(ctx), getInteger(ctx, "layer"),
                            getLong(ctx, "note"), IdentifierArgumentType.getIdentifier(ctx, "sound").toString(),
                            getFloat(ctx, "volume"), getFloat(ctx, "pitch"))))))))))))
                .then(literal("stop")
                    .then(argument("targets", EntityArgumentType.players())
                    .then(argument("song", IdentifierArgumentType.identifier())
                    .then(argument("start", integer(0, 65535))
                    .then(argument("end", integer(0, 65535))
                        .executes(ctx -> send(ctx, SoundPayload.stopRange(song(ctx),
                            getInteger(ctx, "start"), getInteger(ctx, "end")))))))))
                .then(literal("stopnote")
                    .then(argument("targets", EntityArgumentType.players())
                    .then(argument("song", IdentifierArgumentType.identifier())
                    .then(argument("note", longArg(0))
                        .executes(ctx -> send(ctx, SoundPayload.stopNote(song(ctx), getLong(ctx, "note"))))))))
                .then(literal("stopall")
                    .then(argument("targets", EntityArgumentType.players())
                    .then(argument("song", IdentifierArgumentType.identifier())
                        .executes(ctx -> send(ctx, SoundPayload.stopRange(song(ctx), 0, 0))))))
                .then(literal("status")
                    .then(argument("targets", EntityArgumentType.players()).executes(ctx -> {
                        int ready = 0;
                        for (var player : EntityArgumentType.getPlayers(ctx, "targets")) {
                            boolean supported = ServerPlayNetworking.canSend(player, SoundPayload.ID);
                            if (supported) ready++;
                            ctx.getSource().sendFeedback(() -> Text.literal(player.getName().getString()
                                + (supported ? ": WirelessNBS ready" : ": WirelessNBS client mod missing")), false);
                        }
                        return ready;
                    }))));
        });
        LOGGER.info("WirelessNBS 1.21.1 sound commands registered");
    }

    private static String song(CommandContext<ServerCommandSource> ctx) {
        return IdentifierArgumentType.getIdentifier(ctx, "song").toString();
    }

    private static int send(CommandContext<ServerCommandSource> ctx, SoundPayload packet) throws CommandSyntaxException {
        int sent = 0;
        for (var player : EntityArgumentType.getPlayers(ctx, "targets")) {
            if (ServerPlayNetworking.canSend(player, SoundPayload.ID)) {
                ServerPlayNetworking.send(player, packet);
                sent++;
            }
        }
        // No broadcast /playsound fallback: that would break layer isolation again.
        return sent;
    }
}
