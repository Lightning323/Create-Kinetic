    package org.lightning323.createkinetic.blocks.thruster;

    import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
    import dev.ryanhcode.sable.api.block.propeller.BlockEntityPropeller;
    import dev.ryanhcode.sable.api.block.propeller.BlockEntitySubLevelPropellerActor;
    import net.minecraft.core.BlockPos;
    import net.minecraft.core.Direction;
    import net.minecraft.core.particles.ParticleTypes;
    import net.minecraft.server.level.ServerLevel;
    import net.minecraft.world.level.Level;
    import net.minecraft.world.level.block.entity.BlockEntity;
    import net.minecraft.world.level.block.entity.BlockEntityType;
    import net.minecraft.world.level.block.state.BlockState;
    import org.lightning323.createkinetic.Config;
    import org.lightning323.createkinetic.registries.KineticParticles;

    public class ThrusterBlockEntity extends BlockEntity
            implements BlockEntitySubLevelPropellerActor, BlockSubLevelAssemblyListener, BlockEntityPropeller {

        Direction facing = getBlockState().getValue(ThrusterBlock.FACING);

        private double thrust = 50;

        private double airflow = 200;

        @Override
        public BlockEntityPropeller getPropeller() {
            return this;
        }

        public ThrusterBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
            super(typeIn, pos, state);
        }

        @Override
        public Direction getBlockDirection() {
            return facing.getOpposite();
        }

        @Override
        public double getAirflow() {
            return airflow * Config.airflowMultiplier;
        }

        @Override
        public double getThrust() {
            return thrust * Config.thrustMultiplier;
        }

        @Override
        public boolean isActive() {
           return getJetPower() > 0.01f;
        }


        public Direction getFacing() {
            return facing;
        }

        public static void tick(Level level, BlockPos pos, BlockState state, ThrusterBlockEntity be) {
            Direction dir = state.getValue(ThrusterBlock.FACING).getOpposite();
            float particleSpeed = 1f;
            if (level.isClientSide && state.getValue(ThrusterBlock.POWERED)) {
                level.addParticle(
                        KineticParticles.PLUME.get(), //ParticleTypes.CAMPFIRE_SIGNAL_SMOKE
                        pos.getX() + 0.5 + dir.getStepX(),
                        pos.getY() + 0.5 + dir.getStepY(),
                        pos.getZ() + 0.5 + dir.getStepZ(),
                        dir.getStepX() * particleSpeed, dir.getStepY() * particleSpeed, dir.getStepZ() * particleSpeed// motion
                );
            }
        }



        double getJetPower()
        {
            int redstoneLevel = 0;
            if (level != null) {
                redstoneLevel = level.getBestNeighborSignal(getBlockPos());
            }
            return redstoneLevel * thrust;
        }

        @Override
        public void afterMove(ServerLevel oldLevel, ServerLevel newLevel, BlockState state,
                              BlockPos oldPos, BlockPos newPos) {
        }
    }