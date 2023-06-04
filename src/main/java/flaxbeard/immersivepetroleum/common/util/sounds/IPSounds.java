package flaxbeard.immersivepetroleum.common.util.sounds;

import java.util.HashSet;
import java.util.Set;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPRegisters;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

public class IPSounds{

	public final static RegistryObject<SoundEvent> FLARESTACK = register("flarestack_fire");
	public final static RegistryObject<SoundEvent> PROJECTOR = register("projector");
	
	static RegistryObject<SoundEvent> register(String name){
		ResourceLocation rl = ResourceUtils.ip(name);
		SoundEvent event = new SoundEvent(rl);
		return IPRegisters.registerSound(name, ()->event);
	}

	public static void forceload(){}
}
