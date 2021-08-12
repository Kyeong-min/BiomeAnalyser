package me.htna.project.biomeanalyser.biomeanalyser;

import lombok.Getter;
import lombok.Setter;
import me.htna.project.biomeanalyser.biomeanalyser.Entity.ChunkPosition;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    private ChunkPosition cpos1;

    @Getter
    @Setter
    private ChunkPosition cpos2;

    private Analyser analyser;

    private boolean mutex;

    private AnalyseManager() {
        clear();
    }

    public void clear() {
        world = null;
        cpos1 = null;
        cpos2 = null;
        analyser = null;
        mutex = false;
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
        if (!optPlayer.isPresent())
            return "";

        return optPlayer.get().getName();
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
            if (analyser.write()) {
                BiomeAnalyser.getInstance().getLogger().info("Success write");
                // TODO:
            } else {
                BiomeAnalyser.getInstance().getLogger().info("Failed write");
            }
        });
    }

    public boolean load() {

        return true;
    }
}
