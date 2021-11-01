package me.htna.project.biomeanalyser.biomeanalyser;

import com.flowpowered.math.vector.Vector2i;
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

    private Vector2i blockCoordiToImageCoordi(int x, int z) {
        return Vector2i.from(x + offsetX, z + offsetY);
    }

    private Vector2i imageCoordiToBlockCoordi(int x, int y) {
        return Vector2i.from(x - offsetX, y - offsetY);
    }

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

    /**
     * 바이옴 정보를 이미지로 저장합니다.
     *
     * @param infos 바이옴 정보
     * @return 성공시 true, 실패시 false
     */
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
     * 청크 단위로 이미지로부터 바이옴 정보를 읽습니다.
     * @param image {@link BufferedImage}
     * @param type 바이옴 타입
     * @param cinfo 청크 정보
     * @return 청크 정보
     */
    public ChunkInfo loadByChunk(BufferedImage image, BiomeType type, ChunkInfo cinfo) {

        Vector3i lt = cinfo.getLTBlockCoordinate();
        Vector3i rb = cinfo.getRBBlockCoordinate();

        Vector2i ltImageCoordi = blockCoordiToImageCoordi(lt.getX(), lt.getZ());
        Vector2i rbImageCoordi = blockCoordiToImageCoordi(rb.getX(), rb.getZ());

        Logger logger = BiomeAnalyser.getInstance().getLogger();
        logger.debug(String.format("loadByChunk chunk: %s, block lt: %s, rb: %s, image lt: %s, rb: %s", cinfo, lt, rb, ltImageCoordi, rbImageCoordi));

        for (int y = ltImageCoordi.getY(); y <= rbImageCoordi.getY(); y++) {
            for (int x = ltImageCoordi.getX(); x <= rbImageCoordi.getX(); x++) {
                Color color = new Color(image.getRGB(x, y), true);
                int alpha = color.getAlpha();
                if (alpha < 128)
                    continue;

                Vector2i bc = imageCoordiToBlockCoordi(x, y);
                cinfo.addBiomeInfo(new BiomeInfo(type, bc.getX(), bc.getY()));
            }
        }

        return cinfo;
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
        // 이미지 크기에 해당하는 청크 정보를 생성
        Vector2i ltBlockCoordi = imageCoordiToBlockCoordi(0, 0);
        Vector2i rbBlockCoordi = imageCoordiToBlockCoordi(width - 1, height - 1);
        ChunkInfo ltChunk = ChunkInfo.fromBlock(Vector3i.from(ltBlockCoordi.getX(), 0, ltBlockCoordi.getY()));
        ChunkInfo rbChunk = ChunkInfo.fromBlock(Vector3i.from(rbBlockCoordi.getX(), 0, rbBlockCoordi.getY()));
        logger.info(String.format("lt: %s, rb: %s", ltChunk, rbChunk));

        ArrayList<ChunkInfo> cinfos = new ArrayList<>();
        // 청크별로 이미지로부터 바이옴 정보를 읽어옴 (병렬처리)
        for (int cz = ltChunk.getCz(); cz <= rbChunk.getCz(); cz++) {
            for (int cx = ltChunk.getCx(); cx <= rbChunk.getCx(); cx++) {
                ChunkInfo c = new ChunkInfo();
                c.setPosition(cx, cz);
                cinfos.add(c);
            }
        }

        try {
            BufferedImage img = ImageIO.read(file);
            cinfos.stream().parallel().forEach(c -> {
                loadByChunk(img, type, c);
            });
        } catch (IOException e) {
            logger.error("Read image exception occur");
            e.printStackTrace();
        }

        logger.info(String.format("Read image success, chunk count: %d", cinfos.size()));
        return Optional.of(cinfos);
    }
}
