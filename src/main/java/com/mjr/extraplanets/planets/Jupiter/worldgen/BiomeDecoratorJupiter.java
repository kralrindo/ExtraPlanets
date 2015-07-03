package com.mjr.extraplanets.planets.Jupiter.worldgen;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;

import com.mjr.extraplanets.blocks.ExtraPlanetsBlocks;
import com.mjr.extraplanets.blocks.fluid.ExtraPlanetsFluids;
import com.mjr.extraplanets.worldGen.WorldGenCustomLakes;

public class BiomeDecoratorJupiter extends BiomeDecoratorSpace {

    private WorldGenerator deshGen;
    private WorldGenerator tinGen;
    private WorldGenerator copperGen;
    private WorldGenerator ironGen;
    private WorldGenerator palladiumGen;
    private WorldGenerator nickelGen;
    private WorldGenerator gravelGen;

    private int LakesPerChunk = 5;

    private World currentWorld;

    public BiomeDecoratorJupiter()
    {
	this.copperGen = new WorldGenMinableMeta(ExtraPlanetsBlocks.jupiterOreCopper, 4, 0, false, ExtraPlanetsBlocks.jupiterStone, 1);
	this.tinGen = new WorldGenMinableMeta(ExtraPlanetsBlocks.jupiterOreIron, 4, 0, false, ExtraPlanetsBlocks.jupiterStone, 1);
	this.ironGen = new WorldGenMinableMeta(ExtraPlanetsBlocks.jupiterOreIron, 8, 0, false, ExtraPlanetsBlocks.jupiterStone, 1);
	this.palladiumGen = new WorldGenMinableMeta(ExtraPlanetsBlocks.OrePalladium, 8, 0, false, ExtraPlanetsBlocks.jupiterStone, 1);
	this.nickelGen = new WorldGenMinableMeta(ExtraPlanetsBlocks.OreNickel, 8, 0, false, ExtraPlanetsBlocks.jupiterStone, 1);
	this.gravelGen = new WorldGenMinableMeta(ExtraPlanetsBlocks.jupiterGravel, 12, 0, false, ExtraPlanetsBlocks.jupiterStone, 1);

	//WorldGenMinableMeta(Block OreBlock, int numberOfBlocks, int OreMeta, boolean usingMetaData, Block StoneBlock, int StoneMeta);
    }

    @Override
    protected void setCurrentWorld(World world) {
	this.currentWorld = world;
    }

    @Override
    protected World getCurrentWorld() {
	return this.currentWorld;
    }

    @Override
    protected void decorate() {
	this.generateOre(26, this.copperGen, 0, 60);
	this.generateOre(23, this.tinGen, 0, 60);
	this.generateOre(20, this.ironGen, 0, 64);
	this.generateOre(20, this.palladiumGen, 0, 64);
	this.generateOre(20, this.nickelGen, 0, 64);
	this.generateOre(15, this.gravelGen, 0, 80);
	
	MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(this.currentWorld, this.rand, this.chunkX, this.chunkZ));
	for (int i = 0; i < this.LakesPerChunk; i++) {
	    if (this.rand.nextInt(10) == 0) {
		int x = this.chunkX + this.rand.nextInt(16) + 8;
		// int y = this.rand.nextInt(16) + 16;
		int z = this.chunkZ + this.rand.nextInt(16) + 8;
		int y = this.currentWorld.getHeightValue(x, z);
		new WorldGenCustomLakes(ExtraPlanetsFluids.magma).generate(this.currentWorld, this.rand, x, y, z, ExtraPlanetsBlocks.jupiterStone);
	    }
	}
	MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(this.currentWorld, this.rand, this.chunkX, this.chunkZ));

	//generateOre(int amountPerChunk, WorldGenerator worldGenerator, int minY, int maxY);
    }

}
