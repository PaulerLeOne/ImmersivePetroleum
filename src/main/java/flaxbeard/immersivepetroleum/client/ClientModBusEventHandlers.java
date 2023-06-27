package flaxbeard.immersivepetroleum.client;

import java.util.function.Supplier;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.client.render.AutoLubricatorRenderer;
import flaxbeard.immersivepetroleum.client.render.DerrickRenderer;
import flaxbeard.immersivepetroleum.client.render.MotorboatRenderer;
import flaxbeard.immersivepetroleum.client.render.MultiblockDistillationTowerRenderer;
import flaxbeard.immersivepetroleum.client.render.MultiblockPumpjackRenderer;
import flaxbeard.immersivepetroleum.client.render.OilTankRenderer;
import flaxbeard.immersivepetroleum.client.render.SeismicSurveyBarrelRenderer;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.entity.MotorboatEntity;
import flaxbeard.immersivepetroleum.common.items.ProjectorItem;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = ImmersivePetroleum.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientModBusEventHandlers{
	@SubscribeEvent
	public static void registerKeybinds(RegisterKeyMappingsEvent event){
		ProjectorItem.ClientInputHandler.keybind_preview_flip.setKeyConflictContext(KeyConflictContext.IN_GAME);
		event.register(ProjectorItem.ClientInputHandler.keybind_preview_flip);
	}
	@SubscribeEvent
	public static void registerRenders(RegisterRenderers ev){
		registerBERender(ev, IPTileTypes.TOWER.master(), MultiblockDistillationTowerRenderer::new);
		registerBERender(ev, IPTileTypes.PUMP.master(), MultiblockPumpjackRenderer::new);
		registerBERender(ev, IPTileTypes.OILTANK.master(), OilTankRenderer::new);
		registerBERender(ev, IPTileTypes.DERRICK.master(), DerrickRenderer::new);
		
		registerBERender(ev, IPTileTypes.AUTOLUBE.get(), AutoLubricatorRenderer::new);
		registerBERender(ev, IPTileTypes.SEISMIC_SURVEY.get(), SeismicSurveyBarrelRenderer::new);
		
		ev.registerEntityRenderer(MotorboatEntity.TYPE, MotorboatRenderer::new);
	}
	
	private static <T extends BlockEntity> void registerBERender(RegisterRenderers ev, BlockEntityType<T> type, Supplier<BlockEntityRenderer<T>> factory){
		ev.registerBlockEntityRenderer(type, ctx -> factory.get());
	}
}
