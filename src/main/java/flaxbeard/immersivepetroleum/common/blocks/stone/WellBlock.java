package flaxbeard.immersivepetroleum.common.blocks.stone;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class WellBlock extends IPBlockBase{
	public WellBlock(){
		super("well", Block.Properties.create(Material.ROCK).hardnessAndResistance(-1.0F, 3600000.0F).noDrops().setAllowsSpawn((s, r, p, e) -> false));
	}
	
	@Override
	protected BlockItem createBlockItem(){
		// Nobody is supposed to have this in their inventory
		return null;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state){
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world){
		WellTileEntity tile = IPTileTypes.WELL.get().create();
		return tile;
	}
}
