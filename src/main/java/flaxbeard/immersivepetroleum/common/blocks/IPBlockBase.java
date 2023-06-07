package flaxbeard.immersivepetroleum.common.blocks;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPClientTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPCommonTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPServerTickableTile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class IPBlockBase extends Block{
	public IPBlockBase(Block.Properties props){
		super(props);
	}
	
	public Supplier<BlockItem> blockItemSupplier(){
		return () -> new IPBlockItemBase(this, new Item.Properties().tab(ImmersivePetroleum.creativeTab));
	}
	
	@Nullable
	public static <E extends BlockEntity & IPCommonTickableTile, A extends BlockEntity> BlockEntityTicker<A> createCommonTicker(boolean isClient, BlockEntityType<A> actual, RegistryObject<BlockEntityType<E>> expected){
		return createCommonTicker(isClient, actual, expected.get());
	}
	
	@Nullable
	public static <E extends BlockEntity & IPCommonTickableTile, A extends BlockEntity> BlockEntityTicker<A> createCommonTicker(boolean isClient, BlockEntityType<A> actual, BlockEntityType<E> expected){
		if(isClient){
			return createClientTicker(actual, expected);
		}else{
			return createServerTicker(actual, expected);
		}
	}
	
	@Nullable
	public static <E extends BlockEntity & IPClientTickableTile, A extends BlockEntity> BlockEntityTicker<A> createClientTicker(BlockEntityType<A> actual, BlockEntityType<E> expected){
		return createTickerHelper(actual, expected, IPClientTickableTile::makeTicker);
	}
	
	@Nullable
	public static <E extends BlockEntity & IPServerTickableTile, A extends BlockEntity> BlockEntityTicker<A> createServerTicker(BlockEntityType<A> actual, BlockEntityType<E> expected){
		return createTickerHelper(actual, expected, IPServerTickableTile::makeTicker);
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected, Supplier<BlockEntityTicker<? super E>> ticker){
		return expected == actual ? (BlockEntityTicker<A>) ticker.get() : null;
	}
}
