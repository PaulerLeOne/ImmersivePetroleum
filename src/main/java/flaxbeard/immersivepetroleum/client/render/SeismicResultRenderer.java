package flaxbeard.immersivepetroleum.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

import flaxbeard.immersivepetroleum.client.render.dyn.DynamicTextureWrapper;
import flaxbeard.immersivepetroleum.client.utils.MCUtil;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.items.SurveyResultItem;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import flaxbeard.immersivepetroleum.common.util.survey.ISurveyInfo;
import flaxbeard.immersivepetroleum.common.util.survey.SurveyScan;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author TwistedGate
 */
public class SeismicResultRenderer{
	
	@SubscribeEvent
	public void renderHandEvent(RenderHandEvent event){
		ItemStack stack = event.getItemStack();
		if(stack.getItem().equals(IPContent.Items.SURVEYRESULT.get()) && stack.hasTag() && stack.getTagElement("surveyscan") != null){
			event.setCanceled(true);
		}
	}
	
	static final Tesselator TESSELATOR = new Tesselator();
	
	private static final ResourceLocation OVERLAY = ResourceUtils.ip("textures/gui/seismic_overlay.png");
	
	@SubscribeEvent
	public void renderGameOverlayEvent(RenderGuiOverlayEvent.Post event){
		Minecraft mc = Minecraft.getInstance();
		
		if(mc.player != null && event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_LIST.id())){
			ItemStack main = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
			ItemStack off = mc.player.getItemInHand(InteractionHand.OFF_HAND); // TODO Offhand variant?
			
			if((!main.isEmpty() && main.getItem() == IPContent.Items.SURVEYRESULT.get()) || (!off.isEmpty() && off.getItem() == IPContent.Items.SURVEYRESULT.get())){
				PoseStack matrix = event.getPoseStack();
				
				int guiScaledWidth = event.getWindow().getGuiScaledWidth();
				int guiScaledHeight = event.getWindow().getGuiScaledHeight();
				
				float xCenter = guiScaledWidth / 2F;
				float yCenter = guiScaledHeight / 2F;
				
				if(ISurveyInfo.from(main) instanceof SurveyScan scan){
					DynamicTextureWrapper wrapper = DynamicTextureWrapper.getOrCreate(SurveyScan.SCAN_SIZE, SurveyScan.SCAN_SIZE, scan);
					
					if(wrapper != null){
						MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(TESSELATOR.getBuilder());
						
						matrix.pushPose();
						{
							matrix.translate(xCenter, yCenter, 0);
							matrix.scale(2F, 2F, 2F);
							
							matrix.pushPose();
							{
								matrix.translate(-wrapper.width / 2F, -wrapper.height / 2F, 0);
								
								int a = wrapper.width;
								int b = wrapper.height;
								VertexConsumer builder = buffer.getBuffer(wrapper.renderType);
								builder.defaultColor(255, 255, 255, 255);
								Matrix4f mat = matrix.last().pose();
								
								builder.vertex(mat, 0, 0, 0).uv(1.0F, 1.0F).uv2(0xF000F0).endVertex();
								builder.vertex(mat, 0, b, 0).uv(1.0F, 0.0F).uv2(0xF000F0).endVertex();
								builder.vertex(mat, a, b, 0).uv(0.0F, 0.0F).uv2(0xF000F0).endVertex();
								builder.vertex(mat, a, 0, 0).uv(0.0F, 1.0F).uv2(0xF000F0).endVertex();
							}
							matrix.popPose();
							
							matrix.pushPose();
							{
								final int w = 85;
								final int h = 85;
								final float u = w / 256F;
								final float v = h / 256F;
								
								matrix.translate(-w / 2F, -h / 2F, 1);
								
								VertexConsumer builder = buffer.getBuffer(RenderType.text(OVERLAY));
								builder.defaultColor(255, 255, 255, 255);
								Matrix4f mat = matrix.last().pose();
								
								builder.vertex(mat, 0, 0, 0).uv(0.0F, 0.0F).uv2(0xF000F0).endVertex();
								builder.vertex(mat, 0, h, 0).uv(0.0F, v).uv2(0xF000F0).endVertex();
								builder.vertex(mat, w, h, 0).uv(u, v).uv2(0xF000F0).endVertex();
								builder.vertex(mat, w, 0, 0).uv(u, 0.0F).uv2(0xF000F0).endVertex();
							}
							matrix.popPose();
						}
						matrix.popPose();
						
						buffer.endBatch();
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onRenderItemFrame(RenderItemInFrameEvent event){
		if(event.getItemStack().getItem() instanceof SurveyResultItem && MCUtil.getPlayer().distanceTo(event.getItemFrameEntity()) < 1000){
			if(ISurveyInfo.from(event.getItemStack()) instanceof SurveyScan scan){
				DynamicTextureWrapper wrapper = DynamicTextureWrapper.getOrCreate(SurveyScan.SCAN_SIZE, SurveyScan.SCAN_SIZE, scan);
				
				if(wrapper != null){
					PoseStack matrix = event.getPoseStack();
					// MultiBufferSource buffer = event.getMultiBufferSource(); // Breaks things left and right
					MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(TESSELATOR.getBuilder());
					
					matrix.pushPose();
					{
						int light = event.getPackedLight();
						int rot = event.getItemFrameEntity().getRotation();
						float scale = 1F / SurveyScan.SCAN_SIZE;
						matrix.mulPose(new Quaternion(0, 0, 180F - -rot * 45F, true));
						matrix.translate(-0.5F, -0.5F, -0.00625F);
						matrix.scale(scale, scale, 1F);
						
						matrix.pushPose();
						{
							int a = wrapper.width;
							int b = wrapper.height;
							VertexConsumer builder = buffer.getBuffer(wrapper.renderType);
							builder.defaultColor(255, 255, 255, 255);
							Matrix4f mat = matrix.last().pose();
							
							builder.vertex(mat, 0, 0, 0).uv(1.0F, 1.0F).uv2(light).endVertex();
							builder.vertex(mat, 0, b, 0).uv(1.0F, 0.0F).uv2(light).endVertex();
							builder.vertex(mat, a, b, 0).uv(0.0F, 0.0F).uv2(light).endVertex();
							builder.vertex(mat, a, 0, 0).uv(0.0F, 1.0F).uv2(light).endVertex();
						}
						matrix.popPose();
						
						// Only shows the grid, the rest is "cut off"
						matrix.pushPose();
						{
							final int w = SurveyScan.SCAN_SIZE;
							final int h = SurveyScan.SCAN_SIZE;
							final float u = (w + 10) / 256F;
							final float v = (h + 10) / 256F;
							
							matrix.translate(0, 0, -0.002F);
							
							VertexConsumer builder = buffer.getBuffer(RenderType.text(OVERLAY));
							builder.defaultColor(255, 255, 255, 255);
							Matrix4f mat = matrix.last().pose();
							
							builder.vertex(mat, 0, 0, 0).uv(0.0390625F, 0.0390625F).uv2(light).endVertex();
							builder.vertex(mat, 0, h, 0).uv(0.0390625F, v).uv2(light).endVertex();
							builder.vertex(mat, w, h, 0).uv(u, v).uv2(light).endVertex();
							builder.vertex(mat, w, 0, 0).uv(u, 0.0390625F).uv2(light).endVertex();
						}
						matrix.popPose();
					}
					matrix.popPose();
					
					buffer.endBatch();
					event.setCanceled(true);
				}
			}
		}
	}
}
