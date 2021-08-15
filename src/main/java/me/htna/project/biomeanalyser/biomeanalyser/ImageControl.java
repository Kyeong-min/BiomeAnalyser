package me.htna.project.biomeanalyser.biomeanalyser;

import com.flowpowered.math.vector.Vector3i;
import lombok.Builder;
import lombok.var;
import me.htna.project.biomeanalyser.biomeanalyser.Entity.BiomeInfo;
import me.htna.project.biomeanalyser.biomeanalyser.Entity.ChunkInfo;
import org.slf4j.Logger;
import org.spongepowered.api.world.biome.BiomeType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ImageControl {

    private String outputPath;

    private int width;

    private int height;

    private int offsetX;

    private int offsetY;

    @Builder
    public ImageControl(String path,
                        int width,
                        int height,
                        int offsetX,
                        int offsetY) {

        this.outputPath = path;
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public boolean save(Map<String, List<BiomeInfo>> infos) {

        var logger = BiomeAnalyser.getInstance().getLogger();
        infos.forEach((k, v) -> {
            try {
                logger.info(
                        String.format("Draw biome analysed info, width: %d, height: %d, type: %s, offset x: %d, offset y: %d",
                        width, height, k, offsetX, offsetY)
                );
                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                for (BiomeInfo b : v) {
                    img.setRGB(b.getX() + offsetX, b.getZ() + offsetY, Color.RED.getRGB());
                }

                Path path = Paths.get(outputPath, k + ".png");
                File file = path.toFile();
                try {
                    logger.info("Save file: " + path);
                    ImageIO.write(img, "png", file);
                } catch (IOException e) {
                    logger.error("Save exception");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                logger.error("Image exception");
                e.printStackTrace();
            }
        });

        return true;
    }

    /**
     * 이미지 픽셀로부터 바이옴 정보를 읽습니다.
     * 픽셀의 알파값이 128 이상일 때 바이옴 정보를 적용합니다.
     *
     * @param path 이미지 경로
     * @param type 바이옴 타입
     * @return 바이옴 정보를 포함하고있는 청크 정보 리스트
     */
    public Optional<List<ChunkInfo>> load(Path path, BiomeType type) {
        Logger logger = BiomeAnalyser.getInstance().getLogger();
        logger.debug("ImageControl#load");

        File file = path.toFile();
        if (!file.exists() || !file.isFile()) {
            logger.info(String.format("Invalid path, path: %s", path));
            return Optional.empty();
        }

        logger.info(String.format("Read image, path: %s, type: %s", path, type.getName()));

        List<ChunkInfo> chunkInfos = new ArrayList<>();
        try {
            BufferedImage img = ImageIO.read(file);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = new Color(img.getRGB(x, y), true);
                    int alpha = color.getAlpha();
                    if (alpha < 128)
                        continue;

                    int bx = x - offsetX;
                    int bz = y - offsetY;
                    BiomeInfo info = new BiomeInfo(type, bx, bz);
                    Vector3i cpos = ChunkInfo.coordinateToChunkPos(bx, bz);
                    ChunkInfo chunk;
                    Optional<ChunkInfo> optChunk = chunkInfos.stream().filter(c -> c.getCx() == cpos.getX() && c.getCz() == cpos.getZ()).findFirst();
                    if (!optChunk.isPresent()) {
                        chunk = new ChunkInfo(cpos);
                        chunkInfos.add(chunk);
                    } else {
                        chunk = optChunk.get();
                    }
                    chunk.addBiomeInfo(info);
                }
            }
        } catch (IOException e) {
            logger.error("Read image exception occur");
            e.printStackTrace();
            return Optional.empty();
        }

        logger.info(String.format("Read image success, chunk count: %d", chunkInfos.size()));

        return Optional.of(chunkInfos);
    }

}
