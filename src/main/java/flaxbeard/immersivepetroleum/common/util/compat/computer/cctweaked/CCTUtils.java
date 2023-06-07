package flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CCTUtils{
	public static Map<String, Object> itemToMap(ItemStack stack){
		ResourceLocation rl = stack.getItem().getRegistryName();
		String regName = rl == null ? null : rl.toString();
		
		Map<String, Object> outputMap = new HashMap<>();
		outputMap.put("name", regName);
		outputMap.put("count", stack.getCount());
		return outputMap;
	}
	
	public static Map<String, Object> fluidToMap(FluidStack stack){
		ResourceLocation rl = stack.getFluid().getRegistryName();
		String regName = rl == null ? null : rl.toString();
		
		Map<String, Object> outputMap = new HashMap<>();
		outputMap.put("name", regName);
		outputMap.put("amount", stack.getAmount());
		return outputMap;
	}
}
