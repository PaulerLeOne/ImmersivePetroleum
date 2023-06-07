package flaxbeard.immersivepetroleum.api.crafting;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;

public abstract class IPMultiblockRecipe extends MultiblockRecipe{
	Lazy<Integer> totalProcessTime;
	Lazy<Integer> totalProcessEnergy;
	protected <T extends Recipe<?>> IPMultiblockRecipe(ItemStack outputDummy, TypeWithClass<T> type, ResourceLocation id){
		super(Lazy.of(() -> outputDummy), type, id);
	}
	
	protected void timeAndEnergy(int time, int energy){
		this.totalProcessEnergy = Lazy.of(() -> energy);
		this.totalProcessTime = Lazy.of(() -> time);
	}
	
	@Override
	public void modifyTimeAndEnergy(DoubleSupplier timeModifier, DoubleSupplier energyModifier){
		final Lazy<Integer> oldTime = this.totalProcessTime;
		final Lazy<Integer> oldEnergy = this.totalProcessEnergy;
		this.totalProcessTime = Lazy.of(() -> (int) (Math.max(1, oldTime.get() * timeModifier.getAsDouble())));
		this.totalProcessEnergy = Lazy.of(() -> (int) (Math.max(1, oldEnergy.get() * energyModifier.getAsDouble())));
	}
	
	@Override
	public int getTotalProcessTime(){
		return this.totalProcessTime.get();
	}
	
	@Override
	public int getTotalProcessEnergy(){
		return this.totalProcessEnergy.get();
	}
}
