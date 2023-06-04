package flaxbeard.immersivepetroleum.common.fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPRegisters;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.registries.RegistryObject;

public class IPFluid extends FlowingFluid{
	public static final List<IPFluidEntry> FLUIDS = new ArrayList<>();
	
	protected final IPFluidEntry entry;

	public IPFluid(IPFluidEntry entry){
		this.entry = entry;
	}
	
	public static IPFluidEntry makeFluid(String name, Function<IPFluidEntry, IPFluid> factory, Consumer<FluidType.Properties> buildAtrributes){
		return makeFluid(name, factory, buildAtrributes, IPFluidBlock::new);
	}
	
	public static IPFluidEntry makeFluid(String name, Function<IPFluidEntry, IPFluid> factory, Consumer<FluidType.Properties> buildAtrributes, Function<IPFluidEntry, Block> blockFactory){
		Mutable<IPFluidEntry> entry = new MutableObject<>();

		FluidType.Properties builder =  FluidType.Properties.create();
		buildAtrributes.accept(builder);
		
		entry.setValue(new IPFluidEntry(
				name,
				IPRegisters.registerFluid(name, () -> factory.apply(entry.getValue())),
				IPRegisters.registerFluid(name+"_flowing", () -> new IPFluidFlowing(entry.getValue().still.get())),
				IPRegisters.registerBlock(name, () -> blockFactory.apply(entry.getValue())),
				IPRegisters.registerItem(name+"_bucket", () -> new IPBucketItem(entry.getValue().still())),
				IPRegisters.registerFluidType(name, () -> {return new FluidType(builder){
					@Override
					public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
						consumer.accept(
								new IClientFluidTypeExtensions() {
									@Override
									public ResourceLocation getStillTexture() {
										return new ResourceLocation(ImmersivePetroleum.MODID, "block/fluid/"+name+"_still");
									}
									@Override
									public ResourceLocation getFlowingTexture() {
										return new ResourceLocation(ImmersivePetroleum.MODID,"block/fluid/"+name+"_flow");
									}
								}
						);
					}
				};
				})
		));
		FLUIDS.add(entry.getValue());
		return entry.getValue();
	}

	@Override
	public FluidType getFluidType() {
		return entry.type().get();
	}

	@Override
	protected void beforeDestroyingBlock(@Nonnull LevelAccessor arg0, @Nonnull BlockPos arg1, @Nonnull BlockState arg2){
	}
	
	@Override
	protected boolean canConvertToSource(){
		return false;
	}
	
	@Override
	@Nonnull
	public Fluid getFlowing(){
		return this.entry.flowing.get();
	}
	
	@Override
	@Nonnull
	public Fluid getSource(){
		return this.entry.still.get();
	}
	
	@Override
	@Nonnull
	public Item getBucket(){
		return this.entry.bucket.get();
	}
	
	@Override
	protected int getDropOff(@Nonnull LevelReader arg0){
		return 1;
	}
	
	@Override
	protected int getSlopeFindDistance(@Nonnull LevelReader arg0){
		return 4;
	}
	
	@Override
	protected boolean canBeReplacedWith(@Nonnull FluidState p_215665_1_, @Nonnull BlockGetter p_215665_2_, @Nonnull BlockPos p_215665_3_, @Nonnull Fluid p_215665_4_, @Nonnull Direction p_215665_5_){
		return p_215665_5_ == Direction.DOWN && !isSame(p_215665_4_);
	}
	
	@Override
	public int getTickDelay(@Nonnull LevelReader p_205569_1_){
		return 5;
	}
	
	@Override
	protected float getExplosionResistance(){
		return 100;
	}
	
	@Override
	protected @Nonnull BlockState createLegacyBlock(@Nonnull FluidState state){
		return this.entry.block.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
	}
	
	@Override
	public boolean isSource(FluidState state){
		return state.is(this.getSource());
	}
	
	@Override
	public int getAmount(@Nonnull FluidState state){
		return isSource(state) ? 8 : state.getValue(LEVEL);
	}
	
	@Override
	public boolean isSame(Fluid fluidIn){
		return fluidIn.equals(this.getSource()) || fluidIn.equals(this.getFlowing());
	}
	
	// STATIC CLASSES
	
	public static class IPFluidBlock extends LiquidBlock{
		public IPFluidBlock(IPFluidEntry entry){
			super(entry.still(), BlockBehaviour.Properties.of(Material.WATER));
		}
	}
	
	public static class IPBucketItem extends BucketItem{
		private static final Item.Properties PROPS = new Item.Properties().stacksTo(1).tab(ImmersivePetroleum.creativeTab);
		
		public IPBucketItem(Supplier<? extends Fluid> fluid){
			super(fluid, PROPS);
		}
		
		@Override
		public ItemStack getCraftingRemainingItem(ItemStack itemStack){
			return new ItemStack(Items.BUCKET);
		}
		
		@Override
		public boolean hasCraftingRemainingItem(ItemStack stack){
			return true;
		}
		
		@Override
		public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundTag nbt){
			return new FluidBucketWrapper(stack);
		}
	}
	
	public static class IPFluidFlowing extends IPFluid{
		public IPFluidFlowing(IPFluid source){
			super(source.entry);
			registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 7));
		}
		
		@Override
		protected void createFluidStateDefinition(@Nonnull Builder<Fluid, FluidState> builder){
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}
	}
	
	public record IPFluidEntry(String name, RegistryObject<IPFluid> still, RegistryObject<IPFluid> flowing, RegistryObject<Block> block, RegistryObject<Item> bucket, RegistryObject<FluidType> type){
		public Fluid get(){
			return still().get();
		}
	}

	public static Consumer<FluidType.Properties> createBuilder(int density, int viscosity)
	{
		return builder -> builder.viscosity(viscosity).density(density);
	}
}
