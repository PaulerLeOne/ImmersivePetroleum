package flaxbeard.immersivepetroleum.api.reservoir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import flaxbeard.immersivepetroleum.common.ReservoirRegionDataStorage.RegionData;
import net.minecraft.ResourceLocationException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * Every instance of this class is it's own little ecosystem.
 * <p>
 * What kind of Fluid it has, how much of it, etc.
 * 
 * @author TwistedGate
 */
public class ReservoirIsland{
	/** Primary mB/t */
	public static final int MIN_MBPT = 15;
	
	/** Pressure related maximum mB/t */
	public static final int MAX_MBPT = 2500;
	
	/** "Unsigned 32-Bit" */
	public static final long MAX_AMOUNT = 0xFFFFFFFFL;
	
	private RegionData regionData;
	
	@Nonnull
	private ReservoirType reservoir;
	@Nonnull
	private List<ColumnPos> poly;
	private AxisAlignedIslandBB islandAABB;
	private long amount;
	private long capacity;
	
	private ReservoirIsland(){}
	
	public ReservoirIsland(@Nonnull List<ColumnPos> poly, @Nonnull ReservoirType reservoir, long amount){
		Objects.requireNonNull(poly);
		Objects.requireNonNull(reservoir);
		
		this.poly = poly;
		this.reservoir = reservoir;
		setAmountAndCapacity(amount, amount);
		
		createBoundingBox();
	}
	
	void createBoundingBox(){
		int minX = Integer.MAX_VALUE;
		int minZ = Integer.MAX_VALUE;
		
		int maxX = Integer.MIN_VALUE;
		int maxZ = Integer.MIN_VALUE;
		
		for(ColumnPos p:this.poly){
			if(p.x < minX) minX = p.x;
			if(p.z < minZ) minZ = p.z;
			
			if(p.x > maxX) maxX = p.x;
			if(p.z > maxZ) maxZ = p.z;
		}
		
		this.islandAABB = new AxisAlignedIslandBB(minX, minZ, maxX, maxZ);
	}
	
	public void setRegion(RegionData data){
		if(this.regionData == null)
			this.regionData = data;
	}
	
	/**
	 * @param amount   of fluid in this reservoir. (Range: 0 - {@link #MAX_AMOUNT}; Capacity Clamped})
	 * @param capacity of this reservoir. (Range: 0 - {@link #MAX_AMOUNT}; Clamped})
	 * @return {@link ReservoirIsland} self
	 */
	public ReservoirIsland setAmountAndCapacity(long amount, long capacity){
		setCapacity(capacity);
		setAmount(amount);
		return this;
	}
	
	/**
	 * Sets the reservoirs current fluid amount in millibuckets.
	 * 
	 * @param amount of fluid in this reservoir. (Range: 0 - {@link #MAX_AMOUNT}; Capacity Clamped})
	 */
	public ReservoirIsland setAmount(long amount){
		this.amount = clamp(amount, 0L, this.capacity);
		return this;
	}
	
	/**
	 * Sets the reservoirs current fluid capacity in millibuckets.
	 * 
	 * @param capacity of this reservoir. (Range: 0 - {@link #MAX_AMOUNT}; Clamped})
	 */
	public ReservoirIsland setCapacity(long capacity){
		this.capacity = clamp(capacity, 0L, MAX_AMOUNT);
		return this;
	}
	
	public static long clamp(long num, long min, long max){
		return Math.max(min, Math.min(max, num));
	}
	
	/**
	 * Sets the Reservoir Type
	 */
	public ReservoirIsland setReservoirType(@Nonnull ReservoirType reservoir){
		this.reservoir = Objects.requireNonNull(reservoir);
		return this;
	}
	
	/**
	 * @return amount of fluid currently in this Reservoir.
	 */
	public long getAmount(){
		return this.amount;
	}
	
	/**
	 * @return The current capacity of this Reservoir.
	 */
	public long getCapacity(){
		return this.capacity;
	}
	
	public boolean isEmpty(){
		return this.amount <= 0L;
	}
	
	public void setDirty(){
		if(this.regionData != null){
			this.regionData.setDirty();
		}
	}
	
	@Nonnull
	public ReservoirType getType(){
		return this.reservoir;
	}
	
	public Fluid getFluid(){
		return this.reservoir.getFluid();
	}
	
	public AxisAlignedIslandBB getBoundingBox(){
		return this.islandAABB;
	}
	
	public List<ColumnPos> getPolygon(){
		return Collections.unmodifiableList(this.poly);
	}
	
	private long lastEquilibriumTick = -1L;
	
	/**
	 * Used by WellTileEntity to check to see if reservoir should regenerate residuals or not
	 *
	 * @param level needed to check game time
	 * @return boolean on whether reservoir is below hydrostatic equilibrium
	 */
	public boolean belowHydrostaticEquilibrium(@Nonnull Level level) {
		return this.reservoir.residual > 0 && this.amount <= this.reservoir.equilibrium && this.lastEquilibriumTick != level.getGameTime();
	}
	
	/**
	 * Used by WellTileEntity to handle reservoir residual regeneration
	 *
	 * @param level needed to check game time
	 */
	public void equalizeHydrostaticPressure(@Nonnull Level level) {
		if(this.amount <= this.reservoir.equilibrium && this.lastEquilibriumTick != level.getGameTime()){
			this.lastEquilibriumTick = level.getGameTime();
			this.amount += this.reservoir.residual;
		}
	}
	
	/**
	 * Used by Pumpjack
	 *
	 * @param amount      to extract
	 * @param fluidAction the {@link FluidAction} to extract with
	 * @return how much has been extracted
	 */
	public int extract(int amount, FluidAction fluidAction){
		if(isEmpty()){
			return 0;
		}
		
		int extracted = (int) Math.min(amount, this.amount);
		
		if(fluidAction == FluidAction.EXECUTE){
			this.amount -= extracted;
			setDirty();
		}
		
		return extracted;
	}
	
	/**
	 * @param x x-coordinate to extract from
	 * @param z z-coordinate to extract from
	 * @return How much was extracted
	 */
	public int extractWithPressure(Level world, int x, int z){
		float pressure = getPressure(world, x, z);
		
		if(pressure > 0.0 && this.amount > 0){
			int flow = (int) Math.min(getFlow(pressure), this.amount);
			
			this.amount -= flow;
			setDirty();
			return flow;
		}
		
		return 0;
	}
	
	/**
	 * @param pressure (Clamped: 0.0 - 1.0)
	 * @return the Flowrate in mB for the given Pressure.
	 */
	public static int getFlow(float pressure){
		return MIN_MBPT + (int) Math.floor((MAX_MBPT - MIN_MBPT) * Mth.clamp(pressure, 0.0F, 1.0F));
	}
	
	/**
	 * <i>Only call on server side!</i>
	 * 
	 * @param level {@link Level} to query in
	 * @param x     x-coordinate to query
	 * @param z     z-coordinate to query
	 * @return Pressure float
	 */
	public float getPressure(Level level, int x, int z){
		// prevents outside use
		double noise = ReservoirHandler.getValueOf(level, x, z);
		
		if(noise > 0.0D){
			// Pressure should drop from 100% to 0%
			// While the reservoir is between 100% and 50% at max
			
			double half = this.capacity * 0.50;
			double alt = this.amount - half;
			if(alt > 0){
				double pre = alt / half;
				return (float) (pre * noise);
			}
		}
		
		return 0.0F;
	}
	
	public CompoundTag writeToNBT(){
		CompoundTag nbt = new CompoundTag();
		nbt.putString("reservoir", this.reservoir.getId().toString());
		nbt.putInt("amount", (int) (this.getAmount() & MAX_AMOUNT));
		nbt.putInt("capacity", (int) (this.getCapacity() & MAX_AMOUNT));
		nbt.put("bounds", this.getBoundingBox().writeToNBT());
		
		final AxisAlignedIslandBB bounds = this.getBoundingBox();
		final ListTag points = new ListTag();
		this.poly.forEach(pos -> {
			byte x = (byte) ((pos.x - bounds.minX) & 0xFF);
			byte z = (byte) ((pos.z - bounds.minZ) & 0xFF);
			
			CompoundTag point = new CompoundTag();
			point.putByte("x", x);
			point.putByte("z", z);
			points.add(point);
			
		});
		nbt.put("points", points);
		
		return nbt;
	}
	
	public static ReservoirIsland readFromNBT(CompoundTag nbt){
		try{
			ReservoirType reservoir = ReservoirType.map.get(new ResourceLocation(nbt.getString("reservoir")));
			if(reservoir != null){
				long amount = ((long) nbt.getInt("amount")) & MAX_AMOUNT;
				long capacity = ((long) nbt.getInt("capacity")) & MAX_AMOUNT;
				AxisAlignedIslandBB bounds = new AxisAlignedIslandBB(nbt.getCompound("bounds"));
				
				final List<ColumnPos> points = new ArrayList<>();
				final ListTag list = nbt.getList("points", Tag.TAG_COMPOUND);
				list.forEach(tag -> {
					CompoundTag point = (CompoundTag) tag;
					int x = bounds.minX + ((int) point.getByte("x") & 0xFF);
					int z = bounds.minZ + ((int) point.getByte("z") & 0xFF);
					points.add(new ColumnPos(x, z));
				});
				
				ReservoirIsland island = new ReservoirIsland();
				island.reservoir = reservoir;
				island.amount = amount;
				island.capacity = capacity;
				island.poly = points;
				island.islandAABB = bounds;
				return island;
			}
		}catch(ResourceLocationException e){
			// Dont care, if it doesnt exist just move on
		}
		
		return null;
	}
	
	/**
	 * Convenience method.
	 * 
	 * @see {@link #contains(int, int)}
	 */
	public boolean contains(ColumnPos pos){
		return contains(pos.x, pos.z);
	}
	
	/**
	 * Same as {@link #polygonContains(int, int)} but with the Bounds as the first check.
	 * 
	 * @param x x-coordinate to query for
	 * @param z z-coordinate to query for
	 * @return whether the reservoir contains this position
	 */
	public boolean contains(int x, int z){
		if(!this.islandAABB.contains(x, z)){
			return false;
		}
		
		return polygonContains(x, z);
	}
	
	/**
	 * Convenience method.
	 * 
	 * @see {@link #polygonContains(int, int)}
	 */
	public boolean polygonContains(ColumnPos pos){
		return polygonContains(pos.x, pos.z);
	}
	
	/**
	 * Test wether or not the given XZ coordinates are within the islands polygon.
	 * 
	 * @param x x-coordinate to test
	 * @param z y-coordinate to test
	 * @return true if the coordinates are inside, false otherwise
	 */
	public boolean polygonContains(int x, int z){
		boolean ret = false;
		int j = this.poly.size() - 1;
		for(int i = 0;i < this.poly.size();i++){
			ColumnPos a = this.poly.get(i);
			ColumnPos b = this.poly.get(j);
			
			// They need to be floats or it wont work for some reason
			float ax = a.x, az = a.z;
			float bx = b.x, bz = b.z;
			
			// Any point directly on the edge is considered "outside"
			if((ax == x && az == z)){
				return false;
			}else if((ax == x && bx == x) && ((z > bz && z < az) || (z > az && z < bz))){
				return false;
			}else if((az == z && bz == z) && ((x > ax && x < bx) || (x > bx && x < ax))){
				return false;
			}
			
			// Voodoo Magic for Point-In-Polygon
			if(((az < z && bz >= z) || (bz < z && az >= z)) && (ax <= x || bx <= x)){
				float f0 = ax + (z - az) / (bz - az) * (bx - ax);
				ret ^= (f0 < x);
			}
			
			j = i;
		}
		
		return ret;
	}
}
