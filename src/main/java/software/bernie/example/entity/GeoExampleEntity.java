package software.bernie.example.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimated;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class GeoExampleEntity extends PathfinderMob implements IAnimated, IAnimationTickable {
	private final AnimationData data = new AnimationData();

	private <E extends IAnimated> AnimationBuilder predicate(AnimationController<E> controller, AnimationEvent<E> event) {
		return new AnimationBuilder().addAnimation("animation.bat.fly", true);
	}

	public GeoExampleEntity(EntityType<? extends GeoExampleEntity> type, Level worldIn) {
		super(type, worldIn);
		this.noCulling = true;
		data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationData getAnimationData() {
		return data;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
		super.registerGoals();
	}

	@Override
	public int tickTimer() {
		return tickCount;
	}
}
