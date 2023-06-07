package flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks;

import java.util.HashMap;
import java.util.Map;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.CokingChamber;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.CCTUtils;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks.generic.PoweredMultiblockPeripheral;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class CokerUnitPeripheral extends PoweredMultiblockPeripheral{
	CokerUnitTileEntity master;
	public CokerUnitPeripheral(CokerUnitTileEntity coker){
		super(coker);
		this.master = coker.master();
	}
	
	@Override
	public String getType(){
		return "ip_cokerunit";
	}
	
	@LuaFunction
	public final Map<String, Object> getChamberA(){
		return getChamber(CokerUnitTileEntity.CHAMBER_A);
	}
	
	@LuaFunction
	public final Map<String, Object> getChamberB(){
		return getChamber(CokerUnitTileEntity.CHAMBER_B);
	}
	
	private Map<String, Object> getChamber(int id){
		CokingChamber chamber = this.master.chambers[id];
		
		Map<String, Object> map = new HashMap<>();
		
		{
			ResourceLocation rl = ForgeRegistries.ITEMS.getKey(chamber.getInputItem().getItem());
			String regName = rl == null ? null : rl.toString();
			
			Map<String, Object> inputMap = new HashMap<>();
			inputMap.put("name", regName);
			inputMap.put("count", chamber.getInputAmount());
			map.put("input", inputMap);
		}
		
		{
			ResourceLocation rl = ForgeRegistries.ITEMS.getKey(chamber.getOutputItem().getItem());
			String regName = rl == null ? null : rl.toString();
			
			Map<String, Object> outputMap = new HashMap<>();
			outputMap.put("name", regName);
			outputMap.put("count", chamber.getOutputAmount());
			map.put("output", outputMap);
		}
		
		map.put("state", chamber.getState().toString());
		map.put("tank", CCTUtils.fluidToMap(chamber.getTank().getFluid()));
		map.put("tankCapacity", chamber.getTank().getCapacity());
		map.put("itemCapacity", chamber.getCapacity());
		return map;
	}
	
	@LuaFunction
	public final MethodResult getTankSize(int tank){
		switch(tank){
			case 1:
			case 2:
				return MethodResult.of(this.master.bufferTanks[tank - 1].getCapacity());
			default:
				return MethodResult.of(null, "Index " + tank + " out of Bounds.");
		}
	}
	
	@LuaFunction
	public final Map<String, Object> getInputTank(){
		return CCTUtils.fluidToMap(this.master.bufferTanks[CokerUnitTileEntity.TANK_INPUT].getFluid());
	}
	
	@LuaFunction
	public final Map<String, Object> getOutputTank(){
		return CCTUtils.fluidToMap(this.master.bufferTanks[CokerUnitTileEntity.TANK_OUTPUT].getFluid());
	}
}
