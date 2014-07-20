package io.endertech.multiblock.block;

import io.endertech.EnderTech;
import io.endertech.multiblock.IMultiblockPart;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.MultiblockTileEntityBase;
import io.endertech.multiblock.controller.ControllerTank;
import io.endertech.multiblock.tile.TileTankPart;
import io.endertech.reference.Strings;
import io.endertech.util.BlockCoord;
import io.endertech.util.IOutlineDrawer;
import io.endertech.util.RGBA;
import io.endertech.util.RenderHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import java.util.List;
import java.util.Set;

public class BlockTankPart extends BlockContainer implements IOutlineDrawer
{
    public static final int FRAME_METADATA_BASE = 0; // Frame metadata
    public static final int FRAME_CORNER = 1;
    public static final int FRAME_CENTER = 2;
    public static final int FRAME_VERTICAL = 3;
    public static final int FRAME_EASTWEST = 4;
    public static final int FRAME_NORTHSOUTH = 5;

    public static final int CONTROLLER_METADATA_BASE = 6; // Disabled, Idle, Active
    public static final int CONTROLLER_IDLE = 7;
    public static final int CONTROLLER_ACTIVE = 8;

    public static ItemStack itemBlockTankFrame;
    public static ItemStack itemBlockTankController;

    public BlockTankPart()
    {
        super(Material.glass);
        this.setCreativeTab(EnderTech.tabET);
        this.setBlockName(Strings.Blocks.TANK_PART_NAME);
    }

    public void init()
    {
        TileTankPart.init();

        itemBlockTankFrame = new ItemStack(this, 1, FRAME_METADATA_BASE);
        itemBlockTankController = new ItemStack(this, 1, CONTROLLER_METADATA_BASE);
    }

    public static boolean isFrame(int metadata) { return metadata >= FRAME_METADATA_BASE && metadata < CONTROLLER_METADATA_BASE; }

    public static boolean isController(int metadata) { return metadata >= CONTROLLER_METADATA_BASE && metadata < CONTROLLER_ACTIVE + 1; }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileTankPart();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float par7, float par8, float par9)
    {
        if (player.isSneaking())
        {
            return false;
        }

        if (!world.isRemote)
        {
            ItemStack currentEquippedItem = player.getCurrentEquippedItem();

            if (currentEquippedItem == null)
            {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof IMultiblockPart)
                {
                    MultiblockControllerBase controller = ((IMultiblockPart) te).getMultiblockController();
                    if (controller != null)
                    {
                        String chatLine = "Tank status: ";

                        if (controller.isAssembled())
                            chatLine += EnumChatFormatting.GREEN + "assembled" + EnumChatFormatting.RESET + ", ";
                        else chatLine += EnumChatFormatting.RED + "not assembled" + EnumChatFormatting.RESET + ", ";

                        boolean active = false;
                        if (controller instanceof ControllerTank)
                        {
                            ControllerTank tankController = (ControllerTank) controller;
                            active = tankController.isActive();
                        }

                        if (active) chatLine += EnumChatFormatting.GREEN + "active" + EnumChatFormatting.RESET + ".";
                        else chatLine += EnumChatFormatting.RED + "not active" + EnumChatFormatting.RESET + ".";

                        player.addChatComponentMessage(new ChatComponentText(chatLine));

                        Exception e = controller.getLastValidationException();
                        if (e != null)
                        {
                            player.addChatComponentMessage(new ChatComponentText("Last reason for not being able to assemble:"));
                            player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
                        }
                    } else
                    {
                        player.addChatComponentMessage(new ChatComponentText("Block is not connected to a reactor. This could be due to lag, or a bug. If the problem persists, try breaking and re-placing the block."));
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return true;
    }

    public ItemStack getTankFrameItemStack()
    {
        return new ItemStack(this, 1, FRAME_METADATA_BASE);
    }

    public ItemStack getTankControllerItemStack()
    {
        return new ItemStack(this, 1, CONTROLLER_METADATA_BASE);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(getTankFrameItemStack());
        par3List.add(getTankControllerItemStack());
    }

    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
        BlockCoord target = new BlockCoord(event.target.blockX, event.target.blockY, event.target.blockZ);
        World world = event.player.worldObj;

        TileEntity tile = world.getTileEntity(target.x, target.y, target.z);
        if (tile == null)
        {
            RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Blue.setAlpha(0.6f), 2.0f, event.partialTicks);
            return true;
        }

        if (tile instanceof IMultiblockPart)
        {
            MultiblockControllerBase controller = ((MultiblockTileEntityBase) tile).getMultiblockController();
            if (controller == null)
            {
                RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Red.setAlpha(0.6f), 2.0f, event.partialTicks);
                return true;
            }

            Set<IMultiblockPart> connectedParts = controller.getConnectedParts();
            if (connectedParts.isEmpty())
            {
                RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.White.setAlpha(0.6f), 2.0f, event.partialTicks);
            }

            RGBA colour = RGBA.Blue.setAlpha(0.6f);
            if (controller.isAssembled())
            {
                colour = RGBA.Green.setAlpha(0.6f);
            }

            for (IMultiblockPart part : connectedParts)
            {
                BlockCoord partCoord = part.getWorldLocation();

                if (BlockTankPart.isController(world.getBlockMetadata(partCoord.x, partCoord.y, partCoord.z)))
                    RenderHelper.renderBlockOutline(event.context, event.player, partCoord, RGBA.White.setAlpha(0.6f), 10.0f, event.partialTicks);
                else
                    RenderHelper.renderBlockOutline(event.context, event.player, partCoord, colour, 2.0f, event.partialTicks);

                if (part.isMultiblockSaveDelegate())
                    RenderHelper.renderBlockOutline(event.context, event.player, partCoord, RGBA.Red, 10.0f, event.partialTicks);
            }


            return true;
        }

        return false;
    }

    @Override
    public int damageDropped(int meta)
    {
        if (isFrame(meta)) return FRAME_METADATA_BASE;
        else if (isController(meta)) return CONTROLLER_METADATA_BASE;

        return FRAME_METADATA_BASE;
    }
}
