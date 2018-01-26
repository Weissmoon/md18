package quaternary.dazzle.block.stadium;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import quaternary.dazzle.block.BlockBase;

import javax.annotation.Nullable;

public class BlockStadiumLightBase extends BlockBase {
	public enum ComponentType {
		BASE, POLE, TOP
	}
	
	public final ComponentType type;
	
	public BlockStadiumLightBase(String name, ComponentType type) {
		super(name, Material.IRON, MapColor.GRAY_STAINED_HARDENED_CLAY);
		this.type = type;
	}
	
	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	private boolean canSupport(ComponentType aboveType) {
		if(type == ComponentType.BASE) return aboveType == ComponentType.POLE;
		if(type == ComponentType.POLE) return aboveType == ComponentType.POLE || aboveType == ComponentType.TOP;
		return false;
	}
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if(type == ComponentType.BASE) return;
		
		Block below = world.getBlockState(pos.down()).getBlock();
		
		if(below instanceof BlockStadiumLightBase) {
			if(!((BlockStadiumLightBase) below).canSupport(type)) {
				world.destroyBlock(pos, true);
			}
		} else {
			world.destroyBlock(pos, true);
		}
	}
	
	@Nullable
	public static BlockPos climbPole(World w, BlockPos startingPos) {
		BlockPos climber = startingPos.add(0, 0, 0);
		
		while(w.getBlockState(climber).getBlock() instanceof BlockStadiumLightBase) {
			climber = climber.up();
		}
		
		if(climber.getY() > 255) return null;
		else return climber;
	}
	
	@GameRegistry.ObjectHolder("dazzle:stadium_pole")
	public static final Item POLE_ITEM = Items.AIR;
	
	@GameRegistry.ObjectHolder("dazzle:stadium_pole")
	public static final Block POLE_BLOCK = Blocks.AIR;
	
	@GameRegistry.ObjectHolder("dazzle:stadium_top")
	public static final Item TOP_ITEM = Items.AIR;
	
	@GameRegistry.ObjectHolder("dazzle:stadium_top")
	public static final Block TOP_BLOCK = Blocks.AIR;
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(type == ComponentType.TOP) return false;
		
		ItemStack heldStack = player.getHeldItem(hand);
		if(heldStack.getItem() == POLE_ITEM || heldStack.getItem() == TOP_ITEM) {
			BlockPos poleTop = climbPole(world, pos);
			if(poleTop != null && world.getBlockState(poleTop).getBlock().isReplaceable(world, poleTop)) {
				if(heldStack.getItem() == POLE_ITEM) {
					world.setBlockState(poleTop, POLE_BLOCK.getDefaultState());
				} else {
					world.setBlockState(poleTop, TOP_BLOCK.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, 696969, player, hand));
				}
				if(!player.capabilities.isCreativeMode) heldStack.shrink(1);
				world.playSound(null, poleTop, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1, 1);
				return true;
			}
		}
		return false;
	}
}
