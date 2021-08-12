package me.htna.project.biomeanalyser.biomeanalyser.Commands;

import lombok.var;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadedChunkListCommand extends BaseCommand {

    public final static String SUBPERMISSION = "loadedchunklist";
    public final static String[] ALIAS = {"loadedchunklist"};

    public LoadedChunkListCommand() {
        super(ALIAS, "로드 된 청크 정보를 출력합니다.", SUBPERMISSION);

        elementList = new ArrayList<>();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        World world = player.getWorld();

        var chunks = world.getLoadedChunks();
        AtomicInteger count = new AtomicInteger();
        chunks.forEach(chunk -> {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(count.incrementAndGet()).append("] ").append(chunk.getPosition());

            player.sendMessage(Text.of(sb.toString()));
        });

        return CommandResult.success();
    }
}
