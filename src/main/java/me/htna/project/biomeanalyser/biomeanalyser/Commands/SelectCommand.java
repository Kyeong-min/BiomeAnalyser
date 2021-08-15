package me.htna.project.biomeanalyser.biomeanalyser.Commands;

import com.flowpowered.noise.module.combiner.Select;
import me.htna.project.biomeanalyser.biomeanalyser.AnalyseManager;
import me.htna.project.biomeanalyser.biomeanalyser.BiomeAnalyser;
import me.htna.project.biomeanalyser.biomeanalyser.Commands.customargs.BiomeCommandElement;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.ArrayList;
import java.util.Optional;

public class SelectCommand extends BaseCommand {

    public final static String SUBPERMISSION = "select";
    public final static String[] ALIAS = {"select"};

    public SelectCommand() {
        super(ALIAS, "로드할 이미지와 바이옴 타입을 선택합니다.", SUBPERMISSION);

        elementList = new ArrayList<>();
        elementList.add(GenericArguments.integer(Text.of("Index")));
        elementList.add(new BiomeCommandElement(Text.of("Biome")));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        BiomeAnalyser.getInstance().getLogger().info("Select command");

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

        Optional<Integer> idx = args.getOne("Index");
        Optional<BiomeType> biomeType = args.getOne("Biome");
        if (!idx.isPresent() || idx.get() < 0) {
            src.sendMessage(Text.of("인덱스가 올바르지 않습니다."));
            return CommandResult.empty();
        }

        if (!biomeType.isPresent()) {
            src.sendMessage(Text.of("바이옴 타입이 올바르지 않습니다."));
            return CommandResult.empty();
        }

        if (!manager.selectImageAsync(idx.get(), biomeType.get())) {
            src.sendMessage(Text.of("파일 선택에 실패했습니다."));
            return CommandResult.empty();
        }

        src.sendMessage(Text.of(String.format("파일이 선택되었습니다. [%d] - %s, 바이옴 정보를 로드합니다.", idx.get(), biomeType.get().getName())));
        return CommandResult.success();
    }
}
