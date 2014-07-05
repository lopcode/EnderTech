package io.endertech.block;

import cpw.mods.fml.common.network.NetworkRegistry;
import io.endertech.EnderTech;
import io.endertech.lib.Strings;
import io.endertech.network.NetworkHandler;
import io.endertech.network.message.MessageTileSpinningCube;
import io.endertech.tile.TileSpinningCube;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSpinningCube extends BlockContainer
{
    public static ItemStack itemCube;

    public BlockSpinningCube()
    {
        super(Material.iron);
        this.setCreativeTab(EnderTech.tabET);
        this.setBlockName(Strings.Blocks.SPINNING_CUBE_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileSpinningCube();
    }

    public void init()
    {
        TileSpinningCube.init();

        itemCube = new ItemStack(this, 1, 0);
    }

    public int getRenderType()
    {
        return -1;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float par7, float par8, float par9)
    {
        if (!world.isRemote)
        //if(true)
        {
            TileEntity tileentity = world.getTileEntity(x, y, z);
            if (tileentity instanceof TileSpinningCube)
            {
                TileSpinningCube tile = (TileSpinningCube) tileentity;
                if (player.isSneaking()) tile.applySpeedUp();
                else tile.createRandomAddition();


                NetworkHandler.INSTANCE.sendToAllAround(new MessageTileSpinningCube(tile), new NetworkRegistry.TargetPoint(player.dimension, x, y, z, 4096));
            }
        }

        return true;
    }
}
