package me.htna.project.biomeanalyser.biomeanalyser.Commands;

import me.htna.project.biomeanalyser.biomeanalyser.AnalyseManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class UnlockCommand extends BaseCommand {

    public final static String SUBPERMISSION = "unlock";
    public final static String[] ALIAS = {"unlock"};

    public UnlockCommand() {
        super(ALIAS, "사용권을 해제합니다.", SUBPERMISSION);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!isPlayerSource(src)) {
            src.sendMessage(Text.of("This command is player only."));
            return CommandResult.empty();
        }

        AnalyseManager manager = AnalyseManager.getInstance();
        if (!manager.isLocked()) {
            src.sendMessage(Text.of("사용권이 선언되지 않았습니다."));
            return CommandResult.empty();
        }

        if (!manager.unlock((Player)src)) {
            src.sendMessage(Text.of("사용권 소유자가 아닙니다."));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }
}
