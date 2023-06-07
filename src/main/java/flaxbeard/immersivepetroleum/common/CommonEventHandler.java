package flaxbeard.immersivepetroleum.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler.ILubricationHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler.LubricatedTileInfo;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import flaxbeard.immersivepetroleum.common.entity.MotorboatEntity;
import flaxbeard.immersivepetroleum.common.fluids.NapalmFluid;
import flaxbeard.immersivepetroleum.common.util.IPEffects;
import flaxbeard.immersivepetroleum.common.util.Utils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonEventHandler{
	@SubscribeEvent
	public void onSave(WorldEvent.Save event){
		if(!event.getWorld().isClientSide()){
			IPSaveData.markDirty();
		}
	}
	
	@SubscribeEvent
	public void onUnload(WorldEvent.Unload event){
		if(!event.getWorld().isClientSide()){
			IPSaveData.markDirty();
			ReservoirRegionDataStorage.get().markAllDirty();
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onServerStopped(ServerStoppedEvent event){
		ImmersivePetroleum.log.debug("[ReservoirIslands]: Clearing Cache...");
		ReservoirHandler.clearCache();
	}
	
	@SubscribeEvent
	public void handleBoatImmunity(LivingAttackEvent event){
		if(event.getSource() == DamageSource.LAVA || event.getSource() == DamageSource.ON_FIRE || event.getSource() == DamageSource.IN_FIRE){
			LivingEntity entity = event.getEntityLiving();
			if(entity.getVehicle() instanceof MotorboatEntity boat){
				if(boat.isFireproof){
					event.setCanceled(true);
					return;
				}
			}
			
			if(entity.getRemainingFireTicks() > 0 && entity.getEffect(IPEffects.ANTI_DISMOUNT_FIRE.get()) != null){
				entity.clearFire();
				entity.removeEffect(IPEffects.ANTI_DISMOUNT_FIRE.get());
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void handleBoatImmunity(PlayerTickEvent event){
		Player entity = event.player;
		if(entity.isOnFire() && entity.getVehicle() instanceof MotorboatEntity boat){
			if(boat.isFireproof){
				entity.clearFire();
				boat.setSharedFlag(0, false);
			}
		}
	}
	
	/**
	 * Handles dismounting the Speedboat while in lava to trying avoid getting burned
	 */
	@SubscribeEvent
	public void handleDismountingBoat(EntityMountEvent event){
		if(event.getEntityMounting() == null){
			return;
		}
		
		if(event.getEntityMounting() instanceof LivingEntity living && event.getEntityBeingMounted() instanceof MotorboatEntity boat){
			if(event.isDismounting()){
				if(boat.isFireproof){
					FluidState fluidstate = event.getWorldObj().getBlockState(new BlockPos(boat.position().add(0.5, 0, 0.5))).getFluidState();
					if(fluidstate != Fluids.EMPTY.defaultFluidState() && fluidstate.is(FluidTags.LAVA)){
						living.addEffect(new MobEffectInstance(IPEffects.ANTI_DISMOUNT_FIRE.get(), 1, 0, false, false));
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void handleLubricatingMachinesServer(WorldTickEvent event){
		if(event.phase == Phase.END){
			handleLubricatingMachines(event.world);
		}
	}
	
	static final Random random = new Random();
	@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
	public static void handleLubricatingMachines(Level world){
		Set<LubricatedTileInfo> toRemove = new HashSet<>();
		for(LubricatedTileInfo info:LubricatedHandler.lubricatedTiles){
			if(info.world == world.dimension() && world.isAreaLoaded(info.pos, 0)){
				BlockEntity te = world.getBlockEntity(info.pos);
				ILubricationHandler lubeHandler = LubricatedHandler.getHandlerForTile(te);
				if(lubeHandler != null){
					if(lubeHandler.isMachineEnabled(world, te)){
						if(world.isClientSide){
							lubeHandler.lubricateClient((ClientLevel) world, info.lubricant, info.ticks, te);
						}else{
							lubeHandler.lubricateServer((ServerLevel) world, info.lubricant, info.ticks, te);
						}
					}
					
					if(world.isClientSide){
						if(te instanceof MultiblockPartBlockEntity<?> part){
							
							Vec3i size = lubeHandler.getStructureDimensions();
							int numBlocks = (int) (size.getX() * size.getY() * size.getZ() * 0.25F);
							for(int i = 0;i < numBlocks;i++){
								BlockPos pos = part.getBlockPosForPos(new BlockPos(size.getX() * random.nextFloat(), size.getY() * random.nextFloat(), size.getZ() * random.nextFloat()));
								
								if(world.getBlockState(pos).getBlock() != Blocks.AIR && world.getBlockEntity(pos) instanceof MultiblockPartBlockEntity part2 && part2.master() == part.master()){
									for(Direction facing:Direction.Plane.HORIZONTAL){
										if(world.random.nextInt(30) == 0){
											Vec3i direction = facing.getNormal();
											
											float x = (pos.getX() + .5f) + (direction.getX() * .65f);
											float y = pos.getY() + 1;
											float z = (pos.getZ() + .5f) + (direction.getZ() * .65f);
											
											world.addParticle(ParticleTypes.FALLING_HONEY, x, y, z, 0, 0, 0);
										}
									}
								}
							}
						}
					}
					
					if(info.ticks-- <= 0)
						toRemove.add(info);
				}
			}
		}
		
		for(LubricatedTileInfo info:toRemove){
			LubricatedHandler.lubricatedTiles.remove(info);
		}
	}
	
	@SubscribeEvent
	public void onEntityJoiningWorld(EntityJoinWorldEvent event){
		if(event.getEntity() instanceof Player player){
			if(event.getEntity() instanceof FakePlayer){
				return;
			}
			
			if(IPServerConfig.MISCELLANEOUS.autounlock_recipes.get()){
				List<Recipe<?>> l = new ArrayList<>();
				Collection<Recipe<?>> recipes = event.getWorld().getRecipeManager().getRecipes();
				recipes.forEach(recipe -> {
					ResourceLocation name = recipe.getId();
					if(name.getNamespace().equals(ImmersivePetroleum.MODID)){
						l.add(recipe);
					}
				});
				
				player.awardRecipes(l);
			}
		}
	}
	
	@SubscribeEvent
	public void livingDeath(LivingDeathEvent event){
		if(event.getEntityLiving() instanceof Skeleton skelly && !skelly.level.isClientSide){
			DamageSource src = event.getSource();
			if(src.getEntity() instanceof Player player && !player.level.isClientSide){
				if(player.getVehicle() instanceof MotorboatEntity motorboat && !motorboat.level.isClientSide){
					if(src.isProjectile() && motorboat.isSpinningFastEnough() && motorboat.hasRudders){
						Utils.unlockIPAdvancement(player, "main/rudders");
					}
				}
			}
		}
	}
	
	public static final Map<ResourceLocation, List<BlockPos>> napalmPositions = new HashMap<>();
	public static final Map<ResourceLocation, List<BlockPos>> toRemove = new HashMap<>();
	
	@SubscribeEvent
	public void handleNapalm(WorldTickEvent event){
		ResourceLocation d = event.world.dimension().location();
		
		if(event.phase == Phase.START){
			toRemove.put(d, new ArrayList<>());
			if(napalmPositions.get(d) != null){
				List<BlockPos> iterate = new ArrayList<>(napalmPositions.get(d));
				for(BlockPos position:iterate){
					BlockState state = event.world.getBlockState(position);
					if(state.getBlock() instanceof LiquidBlock && state.getBlock() == IPContent.Fluids.NAPALM.block().get()){
						NapalmFluid.processFire(IPContent.Fluids.NAPALM, event.world, position);
					}
					toRemove.get(d).add(position);
				}
			}
		}else if(event.phase == Phase.END){
			if(toRemove.get(d) != null && napalmPositions.get(d) != null){
				for(BlockPos position:toRemove.get(d)){
					napalmPositions.get(d).remove(position);
				}
			}
		}
	}
}
