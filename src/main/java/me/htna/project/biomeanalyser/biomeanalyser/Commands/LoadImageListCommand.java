package me.htna.project.biomeanalyser.biomeanalyser.Commands;

import me.htna.project.biomeanalyser.biomeanalyser.AnalyseManager;
import me.htna.project.biomeanalyser.biomeanalyser.BiomeAnalyser;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;

public class LoadImageListCommand extends BaseCommand {

    public final static String SUBPERMISSION = "loadimagelist";
    public final static String[] ALIAS = {"loadimagelist"};

    public LoadImageListCommand() {
        super(ALIAS, "이미지 목록을 가져옵니다.", SUBPERMISSION);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        BiomeAnalyser.getInstance().getLogger().info("Load image command");

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

        p.sendMessage(Text.of("이미지 목록"));
        if (!manager.loadImageList()) {
            p.sendMessage(Text.of("이미지 목록 읽어오기 실패"));
            return CommandResult.empty();
        }
        List<String> list = manager.getFileList();

        int i = 0;
        for(String name: list) {
            // #n 파일명
            p.sendMessage(Text.of(String.format("#%d - %s", i++, name)));
        }

        return CommandResult.success();
    }
}
