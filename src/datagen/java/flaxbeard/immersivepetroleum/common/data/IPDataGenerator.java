package flaxbeard.immersivepetroleum.common.data;

import net.minecraftforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.data.loot.IPLootGenerator;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class IPDataGenerator{
	public static final Logger log = LogManager.getLogger(ImmersivePetroleum.MODID + "/DataGenerator");
	
	@SubscribeEvent
	public static void generate(GatherDataEvent event){
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper exhelper = event.getExistingFileHelper();
		StaticTemplateManager.EXISTING_HELPER = exhelper;
		
		if(event.includeServer()){
			IPBlockTags blockTags = new IPBlockTags(generator, exhelper);
			generator.addProvider(true, blockTags);
			generator.addProvider(true, new IPItemTags(generator, blockTags, exhelper));
			generator.addProvider(true, new IPFluidTags(generator, exhelper));
			generator.addProvider(true, new IPLootGenerator(generator));
			generator.addProvider(true, new IPRecipes(generator));
			generator.addProvider(true, new IPAdvancements(generator, exhelper));
		}
		
		if(event.includeClient()){
			generator.addProvider(true, new IPBlockStates(generator, exhelper));
			generator.addProvider(true, new IPItemModels(generator, exhelper));
		}
	}
}
