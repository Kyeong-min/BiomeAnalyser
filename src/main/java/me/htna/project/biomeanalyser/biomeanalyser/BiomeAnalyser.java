package me.htna.project.biomeanalyser.biomeanalyser;

import com.google.inject.Inject;
import lombok.Getter;
import me.htna.project.biomeanalyser.biomeanalyser.Commands.*;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

@Plugin(
        id = "biomeanalyser",
        name = "Biomeanalyser",
        description = "Biome analyser",
        authors = {
                "HATENA"
        }
)
public class BiomeAnalyser {

    @Getter
    private static BiomeAnalyser instance;

    @Inject
    @Getter
    private Game game;

    @Inject
    @Getter
    private Logger logger;

    private void registerCommands() {
        logger.debug("BiomeAnalyser#registerCommands");
        CommandSpec spec= CommandSpec.builder()
                .description(Text.of("Biome analyser"))
                .permission(BaseCommand.PERMISSION_BASE)
                .child(new AnalyseCommand().buildSelf(), AnalyseCommand.ALIAS)
                .child(new BiomeTypeCommand().buildSelf(), BiomeTypeCommand.ALIAS)
                .child(new LoadedChunkListCommand().buildSelf(), LoadedChunkListCommand.ALIAS)
                .child(new Pos1Command().buildSelf(), Pos1Command.ALIAS)
                .child(new Pos2Command().buildSelf(), Pos2Command.ALIAS)
                .child(new PosCommand().buildSelf(), PosCommand.ALIAS)
                .child(new LockCommand().buildSelf(), LockCommand.ALIAS)
                .child(new UnlockCommand().buildSelf(), UnlockCommand.ALIAS)
                .child(new SaveCommand().buildSelf(), SaveCommand.ALIAS)
                .build();

        Sponge.getCommandManager().register(this, spec, "biomeanalyser", "ba");
    }

    public BiomeAnalyser() {
        BiomeAnalyser.instance = this;
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        logger.debug("BiomeAnalyser#onInitialization");
        registerCommands();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.debug("BiomeAnalyser#onServerStart");
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event, @First Player p) {
        AnalyseManager m = AnalyseManager.getInstance();
        if (m.isOwner(p)) {
            logger.info("Disconnected owner player, force unlock");
            m.unlockForce();
        }
    }
}
