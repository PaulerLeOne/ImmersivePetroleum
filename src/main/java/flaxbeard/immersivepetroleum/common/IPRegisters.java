package flaxbeard.immersivepetroleum.common;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.util.IPEffects.IPEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IPRegisters{
	private static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, ImmersivePetroleum.MODID);
	private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, ImmersivePetroleum.MODID);
	private static final DeferredRegister<Fluid> FLUID_REGISTER = DeferredRegister.create(ForgeRegistries.FLUIDS, ImmersivePetroleum.MODID);
	private static final DeferredRegister<BlockEntityType<?>> TE_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ImmersivePetroleum.MODID);
	private static final DeferredRegister<EntityType<?>> ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, ImmersivePetroleum.MODID);
	private static final DeferredRegister<MenuType<?>> MENU_REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, ImmersivePetroleum.MODID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ImmersivePetroleum.MODID);
	private static final DeferredRegister<MobEffect> MOB_EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ImmersivePetroleum.MODID);
	
	public static void addRegistersToEventBus(IEventBus eventBus){
		FLUID_REGISTER.register(eventBus);
		BLOCK_REGISTER.register(eventBus);
		ITEM_REGISTER.register(eventBus);
		TE_REGISTER.register(eventBus);
		ENTITY_REGISTER.register(eventBus);
		MENU_REGISTER.register(eventBus);
		RECIPE_SERIALIZERS.register(eventBus);
		MOB_EFFECT.register(eventBus);
	}
	
	public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockConstructor){
		return registerBlock(name, blockConstructor, null);
	}
	
	public static <T extends Block> RegistryObject<T> registerMultiblockBlock(String name, Supplier<T> blockConstructor){
		return registerBlock(name, blockConstructor, block -> new BlockItem(block, new Item.Properties()));
	}
	
	public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockConstructor, @Nullable Function<T, ? extends BlockItem> blockItem){
		RegistryObject<T> block = BLOCK_REGISTER.register(name, blockConstructor);
		if(blockItem != null){
			registerItem(name, () -> blockItem.apply(block.get()));
		}
		return block;
	}
	
	public static <T extends IPBlockBase> RegistryObject<T> registerIPBlock(String name, Supplier<T> blockConstructor){
		RegistryObject<T> block = BLOCK_REGISTER.register(name, blockConstructor);
		
		registerItem(name, () -> block.get().blockItemSupplier().get());
		
		return block;
	}
	
	public static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> itemConstructor){
		return ITEM_REGISTER.register(name, itemConstructor);
	}
	
	public static <T extends Fluid> RegistryObject<T> registerFluid(String name, Supplier<T> fluidConstructor){
		return FLUID_REGISTER.register(name, fluidConstructor);
	}
	
	public static <T extends Fluid> RegistryObject<T> registerFlowingFluid(String name, Supplier<T> fluidConstructor){
		return FLUID_REGISTER.register(name, fluidConstructor);
	}
	
	public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerTE(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> valid){
		return TE_REGISTER.register(name, () -> new BlockEntityType<>(factory, ImmutableSet.of(valid.get()), null));
	}
	
	public static <T extends BlockEntity & IEBlockInterfaces.IGeneralMultiblock> MultiblockBEType<T> registerMultiblockTE(String name, MultiblockBEType.BEWithTypeConstructor<T> factory, Supplier<? extends Block> valid){
		return new MultiblockBEType<>(name, TE_REGISTER, factory, valid, state -> state.hasProperty(IEProperties.MULTIBLOCKSLAVE) && !state.getValue(IEProperties.MULTIBLOCKSLAVE));
	}
	
	public static <T extends EntityType<?>> RegistryObject<T> registerEntity(String name, Supplier<T> entityConstructor){
		return ENTITY_REGISTER.register(name, entityConstructor);
	}
	
	public static <T extends RecipeSerializer<?>> RegistryObject<T> registerSerializer(String name, Supplier<T> serializer){
		return RECIPE_SERIALIZERS.register(name, serializer);
	}
	
	public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenu(String name, Supplier<MenuType<T>> factory){
		return MENU_REGISTER.register(name, factory);
	}
	
	public static <T extends IPEffect> RegistryObject<T> registerMobEffect(String name, Supplier<T> constructor){
		return MOB_EFFECT.register(name, constructor);
	}
	
	private IPRegisters(){
	}
}
