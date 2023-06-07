package flaxbeard.immersivepetroleum.common.gui;

import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.INV_0;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.INV_1;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.INV_2;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.INV_3;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.TANK_INPUT;

import javax.annotation.Nonnull;

import flaxbeard.immersivepetroleum.api.crafting.DistillationTowerRecipe;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity;
import flaxbeard.immersivepetroleum.common.gui.IPSlot.FluidContainer.FluidFilter;
import flaxbeard.immersivepetroleum.common.multiblocks.DistillationTowerMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class DistillationTowerContainer extends MultiblockAwareGuiContainer<DistillationTowerTileEntity>{
	public DistillationTowerContainer(MenuType<?> type, int id, Inventory playerInventory, final DistillationTowerTileEntity tile){
		super(type, tile, id, DistillationTowerMultiblock.INSTANCE);
		
		addSlot(new IPSlot(this.inv, INV_0, 12, 17){
			@Override
			public boolean mayPlace(@Nonnull ItemStack stack){
				return FluidUtil.getFluidHandler(stack).map(h -> {
					if(h.getTanks() <= 0){
						return false;
					}
					
					FluidStack fs = h.getFluidInTank(0);
					if(fs.isEmpty() || (tile.tanks[TANK_INPUT].getFluidAmount() > 0 && !fs.isFluidEqual(tile.tanks[TANK_INPUT].getFluid()))){
						return false;
					}
					
					DistillationTowerRecipe recipe = DistillationTowerRecipe.findRecipe(fs);
					return recipe != null;
				}).orElse(false);
			}
		});
		addSlot(new IPSlot.ItemOutput(this.inv, INV_1, 12, 53));
		
		addSlot(new IPSlot.FluidContainer(this.inv, INV_2, 134, 17, FluidFilter.EMPTY));
		addSlot(new IPSlot.ItemOutput(this.inv, INV_3, 134, 53));
		
		this.slotCount = 4;
		
		addPlayerInventorySlots(playerInventory, 8, 85);
		addPlayerHotbarSlots(playerInventory, 8, 143);
	}
}
