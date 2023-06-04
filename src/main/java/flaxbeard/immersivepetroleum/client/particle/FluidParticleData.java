package flaxbeard.immersivepetroleum.client.particle;

import javax.annotation.Nonnull;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidParticleData implements ParticleOptions{
	public static final Codec<FluidParticleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.fieldOf("fluid").forGetter(data -> ForgeRegistries.FLUIDS.getKey(data.fluid).toString())).apply(instance, FluidParticleData::new));
	
	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<FluidParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>(){
		@Override
		@Nonnull
		public FluidParticleData fromCommand(@Nonnull ParticleType<FluidParticleData> particleTypeIn, StringReader reader){
			String name = reader.getString();
			return new FluidParticleData(name);
		}
		
		@Override
		@Nonnull
		public FluidParticleData fromNetwork(@Nonnull ParticleType<FluidParticleData> particleTypeIn, FriendlyByteBuf buffer){
			String name = buffer.readUtf();
			return new FluidParticleData(name);
		}
	};
	
	private final Fluid fluid;
	public FluidParticleData(String name){
		this(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name)));
	}
	
	public FluidParticleData(Fluid fluid){
		this.fluid = fluid;
	}
	
	@Override
	@Nonnull
	public ParticleType<FluidParticleData> getType(){
		return IPParticleTypes.FLUID_SPILL;
	}
	
	@Override
	public void writeToNetwork(FriendlyByteBuf buffer){
		buffer.writeUtf(ForgeRegistries.FLUIDS.getKey(this.fluid).toString());
	}
	
	@Override
	@Nonnull
	public String writeToString(){
		return ForgeRegistries.FLUIDS.getKey(this.fluid).toString();
	}
	
	@OnlyIn(Dist.CLIENT)
	public Fluid getFluid(){
		return this.fluid;
	}
}
