package org.lightning323.createkinetic.content.heat.burners.solid;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.lightning323.createkinetic.config.PropulsionConfig;
import org.lightning323.createkinetic.content.heat.burners.AbstractBurnerBlock;
import org.lightning323.createkinetic.content.heat.burners.AbstractBurnerBlockEntity;
import org.lightning323.createkinetic.content.heat.burners.BurnerDamager;
import org.lightning323.createkinetic.registries.KineticBlockEntities;
import org.lightning323.createkinetic.utility.burners.BurnerFuelBehaviour;
import org.lightning323.createkinetic.utility.burners.IBurner;

import java.util.List;

public class SolidBurnerBlockEntity extends AbstractBurnerBlockEntity implements IBurner,Clearable {
    private BurnerFuelBehaviour fuelInventory;
    private BurnerDamager damager;
    
    private int burnTime = 0;
    private static final float MAX_HEAT = 400.0f;

    public SolidBurnerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public SolidBurnerBlockEntity(BlockPos pos, BlockState state) {
        this(KineticBlockEntities.SOLID_BURNER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void setBurnTime(int burnTime) {
        double consumptionMultiplier = Math.max(0.01, PropulsionConfig.SOLID_BURNER_FUEL_CONSUMPTION_MULTIPLIER.get());
        this.burnTime = Math.max(1, (int) Math.ceil(burnTime / consumptionMultiplier));
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        
        fuelInventory = new BurnerFuelBehaviour(this, () -> {});
        behaviours.add(fuelInventory);
        
        damager = new BurnerDamager(this);
        behaviours.add(damager);
    }

    public ItemStack getFuelStack() {
        return fuelInventory.fuelStack;
    }

    public IItemHandler getItemHandler(Direction side) {
        return fuelInventory == null ? null : fuelInventory.getItemHandler();
    }

    @Override
    public float getHeatPerTick() { return 1; }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide()) return;

        //Calculate rates
        float heatGeneration = (burnTime > 0) ? getHeatPerTick() : 0;
        if (burnTime > 0) burnTime--;

        //Apply heat changes
        tickHeatPhysics(heatGeneration);

        //Thermostat
        boolean refueled = false;
        if (burnTime <= 0 && shouldThermostatBurn()) {
            refueled = fuelInventory.tryConsumeFuel();
        }

        //Sync and update state
        if (refueled) {
            notifyUpdate();
        }

        updateBlockState();
        updateHeatLevelName();
    }

    private void updateBlockState() {
        boolean isBurningNow = burnTime > 0;
        HeatLevel currentHeatLevel = calculateHeatLevel();

        BlockState state = getBlockState();
        if (state.getValue(SolidBurnerBlock.LIT) != isBurningNow || state.getValue(AbstractBurnerBlock.HEAT) != currentHeatLevel) {
            level.setBlock(worldPosition, state
                .setValue(SolidBurnerBlock.LIT, isBurningNow)
                .setValue(AbstractBurnerBlock.HEAT, currentHeatLevel), 3);
        }
    }

    @Override
    protected Direction getHeatCapSide() { return Direction.UP; }

    @Override
    protected float getBaseHeatCapacity() { return MAX_HEAT; }

    @Override
    protected void addSpecificTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        ItemStack fuel = fuelInventory.fuelStack;
        if (!fuel.isEmpty()) {
            LangBuilder fuelName = CreateLang.builder().add(fuel.getHoverName()).style(ChatFormatting.GRAY);
            LangBuilder fuelCount = CreateLang.builder().text("x").text(String.valueOf(fuel.getCount())).style(ChatFormatting.GREEN);

            CreateLang.builder().add(fuelName).space().add(fuelCount).forGoggles(tooltip);
        }
    }

    @Override
    protected void write(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("burnTime", burnTime);
    }

    @Override
    protected void read(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        burnTime = tag.getInt("burnTime");
    }
    
    @Override
	  public void clearContent() {
        fuelInventory.fuelStack = ItemStack.EMPTY;
	  }
}
