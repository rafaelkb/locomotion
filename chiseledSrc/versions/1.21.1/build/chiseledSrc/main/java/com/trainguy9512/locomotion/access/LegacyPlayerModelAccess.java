package com.trainguy9512.locomotion.access;

import net.minecraft.client.model.geom.ModelPart;

/** Access to the package-private outer-layer parts of the legacy 1.21.1 player model. */
public interface LegacyPlayerModelAccess {
    ModelPart locomotion$getJacket();
    ModelPart locomotion$getLeftSleeve();
    ModelPart locomotion$getRightSleeve();
    ModelPart locomotion$getLeftPants();
    ModelPart locomotion$getRightPants();
}
