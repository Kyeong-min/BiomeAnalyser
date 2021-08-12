package me.htna.project.biomeanalyser.biomeanalyser.Commands;

import com.flowpowered.math.vector.Vector3i;
import lombok.var;
import me.htna.project.biomeanalyser.biomeanalyser.AnalyseManager;
import me.htna.project.biomeanalyser.biomeanalyser.Analyser;
import me.htna.project.biomeanalyser.biomeanalyser.BiomeAnalyser;
import me.htna.project.biomeanalyser.biomeanalyser.Commands.customargs.Vector3iCommandElement;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Optional;

public class AnalyseCommand extends BaseCommand {

    public final static String SUBPERMISSION = "analyse";
    public final static String[] ALIAS = {"analyse", "a"};

    public AnalyseCommand() {
        super(ALIAS, "바이옴 상태를 분석합니다.", SUBPERMISSION);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
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

        if (!manager.getWorld().equals(player.getWorld())) {
            src.sendMessage(Text.of("바이옴 분포를 분석할 월드로 이동 후에 명령을 실행해주세요."));
            return CommandResult.empty();
        }

        Location<World> loc = player.getLocation();
        manager.analyse(player);
        player.setLocation(loc);
        player.sendMessage(Text.of("분석 완료."));

        var optList = manager.getBiomeTypeList();
        optList.ifPresent(list -> {
            list.stream().forEach(t -> {
                player.sendMessage(Text.of(String.format("Type: %s, Count: %d", t.getFirst(), t.getSecond())));
            });
        });

        return CommandResult.success();
    }
}
