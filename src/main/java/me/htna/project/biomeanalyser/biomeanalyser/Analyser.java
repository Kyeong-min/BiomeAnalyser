package me.htna.project.biomeanalyser.biomeanalyser;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import me.htna.project.biomeanalyser.biomeanalyser.Entity.BiomeInfo;
import me.htna.project.biomeanalyser.biomeanalyser.Entity.ChunkInfo;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

/**
 * 월드의 바이옴을 분석하거나 바이옴 정보를 월드에 적용합니다.
 */
public class Analyser {

    /**
     * 월드에서 읽어들인 바이옴 정보
     */
    private Map<String, List<BiomeInfo>> infos;

    /**
     * 월드에 적용할 바이옴 정보
     */
    private List<ChunkInfo> readInfos;

    ChunkInfo ltC;
    ChunkInfo rbC;

    /**
     * 청크의 바이옴 정보를 분석합니다.
     * @param player 플레이어
     * @param world 청크를 찾을 월드
     * @param cp 청크의 좌표
     * @return
     */
    public boolean analyseBiomeInChunk(Player player, World world, ChunkInfo cp) {
        Vector3i lt = cp.getLTBlockCoordinate();
        Vector3i rb = cp.getRBBlockCoordinate();
        int ltX = lt.getX();
        int ltZ = lt.getZ();
        int rbX = rb.getX();
        int rbZ = rb.getZ();

        player.setLocation(Vector3d.from(ltX, 0, ltZ), world.getUniqueId());

        for (int z = ltZ; z <= rbZ; z++) {
            for (int x = ltX; x <= rbX; x++) {
                BiomeType type = world.getBiome(x, 0, z);
                String name = type.getName();

                List<BiomeInfo> list;
                if (!infos.containsKey(name)) {
                    list = new ArrayList<>();
                    infos.put(name, list);
                } else {
                    list = infos.get(name);
                }

                BiomeInfo info = new BiomeInfo(type, x, z);
                list.add(info);
            }
        }

        return true;
    }

    public Analyser() {
        infos = null;
        readInfos= null;
    }

    /**
     * 바이옴 분포를 분석합니다.
     *
     * @param player 플레이어
     * @param world 분석 할 월드
     * @param cpos1 첫번째 청크 좌표
     * @param cpos2 두번째 청크 좌표
     */
    public void analyse(Player player, World world, ChunkInfo cpos1, ChunkInfo cpos2) {
        int ltX = Math.min(cpos1.getCx(), cpos2.getCx());
        int ltZ = Math.min(cpos1.getCz(), cpos2.getCz());
        int rbX = Math.max(cpos1.getCx(), cpos2.getCx());
        int rbZ = Math.max(cpos1.getCz(), cpos2.getCz());

        ltC = new ChunkInfo(ltX, ltZ);
        rbC = new ChunkInfo(rbX, rbZ);

        BiomeAnalyser.getInstance().getLogger().info(String.format("Start analyse, (%s / %s)", ltC, rbC));

        infos = new HashMap<>();
        for (int cz = ltZ; cz <= rbZ; cz++) {
            for (int cx = ltX; cx <= rbX; cx++) {
                analyseBiomeInChunk(player, world, new ChunkInfo(cx, cz));
            }
        }
    }

    /**
     * 바이옴 타입 리스트를 가져옵니다.
     * 
     * @return 바이옴 타입명, 바이옴의 갯수 튜블로 이루어진 리스트
     */
    public Optional<List<Tuple<String, Integer>>> getBiomeTypeList() {
        if (infos == null)
            return Optional.empty();

        List<Tuple<String, Integer>> list = new ArrayList<>();
        infos.forEach((k, v) -> {
            Tuple<String, Integer> tuple = new Tuple<>(k, v.size());
            list.add(tuple);
        });

        return Optional.ofNullable(list);
    }

    /**
     * 바이옴 타입 갯수를 가져옵니다.
     * 
     * @return 바이옴 타입 갯수
     */
    public Optional<Integer> getBiomeTypeCount() {
        if (infos == null)
            return Optional.empty();

        return Optional.ofNullable(infos.keySet().size());
    }

    /**
     * 월드에서 읽어들인 바이옴 정보를 이미지로 저장합니다.
     *
     * @return 성공시 true, 실패시 false
     */
    public boolean write() {
        if (infos == null) {
            BiomeAnalyser.getInstance().getLogger().info(String.format("Biome info list null"));
            return false;
        }

        Path path = BiomeAnalyser.getInstance().getOutputPath();
        BiomeAnalyser.getInstance().getLogger().info(String.format("Save path: %s", path));

        Vector3i ltBlockPos = ltC.getLTBlockCoordinate();
        Vector3i rbBlockPos = rbC.getRBBlockCoordinate();

        int offsetToZeroX = -ltC.getLTBlockCoordinate().getX();
        int offsetToZeroZ = -ltC.getLTBlockCoordinate().getZ();

        ImageControl control = new ImageControl.ImageControlBuilder()
                .width((rbBlockPos.getX() - ltBlockPos.getX()+1))
                .height((rbBlockPos.getZ() - ltBlockPos.getZ()+1))
                .offsetX(offsetToZeroX)
                .offsetY(offsetToZeroZ)
                .path(path.toString())
                .build();
        if (control.save(infos))
            return true;

        return false;
    }

    /**
     * 이미지로부터 바이옴 정보를 읽어들입니다.
     *
     * @param path 이미지 경로
     * @param type 바이옴 타입
     * @return 성공시 true, 실패시 false
     */
    public boolean read(Path path, BiomeType type) {
        Logger logger = BiomeAnalyser.getInstance().getLogger();
        logger.info(String.format("Load path: %s", path));
        File file = path.toFile();
        if (!file.exists() || !file.isFile()) {
            logger.error(String.format("File not exists, %s", file.getAbsolutePath()));
            return false;
        }

        Vector3i ltBlockPos = ltC.getLTBlockCoordinate();
        Vector3i rbBlockPos = rbC.getRBBlockCoordinate();

        int offsetToZeroX = -ltC.getLTBlockCoordinate().getX();
        int offsetToZeroZ = -ltC.getLTBlockCoordinate().getZ();

        int width = rbBlockPos.getX() - ltBlockPos.getX()+1;
        int height = rbBlockPos.getZ() - ltBlockPos.getZ()+1;

        logger.info(
                String.format("read biome image, width: %d, height: %d, type: %s, offset x: %d, offset y: %d",
                        width, height, type.getName(), offsetToZeroX, offsetToZeroZ)
        );

        ImageControl control = new ImageControl.ImageControlBuilder()
                .width(width)
                .height(rbBlockPos.getZ() - ltBlockPos.getZ()+1)
                .offsetX(offsetToZeroX)
                .offsetY(offsetToZeroZ)
                .build();

        Optional<List<ChunkInfo>> optResult = control.load(path, type);
        if (!optResult.isPresent()) {
            logger.error("Failed read biome info from image");
            return false;
        }

        readInfos = optResult.get();
        logger.error(String.format("Success read biome info from image, count: %d", readInfos.size()));
        return true;
    }

    /**
     * 읽어들인 바이옴 정보를 월드에 적용합니다.
     *
     * @param p 플레이어
     * @param world 적용할 월드
     * @return 성공시 true, 실패시 false
     */
    public boolean apply(Player p, World world) {
        if (readInfos == null)
            return false;

        List<ChunkInfo> result = readInfos;
        for (ChunkInfo info : result) {
            List<BiomeInfo> biomeInfos = info.getBiomeInfoList();
            if (biomeInfos.isEmpty())
                continue;

            Vector3i pos = info.getLTBlockCoordinate();
            p.setLocation(Vector3d.from(pos.getX(), 0, pos.getZ()), world.getUniqueId());
            for (BiomeInfo biomeInfo : biomeInfos) {
                world.setBiome(biomeInfo.getX(), 0, biomeInfo.getZ(), biomeInfo.getType());
            }
        }

        return true;
    }
}
