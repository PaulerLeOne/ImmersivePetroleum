package flaxbeard.immersivepetroleum.common.util.compat.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class IPRecipeCategory<T> implements IRecipeCategory<T>{
	public String localizedName;
	protected final IGuiHelper guiHelper;
	private IDrawableStatic background;
	private IDrawable icon;
	
	private RecipeType<T> type;
	
	public IPRecipeCategory(Class<? extends T> recipeClass, IGuiHelper guiHelper, ResourceLocation id, String localKey){
		this.guiHelper = guiHelper;
		this.localizedName = I18n.get(localKey);
		
		this.type = new RecipeType<>(id, recipeClass);
	}
	
	protected void setBackground(IDrawableStatic background){
		this.background = background;
	}
	
	protected void setIcon(ItemStack stack){
		setIcon(this.guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, stack));
	}
	
	protected void setIcon(IDrawable icon){
		this.icon = icon;
	}
	
	@Override
	@Nonnull
	public Component getTitle(){
		return new TranslatableComponent(this.localizedName);
	}
	
	@Override
	@Nonnull
	public IDrawable getBackground(){
		return this.background;
	}
	
	@Override
	@Nonnull
	public IDrawable getIcon(){
		return this.icon;
	}
	
	@Override
	@Nonnull
	public RecipeType<T> getRecipeType(){
		return this.type;
	}
}
