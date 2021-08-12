package me.htna.project.biomeanalyser.biomeanalyser.Commands;

import com.flowpowered.math.vector.Vector3i;
import me.htna.project.biomeanalyser.biomeanalyser.AnalyseManager;
import me.htna.project.biomeanalyser.biomeanalyser.Commands.customargs.Vector3iCommandElement;
import me.htna.project.biomeanalyser.biomeanalyser.Entity.ChunkPosition;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Optional;

public class PosCommand extends BaseCommand {

    public final static String SUBPERMISSION = "pos";
    public final static String[] ALIAS = {"pos", "p"};

    public PosCommand() {
        super(ALIAS, "Set positions", SUBPERMISSION);

        elementList = new ArrayList<>();
        elementList.add(new Vector3iCommandElement(Text.of("Pos1")));
        elementList.add(new Vector3iCommandElement(Text.of("Pos2")));
        elementList.add(GenericArguments.optionalWeak(GenericArguments.world(Text.of("world"))));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!isPlayerSource(src)) {
            src.sendMessage(Text.of("This command is player only."));
            return CommandResult.empty();
        }

        Player p = (Player)src;

        Optional<Vector3i> optPos1 = args.getOne("Pos1");
        Optional<Vector3i> optPos2 = args.getOne("Pos2");
        Optional<World> optWorld = args.getOne("world");

        if (!optPos1.isPresent() || !optPos2.isPresent()) {
            src.sendMessage(Text.of("매게변수가 올바르지 않습니다."));
            return CommandResult.empty();
        }

        World world = optWorld.orElse(p.getWorld());

        Vector3i pos1 = optPos1.get();
        Vector3i pos2 = optPos2.get();

        ChunkPosition cpos1 = ChunkPosition.fromBlock(pos1);
        ChunkPosition cpos2 = ChunkPosition.fromBlock(pos2);

        AnalyseManager manager = AnalyseManager.getInstance();
        if (!manager.isOwner(p)) {
            src.sendMessage(Text.of("사용권 소유자가 아닙니다."));
            return CommandResult.empty();
        }

        manager.setWorld(world);
        manager.setCpos1(cpos1);
        manager.setCpos2(cpos2);

        src.sendMessage(Text.of("청크 위치가 설정되었습니다."));
        src.sendMessage(Text.of("Pos1: " + cpos1));
        src.sendMessage(Text.of("Pos2: " + cpos2));
        return CommandResult.success();
    }
}
