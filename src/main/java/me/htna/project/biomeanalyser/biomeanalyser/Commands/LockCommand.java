package me.htna.project.biomeanalyser.biomeanalyser.Commands;

import me.htna.project.biomeanalyser.biomeanalyser.AnalyseManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public class LockCommand extends BaseCommand {

    public final static String SUBPERMISSION = "lock";
    public final static String[] ALIAS = {"lock"};

    public LockCommand() {
        super(ALIAS, "사용권을 선언합니다.", SUBPERMISSION);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!isPlayerSource(src)) {
            src.sendMessage(Text.of("This command is player only."));
            return CommandResult.empty();
        }

        AnalyseManager manager = AnalyseManager.getInstance();
        if (!manager.lock((Player)src)) {
            StringBuilder sb = new StringBuilder();
            sb.append("사용권 선언에 실패했습니다. 사용권을 가진 플레이어: ").append(manager.getOwnerPlayerName());
            src.sendMessage(Text.of(sb.toString()));
            return CommandResult.empty();
        }

        src.sendMessage(Text.of("사용권이 선언되었습니다."));
        return CommandResult.success();
    }
}
