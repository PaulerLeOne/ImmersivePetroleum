package flaxbeard.immersivepetroleum;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraftforge.event.level.LevelEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import flaxbeard.immersivepetroleum.api.crafting.IPRecipeTypes;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.client.ClientProxy;
import flaxbeard.immersivepetroleum.common.CommonEventHandler;
import flaxbeard.immersivepetroleum.common.CommonProxy;
import flaxbeard.immersivepetroleum.common.ExternalModContent;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPContent.Fluids;
import flaxbeard.immersivepetroleum.common.IPRegisters;
import flaxbeard.immersivepetroleum.common.IPSaveData;
import flaxbeard.immersivepetroleum.common.IPToolShaders;
import flaxbeard.immersivepetroleum.common.ReservoirRegionDataStorage;
import flaxbeard.immersivepetroleum.common.cfg.IPClientConfig;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import flaxbeard.immersivepetroleum.common.crafting.RecipeReloadListener;
import flaxbeard.immersivepetroleum.common.network.IPPacketHandler;
import flaxbeard.immersivepetroleum.common.util.commands.IslandCommand;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.IPPeripheralProvider;
import flaxbeard.immersivepetroleum.common.util.loot.IPLootFunctions;
import flaxbeard.immersivepetroleum.common.world.IPWorldGen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(ImmersivePetroleum.MODID)
public class ImmersivePetroleum{
	public static final String MODID = "immersivepetroleum";
	
	public static final Logger log = LogManager.getLogger(MODID);

	public static final CreativeModeTab creativeTab = new CreativeModeTab(MODID){
		@Override
		@Nonnull
		public ItemStack makeIcon(){
			return new ItemStack(Fluids.CRUDEOIL.bucket().get());
		}
	};
	
	// Complete hack: DistExecutor::safeRunForDist intentionally tries to access the "wrong" supplier in dev, which
	// throws an error (rather than an exception) on J16 due to trying to load a client-only class. So we need to
	// replace the error with an exception in dev.
	public static <T> Supplier<T> bootstrapErrorToXCPInDev(Supplier<T> in){
		if(FMLLoader.isProduction())
			return in;
		return () -> {
			try{
				return in.get();
			}catch(BootstrapMethodError e){
				throw new RuntimeException(e);
			}
		};
	}
	//Assigns proxy according to active side
	public static final CommonProxy proxy = DistExecutor.safeRunForDist(bootstrapErrorToXCPInDev(() -> ClientProxy::new), bootstrapErrorToXCPInDev(() -> CommonProxy::new));
	
	public ImmersivePetroleum(){ //OTHER DEVS: These new comments were put here in order to make porting easier. Feel free to remove them

		//Registers configs (ForgeConfigSpec) on the mod loading context
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, IPServerConfig.ALL);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, IPClientConfig.ALL);

		//Subscribes to the mod event bus (mod specific events)
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup); //Subscribe to CommonSetup
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete); //Subscribes to LoadComplete

		//Subscribes to forge event bus (global events)
		MinecraftForge.EVENT_BUS.addListener(this::worldLoad);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
		MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);

		//Adds registers to the mod event bus
		IEventBus eBus = FMLJavaModLoadingContext.get().getModEventBus();
		IPRegisters.addRegistersToEventBus(eBus);


		IPContent.modConstruction(); //Adds content to registers by force loading static calls
		IPLootFunctions.modConstruction(); //Registers new type of loot entry
		IPRecipeTypes.modConstruction(); //Registers new type of recipe


		MinecraftForge.EVENT_BUS.register(new IPWorldGen()); //Subscribes forge event methods in IpWorldGen
		IPWorldGen.init(eBus); //Registers features
	}
	
	public void setup(FMLCommonSetupEvent event){
		proxy.setup(); //Does nothing
		
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		proxy.preInit(); //Does nothing
		
		IPContent.preInit(); //Does nothing
		IPPacketHandler.preInit(); //Registers messageTypes into simpleChannel
		IPToolShaders.preInit(); //Adds IEShaders using IE API
		
		proxy.preInitEnd(); //Does nothing
		
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		IPContent.init(event); //Registers mod content not related to the fml registries, (multiblocks, chemthower effects...)
		
		MinecraftForge.EVENT_BUS.register(new CommonEventHandler()); //Handles several events
		
		proxy.init(); //ocs Subscribes to client sided events
		
		if(ModList.get().isLoaded("computercraft")){
			IPPeripheralProvider.init(); //Registers CC Peripherals with the CC API
		}
		
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		proxy.postInit(); // Does nothing
		
		ReservoirHandler.recalculateChances(); //Resets reservoir weight map to empty
		ExternalModContent.init(); //Loads content from IE
		
		proxy.registerContainersAndScreens(); //ocs Registers menu screens
	}
	
	public void loadComplete(FMLLoadCompleteEvent event){
		proxy.completed(event); //Adds configGetters for the IE manual
	}
	
	public void registerCommand(RegisterCommandsEvent event){
		LiteralArgumentBuilder<CommandSourceStack> ip = Commands.literal("ip");
		
		ip.then(IslandCommand.create());
		
		event.getDispatcher().register(ip); //Register new command
	}
	
	public void addReloadListeners(AddReloadListenerEvent event){
		event.addListener(new RecipeReloadListener(event.getServerResources())); //Loads IP recipes (including reservoirs types) on reload
	}
	
	public void worldLoad(LevelEvent.Load event){
		if(!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel world && world.dimension() == Level.OVERWORLD){
			ReservoirRegionDataStorage.init(world.getDataStorage()); //Loads reservoir region data
			world.getDataStorage().computeIfAbsent(IPSaveData::new, IPSaveData::new, IPSaveData.dataName); //Loads IP save data
		}
	}
	
	public void serverStarting(ServerStartingEvent event){
		ReservoirHandler.recalculateChances(); //Resets reservoir weight map to empty
	}
}
