package flaxbeard.immersivepetroleum.common.gui;

import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.TANK_INPUT;

import blusunrize.immersiveengineering.api.energy.IMutableEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers;
import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.gui.IPSlot.FluidContainer.FluidFilter;
import flaxbeard.immersivepetroleum.common.multiblocks.CokerUnitMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CokerUnitContainer extends MultiblockAwareGuiContainer<CokerUnitTileEntity>{

	public final FluidTank[] bufferTanks;
	public final CokerUnitTileEntity.CokingChamber[] chambers;
	public final IMutableEnergyStorage energyStorage;

	public static CokerUnitContainer makeServer(MenuType<?> type, int id, Inventory invPlayer,CokerUnitTileEntity be){
		return new CokerUnitContainer(blockCtx(type, id, be), invPlayer, new ItemStackHandler(be.inventory),
				be.bufferTanks, be.chambers, be.energyStorage);
	}

	public static CokerUnitContainer makeClient(MenuType<?> type, int id, Inventory invPlayer){
		return new CokerUnitContainer(
				clientCtx(type,id),
				invPlayer,
				new ItemStackHandler(CokerUnitTileEntity.Inventory.values().length),
				CokerUnitTileEntity.makeTanks(),
				CokerUnitTileEntity.makeChambers(),
				new MutableEnergyStorage(CokerUnitTileEntity.ENERGY_CAPACITY));
	}

	public CokerUnitContainer(MenuContext ctx, Inventory playerInventory, IItemHandler inv, FluidTank[] bufferTanks, CokerUnitTileEntity.CokingChamber[] chambers, IMutableEnergyStorage energyStorage){
		super(ctx);
		this.bufferTanks = bufferTanks;
		this.chambers = chambers;
		this.energyStorage = energyStorage;

		addSlot(new IPSlot.CokerInput(this, inv, CokerUnitTileEntity.Inventory.INPUT.id(), 20, 71));
		addSlot(new IPSlot(inv, CokerUnitTileEntity.Inventory.INPUT_FILLED.id(), 9, 14, stack -> {
			return FluidUtil.getFluidHandler(stack).map(h -> {
				if(h.getTanks() <= 0 || h.getFluidInTank(0).isEmpty()){
					return false;
				}
				
				FluidStack fs = h.getFluidInTank(0);
				if(fs.isEmpty() || (bufferTanks[TANK_INPUT].getFluidAmount() > 0 && !fs.isFluidEqual(bufferTanks[TANK_INPUT].getFluid()))){
					return false;
				}
				
				return CokerUnitRecipe.hasRecipeWithInput(fs, true);
			}).orElse(false);
		}));
		addSlot(new IPSlot.ItemOutput(inv, CokerUnitTileEntity.Inventory.INPUT_EMPTY.id(), 9, 45));
		
		addSlot(new IPSlot.FluidContainer(inv, CokerUnitTileEntity.Inventory.OUTPUT_EMPTY.id(), 175, 14, FluidFilter.EMPTY));
		addSlot(new IPSlot.ItemOutput(inv, CokerUnitTileEntity.Inventory.OUTPUT_FILLED.id(), 175, 45));
		
		ownSlotCount = CokerUnitTileEntity.Inventory.values().length;
		
		addPlayerInventorySlots(playerInventory, 20, 105);
		addPlayerHotbarSlots(playerInventory, 20, 163);

		addGenericData(GenericContainerData.energy(energyStorage));
		for (int i = 0; i<2; i++){
			addGenericData(GenericContainerData.fluid(bufferTanks[i]));
		}
		for (int i = 0; i<2; i++){
			addGenericData(GenericContainerData.fluid(chambers[i].getTank()));
		}
	}
}
