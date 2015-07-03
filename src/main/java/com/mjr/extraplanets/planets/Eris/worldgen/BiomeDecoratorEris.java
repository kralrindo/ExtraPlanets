package com.mjr.extraplanets.planets.Eris.worldgen;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.mjr.extraplanets.blocks.ExtraPlanetsBlocks;

public class BiomeDecoratorEris extends BiomeDecoratorSpace {

    private WorldGenerator copperGen;
    private WorldGenerator tinGen;
    private WorldGenerator ironGen;
    private WorldGenerator gravelGen;

    private World currentWorld;

    public BiomeDecoratorEris()
    {
	this.copperGen = new WorldGenMinableMeta(ExtraPlanetsBlocks.erisOreCopper, 4, 0, false, ExtraPlanetsBlocks.erisStone, 1);
	this.tinGen = new WorldGenMinableMeta(ExtraPlanetsBlocks.erisOreTin, 4, 0, false, ExtraPlanetsBlocks.erisStone, 1);
	this.ironGen = new WorldGenMinableMeta(ExtraPlanetsBlocks.erisOreIron, 8, 0, false, ExtraPlanetsBlocks.erisStone, 1);
	this.gravelGen = new WorldGenMinableMeta(ExtraPlanetsBlocks.erisGravel, 12, 0, false, ExtraPlanetsBlocks.erisStone, 1);

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
	this.generateOre(15, this.gravelGen, 0, 80);

	//generateOre(int amountPerChunk, WorldGenerator worldGenerator, int minY, int maxY);
    }
}
