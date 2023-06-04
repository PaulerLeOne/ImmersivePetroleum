package flaxbeard.immersivepetroleum.common.gui;

import blusunrize.immersiveengineering.api.energy.IMutableEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.HydrotreaterTileEntity;
import flaxbeard.immersivepetroleum.common.multiblocks.HydroTreaterMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class HydrotreaterContainer extends MultiblockAwareGuiContainer<HydrotreaterTileEntity>{

	public final FluidTank[] tanks;
	public final IMutableEnergyStorage energyStorage;

	public static HydrotreaterContainer makeServer(MenuType<?> type, int id, Inventory invPlayer, HydrotreaterTileEntity be){
		return new HydrotreaterContainer(blockCtx(type,id,be), invPlayer, be.tanks, be.energyStorage);
	}

	public static HydrotreaterContainer makeClient(MenuType<?> type, int id, Inventory invPlayer){
		return new HydrotreaterContainer(
				clientCtx(type,id),
				invPlayer,
				HydrotreaterTileEntity.makeTanks(),
				new MutableEnergyStorage(HydrotreaterTileEntity.ENERGY_CAPACITY));
	}

	public HydrotreaterContainer(MenuContext ctx, Inventory playerInventory, FluidTank[] tanks, IMutableEnergyStorage energyStorage){
		super(ctx);
		this.tanks=tanks;
		this.energyStorage = energyStorage;

		addGenericData(GenericContainerData.energy(energyStorage));
		for(int i = 0; i<3; i++){
			addGenericData(GenericContainerData.fluid(tanks[i]));
		}
	}
}
