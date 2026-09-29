package com.trainguy9512.locomotion.mixin.access;

//? if >= 1.21.2 {
//?} else {

import com.trainguy9512.locomotion.access.LegacyShulkerModelAccess;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShulkerModel.class)
public interface MixinLegacyShulkerModel extends LegacyShulkerModelAccess {
    @Accessor("base") ModelPart locomotion$getBase();
}
//?}
