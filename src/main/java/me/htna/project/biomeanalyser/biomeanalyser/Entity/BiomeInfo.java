package me.htna.project.biomeanalyser.biomeanalyser.Entity;

import com.flowpowered.math.vector.Vector3i;
import lombok.Getter;
import org.spongepowered.api.world.biome.BiomeType;

public class BiomeInfo {

    @Getter
    private BiomeType type;

    @Getter
    private int x;

    @Getter
    private int z;

    public BiomeInfo(BiomeType type, int x, int z) {
        this.type = type;
        this.x = x;
        this.z = z;
    }
}
