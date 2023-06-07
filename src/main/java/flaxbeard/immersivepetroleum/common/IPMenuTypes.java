package flaxbeard.immersivepetroleum.common;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import blusunrize.immersiveengineering.common.register.IEContainerTypes;
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
			register("distillation_tower", DistillationTowerContainer::new);
	public static final BEContainerIP<DerrickTileEntity, DerrickContainer> DERRICK =
			register("derrick", DerrickContainer::new);
	public static final BEContainerIP<CokerUnitTileEntity, CokerUnitContainer> COKER =
			register("coker", CokerUnitContainer::new);
	public static final BEContainerIP<HydrotreaterTileEntity, HydrotreaterContainer> HYDROTREATER =
			register("hydrotreater", HydrotreaterContainer::new);
	
	public static void forceClassLoad(){}
	
	@SuppressWarnings("unchecked")
	public static <T extends BlockEntity, C extends IEBaseContainer<? super T>> BEContainerIP<T, C> register(String name, IEContainerTypes.BEContainerConstructor<T, C> container){
		RegistryObject<MenuType<C>> typeRef = IPRegisters.registerMenu(name, () -> {
			Mutable<MenuType<C>> typeBox = new MutableObject<>();
			MenuType<C> type = new MenuType<>((IContainerFactory<C>) (windowId, inv, data) -> {
				Level world = ImmersivePetroleum.proxy.getClientWorld();
				BlockPos pos = data.readBlockPos();
				BlockEntity te = world.getBlockEntity(pos);
				return container.construct(typeBox.getValue(), windowId, inv, (T) te);
			});
			typeBox.setValue(type);
			return type;
		});
		return new BEContainerIP<>(typeRef, container);
	}
}
