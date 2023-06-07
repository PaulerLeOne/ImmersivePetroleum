package flaxbeard.immersivepetroleum.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import blusunrize.immersiveengineering.api.utils.CapabilityUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class IPItemStackHandler extends ItemStackHandler implements ICapabilityProvider{
	private static final Runnable EMPTY_RUN = () -> {};
	
	@Nonnull
	private Runnable onChange = EMPTY_RUN;
	public IPItemStackHandler(int invSize){
		super(invSize);
	}
	
	public void setTile(BlockEntity tile){
		this.onChange = tile != null ? tile::setChanged : EMPTY_RUN;
	}
	
	public void setInventoryForUpdate(Container inv){
		this.onChange = inv != null ? inv::setChanged : EMPTY_RUN;
	}
	
	protected void onContentsChanged(int slot){
		super.onContentsChanged(slot);
		this.onChange.run();
	}
	
	LazyOptional<IItemHandler> handler = CapabilityUtils.constantOptional(this);
	
	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return this.handler.cast();
		}
		
		return LazyOptional.empty();
	}
	
	public NonNullList<ItemStack> getContainedItems(){
		return this.stacks;
	}
}
