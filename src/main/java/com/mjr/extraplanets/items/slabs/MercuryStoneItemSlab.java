package com.mjr.extraplanets.items.slabs;

import com.mjr.extraplanets.Constants;
import com.mjr.extraplanets.blocks.ExtraPlanetsBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;

public class MercuryStoneItemSlab extends ItemSlab {

    public MercuryStoneItemSlab(Block block) {
	super(block, ((BlockSlab) (ExtraPlanetsBlocks.mercuryStoneBlockHalfSlab)),
		((BlockSlab) (ExtraPlanetsBlocks.mercuryStoneBlockDoubleSlab)), false);
	this.setMaxDamage(0);
	this.setHasSubtypes(true);
	setUnlocalizedName(Constants.TEXTURE_PREFIX + "mercuryStone");
    }

}