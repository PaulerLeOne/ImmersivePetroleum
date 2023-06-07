package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;

import blusunrize.immersiveengineering.api.fluid.IFluidPipe;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.util.MultiblockCapability;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.MultiFluidTank;
import blusunrize.immersiveengineering.common.util.orientation.RelativeBlockFace;
import flaxbeard.immersivepetroleum.api.crafting.DistillationTowerRecipe;
import flaxbeard.immersivepetroleum.common.IPMenuTypes;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPCommonTickableTile;
import flaxbeard.immersivepetroleum.common.gui.IPMenuProvider;
import flaxbeard.immersivepetroleum.common.multiblocks.DistillationTowerMultiblock;
import flaxbeard.immersivepetroleum.common.util.FluidHelper;
import flaxbeard.immersivepetroleum.common.util.inventory.MultiFluidTankFiltered;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class DistillationTowerTileEntity extends PoweredMultiblockBlockEntity<DistillationTowerTileEntity, DistillationTowerRecipe> implements IPCommonTickableTile, IPMenuProvider<DistillationTowerTileEntity>, IEBlockInterfaces.IBlockBounds{
	/** Input Tank ID */
	public static final int TANK_INPUT = 0;
	
	/** Output Tank ID */
	public static final int TANK_OUTPUT = 1;
	
	/** Inventory Fluid Input (Filled Bucket) */
	public static final int INV_0 = 0;
	
	/** Inventory Fluid Input (Empty Bucket) */
	public static final int INV_1 = 1;
	
	/** Inventory Fluid Output (Empty Bucket) */
	public static final int INV_2 = 2;
	
	/** Inventory Fluid Output (Filled Bucket) */
	public static final int INV_3 = 3;
	
	/** Template-Location of the Fluid Input Port. (3 0 3) */
	public static final BlockPos Fluid_IN = new BlockPos(3, 0, 3);
	
	/** Template-Location of the Fluid Output Port. (1 0 3) */
	public static final BlockPos Fluid_OUT = new BlockPos(1, 0, 3);
	
	/** Template-Location of the Item Output Port. (0 0 1) */
	public static final BlockPos Item_OUT = new BlockPos(0, 0, 1);
	
	/** Template-Location of the Energy Input Port. (3 1 3) */
	public static final Set<MultiblockFace> Energy_IN = ImmutableSet.of(new MultiblockFace(3, 1, 3, RelativeBlockFace.UP));
	
	/** Template-Location of the Redstone Input Port. (0 1 3) */
	public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(0, 1, 3));
	
	public NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
	public final MultiFluidTank[] tanks = new MultiFluidTank[]{
			new MultiFluidTankFiltered(24000, fs -> DistillationTowerRecipe.findRecipe(fs) != null),
			new MultiFluidTankFiltered(24000)
	};
	private int cooldownTicks = 0;
	private boolean wasActive = false;
	
	public DistillationTowerTileEntity(BlockEntityType<DistillationTowerTileEntity> type, BlockPos pWorldPosition, BlockState pBlockState){
		super(DistillationTowerMultiblock.INSTANCE, 16000, true, type, pWorldPosition, pBlockState);
	}
	
	@Override
	public void readCustomNBT(CompoundTag nbt, boolean descPacket){
		super.readCustomNBT(nbt, descPacket);
		this.tanks[0].readFromNBT(nbt.getCompound("tank0"));
		this.tanks[1].readFromNBT(nbt.getCompound("tank1"));
		this.cooldownTicks = nbt.getInt("cooldownTicks");
		
		if(!descPacket){
			this.inventory = readInventory(nbt.getCompound("inventory"));
		}
	}
	
	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean descPacket){
		super.writeCustomNBT(nbt, descPacket);
		nbt.put("tank0", this.tanks[TANK_INPUT].writeToNBT(new CompoundTag()));
		nbt.put("tank1", this.tanks[TANK_OUTPUT].writeToNBT(new CompoundTag()));
		nbt.putInt("cooldownTicks", this.cooldownTicks);
		if(!descPacket){
			nbt.put("inventory", writeInventory(this.inventory));
		}
	}
	
	protected NonNullList<ItemStack> readInventory(CompoundTag nbt){
		NonNullList<ItemStack> list = NonNullList.create();
		ContainerHelper.loadAllItems(nbt, list);
		
		if(list.size() == 0){ // Incase it loaded none
			list = this.inventory.size() == 4 ? this.inventory : NonNullList.withSize(4, ItemStack.EMPTY);
		}else if(list.size() < 4){ // Padding incase it loaded less than 4
			while(list.size() < 4)
				list.add(ItemStack.EMPTY);
		}
		return list;
	}
	
	protected CompoundTag writeInventory(NonNullList<ItemStack> list){
		return ContainerHelper.saveAllItems(new CompoundTag(), list);
	}
	
	@Override
	public void tickClient(){
	}
	
	@Override
	public void tickServer(){
		if(isDummy()){
			return;
		}
		
		if(this.cooldownTicks > 0){
			this.cooldownTicks--;
		}
		
		super.tickServer();
		
		boolean update = false;
		
		if(!isRSDisabled()){
			if(this.energyStorage.getEnergyStored() > 0 && this.processQueue.size() < getProcessQueueMaxLength()){
				if(this.tanks[TANK_INPUT].getFluidAmount() > 0){
					DistillationTowerRecipe recipe = DistillationTowerRecipe.findRecipe(this.tanks[TANK_INPUT].getFluid());
					if(recipe != null && this.tanks[TANK_INPUT].getFluidAmount() >= recipe.getInputFluid().getAmount() && this.energyStorage.getEnergyStored() >= recipe.getTotalProcessEnergy()){
						MultiblockProcessInMachine<DistillationTowerRecipe> process = new MultiblockProcessInMachine<>(recipe, this::getRecipeForId).setInputTanks(TANK_INPUT);
						if(addProcessToQueue(process, true)){
							addProcessToQueue(process, false);
							update = true;
						}
					}
				}
			}
			
			if(!this.processQueue.isEmpty()){
				this.wasActive = true;
				this.cooldownTicks = 10;
				update = true;
			}else if(this.wasActive){
				this.wasActive = false;
				update = true;
			}
			
			super.tickServer();
		}
		
		if(this.inventory.get(INV_0) != ItemStack.EMPTY && this.tanks[TANK_INPUT].getFluidAmount() < this.tanks[TANK_INPUT].getCapacity()){
			ItemStack emptyContainer = Utils.drainFluidContainer(this.tanks[TANK_INPUT], this.inventory.get(INV_0), this.inventory.get(INV_1));
			if(!emptyContainer.isEmpty()){
				if(!this.inventory.get(INV_1).isEmpty() && ItemHandlerHelper.canItemStacksStack(this.inventory.get(INV_1), emptyContainer)){
					this.inventory.get(INV_1).grow(emptyContainer.getCount());
				}else if(this.inventory.get(INV_1).isEmpty()){
					this.inventory.set(INV_1, emptyContainer.copy());
				}
				
				this.inventory.get(INV_0).shrink(1);
				if(this.inventory.get(INV_0).getCount() <= 0){
					this.inventory.set(INV_0, ItemStack.EMPTY);
				}
				update = true;
			}
		}
		
		if(this.tanks[TANK_OUTPUT].getFluidAmount() > 0){
			if(this.inventory.get(INV_2) != ItemStack.EMPTY){
				
				if(this.tanks[TANK_OUTPUT].getFluidTypes() > 0){
					MultiFluidTank outTank = this.tanks[TANK_OUTPUT];
					
					for(int i = outTank.getFluidTypes() - 1;i >= 0;i--){
						FluidStack fs = outTank.getFluidInTank(i);
						
						if(fs.getAmount() > 0){
							ItemStack filledContainer = FluidHelper.fillFluidContainer(outTank, fs, this.inventory.get(INV_2), this.inventory.get(INV_3));
							if(!filledContainer.isEmpty()){
								if(this.inventory.get(INV_3).getCount() == 1 && !FluidHelper.isFluidContainerFull(filledContainer)){
									this.inventory.set(INV_3, filledContainer.copy());
								}else{
									if(!this.inventory.get(INV_3).isEmpty() && ItemHandlerHelper.canItemStacksStack(this.inventory.get(INV_3), filledContainer)){
										this.inventory.get(INV_3).grow(filledContainer.getCount());
									}else if(this.inventory.get(INV_3).isEmpty()){
										this.inventory.set(INV_3, filledContainer.copy());
									}
									
									this.inventory.get(INV_2).shrink(1);
									if(this.inventory.get(INV_2).getCount() <= 0){
										this.inventory.set(INV_2, ItemStack.EMPTY);
									}
								}
								
								update = true;
								break;
							}
						}
					}
				}
			}
			
			BlockPos outPos = getBlockPosForPos(Fluid_OUT).relative(getFacing().getOpposite());
			update |= FluidUtil.getFluidHandler(this.level, outPos, getFacing()).map(output -> {
				boolean ret = false;
				if(this.tanks[TANK_OUTPUT].fluids.size() > 0){
					List<FluidStack> toDrain = new ArrayList<>();
					boolean iePipe = this.level.getBlockEntity(outPos) instanceof IFluidPipe;
					
					// Tries to Output the output-fluids in parallel
					for(FluidStack target:this.tanks[TANK_OUTPUT].fluids){
						FluidStack outStack = FluidHelper.copyFluid(target, Math.min(target.getAmount(), 100), iePipe);
						
						int accepted = output.fill(outStack, FluidAction.SIMULATE);
						if(accepted > 0){
							int drained = output.fill(FluidHelper.copyFluid(outStack, Math.min(outStack.getAmount(), accepted), iePipe), FluidAction.EXECUTE);
							
							toDrain.add(new FluidStack(target.getFluid(), drained));
							ret = true;
						}
					}
					
					// If this were to be done in the for-loop it would throw a concurrent exception
					toDrain.forEach(fluid -> this.tanks[TANK_OUTPUT].drain(fluid, FluidAction.EXECUTE));
				}
				
				return ret;
			}).orElse(false);
		}
		
		if(update){
			updateMasterBlock(null, true);
		}
	}
	
	@Override
	public NonNullList<ItemStack> getInventory(){
		return this.inventory;
	}
	
	@Override
	public boolean isStackValid(int slot, ItemStack stack){
		return true;
	}
	
	@Override
	public int getSlotLimit(int slot){
		return 64;
	}
	
	@Override
	public void doGraphicalUpdates(){
		updateMasterBlock(null, true);
	}
	
	@Override
	public DistillationTowerTileEntity getGuiMaster(){
		return master();
	}
	
	@Nonnull
	@Override
	public BEContainerIP<? super DistillationTowerTileEntity, ?> getContainerTypeIP(){
		return IPMenuTypes.DISTILLATION_TOWER;
	}
	
	@Override
	public boolean canUseGui(@Nonnull Player player){
		return this.formed;
	}
	
	/** Locations that don't require sneaking to avoid the GUI */
	public boolean skipGui(BlockHitResult hit){
		Direction facing = getFacing();
		
		// Power input
		if(DistillationTowerTileEntity.Energy_IN.stream().anyMatch((t) -> t.posInMultiblock() == this.posInMultiblock) && hit.getDirection() == Direction.UP){
			return true;
		}
		
		// Redstone controller input
		if(DistillationTowerTileEntity.Redstone_IN.contains(this.posInMultiblock) && (getIsMirrored() ? hit.getDirection() == facing.getClockWise() : hit.getDirection() == facing.getCounterClockWise())){
			return true;
		}
		
		// Fluid I/O Ports
		if((this.posInMultiblock.equals(DistillationTowerTileEntity.Fluid_IN) && (getIsMirrored() ? hit.getDirection() == facing.getCounterClockWise() : hit.getDirection() == facing.getClockWise()))
		|| (this.posInMultiblock.equals(DistillationTowerTileEntity.Fluid_OUT) && hit.getDirection() == facing.getOpposite())){
			return true;
		}
		
		// Item output port
		if(this.posInMultiblock.equals(DistillationTowerTileEntity.Item_OUT) && (getIsMirrored() ? hit.getDirection() == facing.getClockWise() : hit.getDirection() == facing.getCounterClockWise())){
			return true;
		}
		
		return false;
	}
	
	@Override
	protected DistillationTowerRecipe getRecipeForId(Level level, ResourceLocation id){
		return DistillationTowerRecipe.recipes.get(id);
	}
	
	@Override
	public Set<MultiblockFace> getEnergyPos(){
		return Energy_IN;
	}
	
	@Override
	public Set<BlockPos> getRedstonePos(){
		return Redstone_IN;
	}
	
	@Override
	public IFluidTank[] getInternalTanks(){
		return this.tanks;
	}
	
	@Override
	public DistillationTowerRecipe findRecipeForInsertion(ItemStack inserting){
		return null;
	}
	
	@Override
	public int[] getOutputSlots(){
		return null;
	}
	
	@Override
	public int[] getOutputTanks(){
		return new int[]{TANK_OUTPUT};
	}
	
	@Override
	public boolean additionalCanProcessCheck(MultiblockProcess<DistillationTowerRecipe> process){
		int outputAmount = 0;
		for(FluidStack outputFluid:process.getRecipe(this.level).getFluidOutputs()){
			outputAmount += outputFluid.getAmount();
		}
		
		return this.tanks[TANK_OUTPUT].getCapacity() >= (this.tanks[TANK_OUTPUT].getFluidAmount() + outputAmount);
	}
	
	@Override
	public void doProcessOutput(ItemStack output){
		Direction outputdir = (getIsMirrored() ? getFacing().getClockWise() : getFacing().getCounterClockWise());
		BlockPos outputpos = getBlockPosForPos(Item_OUT).relative(outputdir);
		
		BlockEntity te = level.getBlockEntity(outputpos);
		if(te != null){
			IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputdir.getOpposite()).orElse(null);
			if(handler != null){
				output = ItemHandlerHelper.insertItem(handler, output, false);
			}
		}
		
		if(!output.isEmpty()){
			double x = outputpos.getX() + 0.5;
			double y = outputpos.getY() + 0.25;
			double z = outputpos.getZ() + 0.5;
			
			Direction facing = getIsMirrored() ? getFacing().getOpposite() : getFacing();
			if(facing != Direction.EAST && facing != Direction.WEST){
				x = outputpos.getX() + (facing == Direction.SOUTH ? 0.15 : 0.85);
			}
			if(facing != Direction.NORTH && facing != Direction.SOUTH){
				z = outputpos.getZ() + (facing == Direction.WEST ? 0.15 : 0.85);
			}
			
			ItemEntity ei = new ItemEntity(level, x, y, z, output.copy());
			ei.setDeltaMovement(0.075 * outputdir.getStepX(), 0.025, 0.075 * outputdir.getStepZ());
			level.addFreshEntity(ei);
		}
	}
	
	@Override
	public void doProcessFluidOutput(FluidStack output){
	}
	
	@Override
	public void onProcessFinish(MultiblockProcess<DistillationTowerRecipe> process){
	}
	
	@Override
	public float getMinProcessDistance(MultiblockProcess<DistillationTowerRecipe> process){
		return 1.0F;
	}
	
	@Override
	public int getMaxProcessPerTick(){
		return 1;
	}
	
	@Override
	public int getProcessQueueMaxLength(){
		return 1;
	}
	
	@Override
	public boolean isInWorldProcessingMachine(){
		return false;
	}
	
	@Override
	public boolean shouldRenderAsActiveImpl(){
		return this.cooldownTicks > 0 || super.shouldRenderAsActiveImpl();
	}
	
	private final MultiblockCapability<IFluidHandler> outputHandler = MultiblockCapability.make(
			this, be -> be.outputHandler, DistillationTowerTileEntity::master,
			registerFluidOutput(this.tanks[TANK_OUTPUT])
	);
	private final MultiblockCapability<IFluidHandler> inputHandler = MultiblockCapability.make(
			this, be -> be.inputHandler, DistillationTowerTileEntity::master,
			registerFluidInput(this.tanks[TANK_INPUT])
	);
	
	@Nonnull
	@Override
	public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction side){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(this.posInMultiblock.equals(Fluid_IN)){
				if(side == null || (getIsMirrored() ? (side == getFacing().getCounterClockWise()) : (side == getFacing().getClockWise()))){
					return this.inputHandler.getAndCast();
				}
			}
			if(this.posInMultiblock.equals(Fluid_OUT) && (side == null || side == getFacing().getOpposite())){
				return this.outputHandler.getAndCast();
			}
		}
		return super.getCapability(capability, side);
	}
	
	public boolean isLadder(){
		return this.posInMultiblock.getY() > 0 && (this.posInMultiblock.getX() == 2 && this.posInMultiblock.getZ() == 0);
	}
	
	private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(DistillationTowerTileEntity::getShape);
	
	@Override
	@Nonnull
	public VoxelShape getBlockBounds(CollisionContext ctx){
		return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
	}
	
	private static List<AABB> getShape(BlockPos posInMultiblock){
		final int bX = posInMultiblock.getX();
		final int bY = posInMultiblock.getY();
		final int bZ = posInMultiblock.getZ();
		
		// Redstone Input
		if(bY < 2){
			if(bX == 0 && bZ == 3){
				if(bY == 1){ // Actual Input
					return List.of(new AABB(0.0, 0.0, 0.0, 0.5, 1.0, 1.0));
				}else{ // Input Legs
					return Arrays.asList(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0), new AABB(0.125, 0.0, 0.75, 0.375, 1.0, 0.875), new AABB(0.125, 0.0, 0.125, 0.375, 1.0, 0.25));
				}
			}
		}
		
		// Pipe over Furnace
		if(bY == 2 && bX == 3 && bZ == 2){
			return Arrays.asList(new AABB(-0.0625, 0.375, 0.125, 0.0625, 1.125, 0.875), new AABB(0.125, 0, 0.125, 0.875, 0.125, 0.875), new AABB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75), new AABB(0.0, 0.5, 0.25, 0.75, 1.0, 0.75));
		}
		
		// Long Pipe
		if(bY > 0 && bX == 1 && bZ == 3){
			if(bY != 15){
				List<AABB> list = new ArrayList<>();
				list.add(new AABB(0.1875, 0.0, 0.1875, 0.8125, 1.0, 0.8125));
				if(bY > 0 && bY % 4 == 0){ // For pipe passing a platform
					list.add(new AABB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0));
				}
				return list;
			}else{ // Pipe Top Bend
				return List.of(new AABB(0.1875, 0.0, -0.0625, 0.8125, 0.625, 0.8125));
			}
		}
		
		// Ladder
		if(bY > 0 && bX == 2 && bZ == 0){
			List<AABB> list = new ArrayList<>();
			list.add(new AABB(0.0625, bY == 1 ? 0.125 : 0.0, 0.875, 0.9375, 1.0, 1.0625));
			if(bY > 0 && bY % 4 == 0){
				list.add(new AABB(0.0, 0.5, 0.875, 1.0, 1.0, 1.0625));
				list.add(new AABB(0.0, 0.5, 0.0, 1.0, 1.0, 0.0625));
			}
			return list;
		}
		
		// Center
		if(bX > 0 && bX < 3 && bZ > 0 && bZ < 3){
			if(bY > 0){
				// Boiler
				AABB bb = new AABB(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);
				if(bZ == 1){
					if(bX == 1) bb = new AABB(0.0625, 0.0, 0.0625, 1.0, 1.0, 1.0);
					if(bX == 2) bb = new AABB(0.0, 0.0, 0.0625, 0.9375, 1.0, 1.0);
				}else if(bZ == 2){
					if(bX == 1) bb = new AABB(0.0625, 0.0, 0.0, 1.0, 1.0, 0.9375);
					if(bX == 2) bb = new AABB(0.0, 0.0, 0.0, 0.9375, 1.0, 0.9375);
				}
				return List.of(bb);
			}else{
				// Below Boiler
				return Arrays.asList(
						new AABB(-0.125, 0.5, -0.125, 1.125, 1.125, 1.125),
						new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
				);
			}
		}
		
		// Platforms
		if(bY > 0 && bY % 4 == 0){
			return List.of(new AABB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0));
		}
		
		// Base
		if(bY == 0){
			List<AABB> list = new ArrayList<>();
			if((bX == 0 && bZ == 1) || (bX == 1 && bZ == 3) || (bX == 3 && bZ == 2) || (bX == 3 && bZ == 3)){
				list.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
			}else{
				list.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
			}
			return list;
		}
		
		return List.of(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
	}
}
