package flaxbeard.immersivepetroleum.common.blocks.stone;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockItemBase;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class ParaffinWaxBlock extends IPBlockBase{
	
	public ParaffinWaxBlock(){
		super(Properties.of(Material.ICE_SOLID, MaterialColor.COLOR_YELLOW).strength(0.5F, 0.4F).sound(SoundType.HONEY_BLOCK).speedFactor(0.95F).friction(1.05F));
	}
	
	@Override
	public void appendHoverText(@Nonnull ItemStack stack, BlockGetter worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn){
		tooltip(stack, worldIn, tooltip, flagIn);
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
	
	static void tooltip(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("desc.immersivepetroleum.flavour.paraffin_wax").withStyle(ChatFormatting.GRAY));
	}
	
	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face){
		return 100;
	}
	
	@Override
	public Supplier<BlockItem> blockItemSupplier(){
		return () -> new IPBlockItemBase(this, new Item.Properties().tab(ImmersivePetroleum.creativeTab)){
			@Override
			public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType){
				return 8000;
			}
		};
	}
}
