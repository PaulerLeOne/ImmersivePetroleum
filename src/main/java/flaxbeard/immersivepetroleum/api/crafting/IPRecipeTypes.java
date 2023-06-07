package flaxbeard.immersivepetroleum.api.crafting;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirType;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class IPRecipeTypes{
	private static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, ImmersivePetroleum.MODID);
	
	public static final RegistryObject<RecipeType<CokerUnitRecipe>> COKER = makeType("cokerunit");
	public static final RegistryObject<RecipeType<DistillationTowerRecipe>> DISTILLATION = makeType("distillationtower");
	public static final RegistryObject<RecipeType<HighPressureRefineryRecipe>> HYDROTREATER = makeType("hydrotreater");
	
	public static final RegistryObject<RecipeType<ReservoirType>> RESERVOIR = makeType("reservoir");
	
	public static void modConstruction(){
		REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> makeType(String name){
		return REGISTER.register(name, () -> new RecipeType<T>(){
			@Override
			public String toString(){
				return ImmersivePetroleum.MODID + ":" + name;
			}
		});
	}
}
