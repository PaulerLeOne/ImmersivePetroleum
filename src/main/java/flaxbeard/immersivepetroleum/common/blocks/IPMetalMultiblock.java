package flaxbeard.immersivepetroleum.common.blocks;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPCommonTickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IPMetalMultiblock<T extends MultiblockPartBlockEntity<T> & IPCommonTickableTile> extends MetalMultiblockBlock<T>{
	private final MultiblockBEType<T> multiblockBEType;
	
	public IPMetalMultiblock(MultiblockBEType<T> te){
		super(te, Block.Properties.of(Material.METAL)
				.sound(SoundType.METAL)
				.strength(3, 15)
				.requiresCorrectToolForDrops()
				.isViewBlocking((state, blockReader, pos) -> false)
				.noOcclusion()
				.dynamicShape()
		);
		this.multiblockBEType = te;
	}
	
	@Override
	public <E extends BlockEntity> BlockEntityTicker<E> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<E> type){
		return IPBlockBase.createCommonTicker(world.isClientSide, type, multiblockBEType.master());
	}
}
