//package org.lightning323.createkinetic.mixin;
//
//import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
//import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterInteractionHandler;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.lang.ref.WeakReference;
//
//@Mixin(LinkedTypewriterInteractionHandler.class)
//public abstract class LinkedTypewriterInteractionHandlerMixin {
//
//    @Shadow
//    private static WeakReference<LinkedTypewriterBlockEntity> TYPEWRITER;
//
//    @Inject(
//        method = "onKeyPress",
//        at = @At("HEAD")
//    )
//    private static void createkinetic$onKeyPress(
//            int key,
//            int scanCode,
//            int action,
//            int modifiers,
//            CallbackInfo ci
//    ) {
//        System.out.println("KEY PRESS FROM TR "+ key);
////        LinkedTypewriterBlockEntity be =  TYPEWRITER.get();
////
////        if (be == null) {
////            System.out.println("TYPEWRITER is null");
////        } else {
////            System.out.println("TYPEWRITER = " + be.getBlockPos());
////        }
//    }
//}