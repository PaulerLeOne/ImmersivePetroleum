package flaxbeard.immersivepetroleum.common.fluids;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import flaxbeard.immersivepetroleum.common.CommonEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class NapalmFluid extends IPFluid{
	public NapalmFluid(IPFluidEntry entry){
		super(entry, 1000, 4000, false);
	}
	
	public static IPFluidEntry makeFluid(){
		return makeFluid("napalm", NapalmFluid::new, e -> new IPFluidBlock(e){
			@Override
			public void onPlace(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving){
				for(Direction facing:Direction.values()){
					BlockPos notifyPos = pos.relative(facing);
					if(worldIn.getBlockState(notifyPos).getBlock() instanceof FireBlock || worldIn.getBlockState(notifyPos).getMaterial() == Material.FIRE){
						worldIn.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
						break;
					}
				}
				super.onPlace(state, worldIn, pos, oldState, isMoving);
			}
			
			@Override
			public void neighborChanged(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving){
				if(worldIn.getBlockState(fromPos).getBlock() instanceof FireBlock || worldIn.getBlockState(fromPos).getMaterial() == Material.FIRE){
					ResourceLocation d = worldIn.dimension().location();
					if(!CommonEventHandler.napalmPositions.containsKey(d) || !CommonEventHandler.napalmPositions.get(d).contains(fromPos)){
						processFire(e, worldIn, pos);
					}
				}
				
				super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
			}
		});
	}
	
	@Override
	public int getTickDelay(@Nonnull LevelReader p_205569_1_){
		return 10;
	}
	
	public static void processFire(IPFluidEntry entry, Level world, BlockPos pos){
		ResourceLocation d = world.dimension().location();
		if(!CommonEventHandler.napalmPositions.containsKey(d)){
			CommonEventHandler.napalmPositions.put(d, new ArrayList<>());
		}
		CommonEventHandler.napalmPositions.get(d).add(pos);
		
		world.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
		
		for(Direction facing:Direction.values()){
			BlockPos notifyPos = pos.relative(facing);
			if(world.getBlockState(notifyPos).is(entry.block().get())){
				CommonEventHandler.napalmPositions.get(d).add(notifyPos);
			}
		}
	}
}
