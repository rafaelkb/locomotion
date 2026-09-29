package com.trainguy9512.locomotion.animation.animator.entity;

import com.trainguy9512.locomotion.animation.animator.JointAnimator;
import net.minecraft.world.entity.Entity;

/**
 * A joint animator that is associated with a Minecraft entity.
 *
 * <p>The animation system deliberately does not depend on Minecraft's render-state classes here.
 * This keeps the animator API usable on versions before entity render states were introduced.</p>
 */
public interface EntityJointAnimator<T extends Entity> extends JointAnimator<T> {
}
