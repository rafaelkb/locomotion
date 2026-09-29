package com.trainguy9512.locomotion.mixin.access;

//? if >= 1.21.2 {
//?} else {

import com.trainguy9512.locomotion.access.LegacyPlayerModelAccess;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerModel.class)
public interface MixinLegacyPlayerModel extends LegacyPlayerModelAccess {
    @Accessor("jacket") ModelPart locomotion$getJacket();
    @Accessor("leftSleeve") ModelPart locomotion$getLeftSleeve();
    @Accessor("rightSleeve") ModelPart locomotion$getRightSleeve();
    @Accessor("leftPants") ModelPart locomotion$getLeftPants();
    @Accessor("rightPants") ModelPart locomotion$getRightPants();
}
//?}
