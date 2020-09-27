package flaxbeard.immersivepetroleum.common.blocks.multiblocks;

import com.mojang.blaze3d.matrix.MatrixStack;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPContent.Multiblock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DistillationTowerMultiblock extends IETemplateMultiblock{
	public static final DistillationTowerMultiblock INSTANCE = new DistillationTowerMultiblock();
	
	private DistillationTowerMultiblock(){
		super(new ResourceLocation(ImmersivePetroleum.MODID, "multiblocks/distillationtower"),
				new BlockPos(0, 0, 0), new BlockPos(0, 1, 3), () -> IPContent.Multiblock.distillationtower.getDefaultState());
	}
	
	@Override
	public float getManualScale(){
		return 6;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean canRenderFormedStructure(){
		return true;
	}
	
	@OnlyIn(Dist.CLIENT)
	private static ItemStack renderStack;
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderFormedStructure(MatrixStack transform, IRenderTypeBuffer buffer){
		if(renderStack == null)
			renderStack = new ItemStack(Multiblock.distillationtower);
		
		// "Undo" the GUI Perspective Transform
		transform.scale(16.0F, 16.0F, 16.0F);
		transform.translate(0.030, 0.355, 0.300);
		transform.rotate(new Quaternion(0, -225, 0, true));
		transform.rotate(new Quaternion(-30, 0, 0, true));
		
		ClientUtils.mc().getItemRenderer().renderItem(
				renderStack,
				ItemCameraTransforms.TransformType.GUI,
				0xf000f0,
				OverlayTexture.NO_OVERLAY,
				transform, buffer);
	}
}
