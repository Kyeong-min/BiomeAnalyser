package me.htna.project.biomeanalyser.biomeanalyser;

import lombok.Builder;
import lombok.Getter;
import lombok.var;
import me.htna.project.biomeanalyser.biomeanalyser.Entity.BiomeInfo;
import org.spongepowered.api.world.biome.BiomeType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ImageControl {

    private String outputPath;

    private Map<String, List<BiomeInfo>> infos;

    private int width;

    private int height;

    private int offsetX;

    private int offsetY;

    @Builder
    public ImageControl(String path,
                        int width,
                        int height,
                        int offsetX,
                        int offsetY,
                        Map<String, List<BiomeInfo>> infos) {

        this.outputPath = path;
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.infos = infos;
    }

    public boolean save() {

        var logger = BiomeAnalyser.getInstance().getLogger();
        logger.info("CheckPoint 05");
        infos.forEach((k, v) -> {
            try {
                logger.info(
                        String.format("Draw biome analysed info, width: %d, height: %d, type: %s, offset x: %d, offset y: %d",
                        width, height, k, offsetX, offsetY)
                );
                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setColor(Color.RED);

                for (BiomeInfo b : v) {
                    g.drawRect(b.getX() + offsetX, b.getZ() + offsetY, 1, 1);
                }
                    /*
                v.stream().forEach(info -> {
                    StringBuffer sb2 = new StringBuffer()
                            .append("drawRect x: ").append(info.getX()).append(", z: ").append(info.getZ())
                            .append(", image x: ").append(info.getX() + offsetX).append(", image y: ").append(info.getZ() + offsetY);
                    logger.debug(sb2.toString());
                    g.drawRect(info.getX() + offsetX, info.getZ() + offsetY, 1, 1);
                });
                     */

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

}
