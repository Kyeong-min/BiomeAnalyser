package me.htna.project.biomeanalyser.biomeanalyser.Entity;


import com.flowpowered.math.vector.Vector3i;
import lombok.Getter;
import org.spongepowered.api.world.Chunk;

public class ChunkPosition {

    public static ChunkPosition fromBlock(Vector3i blockpos) {
        ChunkPosition cp = new ChunkPosition();
        cp.setPositionWithBlock(blockpos);
        return cp;
    }

    public static ChunkPosition fromChunk(Chunk chunk) {
        ChunkPosition cp = new ChunkPosition(chunk.getPosition());
        return cp;
    }

    @Getter
    public int cx;

    @Getter
    public int cz;

    public ChunkPosition() {

    }

    public ChunkPosition(int cx, int cz) {
        this.cx = cx;
        this.cz = cz;
    }

    public ChunkPosition(Vector3i cpos) {
        this.cx = cpos.getX();
        this.cz = cpos.getZ();
    }

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
     * 블록 좌표를 청크 좌표로 변환합니다.
     * y축은 0으로 가정합니다.
     *
     * @param x 블록 X 좌표
     * @param z 블록 Z 좌표
     * @return 청크 좌표
     */
    public static Vector3i coordinateToChunkPos(int x, int z) {
        boolean isNegative_x = x < 0;
        boolean isNegative_z = z < 0;

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
