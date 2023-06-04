package flaxbeard.immersivepetroleum.common;

import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.HydrotreaterTileEntity;
import flaxbeard.immersivepetroleum.common.gui.CokerUnitContainer;
import flaxbeard.immersivepetroleum.common.gui.DerrickContainer;
import flaxbeard.immersivepetroleum.common.gui.DistillationTowerContainer;
import flaxbeard.immersivepetroleum.common.gui.HydrotreaterContainer;
import flaxbeard.immersivepetroleum.common.gui.IPMenuProvider.BEContainerIP;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;

public class IPMenuTypes{
	public static final BEContainerIP<DistillationTowerTileEntity, DistillationTowerContainer> DISTILLATION_TOWER =
			registerBENew("distillation_tower", DistillationTowerContainer::makeServer, DistillationTowerContainer::makeClient);
	public static final BEContainerIP<DerrickTileEntity, DerrickContainer> DERRICK =
			registerBENew("derrick", DerrickContainer::makeServer, DerrickContainer::makeClient);
	public static final BEContainerIP<CokerUnitTileEntity, CokerUnitContainer> COKER =
			registerBENew("coker", CokerUnitContainer::makeServer, CokerUnitContainer::makeClient);
	public static final BEContainerIP<HydrotreaterTileEntity, HydrotreaterContainer> HYDROTREATER =
			registerBENew("hydrotreater", HydrotreaterContainer::makeServer, HydrotreaterContainer::makeClient);
	
	public static void forceClassLoad(){}


	public static <T extends  BlockEntity, C extends IEContainerMenu> BEContainerIP<T, C> registerBENew
			(String name, IEMenuTypes.BEContainerConstructor<T, C> container, IEMenuTypes.ClientContainerConstructor<C> client ){
		RegistryObject<MenuType<C>> type = registerType(name, client);
		return new BEContainerIP<>(type, container);
	}

	public static <C extends IEContainerMenu> RegistryObject<MenuType<C>> registerType (String name, IEMenuTypes.ClientContainerConstructor<C> client){
		return IPRegisters.registerMenu(
				name, ()->{
					Mutable<MenuType<C>> typeBox = new MutableObject<>();
					MenuType<C> type = new MenuType<>((id, inv)->client.construct(typeBox.getValue(), id, inv));
					typeBox.setValue(type);
					return type;
				}
		);
	}
}
