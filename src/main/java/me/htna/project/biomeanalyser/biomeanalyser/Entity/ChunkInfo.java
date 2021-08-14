package me.htna.project.biomeanalyser.biomeanalyser.Entity;


import com.flowpowered.math.vector.Vector3i;
import lombok.Getter;
import org.spongepowered.api.world.Chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 청크 정보
 */
public class ChunkInfo {

    /**
     * 블록 좌표로부터 청크 정보를 생성
     *
     * @param blockpos 블록 좌표
     * @return {@link ChunkInfo}
     */
    public static ChunkInfo fromBlock(Vector3i blockpos) {
        ChunkInfo cp = new ChunkInfo();
        cp.setPositionWithBlock(blockpos);
        return cp;
    }

    /**
     * {@link Chunk}로부터 청크 정보를 생성
     *
     * @param chunk {@link Chunk}
     * @return {@link ChunkInfo}
     */
    public static ChunkInfo fromChunk(Chunk chunk) {
        ChunkInfo cp = new ChunkInfo(chunk.getPosition());
        return cp;
    }

    /**
     * X 좌표
     */
    @Getter
    private int cx;

    /**
     * Z 좌표
     */
    @Getter
    private int cz;

    /**
     * 청크 내부 바이옴 정보
     */
    private List<BiomeInfo> biomeInfos;

    public ChunkInfo() {
        biomeInfos = new ArrayList<>();
    }

    public ChunkInfo(int cx, int cz) {
        this();
        this.cx = cx;
        this.cz = cz;
    }

    public ChunkInfo(Vector3i cpos) {
        this();
        this.cx = cpos.getX();
        this.cz = cpos.getZ();
    }

    /**
     * 블록 좌표를 청크 좌표로 변환하여 설정합니다.
     * 
     * @param pos 블록 좌표
     */
    public void setPositionWithBlock(Vector3i pos) {
        setPositionWithBlock(pos.getX(), pos.getZ());
    }

    /**
     * 블록 좌표를 청크 좌표로 변환하여 설정합니다.
     *
     * @param x 블록의 X좌표
     * @param z 블록의 Z좌표
     */
    public void setPositionWithBlock(int x, int z) {
        Vector3i pos = coordinateToChunkPos(x, z);
        this.cx = pos.getX();
        this.cz = pos.getZ();
    }

    /**
     * 청크 좌표를 설정합니다.
     *
     * @param x 청크의 X좌표
     * @param z 청크의 Z좌표
     */
    public void setPosition(int x, int z) {
        this.cx = x;
        this.cz = z;
    }

    /**
     * 바이옴 정보를 등록합니다.
     *
     * @param biomeInfo {@link BiomeInfo}
     */
    public void addBiomeInfo(BiomeInfo biomeInfo) {
        this.biomeInfos.add(biomeInfo);
    }

    /**
     * 변경 불가능한 바이옴 정보 리스트를 가져옵니다.
     *
     * @return 변경 불가능한 바이옴 정보 리스트
     */
    public List<BiomeInfo> getBiomeInfoList() {
        return Collections.unmodifiableList(this.biomeInfos);
    }

    /**
     * 블록 좌표를 청크 좌표로 변환합니다.
     * y축은 0으로 가정합니다.
     *
     * @param x 블록 X 좌표
     * @param z 블록 Z 좌표
     * @return 청크 좌표
     */
    public static Vector3i coordinateToChunkPos(int x, int z) {
        return Vector3i.from((int) (Math.floor((double)x / 16f)), 0, (int) Math.floor((double)z / 16f));
    }

    /**
     * 청크 내 좌상단 블록 좌표를 가져옵니다.
     *
     * @return 청크 내 좌상단 좌표, y축은 0으로 고정
     */
    public Vector3i getLTBlockCoordinate() {
        return Vector3i.from(this.cx * 16, 0, this.cz * 16);
    }

    /**
     * 청크 내 우하단 블록 좌표를 가져옵니다.
     *
     * @return 청크 내 우하단 좌표, y축은 0으로 고정
     */
    public Vector3i getRBBlockCoordinate() {
        return Vector3i.from(this.cx * 16 + 15, 0, this.cz * 16 + 15);
    }

    @Override
    public String toString() {
        return String.format("(X %d, Z %d)", this.cx, this.cz);
    }

}
