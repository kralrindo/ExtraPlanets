package com.mjr.extraplanets.client.handlers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.FluidUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import micdoodle8.mods.galacticraft.planets.venus.ConfigManagerVenus;
import micdoodle8.mods.galacticraft.planets.venus.client.FakeLightningBoltRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mjr.extraplanets.Config;
import com.mjr.extraplanets.ExtraPlanets;
import com.mjr.extraplanets.blocks.fluid.ExtraPlanets_Fluids;
import com.mjr.extraplanets.client.gui.overlay.OverlayElectricLaunchCountdown;
import com.mjr.extraplanets.client.gui.overlay.OverlayJupiterLander;
import com.mjr.extraplanets.client.gui.overlay.OverlayMercuryLander;
import com.mjr.extraplanets.client.gui.overlay.OverlayNeptuneLander;
import com.mjr.extraplanets.client.gui.overlay.OverlayPressure;
import com.mjr.extraplanets.client.gui.overlay.OverlaySaturnLander;
import com.mjr.extraplanets.client.gui.overlay.OverlaySolarRadiation;
import com.mjr.extraplanets.client.gui.overlay.OverlayUranusLander;
import com.mjr.extraplanets.client.handlers.capabilities.CapabilityStatsClientHandler;
import com.mjr.extraplanets.client.handlers.capabilities.IStatsClientCapability;
import com.mjr.extraplanets.entities.landers.EntityJupiterLander;
import com.mjr.extraplanets.entities.landers.EntityMercuryLander;
import com.mjr.extraplanets.entities.landers.EntityNeptuneLander;
import com.mjr.extraplanets.entities.landers.EntitySaturnLander;
import com.mjr.extraplanets.entities.landers.EntityUranusLander;
import com.mjr.extraplanets.entities.rockets.EntityElectricRocketBase;
import com.mjr.extraplanets.network.ExtraPlanetsPacketHandler;
import com.mjr.extraplanets.network.PacketSimpleEP;
import com.mjr.extraplanets.network.PacketSimpleEP.EnumSimplePacket;
import com.mjr.extraplanets.planets.Jupiter.WorldProviderJupiter;
import com.mjr.extraplanets.world.CustomWorldProviderSpace;

public class MainHandlerClient {

	private static List<ExtraPlanetsPacketHandler> packetHandlers = Lists.newCopyOnWriteArrayList();
	private Map<BlockPos, Integer> lightning = Maps.newHashMap();

	public static void addPacketHandler(ExtraPlanetsPacketHandler handler) {
		MainHandlerClient.packetHandlers.add(handler);
	}

	@SubscribeEvent
	public void worldUnloadEvent(WorldEvent.Unload event) {
		for (ExtraPlanetsPacketHandler packetHandler : packetHandlers) {
			packetHandler.unload(event.world);
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		final Minecraft minecraft = FMLClientHandler.instance().getClient();
		final WorldClient world = minecraft.theWorld;
		final EntityPlayerSP player = minecraft.thePlayer;

		if (event.phase == Phase.END) {
			if (world != null) {
				for (ExtraPlanetsPacketHandler handler : packetHandlers) {
					handler.tick(world);
				}
			}
		}
		if (event.phase == Phase.START && player != null) {
			boolean isPressed = KeyHandlerClient.spaceKey.isPressed();
			if (!isPressed) {
				ClientProxyCore.lastSpacebarDown = false;
			}

			if (player.ridingEntity != null && isPressed) {
				ExtraPlanets.packetPipeline.sendToServer(new PacketSimpleEP(EnumSimplePacket.S_IGNITE_ROCKET, GCCoreUtil.getDimensionID(player.worldObj), new Object[] {}));
				ClientProxyCore.lastSpacebarDown = true;
			}
		}
	}

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		final Minecraft minecraft = FMLClientHandler.instance().getClient();
		final EntityPlayerSP player = minecraft.thePlayer;
		final EntityPlayerSP playerBaseClient = PlayerUtil.getPlayerBaseClientFromPlayer(player, false);
		if (player != null && player.worldObj.provider instanceof IGalacticraftWorldProvider && OxygenUtil.shouldDisplayTankGui(minecraft.currentScreen) && OxygenUtil.noAtmosphericCombustion(player.worldObj.provider)
				&& !playerBaseClient.isSpectator() && !minecraft.gameSettings.showDebugInfo) {
			if ((player.worldObj.provider instanceof CustomWorldProviderSpace)) {
				CustomWorldProviderSpace provider = (CustomWorldProviderSpace) player.worldObj.provider;

				if (Config.PRESSURE) {
					int pressureLevel = provider.getPressureLevel();
					OverlayPressure.renderPressureIndicator(pressureLevel, !ConfigManagerCore.oxygenIndicatorLeft, !ConfigManagerCore.oxygenIndicatorBottom);
				}
				if (Config.RADIATION) {
					IStatsClientCapability stats = null;

					if (player != null) {
						stats = playerBaseClient.getCapability(CapabilityStatsClientHandler.EP_STATS_CLIENT_CAPABILITY, null);
					}
					int radiationLevel = (int) Math.floor(stats.getRadiationLevel());
					OverlaySolarRadiation.renderSolarRadiationIndicator(radiationLevel, !ConfigManagerCore.oxygenIndicatorLeft, !ConfigManagerCore.oxygenIndicatorBottom);
				}
			}
		}
		if (minecraft.currentScreen == null && player.ridingEntity instanceof EntityElectricRocketBase && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI
				&& !((EntityElectricRocketBase) minecraft.thePlayer.ridingEntity).getLaunched()) {
			OverlayElectricLaunchCountdown.renderCountdownOverlay();
		}
		if (minecraft.currentScreen == null && player.ridingEntity instanceof EntityJupiterLander && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI) {
			OverlayJupiterLander.renderLanderOverlay();
		}
		if (minecraft.currentScreen == null && player.ridingEntity instanceof EntitySaturnLander && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI) {
			OverlaySaturnLander.renderLanderOverlay();
		}
		if (minecraft.currentScreen == null && player.ridingEntity instanceof EntityMercuryLander && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI) {
			OverlayMercuryLander.renderLanderOverlay();
		}
		if (minecraft.currentScreen == null && player.ridingEntity instanceof EntityUranusLander && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI) {
			OverlayUranusLander.renderLanderOverlay();
		}
		if (minecraft.currentScreen == null && player.ridingEntity instanceof EntityNeptuneLander && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI) {
			OverlayNeptuneLander.renderLanderOverlay();
		}
	}

	@SubscribeEvent
	public void renderLightning(ClientProxyCore.EventSpecialRender event) {
		final Minecraft minecraft = FMLClientHandler.instance().getClient();
		final EntityPlayerSP player = minecraft.thePlayer;
		if (player != null && !ConfigManagerVenus.disableAmbientLightning) {
			Iterator<Map.Entry<BlockPos, Integer>> it = lightning.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<BlockPos, Integer> entry = it.next();
				long seed = entry.getValue() / 10 + entry.getKey().getX() + entry.getKey().getZ();
				FakeLightningBoltRenderer.renderBolt(seed, entry.getKey().getX() - ClientProxyCore.playerPosX, entry.getKey().getY() - ClientProxyCore.playerPosY, entry.getKey().getZ() - ClientProxyCore.playerPosZ);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		final Minecraft minecraft = FMLClientHandler.instance().getClient();
		final EntityPlayerSP player = minecraft.thePlayer;

		if (player == event.player) {
			if (!ConfigManagerVenus.disableAmbientLightning) {
				Iterator<Map.Entry<BlockPos, Integer>> it = lightning.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<BlockPos, Integer> entry = it.next();
					int val = entry.getValue();
					if (val - 1 <= 0) {
						it.remove();
					} else {
						entry.setValue(val - 1);
					}
				}

				if (player.getRNG().nextInt(100) == 0 && minecraft.theWorld.provider instanceof WorldProviderJupiter) {
					double freq = player.getRNG().nextDouble() * Math.PI * 2.0F;
					double dist = 180.0F;
					double dX = dist * Math.cos(freq);
					double dZ = dist * Math.sin(freq);
					double posX = player.posX + dX;
					double posY = 70;
					double posZ = player.posZ + dZ;
					minecraft.theWorld.playSound(posX, posY, posZ, "ambient.weather.thunder", 1000.0F, 1.0F + player.getRNG().nextFloat() * 0.2F, false);
					lightning.put(new BlockPos(posX, posY, posZ), 20);
				}
			}
		}
	}

	@SubscribeEvent
	public void onToolTip(ItemTooltipEvent event) {
		ItemStack stack = event.itemStack;
		if (stack != null && stack.getItem() != null && stack.getItem() instanceof UniversalBucket) {
			if (FluidUtil.getFluidContained(stack) != null && FluidUtil.getFluidContained(stack).getFluid() != null) {
				FluidStack fluidStack = FluidUtil.getFluidContained(stack);
				Fluid fluid = fluidStack.getFluid();
				if (fluid.equals(ExtraPlanets_Fluids.FROZEN_WATER_FLUID) || fluid.equals(ExtraPlanets_Fluids.GLOWSTONE_FLUID) || fluid.equals(ExtraPlanets_Fluids.INFECTED_WATER_FLUID) || fluid.equals(ExtraPlanets_Fluids.LIQUID_HYDROCARBON_FLUID)
						|| fluid.equals(ExtraPlanets_Fluids.MAGMA_FLUID) || fluid.equals(ExtraPlanets_Fluids.METHANE_FLUID) || fluid.equals(ExtraPlanets_Fluids.NITROGEN_FLUID) || fluid.equals(ExtraPlanets_Fluids.RADIO_ACTIVE_WATER_FLUID)
						|| fluid.equals(ExtraPlanets_Fluids.SALT_FLUID)) {
					event.toolTip.add(EnumColor.AQUA + GCCoreUtil.translate("gui.bucket.message.finding"));
					event.toolTip.add(EnumColor.AQUA + GCCoreUtil.translate("gui.bucket.message.finding.2"));
				} else if (fluid.equals(ExtraPlanets_Fluids.CLEAN_WATER_FLUID))
					event.toolTip.add(EnumColor.ORANGE + GCCoreUtil.translate("gui.bucket.message.crafting"));
			}
		}
	}
}
