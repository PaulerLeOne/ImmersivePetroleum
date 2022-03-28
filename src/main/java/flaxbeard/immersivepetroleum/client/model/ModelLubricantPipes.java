package flaxbeard.immersivepetroleum.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class ModelLubricantPipes{
	
	public static class Crusher extends IPModel{
		public static final String ID = "crusher_lubepipes";
		
		private ModelPart origin;
		public Crusher(){
			super(RenderType::entitySolid);
		}
		
		@Override
		public void init(){
			MeshDefinition meshDefinition = new MeshDefinition();
			
			PartDefinition origin_Definition = meshDefinition.getRoot().addOrReplaceChild("origin", singleCube(20, 8, 9, 12, 2, 2), PartPose.ZERO);
			
			origin_Definition.addOrReplaceChild("p1", singleCube(-1, -1, 0, 12, 2, 2), PartPose.offsetAndRotation(20, 9, 10, 0, (float) Math.toRadians(270), 0));
			origin_Definition.addOrReplaceChild("p2", singleCube(-1, -1, 0, 18, 2, 2), PartPose.offsetAndRotation(31, 9, -10, 0, (float) Math.toRadians(270), 0));
			origin_Definition.addOrReplaceChild("p3", singleCube(0, -1, -1, 40, 2, 2), PartPose.offsetAndRotation(30, 10, -10, 0, 0, (float) Math.toRadians(90)));
			origin_Definition.addOrReplaceChild("p5", singleCube(31, 8, 5, 1, 2, 2), PartPose.ZERO);
			origin_Definition.addOrReplaceChild("p6", singleCube(23, 48, -11, 6, 2, 2), PartPose.ZERO);
			origin_Definition.addOrReplaceChild("p7", singleCube(8, 8, 19, 10, 2, 2), PartPose.ZERO);
			origin_Definition.addOrReplaceChild("p8", singleCube(-1, -1, 0, 5, 2, 2), PartPose.offsetAndRotation(8, 9, 17, 0, (float) Math.toRadians(270), 0));
			origin_Definition.addOrReplaceChild("p9", singleCube(0, -1, -1, 14, 2, 2), PartPose.offsetAndRotation(7, 10, 17, 0, 0, (float) Math.toRadians(90)));
			
			this.origin = LayerDefinition.create(meshDefinition, 16, 16).bakeRoot().getChild("origin");
			
			ModelPartOLD origin = new ModelPartOLD(this, 0, 0);
			origin.addBox(20, 8, 9, 12, 2, 2);
			
			ModelPartOLD p1 = new ModelPartOLD(this, 0, 0);
			p1.setPos(20, 9, 10);
			p1.addBox(-1, -1, 0, 12, 2, 2);
			p1.yRot = (float) Math.toRadians(270);
			origin.addChild(p1);
			
			ModelPartOLD p2 = new ModelPartOLD(this, 0, 0);
			p2.setPos(31, 9, -10);
			p2.addBox(-1, -1, 0, 18, 2, 2);
			p2.yRot = (float) Math.toRadians(270);
			origin.addChild(p2);
			
			ModelPartOLD p3 = new ModelPartOLD(this, 0, 0);
			p3.setPos(30, 10, -10);
			p3.addBox(0, -1, -1, 40, 2, 2);
			p3.zRot = (float) Math.toRadians(90);
			origin.addChild(p3);
			
			ModelPartOLD p5 = new ModelPartOLD(this, 0, 0);
			p5.addBox(31, 8, 5, 1, 2, 2);
			origin.addChild(p5);
			
			ModelPartOLD p6 = new ModelPartOLD(this, 0, 0);
			p6.addBox(23, 48, -11, 6, 2, 2);
			origin.addChild(p6);
			
			ModelPartOLD p7 = new ModelPartOLD(this, 0, 0);
			p7.addBox(8, 8, 19, 10, 2, 2);
			origin.addChild(p7);
			
			ModelPartOLD p8 = new ModelPartOLD(this, 0, 0);
			p8.setPos(8, 9, 17);
			p8.addBox(-1, -1, 0, 5, 2, 2);
			p8.yRot = (float) Math.toRadians(270);
			origin.addChild(p8);
			
			ModelPartOLD p9 = new ModelPartOLD(this, 0, 0);
			p9.setPos(7, 10, 17);
			p9.addBox(0, -1, -1, 14, 2, 2);
			p9.zRot = (float) Math.toRadians(90);
			origin.addChild(p9);
		}
		
		@Override
		public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha){
			this.origin.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}
	
	public static class Excavator extends IPModel{
		public static final String ID_NORMAL = "excavator_lubepipes_normal";
		public static final String ID_MIRRORED = "excavator_lubepipes_mirrored";
		
		private ModelPart origin;
		private boolean mirrored;
		public Excavator(boolean mirror){
			super(RenderType::entitySolid);
			this.mirrored = mirror;
		}
		
		@Override
		public void init(){
			MeshDefinition meshDefinition = new MeshDefinition();
			
			if(this.mirrored){
				PartDefinition origin_Definition = meshDefinition.getRoot().addOrReplaceChild("origin", singleCube(51, 8, 6, 20, 2, 2), PartPose.ZERO);
				
				origin_Definition.addOrReplaceChild("p1", singleCube(-1, -1, 0, 6, 2, 2), PartPose.offsetAndRotation(71, 9, 1, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p2", singleCube(-1, -1, 0, 4, 2, 2), PartPose.offsetAndRotation(53, 9, 3, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p3", singleCube(0, -1, -1, 6, 2, 2), PartPose.offsetAndRotation(52, 10, 3, 0, 0, (float) Math.toRadians(90)));
				origin_Definition.addOrReplaceChild("p4", singleCube(0, -1, -1, 9, 2, 2), PartPose.offsetAndRotation(52, 32, 8, 0, 0, (float) Math.toRadians(90)));
				origin_Definition.addOrReplaceChild("p5", singleCube(48, 39, 7, 3, 2, 2), PartPose.ZERO);
				origin_Definition.addOrReplaceChild("p6", singleCube(0, -1, -1, 18, 2, 2), PartPose.offsetAndRotation(52, 16, -1, 0, 0, (float) Math.toRadians(90)));
				origin_Definition.addOrReplaceChild("p7", singleCube(-1, -1, 0, 4, 2, 2), PartPose.offsetAndRotation(53, 15, -1, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p8", singleCube(-1, -1, 0, 7, 2, 2), PartPose.offsetAndRotation(53, 33, 1, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p9", singleCube(48, 39, 39, 3, 2, 2), PartPose.ZERO);
				origin_Definition.addOrReplaceChild("p10", singleCube(-1, -1, 0, 2, 2, 2), PartPose.offsetAndRotation(75, 9, 1, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p11", singleCube(73, 8, 2, 16, 2, 2), PartPose.ZERO);
				origin_Definition.addOrReplaceChild("p12", singleCube(-1, -1, 0, 4, 2, 2), PartPose.offsetAndRotation(89, 9, 5, 0, (float) Math.toRadians(270), 0));
				
				ModelPartOLD origin = new ModelPartOLD(this, 0, 0);
				origin.addBox(51, 8, 6, 20, 2, 2);
				
				ModelPartOLD p1 = new ModelPartOLD(this, 0, 0);
				p1.setPos(71, 9, 1);
				p1.addBox(-1, -1, 0, 6, 2, 2);
				p1.yRot = (float) Math.toRadians(270);
				origin.addChild(p1);
				
				ModelPartOLD p2 = new ModelPartOLD(this, 0, 0);
				p2.setPos(53, 9, 3);
				p2.addBox(-1, -1, 0, 4, 2, 2);
				p2.yRot = (float) Math.toRadians(270);
				origin.addChild(p2);
				
				ModelPartOLD p3 = new ModelPartOLD(this, 0, 0);
				p3.setPos(52, 10, 3);
				p3.addBox(0, -1, -1, 6, 2, 2);
				p3.zRot = (float) Math.toRadians(90);
				origin.addChild(p3);
				
				ModelPartOLD p4 = new ModelPartOLD(this, 0, 0);
				p4.setPos(52, 32, 8);
				p4.addBox(0, -1, -1, 9, 2, 2);
				p4.zRot = (float) Math.toRadians(90);
				origin.addChild(p4);
				
				ModelPartOLD p5 = new ModelPartOLD(this, 0, 0);
				p5.addBox(48, 39, 7, 3, 2, 2);
				origin.addChild(p5);
				
				ModelPartOLD p6 = new ModelPartOLD(this, 0, 0);
				p6.setPos(52, 16, -1);
				p6.addBox(0, -1, -1, 18, 2, 2);
				p6.zRot = (float) Math.toRadians(90);
				origin.addChild(p6);
				
				ModelPartOLD p7 = new ModelPartOLD(this, 0, 0);
				p7.setPos(53, 15, -1);
				p7.addBox(-1, -1, 0, 4, 2, 2);
				p7.yRot = (float) Math.toRadians(270);
				origin.addChild(p7);
				
				ModelPartOLD p8 = new ModelPartOLD(this, 0, 0);
				p8.setPos(53, 33, 1);
				p8.addBox(-1, -1, 0, 7, 2, 2);
				p8.yRot = (float) Math.toRadians(270);
				origin.addChild(p8);
				
				ModelPartOLD p9 = new ModelPartOLD(this, 0, 0);
				p9.addBox(48, 39, 39, 3, 2, 2);
				origin.addChild(p9);
				
				ModelPartOLD p10 = new ModelPartOLD(this, 0, 0);
				p10.setPos(75, 9, 1);
				p10.addBox(-1, -1, 0, 2, 2, 2);
				p10.yRot = (float) Math.toRadians(270);
				origin.addChild(p10);
				
				ModelPartOLD p11 = new ModelPartOLD(this, 0, 0);
				p11.addBox(73, 8, 2, 16, 2, 2);
				origin.addChild(p11);
				
				ModelPartOLD p12 = new ModelPartOLD(this, 0, 0);
				p12.setPos(89, 9, 5);
				p12.addBox(-1, -1, 0, 4, 2, 2);
				p12.yRot = (float) Math.toRadians(270);
				origin.addChild(p12);
				
			}else{
				PartDefinition origin_Definition = meshDefinition.getRoot().addOrReplaceChild("origin", singleCube(51, 8, 40, 20, 2, 2), PartPose.ZERO);
				
				origin_Definition.addOrReplaceChild("p1", singleCube(-1, -1, 0, 6, 2, 2), PartPose.offsetAndRotation(71, 9, 43, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p2", singleCube(-1, -1, 0, 4, 2, 2), PartPose.offsetAndRotation(53, 9, 43, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p3", singleCube(0, -1, -1, 6, 2, 2), PartPose.offsetAndRotation(52, 10, 45, 0, 0, (float) Math.toRadians(90)));
				origin_Definition.addOrReplaceChild("p4", singleCube(0, -1, -1, 9, 2, 2), PartPose.offsetAndRotation(52, 32, 40, 0, 0, (float) Math.toRadians(90)));
				origin_Definition.addOrReplaceChild("p5", singleCube(48, 39, 39, 3, 2, 2), PartPose.ZERO);
				origin_Definition.addOrReplaceChild("p6", singleCube(0, -1, -1, 18, 2, 2), PartPose.offsetAndRotation(52, 16, 49, 0, 0, (float) Math.toRadians(90)));
				origin_Definition.addOrReplaceChild("p7", singleCube(-1, -1, 0, 4, 2, 2), PartPose.offsetAndRotation(53, 15, 47, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p8", singleCube(-1, -1, 0, 7, 2, 2), PartPose.offsetAndRotation(53, 33, 42, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p9", singleCube(48, 39, 39, 3, 2, 2), PartPose.ZERO);
				origin_Definition.addOrReplaceChild("p10", singleCube(-1, -1, 0, 2, 2, 2), PartPose.offsetAndRotation(75, 9, 47, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p11", singleCube(73, 8, 44, 16, 2, 2), PartPose.ZERO);
				origin_Definition.addOrReplaceChild("p12", singleCube(-1, -1, 0, 4, 2, 2), PartPose.offsetAndRotation(89, 9, 41, 0, (float) Math.toRadians(270), 0));
				
				ModelPartOLD origin = new ModelPartOLD(this, 0, 0);
				origin.addBox(51, 8, 40, 20, 2, 2);
				
				ModelPartOLD p1 = new ModelPartOLD(this, 0, 0);
				p1.setPos(71, 9, 43);
				p1.addBox(-1, -1, 0, 6, 2, 2);
				p1.yRot = (float) Math.toRadians(270);
				origin.addChild(p1);
				
				ModelPartOLD p2 = new ModelPartOLD(this, 0, 0);
				p2.setPos(53, 9, 43);
				p2.addBox(-1, -1, 0, 4, 2, 2);
				p2.yRot = (float) Math.toRadians(270);
				origin.addChild(p2);
				
				ModelPartOLD p3 = new ModelPartOLD(this, 0, 0);
				p3.setPos(52, 10, 45);
				p3.addBox(0, -1, -1, 6, 2, 2);
				p3.zRot = (float) Math.toRadians(90);
				origin.addChild(p3);
				
				ModelPartOLD p4 = new ModelPartOLD(this, 0, 0);
				p4.setPos(52, 32, 40);
				p4.addBox(0, -1, -1, 9, 2, 2);
				p4.zRot = (float) Math.toRadians(90);
				origin.addChild(p4);
				
				ModelPartOLD p5 = new ModelPartOLD(this, 0, 0);
				p5.addBox(48, 39, 39, 3, 2, 2);
				origin.addChild(p5);
				
				ModelPartOLD p6 = new ModelPartOLD(this, 0, 0);
				p6.setPos(52, 16, 49);
				p6.addBox(0, -1, -1, 18, 2, 2);
				p6.zRot = (float) Math.toRadians(90);
				origin.addChild(p6);
				
				ModelPartOLD p7 = new ModelPartOLD(this, 0, 0);
				p7.setPos(53, 15, 47);
				p7.addBox(-1, -1, 0, 4, 2, 2);
				p7.yRot = (float) Math.toRadians(270);
				origin.addChild(p7);
				
				ModelPartOLD p8 = new ModelPartOLD(this, 0, 0);
				p8.setPos(53, 33, 42);
				p8.addBox(-1, -1, 0, 7, 2, 2);
				p8.yRot = (float) Math.toRadians(270);
				origin.addChild(p8);
				
				ModelPartOLD p9 = new ModelPartOLD(this, 0, 0);
				p9.addBox(48, 39, 39, 3, 2, 2);
				origin.addChild(p9);
				
				ModelPartOLD p10 = new ModelPartOLD(this, 0, 0);
				p10.setPos(75, 9, 47);
				p10.addBox(-1, -1, 0, 2, 2, 2);
				p10.yRot = (float) Math.toRadians(270);
				origin.addChild(p10);
				
				ModelPartOLD p11 = new ModelPartOLD(this, 0, 0);
				p11.addBox(73, 8, 44, 16, 2, 2);
				origin.addChild(p11);
				
				ModelPartOLD p12 = new ModelPartOLD(this, 0, 0);
				p12.setPos(89, 9, 41);
				p12.addBox(-1, -1, 0, 4, 2, 2);
				p12.yRot = (float) Math.toRadians(270);
				origin.addChild(p12);
			}
			
			this.origin = LayerDefinition.create(meshDefinition, 16, 16).bakeRoot().getChild("origin");
		}
		
		@Override
		public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha){
			this.origin.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}
	
	public static class Pumpjack extends IPModel{
		public static final String ID_NORMAL = "pumpjack_lubepipes_normal";
		public static final String ID_MIRRORED = "pumpjack_lubepipes_mirrored";
		
		private boolean mirrored = false;
		private ModelPart origin;
		public Pumpjack(boolean mirror){
			super(RenderType::entitySolid);
			this.mirrored = mirror;
		}
		
		@Override
		public void init(){
			MeshDefinition meshDefinition = new MeshDefinition();
			
			if(this.mirrored){
				PartDefinition origin_Definition = meshDefinition.getRoot().addOrReplaceChild("origin", singleCube(21, 8, 12, 15, 2, 2), PartPose.ZERO);
				
				origin_Definition.addOrReplaceChild("p1", singleCube(-1, -1, 0, 12, 2, 2), PartPose.offsetAndRotation(23, 9, 1, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p2", singleCube(-1, -1, 0, 13, 2, 2), PartPose.offsetAndRotation(38, 9, 13, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p3", singleCube(34, 8, 23, 2, 2, 2), PartPose.ZERO);
				origin_Definition.addOrReplaceChild("p4", singleCube(0, -1, -1, 30, 2, 2), PartPose.offsetAndRotation(33, 8, 24, 0, 0, (float) Math.toRadians(90)));
				origin_Definition.addOrReplaceChild("p5", singleCube(24, 36, 23, 8, 2, 2), PartPose.ZERO);
				origin_Definition.addOrReplaceChild("p6", singleCube(0, -1F, -1, 9, 2, 2), PartPose.offsetAndRotation(26, 9.01F, 0, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p7", singleCube(25, 8, 8.5F, 18, 2, 2), PartPose.ZERO);
				
				PartDefinition leg1_Definition = origin_Definition.addOrReplaceChild("leg1", empty(), PartPose.offsetAndRotation(56 - 13.6F, 8F, 12F, (float) Math.toRadians(9), (float) Math.toRadians(20), (float) Math.toRadians(-14)));
				leg1_Definition.addOrReplaceChild("leg2", singleCube(1F, -1F, -4F, 38, 2, 2), PartPose.rotation(0, 0, (float) Math.toRadians(90)));
				
				origin_Definition.addOrReplaceChild("p8", singleCube(0, 0, 0, 4, 2, 2), PartPose.offsetAndRotation(52.5F, 43.3F, 14.7F, 0, (float) Math.toRadians(30), 0));
				origin_Definition.addOrReplaceChild("p9", singleCube(0, -2, 0, 6, 2, 2), PartPose.offsetAndRotation(55f, 43.3f, 13f, 0, 0, (float) Math.toRadians(90)));
				
				ModelPartOLD origin = new ModelPartOLD(this, 0, 0);
				origin.addBox(21, 8, 12, 15, 2, 2);
				
				ModelPartOLD p1 = new ModelPartOLD(this, 0, 0);
				p1.setPos(23, 9, 1);
				p1.addBox(-1, -1, 0, 12, 2, 2);
				p1.yRot = (float) Math.toRadians(270);
				origin.addChild(p1);
				
				ModelPartOLD p2 = new ModelPartOLD(this, 0, 0);
				p2.setPos(38, 9, 13);
				p2.addBox(-1, -1, 0, 13, 2, 2);
				p2.yRot = (float) Math.toRadians(270);
				origin.addChild(p2);
				
				ModelPartOLD p3 = new ModelPartOLD(this, 0, 0);
				p3.addBox(34, 8, 23, 2, 2, 2);
				origin.addChild(p3);
				
				ModelPartOLD p4 = new ModelPartOLD(this, 0, 0);
				p4.setPos(33, 8, 24);
				p4.addBox(0, -1, -1, 30, 2, 2);
				p4.zRot = (float) Math.toRadians(90);
				origin.addChild(p4);
				
				ModelPartOLD p5 = new ModelPartOLD(this, 0, 0);
				p5.addBox(24, 36, 23, 8, 2, 2);
				origin.addChild(p5);
				
				ModelPartOLD p6 = new ModelPartOLD(this, 0, 0);
				p6.setPos(26, 9.01F, 0);
				p6.addBox(0, -1F, -1, 9, 2, 2);
				p6.yRot = (float) Math.toRadians(270);
				origin.addChild(p6);
				
				ModelPartOLD p7 = new ModelPartOLD(this, 0, 0);
				p7.addBox(25, 8, 8.5F, 18, 2, 2);
				origin.addChild(p7);
				
				ModelPartOLD leg1 = new ModelPartOLD(this, 0, 0);
				// leg1.addBox(-1F, 1F, -4F, 38, 2, 2);
				leg1.setPos(56 - 13.6F, 8F, 12F);
				leg1.xRot = (float) Math.toRadians(9);
				leg1.yRot = (float) Math.toRadians(20);
				leg1.zRot = (float) Math.toRadians(-14);
				origin.addChild(leg1);
				
				ModelPartOLD leg2 = new ModelPartOLD(this, 0, 0);
				leg2.addBox(1F, -1F, -4F, 38, 2, 2);
				leg2.zRot = (float) Math.toRadians(90);
				leg1.addChild(leg2);
				
				ModelPartOLD p8 = new ModelPartOLD(this, 0, 0);
				p8.setPos(52.5F, 43.3F, 14.7F);
				p8.addBox(0, 0, 0, 4, 2, 2);
				p8.yRot = (float) Math.toRadians(30);
				origin.addChild(p8);
				
				ModelPartOLD p10 = new ModelPartOLD(this, 0, 0);
				p10.setPos(55f, 43.3f, 13f);
				p10.addBox(0, -2, 0, 6, 2, 2);
				p10.zRot = (float) Math.toRadians(90);
				origin.addChild(p10);
				
			}else{
				PartDefinition origin_Definition = meshDefinition.getRoot().addOrReplaceChild("origin", singleCube(21, 8, 48 - 12 - 2, 15, 2, 2), PartPose.ZERO);
				
				origin_Definition.addOrReplaceChild("p1", singleCube(-1, -1, 0, 12, 2, 2), PartPose.offsetAndRotation(23, 9, 37, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p2", singleCube(-1, -1, 0, 13, 2, 2), PartPose.offsetAndRotation(38, 9, 24, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p3", singleCube(34, 8, 23, 2, 2, 2), PartPose.ZERO);
				origin_Definition.addOrReplaceChild("p4", singleCube(0, -1, -1, 30, 2, 2), PartPose.offsetAndRotation(33, 8, 24, 0, 0, (float) Math.toRadians(90)));
				origin_Definition.addOrReplaceChild("p5", singleCube(24, 36, 23, 8, 2, 2), PartPose.ZERO);
				origin_Definition.addOrReplaceChild("p6", singleCube(39, -1F, -1, 9, 2, 2), PartPose.offsetAndRotation(26, 9.01F, 0, 0, (float) Math.toRadians(270), 0));
				origin_Definition.addOrReplaceChild("p7", singleCube(25, 8, 38.5F, 18, 2, 2), PartPose.ZERO);
				
				PartDefinition leg1_Definition = origin_Definition.addOrReplaceChild("leg1", empty(), PartPose.offsetAndRotation(56 - 13.6F, 8F, 36F, (float) Math.toRadians(-10), (float) Math.toRadians(-20), (float) Math.toRadians(-15)));
				leg1_Definition.addOrReplaceChild("leg2", singleCube(1F, -1F, 3F, 38, 2, 2), PartPose.rotation(0, 0, (float) Math.toRadians(90)));
				
				origin_Definition.addOrReplaceChild("p8", singleCube(0, 0, 0, 4, 2, 2), PartPose.offsetAndRotation(53F, 43.3F, 46 - 14.3F, 0, (float) Math.toRadians(-30), 0));
				origin_Definition.addOrReplaceChild("p9", singleCube(0, -2, 0, 6, 2, 2), PartPose.offsetAndRotation(55f, 43.3f, 33f, 0, 0, (float) Math.toRadians(90)));
				
				ModelPartOLD origin = new ModelPartOLD(this, 0, 0);
				origin.addBox(21, 8, 48 - 12 - 2, 15, 2, 2);
				
				ModelPartOLD p1 = new ModelPartOLD(this, 0, 0);
				p1.setPos(23, 9, 37);
				p1.addBox(-1, -1, 0, 12, 2, 2);
				p1.yRot = (float) Math.toRadians(270);
				origin.addChild(p1);
				
				ModelPartOLD p2 = new ModelPartOLD(this, 0, 0);
				p2.setPos(38, 9, 24);
				p2.addBox(-1, -1, 0, 13, 2, 2);
				p2.yRot = (float) Math.toRadians(270);
				origin.addChild(p2);
				
				ModelPartOLD p3 = new ModelPartOLD(this, 0, 0);
				p3.addBox(34, 8, 23, 2, 2, 2);
				origin.addChild(p3);
				
				ModelPartOLD p4 = new ModelPartOLD(this, 0, 0);
				p4.setPos(33, 8, 24);
				p4.addBox(0, -1, -1, 30, 2, 2);
				p4.zRot = (float) Math.toRadians(90);
				origin.addChild(p4);
				
				ModelPartOLD p5 = new ModelPartOLD(this, 0, 0);
				p5.addBox(24, 36, 23, 8, 2, 2);
				origin.addChild(p5);
				
				ModelPartOLD p6 = new ModelPartOLD(this, 0, 0);
				p6.setPos(26, 9.01F, 0);
				p6.addBox(39, -1F, -1, 9, 2, 2);
				p6.yRot = (float) Math.toRadians(270);
				origin.addChild(p6);
				
				ModelPartOLD p7 = new ModelPartOLD(this, 0, 0);
				p7.addBox(25, 8, 38.5F, 18, 2, 2);
				origin.addChild(p7);
				
				ModelPartOLD leg1 = new ModelPartOLD(this, 0, 0);
				// leg1.addBox(-1F, 1F, -4F, 38, 2, 2);
				leg1.setPos(56 - 13.6F, 8F, 36F);
				leg1.xRot = (float) Math.toRadians(-10);
				leg1.yRot = (float) Math.toRadians(-20);
				leg1.zRot = (float) Math.toRadians(-15);
				origin.addChild(leg1);
				
				ModelPartOLD leg2 = new ModelPartOLD(this, 0, 0);
				leg2.addBox(1F, -1F, 3F, 38, 2, 2);
				leg2.zRot = (float) Math.toRadians(90);
				leg1.addChild(leg2);
				
				ModelPartOLD p8 = new ModelPartOLD(this, 0, 0);
				p8.setPos(53F, 43.3F, 46 - 14.3F);
				p8.addBox(0, 0, 0, 4, 2, 2);
				p8.yRot = (float) Math.toRadians(-30);
				origin.addChild(p8);
				
				ModelPartOLD p10 = new ModelPartOLD(this, 0, 0);
				p10.setPos(55f, 43.3f, 33f);
				p10.addBox(0, -2, 0, 6, 2, 2);
				p10.zRot = (float) Math.toRadians(90);
				origin.addChild(p10);
			}
			
			this.origin = LayerDefinition.create(meshDefinition, 16, 16).bakeRoot().getChild("origin");
		}
		
		@Override
		public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha){
			this.origin.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}
}
