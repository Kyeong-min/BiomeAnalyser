package me.htna.project.biomeanalyser.biomeanalyser.Commands;

import me.htna.project.biomeanalyser.biomeanalyser.AnalyseManager;
import me.htna.project.biomeanalyser.biomeanalyser.BiomeAnalyser;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class SaveCommand extends BaseCommand {

    public final static String SUBPERMISSION = "save";
    public final static String[] ALIAS = {"save"};

    public SaveCommand() {
        super(ALIAS, "Save image", SUBPERMISSION);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        BiomeAnalyser.getInstance().getLogger().info("Save command");

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

        manager.saveAsync();
        src.sendMessage(Text.of("바이옴 정보 쓰기 시작"));
        return CommandResult.success();
    }
}
