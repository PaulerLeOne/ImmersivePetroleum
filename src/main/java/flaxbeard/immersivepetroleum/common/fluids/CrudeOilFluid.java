package flaxbeard.immersivepetroleum.common.fluids;

import javax.annotation.Nonnull;

import net.minecraft.world.level.LevelReader;

public class CrudeOilFluid extends IPFluid{
	public CrudeOilFluid(IPFluidEntry entry){
		super(entry, 1000, 2250, false);
	}
	
	@Override
	public int getTickDelay(@Nonnull LevelReader p_205569_1_){
		return 10;
	}
}
