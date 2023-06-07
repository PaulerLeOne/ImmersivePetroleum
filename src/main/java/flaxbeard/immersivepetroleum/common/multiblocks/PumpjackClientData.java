package flaxbeard.immersivepetroleum.common.multiblocks;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.client.utils.MCUtil;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.PumpjackTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

public class PumpjackClientData extends IPClientMultiblockProperties{
	public PumpjackClientData(){
		super(PumpjackMultiblock.INSTANCE, 0, 0, 0);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean canRenderFormedStructure(){
		return true;
	}
	
	@OnlyIn(Dist.CLIENT)
	private PumpjackTileEntity te;
	@OnlyIn(Dist.CLIENT)
	List<BakedQuad> list;
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderFormedStructure(PoseStack transform, MultiBufferSource buffer){
		if(this.te == null){
			this.te = new PumpjackTileEntity(IPTileTypes.PUMP.master(), BlockPos.ZERO, IPContent.Multiblock.PUMPJACK.get().defaultBlockState());
		}
		
		if(this.list == null){
			BlockState state = te.getBlockState();
			BakedModel model = MCUtil.getBlockRenderer().getBlockModel(state);
			this.list = model.getQuads(state, null, ApiUtils.RANDOM, EmptyModelData.INSTANCE);
		}
		
		if(this.list != null && this.list.size() > 0){
			Level world = MCUtil.getLevel();
			if(world != null){
				transform.pushPose();
				transform.translate(1, 0, 0);
				RenderUtils.renderModelTESRFast(this.list, buffer.getBuffer(RenderType.solid()), transform, 0xF000F0, OverlayTexture.NO_OVERLAY);
				
				transform.pushPose();
				transform.mulPose(rot);
				transform.translate(-2, -1, -1);
				ImmersivePetroleum.proxy.renderTile(this.te, buffer.getBuffer(RenderType.solid()), transform, buffer);
				transform.popPose();
				
				transform.popPose();
			}
		}
	}
	
	final Quaternion rot = new Quaternion(new Vector3f(0F, 1F, 0F), 90, true);
}
