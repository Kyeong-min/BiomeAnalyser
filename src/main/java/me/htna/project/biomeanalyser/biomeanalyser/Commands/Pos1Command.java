package me.htna.project.biomeanalyser.biomeanalyser.Commands;

import com.flowpowered.math.vector.Vector3i;
import me.htna.project.biomeanalyser.biomeanalyser.AnalyseManager;
import me.htna.project.biomeanalyser.biomeanalyser.Entity.ChunkPosition;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class Pos1Command extends BaseCommand{

    public final static String SUBPERMISSION = "pos";
    public final static String[] ALIAS = {"pos1", "p1"};

    public Pos1Command() {
        super(ALIAS, "Set position 1", SUBPERMISSION);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!isPlayerSource(src)) {
            src.sendMessage(Text.of("This command is player only."));
            return CommandResult.empty();
        }

        Player p = (Player)src;
        AnalyseManager manager = AnalyseManager.getInstance();
        if (!manager.isOwner(p)) {
            src.sendMessage(Text.of("사용권 소유자가 아닙니다."));
            return CommandResult.empty();
        }

        World world = p.getWorld();
        if (manager.getWorld() != null && !manager.getWorld().equals(world)) {
            src.sendMessage(Text.of("월드가 일치하지 않습니다."));
            return CommandResult.empty();
        }

        Vector3i pos = p.getLocation().getBlockPosition();
        Optional<Chunk> chunk = world.getChunkAtBlock(pos);
        if (!chunk.isPresent()) {
            src.sendMessage(Text.of("청크 위치를 읽지못했습니다."));
            return CommandResult.empty();
        }

        ChunkPosition cpos = ChunkPosition.fromChunk(chunk.get());
        manager.setWorld(world);
        manager.setCpos1(cpos);

        src.sendMessage(Text.of("첫번째 청크 위치가 설정되었습니다."));
        src.sendMessage(Text.of(cpos));
        return CommandResult.success();
    }
}
