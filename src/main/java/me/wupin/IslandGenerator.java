package me.wupin;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class IslandGenerator extends ChunkGenerator {
    private final SuperiorSkyblock plugin;

    public IslandGenerator(SuperiorSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        PerlinOctaveGenerator generator = new PerlinOctaveGenerator(new Random(worldInfo.getSeed()), 8);
        generator.setScale(0.05);
        int worldX = chunkX * 16;
        int worldZ = chunkZ * 16;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 98; y++) {
                    if (chunkData.getType(x, y, z).isAir()) {
                        chunkData.setBlock(x, y, z, Material.WATER);
                    }
                }

                // normalised noise value between -1 and 1
                double noise = generator.noise(worldX + x, worldZ + z, .5, .5) * 5;
                int height = (int) (noise + 10);
                if (height > chunkData.getMaxHeight())
                    height = chunkData.getMaxHeight();

                // set blocks to stone with a top layer of sand
                for (int y = chunkData.getMinHeight(); y <= height; y++) {
                    if (y == height) {
                        if (random.nextDouble() < 0.1)
                            chunkData.setBlock(x, y, z, Material.SEAGRASS);
                    } else if (y <= height - 4) {
                        chunkData.setBlock(x, y, z, Material.STONE);
                    } else {
                        chunkData.setBlock(x, y, z, Material.SAND);
                    }
                }
            }
        }
    }

    @Override
    public void generateBedrock(WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, ChunkGenerator.ChunkData chunkData) {
        if (chunkData.getMinHeight() == worldInfo.getMinHeight()) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.BEDROCK);
                }
            }
        }
    }

    @Override
    public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return new BiomeProvider() {
            @Override
            public @NotNull
            Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
                return Biome.WARM_OCEAN;
            }

            @Override
            public @NotNull
            List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
                return List.of(Biome.WARM_OCEAN);
            }

        };
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.emptyList();
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 100, 0);
    }
}