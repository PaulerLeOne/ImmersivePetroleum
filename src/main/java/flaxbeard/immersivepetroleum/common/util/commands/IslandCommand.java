package flaxbeard.immersivepetroleum.common.util.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import flaxbeard.immersivepetroleum.api.reservoir.AxisAlignedIslandBB;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirIsland;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirType;
import flaxbeard.immersivepetroleum.common.ReservoirRegionDataStorage;
import flaxbeard.immersivepetroleum.common.ReservoirRegionDataStorage.RegionData;
import flaxbeard.immersivepetroleum.common.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class IslandCommand{
	private IslandCommand(){
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> create(){
		LiteralArgumentBuilder<CommandSourceStack> main = Commands.literal("reservoir").requires(source -> source.hasPermission(4));
		
		main.then(Commands.literal("locate").executes(IslandCommand::locate));
		main.then(setters());
		main.then(positional(Commands.literal("get"), IslandCommand::get));
		
		return main;
	}
	
	private static int get(CommandContext<CommandSourceStack> context, @Nonnull ReservoirIsland island){
		CommandUtils.sendTranslated(context.getSource(),
				"chat.immersivepetroleum.command.reservoir.get",
				island.getAmount(),
				Utils.fDecimal(island.getAmount() / (double) island.getCapacity() * 100),
				new FluidStack(island.getFluid(), 1).getDisplayName()
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int locate(CommandContext<CommandSourceStack> command){
		CommandSourceStack source = command.getSource();
		BlockPos pos = source.getEntity().blockPosition();
		double dx = pos.getX() + 0.5;
		double dz = pos.getZ() + 0.5;
		int range = 64;
		int rangeSqr = range * range;
		
		Set<ReservoirIsland> nearby = new HashSet<>();
		
		ReservoirRegionDataStorage storage = ReservoirRegionDataStorage.get();
		final ColumnPos playerRegionPos = storage.toRegionCoords(pos);
		final ResourceKey<Level> dimKey = source.getLevel().dimension();
		for(int rz = -1;rz <= 1;rz++){
			for(int rx = -1;rx <= 1;rx++){
				RegionData rd = storage.getRegionData(new ColumnPos(playerRegionPos.x + rx, playerRegionPos.z + rz));
				if(rd != null){
					synchronized(rd.getReservoirIslandList()){
						rd.getReservoirIslandList().get(dimKey).forEach(island -> {
							if(island.getBoundingBox().getCenter().distToCenterSqr(dx, 0, dz) <= rangeSqr){
								nearby.add(island);
							}
						});
					}
				}
			}
		}
		
		if(nearby.isEmpty()){
			CommandUtils.sendTranslated(source, "chat.immersivepetroleum.command.reservoir.notfound");
			return Command.SINGLE_SUCCESS;
		}
		
		// Find the Closest coordinate that can tap into one of them
		ReservoirIsland closestIsland = null;
		double smallestDistance = rangeSqr;
		ColumnPos p = null;
		for(ReservoirIsland island:nearby){
			AxisAlignedIslandBB IAABB = island.getBoundingBox();
			for(int z = IAABB.minZ() + 1;z < IAABB.maxZ();z++){
				for(int x = IAABB.minX() + 1;x < IAABB.maxX();x++){
					if(island.contains(x, z)){
						double xa = (x + 0.5) - dx;
						double za = (z + 0.5) - dz;
						double dst = xa * xa + za * za;
						if((dst < range && dst < smallestDistance)){
							p = new ColumnPos(x, z);
							smallestDistance = dst;
							closestIsland = island;
						}
					}
				}
			}
		}
		
		if(closestIsland == null){
			CommandUtils.sendStringError(source, "Something bad happend");
			return Command.SINGLE_SUCCESS;
		}
		
		// Find the spot with the highest pressure
		double hPressure = 0.0D;
		AxisAlignedIslandBB IAABB = closestIsland.getBoundingBox();
		for(int z = IAABB.minZ() + 1;z < IAABB.maxZ();z++){
			for(int x = IAABB.minX() + 1;x < IAABB.maxX();x++){
				double cPressure;
				if(closestIsland.contains(x, z) && (cPressure = ReservoirHandler.getValueOf(source.getLevel(), x, z)) > hPressure){
					hPressure = cPressure;
					p = new ColumnPos(x, z);
				}
			}
		}
		
		final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + p.x + " ~ " + p.z);
		final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip"));
		
		source.sendSuccess(new TranslatableComponent("chat.immersivepetroleum.command.reservoir.locate",
				closestIsland.getType().name,
				ComponentUtils.wrapInSquareBrackets(new TextComponent(p.x + " " + p.z)).withStyle((s) -> {
					return s.withColor(ChatFormatting.GREEN)
							.withItalic(true)
							.withClickEvent(clickEvent)
							.withHoverEvent(hoverEvent);
				})), true);
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static LiteralArgumentBuilder<CommandSourceStack> setters(){
		LiteralArgumentBuilder<CommandSourceStack> set = Commands.literal("set").requires(source -> source.hasPermission(4));
		
		set.then(Commands.literal("amount").then(positional(Commands.argument("amount", LongArgumentType.longArg(0, ReservoirIsland.MAX_AMOUNT)), IslandCommand::setReservoirAmount)));
		set.then(Commands.literal("capacity").then(positional(Commands.argument("capacity", LongArgumentType.longArg(0, ReservoirIsland.MAX_AMOUNT)), IslandCommand::setReservoirCapacity)));
		set.then(Commands.literal("type").then(positional(Commands.argument("name", StringArgumentType.string()).suggests(IslandCommand::typeSuggestor), IslandCommand::setReservoirType)));
		
		return set;
	}
	
	private static CompletableFuture<Suggestions> typeSuggestor(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder){
		return SharedSuggestionProvider.suggest(ReservoirType.map.values().stream().map(type -> type.name), builder);
	}
	
	private static int setReservoirAmount(CommandContext<CommandSourceStack> context, @Nonnull ReservoirIsland island){
		long amount = context.getArgument("amount", Long.class);
		island.setAmount(amount);
		island.setDirty();
		
		CommandUtils.sendTranslated(context.getSource(), "chat.immersivepetroleum.command.reservoir.set.amount.success", island.getAmount());
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setReservoirCapacity(CommandContext<CommandSourceStack> context, @Nonnull ReservoirIsland island){
		long capacity = context.getArgument("capacity", Long.class);
		island.setAmountAndCapacity(capacity, capacity);
		island.setDirty();
		
		CommandUtils.sendTranslated(context.getSource(), "chat.immersivepetroleum.command.reservoir.set.capacity.success", island.getCapacity());
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setReservoirType(CommandContext<CommandSourceStack> context, @Nonnull ReservoirIsland island){
		String name = context.getArgument("name", String.class);
		ReservoirType reservoir = null;
		for(ReservoirType res:ReservoirType.map.values()){
			if(res.name.equalsIgnoreCase(name))
				reservoir = res;
		}
		
		if(reservoir == null){
			CommandUtils.sendTranslatedError(context.getSource(), "chat.immersivepetroleum.command.reservoir.set.type.fail", name);
			return Command.SINGLE_SUCCESS;
		}
		
		island.setReservoirType(reservoir);
		island.setDirty();
		
		CommandUtils.sendTranslated(context.getSource(), "chat.immersivepetroleum.command.reservoir.set.type.success", reservoir.name);
		return Command.SINGLE_SUCCESS;
	}
	
	static <T extends ArgumentBuilder<CommandSourceStack, T>> T positional(T builder, BiFunction<CommandContext<CommandSourceStack>, ReservoirIsland, Integer> function){
		builder.executes(command -> {
			ColumnPos pos = new ColumnPos(new BlockPos(command.getSource().getPosition()));
			
			ReservoirIsland island = ReservoirHandler.getIsland(command.getSource().getLevel(), pos);
			if(island == null){
				CommandUtils.sendTranslated(command.getSource(), "chat.immersivepetroleum.command.reservoir.notfound");
				return Command.SINGLE_SUCCESS;
			}
			
			return function.apply(command, island);
		}).then(Commands.argument("location", ColumnPosArgument.columnPos()).executes(command -> {
			ColumnPos pos = ColumnPosArgument.getColumnPos(command, "location");
			
			ReservoirIsland island = ReservoirHandler.getIsland(command.getSource().getLevel(), pos);
			if(island == null){
				CommandUtils.sendTranslated(command.getSource(), "chat.immersivepetroleum.command.reservoir.notfound");
				return Command.SINGLE_SUCCESS;
			}
			
			return function.apply(command, island);
		}));
		return builder;
	}
}
