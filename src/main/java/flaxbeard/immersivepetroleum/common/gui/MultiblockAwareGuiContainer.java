package flaxbeard.immersivepetroleum.common.gui;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.phys.AABB;

/**
 * @author TwistedGate © 2021
 */
public class MultiblockAwareGuiContainer<T extends MultiblockPartBlockEntity<T>> extends IEBaseContainer<T>{
	static final Vec3i ONE = new Vec3i(1, 1, 1);
	
	protected BlockPos templateSize;
	public MultiblockAwareGuiContainer(MenuType<?> type, T tile, int id, IETemplateMultiblock template){
		super(type, tile, id);
		
		this.templateSize = new BlockPos(template.getSize(this.tile.getLevelNonnull())).subtract(ONE);
	}
	
	/**
	 * Returns the maximum distance in blocks to the multiblock befor the GUI get's closed automaticly
	 */
	public int getMaxDistance(){
		return 5;
	}
	
	@Override
	public boolean stillValid(@Nonnull Player player){
		if(this.inv != null){
			BlockPos min = this.tile.getBlockPosForPos(BlockPos.ZERO);
			BlockPos max = this.tile.getBlockPosForPos(this.templateSize);
			
			AABB box = new AABB(min, max).inflate(getMaxDistance());
			
			return box.intersects(player.getBoundingBox());
		}
		
		return false;
	}
	
	protected final void addPlayerInventorySlots(Inventory playerInventory, int x, int y){
		for(int i = 0;i < 3;i++){
			for(int j = 0;j < 9;j++){
				addSlot(new Slot(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18));
			}
		}
	}
	
	protected final void addPlayerHotbarSlots(Inventory playerInventory, int x, int y){
		for(int i = 0;i < 9;i++){
			addSlot(new Slot(playerInventory, i, x + i * 18, y));
		}
	}
}
