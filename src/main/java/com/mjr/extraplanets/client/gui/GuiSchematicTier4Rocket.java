package com.mjr.extraplanets.client.gui;

import micdoodle8.mods.galacticraft.api.recipe.ISchematicResultPage;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.mjr.extraplanets.Constants;
import com.mjr.extraplanets.inventory.ContainerSchematicTier4Rocket;

public class GuiSchematicTier4Rocket extends GuiContainer implements ISchematicResultPage {
	private static final ResourceLocation rocketBenchTexture = new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/rocketbenchTier4.png");

	private int pageIndex;

	public GuiSchematicTier4Rocket(InventoryPlayer par1InventoryPlayer, BlockPos pos) {
		super(new ContainerSchematicTier4Rocket(par1InventoryPlayer, pos));
		this.ySize = 238;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 130, this.height / 2 - 30 + 27 - 12, 40, 20, GCCoreUtil.translate("gui.button.back.name")));
		this.buttonList.add(new GuiButton(1, this.width / 2 - 130, this.height / 2 - 30 + 27 + 12, 40, 20, GCCoreUtil.translate("gui.button.next.name")));
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.enabled) {
			switch (par1GuiButton.id) {
			case 0:
				SchematicRegistry.flipToLastPage(this.pageIndex);
				break;
			case 1:
				SchematicRegistry.flipToNextPage(this.pageIndex);
				break;
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRendererObj.drawString(GCCoreUtil.translate("schematic.rocketT4.name"), 7, -20 + 27, 4210752);
		this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, 220 - 104 + 2 + 27, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(GuiSchematicTier4Rocket.rocketBenchTexture);
		final int var5 = (this.width - this.xSize) / 2;
		final int var6 = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void setPageIndex(int index) {
		this.pageIndex = index;
	}
}