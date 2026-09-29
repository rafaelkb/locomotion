package com.trainguy9512.locomotion.debug;

//? if >= 1.21.11 {

/*import com.trainguy9512.locomotion.LocomotionMain;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class LocomotionDebugScreenEntries {

    public static void register(BiConsumer<ResourceLocation, DebugScreenEntry> registrar){
        registrar.accept(ResourceLocation.fromNamespaceAndPath(LocomotionMain.MOD_ID, "first_person_drivers"), new DebugEntryFirstPersonDrivers());
        registrar.accept(ResourceLocation.fromNamespaceAndPath(LocomotionMain.MOD_ID, "currently_evaluating_block_entity_animators"), new DebugEntryBlockEntityAnimators());
    }

}
*///?}
