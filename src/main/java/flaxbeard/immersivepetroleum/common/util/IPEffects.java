package flaxbeard.immersivepetroleum.common.util;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import flaxbeard.immersivepetroleum.common.IPRegisters;
import flaxbeard.immersivepetroleum.common.entity.MotorboatEntity;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.EffectRenderer;
import net.minecraftforge.registries.RegistryObject;

// FIXME Redo this whole thing
public class IPEffects{
	
	/**
	 * This is only as a burn prevention for when someone dismounts the {@link MotorboatEntity} while that is in lava<br>
	 */
	public static final RegistryObject<IPEffect> ANTI_DISMOUNT_FIRE = IPRegisters.registerMobEffect("anti_fire", AntiFireEffect::new);
	
	public static void forceClassLoad(){
	}
	
	private static class AntiFireEffect extends IPEffect{
		public AntiFireEffect(){
			super(MobEffectCategory.BENEFICIAL, 0x7F7F7F);
		}
		
		@Override
		public void initializeClient(Consumer<EffectRenderer> consumer){
			consumer.accept(new EffectRenderer(){
				
				@Override
				public boolean shouldRender(MobEffectInstance effect){
					return false;
				}
				
				@Override
				public boolean shouldRenderHUD(MobEffectInstance effect){
					return false;
				}
				
				@Override
				public boolean shouldRenderInvText(MobEffectInstance effect){
					return false;
				}
				
				@Override
				public void renderInventoryEffect(MobEffectInstance effectInstance, EffectRenderingInventoryScreen<?> gui, PoseStack poseStack, int x, int y, float z){
				}
				
				@Override
				public void renderHUDEffect(MobEffectInstance effectInstance, GuiComponent gui, PoseStack poseStack, int x, int y, float z, float alpha){
				}
			});
		}
		
		@Override
		public void applyEffectTick(LivingEntity living, int amplifier){
			living.clearFire();
		}
	}
	
	public static class IPEffect extends MobEffect{
		protected IPEffect(MobEffectCategory type, int color){
			super(type, color);
		}
	}
}
