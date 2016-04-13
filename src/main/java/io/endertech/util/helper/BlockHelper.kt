package io.endertech.util.helper

import io.endertech.util.BlockCoord
import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.block.BlockTorch
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable
import net.minecraftforge.fluids.BlockFluidBase

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.HashSet

object BlockHelper {
    val softBlocks: MutableSet<Block> = HashSet()

    fun initSoftBlocks() {
        for (o in Block.blockRegistry) {

            if (o is BlockFluidBase || o is BlockLiquid || o is IPlantable || o is BlockTorch) {
                softBlocks.add(o)
            }
        }

        softBlocks.add(Blocks.snow)
        softBlocks.add(Blocks.vine)
        softBlocks.add(Blocks.fire)
    }

    fun isSoftBlock(world: World, x: Int, y: Int, z: Int): Boolean {
        val block = world.getBlock(x, y, z)
        if (block === Blocks.air) {
            return true
        }

        return isSoftBlock(block, world, x, y, z)
    }

    fun isSoftBlock(block: Block?, world: World, x: Int, y: Int, z: Int): Boolean {
        return block == null || softBlocks.contains(block) || world.isAirBlock(x, y, z)
    }

    fun isBlockExposed(world: World, x: Int, y: Int, z: Int): Boolean {
        return isSoftBlock(world, x + 1, y, z) || isSoftBlock(world, x - 1, y, z) || isSoftBlock(world, x, y + 1, z) || isSoftBlock(world, x, y - 1, z) || isSoftBlock(world, x, y, z + 1) || isSoftBlock(world, x, y, z - 1)
    }

    fun areBlocksEqual(blockOne: Block, metaOne: Int, blockTwo: Block, metaTwo: Int): Boolean {
        return blockOne === blockTwo && metaOne == metaTwo
    }

    fun areBlocksEqual(blockAccess: IBlockAccess, one: BlockCoord, two: BlockCoord): Boolean {
        return areBlocksEqual(blockAccess.getBlock(one.x, one.y, one.z), blockAccess.getBlockMetadata(one.x, one.y, one.z), blockAccess.getBlock(two.x, two.y, two.z), blockAccess.getBlockMetadata(two.x, two.y, two.z))
    }

    fun areBlocksEqual(blockAccess: IBlockAccess, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Boolean {
        return areBlocksEqual(blockAccess.getBlock(x1, y1, z1), blockAccess.getBlockMetadata(x1, y1, z1), blockAccess.getBlock(x2, y2, z2), blockAccess.getBlockMetadata(x2, y2, z2))
    }

    fun areBlocksEqual(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, direction: ForgeDirection): Boolean {
        return areBlocksEqual(blockAccess, x, y, z, x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ)
    }

    fun areBlocksEqual(blockAccess: IBlockAccess, block: Block, meta: Int, x: Int, y: Int, z: Int, direction: ForgeDirection): Boolean {
        return block === blockAccess.getBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ) && meta == blockAccess.getBlockMetadata(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ)
    }

    // Silk Touch

    // NOTE: Reflection performance warning
    fun createSilkTouchStack(block: Block, meta: Int): List<ItemStack>? {
        val items = ArrayList<ItemStack>()
        var itemstack: ItemStack? = null

        try {
            val createStackedBlock = Block::class.java.getDeclaredMethod("func_149644_j", Integer.TYPE) // # createStackedBlock
            createStackedBlock.isAccessible = true
            itemstack = createStackedBlock.invoke(block, meta) as ItemStack
        } catch (e: NoSuchMethodException) {
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalAccessException) {
        } catch (e: InvocationTargetException) {
        }

        if (itemstack == null) {
            return null
        }

        items.add(itemstack)

        return items
    }
}
