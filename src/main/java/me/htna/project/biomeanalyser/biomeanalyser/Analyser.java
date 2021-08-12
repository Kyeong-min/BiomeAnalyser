package me.htna.project.biomeanalyser.biomeanalyser;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import me.htna.project.biomeanalyser.biomeanalyser.Entity.BiomeInfo;
import me.htna.project.biomeanalyser.biomeanalyser.Entity.ChunkPosition;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Analyser {

    private Map<String, List<BiomeInfo>> infos;

    ChunkPosition ltC;
    ChunkPosition rbC;

    private int offsetToZeroX;
    private int offsetToZeroZ;

    /**
     * 청크의 바이옴 정보를 분석합니다.
     * @param player 플레이어
     * @param world 청크를 찾을 월드
     * @param cp 청크의 좌표
     * @return
     */
    public boolean analyseBiomeInChunk(Player player, World world, ChunkPosition cp) {
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
        offsetToZeroX = 0;
        offsetToZeroZ = 0;
    }

    /**
     * 바이옴 분포를 분석합니다.
     *
     * @param player 플레이어
     * @param world 분석 할 월드
     * @param cpos1 첫번째 청크 좌표
     * @param cpos2 두번째 청크 좌표
     */
    public void analyse(Player player, World world, ChunkPosition cpos1, ChunkPosition cpos2) {
        int ltX = Math.min(cpos1.getCx(), cpos2.getCx());
        int ltZ = Math.min(cpos1.getCz(), cpos2.getCz());
        int rbX = Math.max(cpos1.getCx(), cpos2.getCx());
        int rbZ = Math.max(cpos1.getCz(), cpos2.getCz());

        ltC = new ChunkPosition(ltX, ltZ);
        rbC = new ChunkPosition(rbX, rbZ);

        BiomeAnalyser.getInstance().getLogger().info(String.format("Start analyse, (%s / %s)", ltC, rbC));

        infos = new HashMap<>();
        for (int cz = ltZ; cz <= rbZ; cz++) {
            for (int cx = ltX; cx <= rbX; cx++) {
                analyseBiomeInChunk(player, world, new ChunkPosition(cx, cz));
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

    public boolean write() {
        if (infos == null)
            return false;

        Path path = Paths.get(BiomeAnalyser.getInstance().getGame().getGameDirectory().toString(),
                "BiomeAnalyser");

        BiomeAnalyser.getInstance().getLogger().info(String.format("Save path: %s", path));

        Vector3i ltBlockPos = ltC.getLTBlockCoordinate();
        Vector3i rbBlockPos = rbC.getRBBlockCoordinate();

        offsetToZeroX = -ltC.getLTBlockCoordinate().getX();
        offsetToZeroZ = -ltC.getLTBlockCoordinate().getZ();

        ImageControl control = new ImageControl.ImageControlBuilder()
                .infos(infos)
                .width((rbBlockPos.getX() - ltBlockPos.getX()+1))
                .height((rbBlockPos.getZ() - ltBlockPos.getZ()+1))
                .offsetX(offsetToZeroX)
                .offsetY(offsetToZeroZ)
                .path(path.toString())
                .build();
        BiomeAnalyser.getInstance().getLogger().info("CheckPoint 04");
        if (control.save())
            return true;

        return false;
    }
}
