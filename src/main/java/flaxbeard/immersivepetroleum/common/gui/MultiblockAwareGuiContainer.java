package flaxbeard.immersivepetroleum.common.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 * @author TwistedGate © 2021
 */
public abstract class MultiblockAwareGuiContainer<T extends MultiblockPartBlockEntity<T>> extends IEContainerMenu{
	static final Vec3i ONE = new Vec3i(1, 1, 1);

	public MultiblockAwareGuiContainer(MenuContext ctx){
		super(ctx);
	}

	/**
	 * Returns the maximum distance in blocks to the multiblock befor the GUI get's closed automaticly
	 */
	public int getMaxDistance(){
		return 5;
	}

	/*IEContainerMenu now evaluates stillValid using the isValid predicate parameter of the passed MenuContext.
	* However, since the MenuContext constructor is protected, it cannot be accessed by this class.
	* I'll use the blockCtx method of IEContainerMenu, which applies its own validation.*/

	/*@Override
	public boolean stillValid(@Nonnull Player player){
		if(this.inv != null){
			BlockPos min = this.tile.getBlockPosForPos(BlockPos.ZERO);
			BlockPos max = this.tile.getBlockPosForPos(this.templateSize);
			
			AABB box = new AABB(min, max).inflate(getMaxDistance());
			
			return box.intersects(player.getBoundingBox());
		}
		
		return false;
	}*/
	
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
