package com.trainguy9512.locomotion.util;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.UseAnim;

public class LocomotionMultiVersionWrappers {

    public static UseAnim getTridentUseAnimation() {
        //? if >= 1.21.11 {
        /*return UseAnim.TRIDENT;
        *///?} else {
        return UseAnim.SPEAR;
        //?}
    }

    public static UseAnim getSpearUseAnimation() {
        //? if >= 1.21.11 {
        /*return UseAnim.SPEAR;
        *///?} else {
        throw new RuntimeException("1.21.11 feature attempted to be used in older version");
         //?}
    }

    public static boolean shouldTriggerClientSwing(InteractionResult result) {
        //? if >= 1.21.2 {
        /*return result instanceof InteractionResult.Success success
                && success.swingSource() == InteractionResult.SwingSource.CLIENT;
        *///?} else {
        return result.shouldSwing();
        //?}
    }

}
