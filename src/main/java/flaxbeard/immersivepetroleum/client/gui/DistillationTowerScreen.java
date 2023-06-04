package flaxbeard.immersivepetroleum.client.gui;

import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.client.gui.info.MultitankArea;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity;
import flaxbeard.immersivepetroleum.common.gui.DistillationTowerContainer;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DistillationTowerScreen extends IEContainerScreen<DistillationTowerContainer>{
	static final ResourceLocation GUI_TEXTURE = ResourceUtils.ip("textures/gui/distillation.png");
	
	public DistillationTowerScreen(DistillationTowerContainer container, Inventory playerInventory, Component title){
		super(container, playerInventory, title, GUI_TEXTURE);
	}
	
	@Override
	protected void renderLabels(PoseStack transform, int mouseX, int mouseY){
		// Render no labels
	}
	
	@Nonnull
	@Override
	protected List<InfoArea> makeInfoAreas(){
		return List.of(
				new FluidInfoArea(menu.tanks[0], new Rect2i(leftPos + 62, topPos + 21, 16, 47), 177, 31, 20, 51, GUI_TEXTURE),
				new EnergyInfoArea(leftPos + 158, topPos + 22, menu.energyStorage),
				new MultitankArea(new Rect2i(leftPos + 112, topPos + 21, 16, 47), DistillationTowerTileEntity.TANK_VOLUME, ()->menu.tanks[1].fluids)
		);
	}
}
