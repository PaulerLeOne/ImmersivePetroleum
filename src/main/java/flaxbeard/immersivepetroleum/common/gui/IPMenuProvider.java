package flaxbeard.immersivepetroleum.common.gui;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import blusunrize.immersiveengineering.common.register.IEContainerTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.RegistryObject;

public interface IPMenuProvider<T extends BlockEntity & IPMenuProvider<T>> extends IEBlockInterfaces.IInteractionObjectIE<T>{
	// This is a hack, and this whole interface should be replaced by something that does not depend on IE internals!
	
	default IEContainerTypes.BEContainer<? super T, ?> getContainerType(){
		return null;
	}
	
	@Nonnull
	BEContainerIP<? super T, ?> getContainerTypeIP();
	
	@Nonnull
	@Override
	default AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity){
		T master = getGuiMaster();
		Preconditions.checkNotNull(master);
		BEContainerIP<? super T, ?> type = getContainerTypeIP();
		return type.create(id, playerInventory, master);
	}
	
	record BEContainerIP<T extends BlockEntity, C extends IEBaseContainer<? super T>> (RegistryObject<MenuType<C>> type, IEContainerTypes.BEContainerConstructor<T, C> factory){
		public C create(int windowId, Inventory playerInv, T tile){
			return factory.construct(getType(), windowId, playerInv, tile);
		}
		
		public MenuType<C> getType(){
			return type.get();
		}
	}
}
