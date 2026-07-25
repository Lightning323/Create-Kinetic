package org.lightning323.createkinetic.ponder;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.lightning323.createkinetic.content.heat.burners.AbstractBurnerBlock;
import org.lightning323.createkinetic.content.heat.burners.liquid.LiquidBurnerBlockEntity;
import org.lightning323.createkinetic.content.heat.burners.solid.SolidBurnerBlock;
import org.lightning323.createkinetic.content.heat.burners.solid.SolidBurnerBlockEntity;

public class StirlingEngineScene {

    public static void stirlingEngine(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("stirling_engine_solid", "Powering a Stirling Engine");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        Selection burner = util.select().position(2, 1, 2);
        Selection stirling_engine = util.select().position(2, 2, 2);
        Selection assembly = burner.copy().add(stirling_engine);

        scene.world().showSection(assembly, Direction.DOWN);
        scene.world().setKineticSpeed(stirling_engine, 0);
        scene.idle(10);

        scene.overlay().showText(70)
                .sharedText("stirling_engine.intro")
                .placeNearTarget()
                .pointAt(util.vector().topOf(util.grid().at(2, 2, 2)));
        scene.idle(80);

        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.WEST), Pointing.LEFT, 20)
                .rightClick()
                .withItem(new ItemStack(Items.COAL));
        scene.idle(20);

        scene.world().modifyBlockEntityNBT(burner, SolidBurnerBlockEntity.class, nbt -> nbt.putInt("burnTime", 200));
        scene.world().modifyBlock(util.grid().at(2, 1, 2),
                state -> state.setValue(SolidBurnerBlock.LIT, true).setValue(AbstractBurnerBlock.HEAT, HeatLevel.KINDLED), false);
        scene.effects().indicateSuccess(util.grid().at(2, 1, 2));
        scene.idle(10);
        scene.world().setKineticSpeed(stirling_engine, 128);
        scene.effects().rotationSpeedIndicator(util.grid().at(2, 2, 2));

        scene.overlay().showText(80)
                .attachKeyFrame()
                .sharedText("stirling_engine.text_1")
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 2), Direction.EAST));
        scene.idle(90);

        scene.world().modifyBlockEntityNBT(burner, SolidBurnerBlockEntity.class, nbt -> nbt.putInt("burnTime", 0));
        scene.world().modifyBlock(util.grid().at(2, 1, 2),
                state -> state.setValue(SolidBurnerBlock.LIT, false).setValue(AbstractBurnerBlock.HEAT, HeatLevel.NONE), false);
        scene.world().setKineticSpeed(stirling_engine, 0);
        scene.effects().indicateSuccess(util.grid().at(2, 1, 2));
        scene.idle(10);

        scene.overlay().showText(80)
                .sharedText("stirling_engine.text_2")
                .placeNearTarget()
                .pointAt(util.vector().topOf(util.grid().at(2, 2, 2)));
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void stirlingEngineLiquid(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("stirling_engine_liquid", "Powering a Stirling Engine");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        Selection burner = util.select().position(2, 1, 2);
        Selection stirling_engine = util.select().position(2, 2, 2);
        Selection pump = util.select().position(0, 1, 2);
        Selection fluidPipe = util.select().position(1, 1, 2);
        Selection clutch = util.select().position(1, 1, 3);
        Selection driveLine = util.select().position(0, 1, 3)
                .add(util.select().position(2, 1, 3));
        Selection fluidLine = pump.copy().add(fluidPipe).add(clutch).add(driveLine);
        Selection assembly = burner.copy().add(stirling_engine).add(fluidLine);

        scene.world().showSection(assembly, Direction.DOWN);
        scene.world().setKineticSpeed(fluidLine, 64);
        scene.world().setKineticSpeed(stirling_engine, 0);
        scene.idle(20);

        scene.overlay().showText(80)
                .sharedText("stirling_engine.intro")
                .placeNearTarget()
                .pointAt(util.vector().topOf(util.grid().at(2, 2, 2)));
        scene.idle(90);

        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(0, 1, 2), Direction.WEST), Pointing.RIGHT, 20)
                .rightClick()
                .withItem(new ItemStack(Items.LAVA_BUCKET));
        scene.idle(30);

        scene.overlay().showText(80)
                .sharedText("liquid_burner.intro")
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(util.grid().at(0, 1, 2), Direction.WEST));
        scene.idle(40);

        scene.world().modifyBlockEntityNBT(burner, LiquidBurnerBlockEntity.class, nbt -> {
            nbt.putInt("burnTime", 200);
            nbt.putFloat("burnEfficiency", 1.0f);
        });
        scene.world().modifyBlock(util.grid().at(2, 1, 2),
                state -> state.setValue(AbstractBurnerBlock.HEAT, HeatLevel.KINDLED), false);
        scene.effects().indicateSuccess(util.grid().at(2, 1, 2));
        scene.idle(50);

        scene.world().setKineticSpeed(stirling_engine, 128);
        scene.effects().rotationSpeedIndicator(util.grid().at(2, 2, 2));
        scene.effects().rotationSpeedIndicator(util.grid().at(3, 1, 2));

        scene.overlay().showText(80)
                .attachKeyFrame()
                .sharedText("stirling_engine.text_1")
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 2), Direction.EAST));
        scene.idle(90);

        scene.overlay().showText(90)
                .attachKeyFrame()
                .sharedText("stirling_engine_liquid.clutch_shutdown")
                .placeNearTarget()
                .pointAt(util.vector().centerOf(util.grid().at(1, 1, 3)));
        scene.world().modifyBlock(util.grid().at(1, 1, 3),
                state -> state.setValue(BlockStateProperties.POWERED, true), false);
        scene.world().setKineticSpeed(burner, 0);
        
        scene.world().setKineticSpeed(fluidLine, 0);

        scene.effects().indicateRedstone(util.grid().at(1, 1, 3));
        scene.idle(60);

        scene.world().setKineticSpeed(stirling_engine, 0);
        scene.world().modifyBlockEntityNBT(burner, LiquidBurnerBlockEntity.class, nbt -> nbt.putInt("burnTime", 0));
        scene.world().modifyBlock(util.grid().at(2, 1, 2),
                state -> state.setValue(AbstractBurnerBlock.HEAT, HeatLevel.NONE), false);
        scene.effects().indicateSuccess(util.grid().at(2, 1, 2));
        scene.idle(40);

        scene.overlay().showText(80)
                .sharedText("stirling_engine.text_2")
                .placeNearTarget()
                .pointAt(util.vector().topOf(util.grid().at(2, 2, 2)));
        scene.idle(90);
        scene.markAsFinished();
    }
}

