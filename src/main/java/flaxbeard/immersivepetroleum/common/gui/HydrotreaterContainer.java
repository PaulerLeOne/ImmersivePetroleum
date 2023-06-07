package flaxbeard.immersivepetroleum.common.gui;

import flaxbeard.immersivepetroleum.common.blocks.tileentities.HydrotreaterTileEntity;
import flaxbeard.immersivepetroleum.common.multiblocks.HydroTreaterMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class HydrotreaterContainer extends MultiblockAwareGuiContainer<HydrotreaterTileEntity>{
	public HydrotreaterContainer(MenuType<?> type, int id, Inventory playerInventory, final HydrotreaterTileEntity tile){
		super(type, tile, id, HydroTreaterMultiblock.INSTANCE);
	}
}
