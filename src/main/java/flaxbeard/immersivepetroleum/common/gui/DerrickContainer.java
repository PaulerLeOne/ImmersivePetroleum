package flaxbeard.immersivepetroleum.common.gui;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.api.energy.IMutableEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers;
import flaxbeard.immersivepetroleum.client.gui.elements.PipeConfig;
import flaxbeard.immersivepetroleum.common.ExternalModContent;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.function.Supplier;

public class DerrickContainer extends MultiblockAwareGuiContainer<DerrickTileEntity>{

	public final FluidTank tank;
	public final IMutableEnergyStorage energyStorage;
	public final DerrickData data;

	public static DerrickContainer makeServer(MenuType<?> type, int id, Inventory playerInv, DerrickTileEntity be){
		return new DerrickContainer(blockCtx(type,id,be), playerInv, new ItemStackHandler(be.inventory), be.tank, be.energyStorage, DerrickData.serverData(be));
	}

	public static DerrickContainer makeClient(MenuType<?> type, int id, Inventory playerInventory){
		return new DerrickContainer(
				clientCtx(type,id),
				playerInventory,
				new ItemStackHandler(DerrickTileEntity.NUM_SLOTS),
				new FluidTank(DerrickTileEntity.TANK_CAPACITY),
				new MutableEnergyStorage(DerrickTileEntity.ENERGY_CAPACITY),
				DerrickData.clientData());
	}

	public DerrickContainer(MenuContext ctx, Inventory playerInventory, ItemStackHandler inv, FluidTank tank, IMutableEnergyStorage energyStorage, DerrickData data){
		super(ctx);
		this.tank = tank;
		this.energyStorage = energyStorage;
		this.data = data;

		addSlot(new SlotItemHandler(inv, 0, 92, 55){
			@Override
			public boolean mayPlace(@Nonnull ItemStack stack){
				return ExternalModContent.isIEItem_Pipe(stack);
			}
		});
		
		ownSlotCount = 1;
		
		addPlayerInventorySlots(playerInventory, 20, 82);
		addPlayerHotbarSlots(playerInventory, 20, 140);

		addGenericData(GenericContainerData.fluid(tank));
		addGenericData(GenericContainerData.energy(energyStorage));

		addGenericData(GenericContainerData.bool(data.isDrillingGetter, b -> data.isDrillingGetter = ()->b));//There's gotta be a way to compact this into different classes or something
		addGenericData(GenericContainerData.bool(data.isSpillingGetter, b -> data.isSpillingGetter = ()->b));
		addGenericData(GenericContainerData.bool(data.isRSDisabledGetter, b -> data.isRSDisabledGetter = ()->b));
		addGenericData(GenericContainerData.int32(data.xGetter, i->data.xGetter = ()->i));
		addGenericData(GenericContainerData.int32(data.yGetter, i->data.yGetter = ()->i));
		addGenericData(GenericContainerData.int32(data.zGetter, i->data.zGetter = ()->i));
		addGenericData(GenericContainerData.int32(data.well.pipesGetter, i->data.well.pipesGetter = ()->i));
		addGenericData(GenericContainerData.int32(data.well.wellPipeLengthGetter, i->data.well.wellPipeLengthGetter = ()->i));
		addGenericData(GenericContainerData.int32(data.well.maxPipeLengthGetter, i->data.well.maxPipeLengthGetter = ()->i));
		addGenericData(GenericContainerData.int32(data.well.yGetter, i->data.well.yGetter = ()->i));
		if (data.gridStorage.get() != null){
			addGenericData(GenericContainerData.int32(data.gridWidth, i->data.gridWidth = ()->i));
			addGenericData(GenericContainerData.int32(data.gridHeight, i->data.gridHeight = ()->i));
			addGenericData(new GenericContainerData<>(GenericDataSerializers.BYTE_ARRAY,
					data.gridStorage.get()::getArray,ba->{
				final PipeConfig.Grid grid = new PipeConfig.Grid(data.gridWidth.get(), data.gridHeight.get());
				grid.setArray(ba);
				data.gridStorage = ()->grid;
			}));
		}

	}

	public static class DerrickData{ //Couldn't use record since they are immutable
		protected Supplier<Boolean> isDrillingGetter;
		protected Supplier<Boolean> isSpillingGetter;
		protected Supplier<Boolean> isRSDisabledGetter;
		protected Supplier<Integer> xGetter; //Might change this to a MutableBlockPos
		protected Supplier<Integer> yGetter;
		protected Supplier<Integer> zGetter;
		protected WellData well;
		protected Supplier<Integer> gridWidth;
		protected Supplier<Integer> gridHeight;
		public Supplier<PipeConfig.Grid> gridStorage;

		public DerrickData(Supplier<Boolean> isDrillingGetter, Supplier<Boolean> isSpillingGetter, Supplier<Boolean> isRSDisabledGetter, Supplier<Integer> xGetter, Supplier<Integer> yGetter, Supplier<Integer> zGetter, WellData well, Supplier<PipeConfig.Grid> gridStorage){
			this.isDrillingGetter = isDrillingGetter;
			this.isSpillingGetter = isSpillingGetter;
			this.isRSDisabledGetter = isRSDisabledGetter;
			this.xGetter = xGetter;
			this.yGetter = yGetter;
			this.zGetter = zGetter;
			this.well = well;
			this.gridWidth = ()->gridStorage.get().getWidth();
			this.gridHeight = ()->gridStorage.get().getHeight();
			this.gridStorage = gridStorage;
		}

		public BlockPos getBlockPos(){return new BlockPos(xGetter.get(), yGetter.get(), zGetter.get());}
		public WellData getWell(){return well;}
		public boolean isRSDisabled(){return isRSDisabledGetter.get();}
		public boolean isSpilling(){return isSpillingGetter.get();}
		public boolean isDrilling(){return isDrillingGetter.get();}

		protected static DerrickData serverData(DerrickTileEntity be){
			return new DerrickData(
					()->be.drilling,
					()->be.spilling,
					be::isRSDisabled,
					be.getBlockPos()::getX,
					be.getBlockPos()::getY,
					be.getBlockPos()::getZ,
					WellData.serverData(be.getWell()),
					()->be.gridStorage);
		}
		protected static DerrickData clientData(){
			return new DerrickData(
					()->false,
					()->false,
					()->false,
					()->0,()->0,()->0,
					WellData.clientData(),
					()->new PipeConfig.Grid(0,0)
			);
		}
	}

	public static class WellData{
		protected Supplier<Integer> pipesGetter;
		protected Supplier<Integer> wellPipeLengthGetter;
		protected Supplier<Integer> maxPipeLengthGetter;
		protected Supplier<Integer> yGetter;

		public static WellData serverData(WellTileEntity be){
			return new WellData(
					()->be.pipes,
					()->be.wellPipeLength,
					be::getMaxPipeLength,
					be.getBlockPos()::getY
			);
		}

		public static WellData clientData(){
			return new WellData(
					()->0, ()->0, ()->0, ()->0
			);
		}
		private WellData(Supplier<Integer> pipesGetter, Supplier<Integer> wellPipeLengthGetter, Supplier<Integer> maxPipeLengthGetter, Supplier<Integer> yGetter) {
			this.pipesGetter = pipesGetter;
			this.wellPipeLengthGetter = wellPipeLengthGetter;
			this.maxPipeLengthGetter = maxPipeLengthGetter;
			this.yGetter = yGetter;
		}

		public int pipes(){return pipesGetter.get();};
		public int wellPipeLength(){return wellPipeLengthGetter.get();}
		public int getMaxPipeLength(){return maxPipeLengthGetter.get();}
		public int getY(){return yGetter.get();}
	}

	/*public class GridData{
		protected Supplier<Integer> widthGetter;
		protected Supplier<Integer> heightGetter;
		protected Supplier<Byte[]>  arrayGetter;

		public int width(){return widthGetter.get();}
		public int height(){return heightGetter.get();}
		public byte[] array(){return ArrayUtils.toPrimitive(arrayGetter.get());} //Thanks, ApacheCommons

		public GridData(Supplier<Integer> widthGetter, Supplier<Integer> heightGetter, Supplier<Byte[]> arrayGetter) {
			this.widthGetter = widthGetter;
			this.heightGetter = heightGetter;
			this.arrayGetter = arrayGetter;
		}
	}*/
}
