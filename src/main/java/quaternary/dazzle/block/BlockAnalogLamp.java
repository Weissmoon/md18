package quaternary.dazzle.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import quaternary.dazzle.Dazzle;
import quaternary.dazzle.item.ItemBlockLamp;

public class BlockAnalogLamp extends BlockBase {
	public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
	
	public final EnumDyeColor color;
	private final String variant;
	
	private final boolean inverted;
	private IBlockState inverseState;
	
	public BlockAnalogLamp(EnumDyeColor c, String variant, boolean inverted) {
		super((inverted ? "inverted_" : "") + c.getName() + "_" + variant + "_analog_lamp", Material.REDSTONE_LIGHT);
		
		this.inverted = inverted;
		this.variant = variant;
		this.color = c;
	}
	
	Item item;
	@Override
	public Item getItemForm() {
		if(item == null) {
			item = new ItemBlockLamp(this, color, variant, "tile.dazzle.analog_lamp.name");
		}
		return item;
	}
	
	//Block colors
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}
	
	@Override
	public boolean hasBlockColors() {
		return true;
	}
	
	@Override
	public Object getBlockColors() {
		return Dazzle.PROXY.getAnalogLampBlockColors();
	}
	
	@Override
	public Object getItemColors() {
		return Dazzle.PROXY.getLampItemColors();
	}
	
	//Inversion
	//I can't use states for inversion because I'm using all 15.
	//Thus, this is implemented using two blocks.
	public void setInverseBlockstate(IBlockState b) {
		inverseState = b;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem(hand).getItem() == Item.getItemFromBlock(Blocks.REDSTONE_TORCH)) {
			world.setBlockState(pos, inverseState.withProperty(POWER, state.getValue(POWER)));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean hasItemForm() {
		return !inverted;
	}
	
	//Light level based on states
	@Override
	public int getLightValue(IBlockState state) {
		return inverted ? 15 - state.getValue(POWER) : state.getValue(POWER);
	}
	
	//Updating light level
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if(!world.isRemote) updateLevel(world, pos, world.isBlockIndirectlyGettingPowered(pos));
	}
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if(!world.isRemote) updateLevel(world, pos, world.isBlockIndirectlyGettingPowered(pos));
	}
	
	private void updateLevel(World world, BlockPos pos, int level) {
		level = MathHelper.clamp(level, 0, 15); //sanity check
		
		world.setBlockState(pos, getDefaultState().withProperty(POWER, level));
	}
	
	//Blockstate boilerplate
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(POWER);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(POWER, meta);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, POWER);
	}
	
	//Statemapper
	@Override
	public boolean hasCustomStatemapper() {
		return true;
	}
	
	@Override
	public Object getCustomStatemapper() {
		return Dazzle.PROXY.getLampStatemapper("lamp_" + variant);
	}
}
