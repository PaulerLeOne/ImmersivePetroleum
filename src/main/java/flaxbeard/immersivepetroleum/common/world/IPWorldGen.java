package flaxbeard.immersivepetroleum.common.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IPWorldGen{
	public static Map<String, Holder<PlacedFeature>> features = new HashMap<>();
	
	private static DeferredRegister<Feature<?>> FEATURE_REGISTER = DeferredRegister.create(ForgeRegistries.FEATURES, ImmersivePetroleum.MODID);
	
	private static RegistryObject<FeatureReservoir> RESERVOIR_FEATURE = FEATURE_REGISTER.register("reservoir", FeatureReservoir::new);
	
	public static void init(IEventBus eBus){
		FEATURE_REGISTER.register(eBus);
	}
	
	public static void registerReservoirGen(){
		Holder<PlacedFeature> reservoirFeature = register(ResourceUtils.ip("reservoir"), RESERVOIR_FEATURE, new NoneFeatureConfiguration());
		features.put("reservoirs", reservoirFeature);
	}
	
	@SubscribeEvent
	public void onBiomeLoad(BiomeLoadingEvent event){
		BiomeGenerationSettingsBuilder generation = event.getGeneration();
		for(Entry<String, Holder<PlacedFeature>> entry:features.entrySet()){
			generation.addFeature(Decoration.UNDERGROUND_ORES, entry.getValue());
		}
	}
	
	private static <Cfg extends FeatureConfiguration, F extends Feature<Cfg>> Holder<PlacedFeature> register(ResourceLocation rl, RegistryObject<F> feature, Cfg cfg){
		Holder<ConfiguredFeature<?, ?>> configured = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, rl, new ConfiguredFeature<>(feature.get(), cfg));
		return BuiltinRegistries.register(BuiltinRegistries.PLACED_FEATURE, rl, new PlacedFeature(configured, List.of()));
	}
}
