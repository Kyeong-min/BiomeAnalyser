package me.htna.project.biomeanalyser.biomeanalyser.Commands;

import com.flowpowered.math.vector.Vector3i;
import me.htna.project.biomeanalyser.biomeanalyser.BiomeAnalyser;
import me.htna.project.biomeanalyser.biomeanalyser.Commands.customargs.Vector3iCommandElement;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.ArrayList;
import java.util.Optional;

public class BiomeTypeCommand extends BaseCommand{

    public final static String SUBPERMISSION = "biometype";
    public final static String[] ALIAS = {"biometype"};

    public BiomeTypeCommand() {
        super(ALIAS, "지정된 좌표의 바이옴 정보를 가져옵니다. (디버깅용)", SUBPERMISSION);

        elementList = new ArrayList<>();
        elementList.add(new Vector3iCommandElement(Text.of("Pos")));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        BiomeAnalyser.getInstance().getLogger().info("biometype command");
        Player player = (Player)src;

        Optional<Vector3i> optPos = args.getOne("Pos");
        if (!optPos.isPresent()) {
            player.sendMessage(Text.of("Invalid arguments"));
            return CommandResult.empty();
        }

        Vector3i pos = optPos.get();
        BiomeType type = player.getWorld().getBiome(pos.getX(), 0, pos.getZ());
        player.sendMessage(Text.of(type.getName()));

        return CommandResult.success();
    }
}
