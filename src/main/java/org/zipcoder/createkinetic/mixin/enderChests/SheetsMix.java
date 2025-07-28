package org.zipcoder.createkinetic.mixin.enderChests;

import org.zipcoder.createkinetic.enderChests.CustomEnderChestBlock;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static org.zipcoder.createkinetic.Createkinetic.MODID;


@Mixin(value = Sheets.class)
public abstract class SheetsMix {
    @Shadow
    @Final
    public static ResourceLocation CHEST_SHEET;

    @Unique
    private static Material endChestMaterial(String string) {
        return new Material(CHEST_SHEET, new ResourceLocation(MODID, "entity/chest/" + string));
    }

    @Unique
    private static final Material IRON = endChestMaterial("iron");
    @Unique
    private static final Material COPPER = endChestMaterial("copper");

    @Unique
    private static final Material GOLD = endChestMaterial("gold");

    @Unique
    private static final Material LAPIS = endChestMaterial("lapis");

    @Unique
    private static final Material REDSTONE = endChestMaterial("redstone");

    @Unique
    private static final Material DIAMOND = endChestMaterial("diamond");

    @Unique
    private static final Material EMERALD = endChestMaterial("emerald");

    @Unique
    private static final Material NETHERITE = endChestMaterial("netherite");

    @Inject(method = "chooseMaterial(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/level/block/state/properties/ChestType;Z)Lnet/minecraft/client/resources/model/Material;", at = @At("HEAD"), cancellable = true)
    private static void injectedHead(BlockEntity blockEntity, ChestType chestType, boolean bl, CallbackInfoReturnable<Material> cir) {
        if (blockEntity.getBlockState().getBlock() instanceof CustomEnderChestBlock customEnderChestBlock) {
            switch (customEnderChestBlock.type) {
                case IRON -> {
                    cir.setReturnValue(IRON);
                }
                case COPPER -> {
                    cir.setReturnValue(COPPER);
                }
                case GOLD -> {
                    cir.setReturnValue(GOLD);
                }
                case LAPIS -> {
                    cir.setReturnValue(LAPIS);
                }
                case REDSTONE -> {
                    cir.setReturnValue(REDSTONE);
                }
                case DIAMOND -> {
                    cir.setReturnValue(DIAMOND);
                }
                case EMERALD -> {
                    cir.setReturnValue(EMERALD);
                }
                case NETHERITE -> {
                    cir.setReturnValue(NETHERITE);
                }
            }
        }
    }
}