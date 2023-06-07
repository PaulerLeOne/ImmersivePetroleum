package flaxbeard.immersivepetroleum.client.particle;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

@OnlyIn(Dist.CLIENT)
public class FluidSpill extends TextureSheetParticle{
	final double ogMotionX, ogMotionY, ogMotionZ;
	protected FluidSpill(Fluid fluid, ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ){
		super(world, x, y, z, motionX, motionY, motionZ);
		setSize(0.5F, 0.5F);
		setLifetime(50);
		this.quadSize = 4 / 16F;
		
		this.hasPhysics = true;
		
		this.yd = motionY;
		
		this.ogMotionX = motionX;
		this.ogMotionY = motionY;
		this.ogMotionZ = motionZ;
		
		FluidStack fs = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
		
		ResourceLocation location = fluid.getAttributes().getStillTexture(fs);
		TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(location);
		setSprite(sprite);
		
		int argb = fluid.getAttributes().getColor(fs);
		this.alpha = ((argb >> 24) & 255) / 255F;
		this.rCol = ((argb >> 16) & 255) / 255F;
		this.gCol = ((argb >> 8 & 255)) / 255F;
		this.bCol = (argb & 255) / 255F;
	}
	
	@Override
	public void tick(){
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if(this.age++ >= this.lifetime){
			this.remove();
		}else{
			this.yd -= 0.04D;
			this.move(this.xd, this.yd, this.zd);
			this.xd *= 0.98D;
			this.yd *= 0.98D;
			this.zd *= 0.98D;
			if(this.onGround){
				this.xd *= 0.7D;
				this.zd *= 0.7D;
			}
			
			this.hasPhysics = this.yd <= 0.0;
			
			this.quadSize *= 0.97D;
		}
	}
	
	@Override
	public void render(@Nonnull VertexConsumer buffer, @Nonnull Camera renderInfo, float partialTicks){
		super.render(buffer, renderInfo, partialTicks);
	}
	
	@Override
	@Nonnull
	public ParticleRenderType getRenderType(){
		return ParticleRenderType.TERRAIN_SHEET;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements ParticleProvider<FluidParticleData>{
		@Override
		public Particle createParticle(FluidParticleData type, @Nonnull ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			return new FluidSpill(type.getFluid(), world, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}
}
