package flaxbeard.immersivepetroleum.client.gui.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import flaxbeard.immersivepetroleum.client.utils.MCUtil;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.cfg.IPClientConfig;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MaterialColor;

public class PipeConfig extends Button{
	static final Button.OnPress NO_ACTION = b -> {
	};
	
	public static final int EMPTY = 0x00;
	
	public static final int PIPE_NORMAL = 0x01;
	
	public static final int PIPE_PERFORATED = 0x02;
	
	public static final int PIPE_PERFORATED_FIXED = 0x03;
	
	private final int dynTextureWidth, dynTextureHeight;
	private final DynamicTexture gridTexture;
	private final RenderType gridTextureRenderType;
	private final ResourceLocation dynTextureRL;
	
	private final int pipeNormalColor;
	private final int pipePerforatedColor;
	private final int pipePerforatedFixedColor;
	
	protected Grid grid;
	protected ColumnPos tilePos;
	protected int gridWidthScaled, gridHeightScaled;
	protected int gridScale;
	public PipeConfig(DerrickTileEntity tile, int x, int y, int width, int height, int gridWidth, int gridHeight, int gridScale){
		super(x, y, width, height, TextComponent.EMPTY, NO_ACTION);
		this.tilePos = new ColumnPos(tile.getBlockPos());
		
		this.grid = new Grid(gridWidth, gridHeight);
		copyGridFrom(tile.gridStorage);
		this.gridWidthScaled = gridWidth * gridScale;
		this.gridHeightScaled = gridHeight * gridScale;
		this.gridScale = Mth.clamp(gridScale, 1, Integer.MAX_VALUE);
		
		this.dynTextureWidth = gridWidth;
		this.dynTextureHeight = gridHeight;
		this.gridTexture = new DynamicTexture(this.dynTextureWidth, this.dynTextureHeight, true);
		this.dynTextureRL = ResourceUtils.ip("pipegrid/" + this.hashCode());
		MCUtil.getTextureManager().register(this.dynTextureRL, this.gridTexture);
		this.gridTextureRenderType = RenderType.text(this.dynTextureRL);
		
		this.pipeNormalColor = Integer.valueOf(IPClientConfig.GRID_COLORS.pipe_normal_color.get(), 16);
		this.pipePerforatedColor = Integer.valueOf(IPClientConfig.GRID_COLORS.pipe_perforated_color.get(), 16);
		this.pipePerforatedFixedColor = Integer.valueOf(IPClientConfig.GRID_COLORS.pipe_perforated_fixed_color.get(), 16);
		
		updateTexture();
	}
	
	public void reset(DerrickTileEntity tile){
		copyGridFrom(tile.gridStorage);
		updateTexture();
	}
	
	private void copyGridFrom(PipeConfig.Grid grid){
		if(grid != null && grid.width == this.grid.width && grid.height == this.grid.height){
			System.arraycopy(grid.array, 0, this.grid.array, 0, this.grid.array.length);
			this.grid.changed = true;
		}
	}
	
	public PipeConfig.Grid getGrid(){
		return this.grid;
	}
	
	/** This has to be called at the end of its life! */
	public void dispose(){
		this.gridTexture.close();
		MCUtil.getTextureManager().release(this.dynTextureRL);
	}
	
	public void updateTexture(){
		NativeImage image = this.gridTexture.getPixels();
		int texCenterX = this.grid.width / this.gridScale;
		int texCenterY = this.grid.height / this.gridScale;
		
		ClientLevel world = MCUtil.getLevel();
		
		for(int gy = 0;gy < this.grid.getHeight();gy++){
			for(int gx = 0;gx < this.grid.getWidth();gx++){
				int color = 0;
				
				switch(this.grid.get(gx, gy)){
					case EMPTY -> {
						if((gx >= texCenterX - 2 && gx <= texCenterX + 2) && (gy >= texCenterY - 2 && gy <= texCenterY + 2)){
							color = 0x000000;
						}else{
							int px = gx - (this.grid.getWidth() / 2);
							int py = gy - (this.grid.getHeight() / 2);
							
							ColumnPos c = new ColumnPos(this.tilePos.x + px, this.tilePos.z + py);
							int y = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(c.x, 0, c.z)).getY();
							
							BlockPos p;
							BlockState state;
							do{
								--y;
								p = new BlockPos(c.x, y, c.z);
								state = world.getBlockState(p);
							}while(state.getMapColor(world, p) == MaterialColor.NONE && y > 0);
							
							int tmp = world.getBlockState(p).getMapColor(world, p).col;
							float f = 0.5F;
							int r = (int) (((tmp >> 16) & 0xFF) * f);
							int g = (int) (((tmp >> 8) & 0xFF) * f);
							int b = (int) (((tmp >> 0) & 0xFF) * f);
							
							color = (r << 16 | g << 8 | b);
						}
					}
					case PIPE_NORMAL -> {
						color = this.pipeNormalColor;
					}
					case PIPE_PERFORATED -> {
						color = this.pipePerforatedColor;
					}
					case PIPE_PERFORATED_FIXED -> {
						color = this.pipePerforatedFixedColor;
					}
				}
				
				image.setPixelRGBA(gx, gy, toABGR(color));
			}
		}
		
		this.gridTexture.upload();
	}
	
	@Override
	public void render(PoseStack matrix, int mx, int my, float partialTicks){
		MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		
		VertexConsumer builder = buffer.getBuffer(this.gridTextureRenderType);
		matrix.pushPose();
		{
			matrix.translate(this.x, this.y, 0);
			Matrix4f mat = matrix.last().pose();
			int x = this.grid.width * this.gridScale;
			int y = this.grid.height * this.gridScale;
			builder.vertex(mat, 0, y, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 1.0F).uv2(0xF000F0).endVertex();
			builder.vertex(mat, x, y, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 1.0F).uv2(0xF000F0).endVertex();
			builder.vertex(mat, x, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 0.0F).uv2(0xF000F0).endVertex();
			builder.vertex(mat, 0, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F).uv2(0xF000F0).endVertex();
		}
		matrix.popPose();
		buffer.endBatch();
		
		List<Component> tooltip = new ArrayList<>();
		
		if((mx >= this.x && mx < (this.x + this.width)) && (my >= this.y && my < (this.y + this.height))){
			int x = (mx - this.x) / this.gridScale;
			int y = (my - this.y) / this.gridScale;
			
			int px = x - (this.grid.getWidth() / 2);
			int py = y - (this.grid.getHeight() / 2);
			
			if((px >= -2 && px <= 2) && (py >= -2 && py <= 2)){
				tooltip.add(new TextComponent("Center (Derrick)"));
			}else{
				String dir = "";
				if(py < 0){
					dir += "North";
				}else if(py > 0){
					dir += "South";
				}
				if(px != 0){
					if(dir.length() > 0){
						dir += "-";
					}
					
					if(px < 0){
						dir += "West";
					}else if(px > 0){
						dir += "East";
					}
				}
				
				tooltip.add(new TextComponent("§n" + dir));
			}
			
			tooltip.add(new TextComponent(String.format(Locale.ENGLISH, "X: %d §7(%d)", (this.tilePos.x + px), px)));
			tooltip.add(new TextComponent(String.format(Locale.ENGLISH, "Z: %d §7(%d)", (this.tilePos.z + py), py)));
			
			int i = this.grid.get(x, y);
			if(i > EMPTY){
				if(i == PIPE_NORMAL){
					tooltip.add(new TextComponent("Normal Pipe"));
				}else if(i == PIPE_PERFORATED){
					tooltip.add(new TextComponent("Perforated Pipe"));
				}else if(i == PIPE_PERFORATED_FIXED){
					tooltip.add(new TextComponent("Perforated Pipe §c(Fixed)§r"));
				}
			}
			
			int xa = this.x + (x * this.gridScale);
			int ya = this.y + (y * this.gridScale);
			GuiComponent.fill(matrix, xa, ya, xa + this.gridScale, ya + this.gridScale, 0x7FFFFFFF);
		}
		
		if(!tooltip.isEmpty()){
			/*
			Minecraft mc = Minecraft.getInstance();
			int width = mc.getWindow().getGuiScaledWidth();
			int height = mc.getWindow().getGuiScaledHeight();
			GuiUtils.drawHoveringText(matrix, tooltip, mx, my, width, height, -1, mc.font);
			*/
			
			// Draw my own crude tooltip, to have *something* here instead of nothing
			
			Font font = MCUtil.getFont();
			int lHeight = font.lineHeight + 2;
			int margin = 3;
			int width = 0;
			int height = lHeight * tooltip.size();
			for(Component c:tooltip){
				int sw = font.width(c);
				if(sw > width)
					width = sw;
			}
			
			mx += 12;
			my -= height;
			
			int x = mx - margin;
			int y = my - margin;
			int w = mx + width + margin;
			int h = my + height;
			
			int fill = 0xFF000000;
			int border = 0xFF3F3FFF;
			
			matrix.pushPose();
			{
				GuiComponent.fill(matrix, x, y, w, h, fill);
				GuiComponent.fill(matrix, x, y, x + 1, h, border);
				GuiComponent.fill(matrix, x, y, w, y + 1, border);
				GuiComponent.fill(matrix, w - 1, y, w, h, border);
				GuiComponent.fill(matrix, x, h - 1, w, h, border);
				
				for(int i = 0;i < tooltip.size();i++){
					GuiComponent.drawString(matrix, font, tooltip.get(i), mx, my + lHeight * i, -1);
				}
			}
			matrix.popPose();
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		if(isValidClickButton(button)){
			boolean flag = this.clicked(mouseX, mouseY);
			if(flag){
				this.playDownSound(Minecraft.getInstance().getSoundManager());
				onGridClick((int) (mouseX - this.x) / this.gridScale, (int) (mouseY - this.y) / this.gridScale, button);
				return true;
			}
		}
		
		return false;
	}
	
	protected void onGridClick(int x, int y, int button){
		int type = this.grid.get(x, y);
		if((type == PIPE_NORMAL || type == PIPE_PERFORATED) && type != PIPE_PERFORATED_FIXED){
			int[] array = getPipeCount();
			if(type == PIPE_NORMAL && array[PIPE_PERFORATED] < 2){
				this.grid.set(x, y, PIPE_PERFORATED);
			}else{
				this.grid.set(x, y, PIPE_NORMAL);
			}
		}else{
			this.grid.clearGrid();
			this.grid.drawLine(this.grid.getWidth() / 2, this.grid.getHeight() / 2, x, y, 1);
			this.grid.set(this.grid.getWidth() / 2, this.grid.getHeight() / 2, PIPE_PERFORATED_FIXED);
			this.grid.set(Mth.clamp(x, 0, this.grid.getWidth() - 1), Mth.clamp(y, 0, this.grid.getHeight() - 1), PIPE_PERFORATED_FIXED);
		}
		updateTexture();
	}
	
	int[] pipeCountArray = new int[4];
	private int[] getPipeCount(){
		if(this.grid.changed){
			this.pipeCountArray = new int[4];
			for(int i = 0;i < this.grid.size();i++){
				int type = this.grid.get(i);
				this.pipeCountArray[type] += 1;
			}
			this.grid.changed = false;
		}
		return this.pipeCountArray;
	}
	
	@Override
	protected boolean isValidClickButton(int button){
		return button == 0;
	}
	
	public PipeConfig copyDataFrom(PipeConfig other){
		if(other != null){
			this.gridScale = other.gridScale;
			this.grid = other.grid;
			updateTexture();
		}
		return this;
	}
	
	private static int toABGR(int rgb){
		int r = rgb >> 16 & 0xFF;
		int g = rgb >> 8 & 0xFF;
		int b = rgb & 0xFF;
		return 0xFF000000 | b << 16 | g << 8 | r;
	}
	
	public static class Grid{
		private boolean changed;
		private int width, height;
		private byte[] array;
		public Grid(int width, int height){
			this.width = width;
			this.height = height;
			this.array = new byte[width * height];
		}
		
		Grid(){
		}
		
		public int getWidth(){
			return this.width;
		}
		
		public int getHeight(){
			return this.height;
		}
		
		public int size(){
			return this.array.length;
		}
		
		public void set(int x, int y, int value){
			set(index(x, y), value);
		}
		
		public void set(int index, int value){
			if(index >= 0 && index < this.array.length && this.array[index] != value){
				this.array[index] = (byte) (value & 0xFF);
				this.changed = true;
			}
		}
		
		/** returns "unsigned" byte */
		public int get(int x, int y){
			return get(index(x, y));
		}
		
		/** returns "unsigned" byte */
		public int get(int index){
			if(index >= 0 && index < this.array.length){
				return ((int) this.array[index]) & 0xFF;
			}
			return 0;
		}
		
		public void clearGrid(){
			for(int i = 0;i < size();i++){
				this.array[i] = 0;
			}
		}
		
		public void drawLine(int xa, int ya, int xb, int yb, int value){
			xa = Mth.clamp(xa, 0, this.width - 1);
			xb = Mth.clamp(xb, 0, this.width - 1);
			ya = Mth.clamp(ya, 0, this.height - 1);
			yb = Mth.clamp(yb, 0, this.height - 1);
			
			int dx = xb - xa;
			int dy = yb - ya;
			
			int length = Math.max(Math.abs(dx), Math.abs(dy));
			float tx = dx / (float) length;
			float ty = dy / (float) length;
			
			float x = xa, y = ya;
			int lx = -1, ly = -1;
			for(int i = 1;i <= length;i++){
				int cx = Math.round(x);
				int cy = Math.round(y);
				
				if(!(lx == cx && ly == cy)){
					set(cx, cy, value);
					lx = cx;
					ly = cy;
				}
				
				x += tx;
				y += ty;
			}
		}
		
		int index(int x, int y){
			return this.width * y + x;
		}
		
		public CompoundTag toCompound(){
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("width", this.width);
			nbt.putInt("height", this.height);
			nbt.putByteArray("grid", this.array);
			return nbt;
		}
		
		public static Grid fromCompound(CompoundTag nbt){
			Grid grid = new Grid();
			grid.width = nbt.getInt("width");
			grid.height = nbt.getInt("height");
			grid.array = nbt.getByteArray("grid");
			
			if(grid.array.length != (grid.width * grid.height)){
				throw new IllegalStateException("Grid width and height don't match array.");
			}
			
			return grid;
		}
	}
}
