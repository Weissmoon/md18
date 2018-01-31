package quaternary.dazzle.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import quaternary.dazzle.particle.ParticleLightSource;

public class TileParticleLightSource extends TileEntity implements ITickable {
	final EnumDyeColor color;
	public TileParticleLightSource(EnumDyeColor color) {
		this.color = color;
	}
	
	@Override
	public void update() {
		if(!world.isRemote || world.getTotalWorldTime() % 5 != 0) return;
		
		Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleLightSource(world, pos, color));
	}
}