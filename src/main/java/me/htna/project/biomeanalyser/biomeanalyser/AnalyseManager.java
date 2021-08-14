package me.htna.project.biomeanalyser.biomeanalyser;

import lombok.Getter;
import lombok.Setter;
import lombok.var;
import me.htna.project.biomeanalyser.biomeanalyser.Entity.ChunkInfo;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AnalyseManager {

    private static AnalyseManager instance;
    public static AnalyseManager getInstance() {
        if (instance == null)
            instance = new AnalyseManager();

        return instance;
    }

    @Getter
    private UUID owner;

    @Getter
    @Setter
    private World world;

    @Getter
    @Setter
    private ChunkInfo cpos1;

    @Getter
    @Setter
    private ChunkInfo cpos2;

    @Getter
    private List<String> fileList;

    private Analyser analyser;

    /**
     * 로드된 이미지 중 선택된 이미지의 인덱스
     */
    private int selectedIdx;

    /**
     * 로드한 이미지로 월드에 적용할 바이옴 타입
     */
    private BiomeType applyType;

    private AnalyseManager() {
        clear();
    }

    public void clear() {
        world = null;
        cpos1 = null;
        cpos2 = null;
        analyser = null;
        fileList = null;
        selectedIdx = -1;
        applyType = null;
    }

    public boolean isLocked() {
        return owner != null;
    }

    public boolean isOwner(Player p) {
        return p.getUniqueId().equals(owner);
    }

    public String getOwnerPlayerName() {
        if (!isLocked())
            return null;

        Optional<Player> optPlayer = Sponge.getServer().getPlayer(owner);
        return optPlayer.map(User::getName).orElse("");

    }

    public boolean lock(Player p) {
        if (isLocked()) {
            return false;
        }

        owner = p.getUniqueId();
        return true;
    }

    public boolean unlock(Player p) {
        if (!isLocked() || !isOwner(p))
            return false;

        owner = null;
        clear();

        return true;
    }

    public void unlockForce() {
        owner = null;
        clear();
    }

    public boolean analyse(Player p) {
        if (cpos1 == null || cpos2 == null || world == null) {
            p.sendMessage(Text.of("분석 할 위치가 올바르게 지정되지 않았습니다."));
            return false;
        }

        Location<World> playerLoc = p.getLocation();

        this.analyser = new Analyser();
        analyser.analyse(p, world, cpos1, cpos2);

        p.setLocation(playerLoc);

        return true;
    }

    public Optional<List<Tuple<String, Integer>>> getBiomeTypeList() {
        if (analyser == null)
            return Optional.empty();

        return analyser.getBiomeTypeList();
    }

    public void save() {
        analyser.write();
    }

    public void saveAsync() {
        CompletableFuture.runAsync(() -> {
            BiomeAnalyser.getInstance().getLogger().info("Write start");
            boolean result = analyser.write();

            var optPlayer = Sponge.getServer().getPlayer(this.owner);
            if (result) {
                BiomeAnalyser.getInstance().getLogger().info("Success write");
                optPlayer.ifPresent(p -> p.sendMessage(Text.of("바이옴 정보 쓰기 완료.")));
            } else {
                BiomeAnalyser.getInstance().getLogger().info("Failed write");
                optPlayer.ifPresent(p -> p.sendMessage(Text.of("바이옴 정보 쓰기 실패.")));
            }
        });
    }

    /**
     * Input 폴더 내의 png파일 목록을 가져옵니다.
     *
     * @return 성공시 true, Input폴더가 존재하지 않을 경우 false
     */
    public boolean loadImageList() {
        Path path = BiomeAnalyser.getInstance().getInputPath();
        BiomeAnalyser.getInstance().getLogger().info(String.format("Load path: %s", path));

        File inputFolder = BiomeAnalyser.getInstance().getInputPath().toFile();
        if (!inputFolder.isDirectory())
            return false;

        String[] list = inputFolder.list((f, n) -> n.toLowerCase().endsWith(".png"));
        if (list == null)
            return false;

        fileList = Arrays.stream(list).collect(Collectors.toList());
        return true;
    }

    /**
     * 월드에 로드할 바이옴 이미지의 인덱스와 바이옴 타입을 설정한다.
     *
     * @param index 이미지의 인덱스
     * @param type 바이옴 타입
     * @return 성공시 true, 이미지가 로드되어 있지 않거나 인덱스가 범위 밖이라면 false
     */
    public boolean selectImage(int index, BiomeType type) {
        if (fileList == null || fileList.size() <= index || index < 0)
            return false;

        selectedIdx = index;
        applyType = type;
        return true;
    }

    /**
     * 선택된 바이옴 이미지를 기반으로 월드에 로드한다.
     *
     * @return 성공시 true, 이미지가 선택되어있지 않거나 로드에 실패하면 false
     */
    public boolean loadImage(Player p) {
        if (selectedIdx == -1 || applyType == null) {
            return false;
        }

        String imageName = fileList.get(selectedIdx);
        Path path = Paths.get(BiomeAnalyser.getInstance().getInputPath().toString(), imageName);

        Location<World> playerLoc = p.getLocation();

        boolean result = analyser.read(p, path, world, applyType);

        p.setLocation(playerLoc);

        return result;
    }
}
