package me.htna.project.biomeanalyser.biomeanalyser.Commands;

import me.htna.project.biomeanalyser.biomeanalyser.AnalyseManager;
import me.htna.project.biomeanalyser.biomeanalyser.BiomeAnalyser;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ApplyCommand extends BaseCommand {

    public final static String SUBPERMISSION = "apply";
    public final static String[] ALIAS = {"apply"};

    public ApplyCommand() {
        super(ALIAS, "Apply biome from image", SUBPERMISSION);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        BiomeAnalyser.getInstance().getLogger().info("Apply command");

        if (!isPlayerSource(src)) {
            src.sendMessage(Text.of("This command is player only."));
            return CommandResult.empty();
        }

        Player player = (Player)src;
        AnalyseManager manager = AnalyseManager.getInstance();
        if (!manager.isOwner(player)) {
            src.sendMessage(Text.of("사용권 소유자가 아닙니다."));
            return CommandResult.empty();
        }

        Location<World> loc = player.getLocation();
        if (!manager.loadImage(player)) {
            src.sendMessage(Text.of("실패했습니다."));
            return CommandResult.empty();
        }
        player.setLocation(loc);
        player.sendMessage(Text.of("적용 완료."));
        
        return CommandResult.success();
    }
}
