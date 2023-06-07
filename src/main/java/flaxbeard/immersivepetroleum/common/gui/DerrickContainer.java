package flaxbeard.immersivepetroleum.common.gui;

import javax.annotation.Nonnull;

import flaxbeard.immersivepetroleum.common.ExternalModContent;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.multiblocks.DerrickMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DerrickContainer extends MultiblockAwareGuiContainer<DerrickTileEntity>{
	public DerrickContainer(MenuType<?> type, int id, Inventory playerInventory, DerrickTileEntity tile){
		super(type, tile, id, DerrickMultiblock.INSTANCE);
		
		this.addSlot(new Slot(this.inv, 0, 92, 55){
			@Override
			public boolean mayPlace(@Nonnull ItemStack stack){
				return ExternalModContent.isIEItem_Pipe(stack);
			}
		});
		
		this.slotCount = 1;
		
		addPlayerInventorySlots(playerInventory, 20, 82);
		addPlayerHotbarSlots(playerInventory, 20, 140);
	}
}
