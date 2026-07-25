package org.lightning323.createkinetic.compat.computercraft;

import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.lightning323.createkinetic.content.heat.engine.StirlingEngineBlockEntity;
import org.lightning323.createkinetic.content.thruster.ion_thruster.IonThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.thruster.ThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.thruster.creative_thruster.CreativeThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.creative_vector_thruster.CreativeVectorThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.liquid_vector_thruster.LiquidVectorThrusterBlockEntity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ComputerBehaviour extends AbstractComputerBehaviour {
    protected IPeripheral peripheral;
    protected Supplier<IPeripheral> peripheralSupplier;

    private static final Map<Class<? extends SmartBlockEntity>, Function<SmartBlockEntity, IPeripheral>> PERIPHERAL_FACTORIES = new HashMap<>();

    @SuppressWarnings("unchecked")
    private static <T extends SmartBlockEntity> void register(Class<T> clazz, Function<T, IPeripheral> factory) {
        PERIPHERAL_FACTORIES.put(clazz, be -> factory.apply((T) be));
    }

    static {
        register(IonThrusterBlockEntity.class, IonThrusterPeripheral::new);
        register(ThrusterBlockEntity.class, ThrusterPeripheral::new);
        register(CreativeVectorThrusterBlockEntity.class, CreativeVectorThrusterPeripheral::new);
        register(VectorThrusterBlockEntity.class, VectorThrusterPeripheral::new);
        register(LiquidVectorThrusterBlockEntity.class, LiquidVectorThrusterPeripheral::new);
        register(CreativeThrusterBlockEntity.class, CreativeThrusterPeripheral::new);
        register(StirlingEngineBlockEntity.class, StirlingEnginePeripheral::new);
    }

    public ComputerBehaviour(SmartBlockEntity blockEntity) {
        this(blockEntity, findPeripheralSupplier(blockEntity).orElseThrow(() ->
            new IllegalArgumentException("No peripheral available for " + blockEntity.getClass().getName())));
    }

    private ComputerBehaviour(SmartBlockEntity blockEntity, Supplier<IPeripheral> peripheralSupplier) {
        super(blockEntity);
        this.peripheralSupplier = peripheralSupplier;
    }

    @Nullable
    public static ComputerBehaviour tryCreate(SmartBlockEntity blockEntity) {
        return findPeripheralSupplier(blockEntity)
            .map(supplier -> new ComputerBehaviour(blockEntity, supplier))
            .orElse(null);
    }

    public static Optional<Supplier<IPeripheral>> findPeripheralSupplier(SmartBlockEntity blockEntity) {
        Class<?> current = blockEntity.getClass();
        while (current != null && SmartBlockEntity.class.isAssignableFrom(current)) {
            @SuppressWarnings("unchecked")
            Function<SmartBlockEntity, IPeripheral> factory =
                PERIPHERAL_FACTORIES.get((Class<? extends SmartBlockEntity>) current);
            if (factory != null) {
                return Optional.of(() -> factory.apply(blockEntity));
            }
            current = current.getSuperclass();
        }
        return Optional.empty();
    }

    /** @deprecated use {@link #findPeripheralSupplier} */
    public static Supplier<IPeripheral> getPeripheralFor(SmartBlockEntity blockEntity) {
        return findPeripheralSupplier(blockEntity).orElseThrow(() ->
            new IllegalArgumentException("No peripheral available for " + blockEntity.getClass().getName()));
    }

    @Override
    public IPeripheral getPeripheralCapability() {
        if (peripheral == null)
            peripheral = peripheralSupplier.get();
        return peripheral;
    }

    @Override
    public void removePeripheral() {
        peripheral = null;
    }
}
