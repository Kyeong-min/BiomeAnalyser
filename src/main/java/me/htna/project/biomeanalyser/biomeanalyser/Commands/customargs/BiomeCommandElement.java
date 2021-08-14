package me.htna.project.biomeanalyser.biomeanalyser.Commands.customargs;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.gen.BiomeGenerator;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeCommandElement extends CommandElement {


    private static Map<String, BiomeType> biomeNameList;

    public BiomeCommandElement(Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {

        if (biomeNameList == null) {
            try {
                biomeNameList = new HashMap<String, BiomeType>();
                Field[] fields = BiomeTypes.class.getFields();
                for (Field field : fields) {
                    BiomeType type = (BiomeType)field.get(null);
                    biomeNameList.put(type.getName().toLowerCase(), type);
                }
            } catch (IllegalAccessException e) {
                throw args.createError(Text.of("Internal server error - Failed create biome list"));
            }
        }

        String biomeName = args.next();
        if (!biomeNameList.containsKey(biomeName.toLowerCase())) {
            throw args.createError(Text.of("Invalid biome type"));
        }

        return biomeNameList.get(biomeName.toLowerCase());
    }
    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Collections.emptyList();
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of("<Biome type>");
    }
}
