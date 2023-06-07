package flaxbeard.immersivepetroleum.client.gui;

import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.CHAMBER_A;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.CHAMBER_B;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.TANK_INPUT;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.TANK_OUTPUT;

import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import flaxbeard.immersivepetroleum.client.gui.elements.CokerChamberInfoArea;
import flaxbeard.immersivepetroleum.client.gui.elements.EnergyDisplay;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.gui.CokerUnitContainer;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CokerUnitScreen extends IEContainerScreen<CokerUnitContainer>{
	public static final ResourceLocation GUI_TEXTURE = ResourceUtils.ip("textures/gui/coker.png");
	
	CokerUnitTileEntity tile;
	public CokerUnitScreen(CokerUnitContainer inventorySlotsIn, Inventory inv, Component title){
		super(inventorySlotsIn, inv, title, GUI_TEXTURE);
		this.tile = menu.tile;
		
		this.imageWidth = 200;
		this.imageHeight = 187;
	}
	
	@Override
	protected void renderLabels(PoseStack transform, int mouseX, int mouseY){
		// Render no labels
	}
	
	@Nonnull
	@Override
	protected List<InfoArea> makeInfoAreas(){
		return List.of(
				new FluidInfoArea(
						this.tile.bufferTanks[TANK_INPUT],
						new Rect2i(this.leftPos + 32, this.topPos + 14, 16, 47),
						202, 2, 16, 47,
						GUI_TEXTURE
				),
				new FluidInfoArea(
						this.tile.bufferTanks[TANK_OUTPUT],
						new Rect2i(this.leftPos + 152, this.topPos + 14, 16, 47),
						202, 2, 16, 47,
						GUI_TEXTURE
				),
				new EnergyDisplay(this.leftPos + 168, this.topPos + 67, 7, 21, this.tile.energyStorage),
				new CokerChamberInfoArea(this.tile.chambers[CHAMBER_A], new Rect2i(this.leftPos + 74, this.topPos + 24, 6, 38)),
				new CokerChamberInfoArea(this.tile.chambers[CHAMBER_B], new Rect2i(this.leftPos + 120, this.topPos + 24, 6, 38))
		);
	}
}
