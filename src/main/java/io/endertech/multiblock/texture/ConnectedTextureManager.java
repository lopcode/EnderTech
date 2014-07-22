package io.endertech.multiblock.texture;

import io.endertech.util.BlockHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.Set;

public class ConnectedTextureManager
{
    private String textureBase;
    private Set<ForgeDirection> connectingDirections;
    private IIcon[] icons;
    private IIcon transparentIcon;

    private static String[] fileMappings = {"none", "up", "down", "up_down", "left", "up_left", "down_left", "up_down_left", "right", "up_right", "right_down", "up_right_down", "right_left", "up_right_left", "right_down_left", "all"};

    public ConnectedTextureManager(String textureBase, Set<ForgeDirection> connectingDirections)
    {
        this.textureBase = textureBase;
        this.connectingDirections = connectingDirections;
        icons = new IIcon[16];
    }

    public void registerBlockIcons(IIconRegister iconRegister)
    {
        this.transparentIcon = iconRegister.registerIcon(textureBase + ".transparent");

        for (int connection = 0; connection < fileMappings.length; connection++)
        {
            icons[connection] = iconRegister.registerIcon(textureBase + "." + fileMappings[connection]);
        }
    }

    public static final ForgeDirection neighborsBySide[][] = new ForgeDirection[][] {{ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST}, {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST}, {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.EAST, ForgeDirection.WEST}, {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.WEST, ForgeDirection.EAST}, {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.NORTH, ForgeDirection.SOUTH}, {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.SOUTH, ForgeDirection.NORTH}};

    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        if (connectingDirections != null && connectingDirections.isEmpty()) return icons[0];

        ForgeDirection dir;
        Block myBlock = blockAccess.getBlock(x, y, z);
        int myBlockMetadata = blockAccess.getBlockMetadata(x, y, z);

        // First check if we have a block in front of us of the same type - if so, just be completely transparent on this side
        ForgeDirection out = ForgeDirection.getOrientation(side);
        if (BlockHelper.areBlocksEqual(blockAccess, myBlock, myBlockMetadata, x, y, z, out))
        {
            return transparentIcon;
        }

        // Calculate icon index based on whether the blocks around this block match it
        // Icons use a naming pattern so that the bits correspond to:
        // 1 = Connected on top, 2 = connected on bottom, 4 = connected on left, 8 = connected on right
        int iconIdx = 0;
        for (int i = 0; i < 4; i++)
        {
            dir = neighborsBySide[side][i];
            // Same blockID and metadata on this side?
            if (BlockHelper.areBlocksEqual(blockAccess, myBlock, myBlockMetadata, x, y, z, dir))
            {
                // Connected!
                iconIdx |= 1 << i;
            }
        }

        return icons[iconIdx];
    }

    public IIcon getUnconnectedTexture()
    {
        return icons[0];
    }
}
