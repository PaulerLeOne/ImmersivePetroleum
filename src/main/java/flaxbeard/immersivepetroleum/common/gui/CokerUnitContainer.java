package flaxbeard.immersivepetroleum.common.gui;

import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.TANK_INPUT;

import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.gui.IPSlot.FluidContainer.FluidFilter;
import flaxbeard.immersivepetroleum.common.multiblocks.CokerUnitMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class CokerUnitContainer extends MultiblockAwareGuiContainer<CokerUnitTileEntity>{
	public CokerUnitContainer(MenuType<?> type, int id, Inventory playerInventory, final CokerUnitTileEntity tile){
		super(type, tile, id, CokerUnitMultiblock.INSTANCE);
		
		addSlot(new IPSlot.CokerInput(this, this.inv, CokerUnitTileEntity.Inventory.INPUT.id(), 20, 71));
		addSlot(new IPSlot(this.inv, CokerUnitTileEntity.Inventory.INPUT_FILLED.id(), 9, 14, stack -> {
			return FluidUtil.getFluidHandler(stack).map(h -> {
				if(h.getTanks() <= 0 || h.getFluidInTank(0).isEmpty()){
					return false;
				}
				
				FluidStack fs = h.getFluidInTank(0);
				if(fs.isEmpty() || (tile.bufferTanks[TANK_INPUT].getFluidAmount() > 0 && !fs.isFluidEqual(tile.bufferTanks[TANK_INPUT].getFluid()))){
					return false;
				}
				
				return CokerUnitRecipe.hasRecipeWithInput(fs, true);
			}).orElse(false);
		}));
		addSlot(new IPSlot.ItemOutput(this.inv, CokerUnitTileEntity.Inventory.INPUT_EMPTY.id(), 9, 45));
		
		addSlot(new IPSlot.FluidContainer(this.inv, CokerUnitTileEntity.Inventory.OUTPUT_EMPTY.id(), 175, 14, FluidFilter.EMPTY));
		addSlot(new IPSlot.ItemOutput(this.inv, CokerUnitTileEntity.Inventory.OUTPUT_FILLED.id(), 175, 45));
		
		this.slotCount = CokerUnitTileEntity.Inventory.values().length;
		
		addPlayerInventorySlots(playerInventory, 20, 105);
		addPlayerHotbarSlots(playerInventory, 20, 163);
	}
}
