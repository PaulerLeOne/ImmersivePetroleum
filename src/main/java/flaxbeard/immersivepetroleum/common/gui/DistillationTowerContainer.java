package flaxbeard.immersivepetroleum.common.gui;

import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.INV_0;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.INV_1;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.INV_2;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.INV_3;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.TANK_INPUT;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.api.energy.IMutableEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;
import blusunrize.immersiveengineering.common.util.inventory.MultiFluidTank;
import flaxbeard.immersivepetroleum.api.crafting.DistillationTowerRecipe;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity;
import flaxbeard.immersivepetroleum.common.gui.IPSlot.FluidContainer.FluidFilter;
import flaxbeard.immersivepetroleum.common.multiblocks.DistillationTowerMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class DistillationTowerContainer extends MultiblockAwareGuiContainer<DistillationTowerTileEntity>{

	public final MultiFluidTank[] tanks;
	public final IMutableEnergyStorage energyStorage;

	public static DistillationTowerContainer makeServer (MenuType<?> type, int id, Inventory invPlayer, DistillationTowerTileEntity be){
		return new DistillationTowerContainer(blockCtx(type,id,be), invPlayer, new ItemStackHandler(be.inventory), be.tanks, be.energyStorage);
	}

	public static DistillationTowerContainer makeClient (MenuType<?> type, int id, Inventory invPlayer){
		return new DistillationTowerContainer(
				clientCtx(type,id),
				invPlayer,
				new ItemStackHandler(DistillationTowerTileEntity.NUM_SLOTS),
				DistillationTowerTileEntity.makeTanks(),
				new MutableEnergyStorage(DistillationTowerTileEntity.ENERGY_CAPACITY));
	}


	public DistillationTowerContainer(MenuContext ctx, Inventory playerInventory, ItemStackHandler inv, MultiFluidTank[] tanks, IMutableEnergyStorage energyStorage){
		super(ctx);
		this.tanks = tanks;
		this.energyStorage = energyStorage;
		addSlot(new IPSlot(inv, INV_0, 12, 17){
			@Override
			public boolean mayPlace(@Nonnull ItemStack stack){
				return FluidUtil.getFluidHandler(stack).map(h -> {
					if(h.getTanks() <= 0){
						return false;
					}
					
					FluidStack fs = h.getFluidInTank(0);
					if(fs.isEmpty() || (tanks[TANK_INPUT].getFluidAmount() > 0 && !fs.isFluidEqual(tanks[TANK_INPUT].getFluid()))){
						return false;
					}
					
					DistillationTowerRecipe recipe = DistillationTowerRecipe.findRecipe(fs);
					return recipe != null;
				}).orElse(false);
			}
		});
		addSlot(new IPSlot.ItemOutput(inv, INV_1, 12, 53));
		
		addSlot(new IPSlot.FluidContainer(inv, INV_2, 134, 17, FluidFilter.EMPTY));
		addSlot(new IPSlot.ItemOutput(inv, INV_3, 134, 53));
		
		this.ownSlotCount = 4;
		
		addPlayerInventorySlots(playerInventory, 8, 85);
		addPlayerHotbarSlots(playerInventory, 8, 143);

		addGenericData(GenericContainerData.energy(energyStorage));
		for(int i = 0; i<2; i++ ){
			final MultiFluidTank tank = tanks[i];
			addGenericData(new GenericContainerData<>(GenericDataSerializers.FLUID_STACKS, new GetterAndSetter<List<FluidStack>>(()->tank.fluids, a->tank.fluids = a)));
		}
	}
}
