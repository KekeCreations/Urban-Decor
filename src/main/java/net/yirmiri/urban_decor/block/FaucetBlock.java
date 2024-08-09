package net.yirmiri.urban_decor.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.yirmiri.urban_decor.datagen.UDItemTagProvider;
import net.yirmiri.urban_decor.util.UDUtils;

public class FaucetBlock extends AbstractDecorBlock {
    public static final BooleanProperty OUTDOOR = BooleanProperty.of("outdoor");

    private static final VoxelShape SHAPE_NORTH = VoxelShapes.combineAndSimplify(Block.createCuboidShape(7, 8, 10, 9, 10, 18),
            Block.createCuboidShape(7, 6, 10, 9, 8, 12), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_WEST = VoxelShapes.combineAndSimplify(Block.createCuboidShape(10, 8, 7, 18, 10, 9),
            Block.createCuboidShape(10, 6, 7, 12, 8, 9), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_EAST = VoxelShapes.combineAndSimplify(Block.createCuboidShape(-2, 8, 7, 6, 10, 9),
            Block.createCuboidShape(4, 6, 7, 6, 8, 9), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_SOUTH = VoxelShapes.combineAndSimplify(Block.createCuboidShape(7, 8, -2, 9, 10, 6),
            Block.createCuboidShape(7, 6, 4, 9, 8, 6), BooleanBiFunction.OR);

    public FaucetBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(WATERLOGGED, false).with(OUTDOOR, false));
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stackHand = player.getStackInHand(hand);
        if (stackHand.isIn(UDItemTagProvider.TOOLBOXES)) {
            world.setBlockState(pos, state.cycle(OUTDOOR));
            UDUtils.ToolboxUsed(world, pos);
            player.sendMessage(Text.translatable("toolbox.faucet.variant_" + state.get(OUTDOOR)), true);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return switch (state.get(FACING)) {
            default -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, OUTDOOR);
    }
}
