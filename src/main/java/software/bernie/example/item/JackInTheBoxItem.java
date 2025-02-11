package software.bernie.example.item;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.client.renderer.item.JackInTheBoxRenderer;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatableSingleton;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class JackInTheBoxItem extends Item implements IAnimatableSingleton<ItemStack>, ISyncable {
	private static final String CONTROLLER_NAME = "popupController";
	private static final int ANIM_OPEN = 0;
	public AnimationFactory<Integer> factory = new AnimationFactory<>(this::createAnimationData);

	public JackInTheBoxItem(Properties properties) {
		super(properties.tab(GeckoLibMod.geckolibItemGroup));
		GeckoLibNetwork.registerSyncable(this);
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IItemRenderProperties() {
			private final BlockEntityWithoutLevelRenderer renderer = new JackInTheBoxRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return renderer;
			}
		});
	}

	private AnimationBuilder predicate(AnimationController<JackInTheBoxItem> controller,
			AnimationEvent<JackInTheBoxItem> event) {
		// Not setting an animation here as that's handled below
		// TODO: item animation refactor
		return null;
	}

	public AnimationData createAnimationData(Integer stack) {
		AnimationData animationData = new AnimationData();
		AnimationController<JackInTheBoxItem> controller = new AnimationController<>(this, CONTROLLER_NAME, 20, this::predicate);

		// Registering a sound listener just makes it so when any sound keyframe is hit
		// the method will be called.
		// To register a particle listener or custom event listener you do the exact
		// same thing, just with registerParticleListener and
		// registerCustomInstructionListener, respectively.
		controller.registerSoundListener(this::soundListener);
		animationData.addAnimationController(controller);
		return animationData;
	}

	private void soundListener(SoundKeyframeEvent<JackInTheBoxItem> event) {
		// The animation for the JackInTheBoxItem has a sound keyframe at time 0:00.
		// As soon as that keyframe gets hit this method fires and it starts playing the
		// sound to the current player.
		// The music is synced with the animation so the box opens as soon as the music
		// plays the box opening sound
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			player.playSound(SoundRegistry.JACK_MUSIC.get(), 1, 1);
		}
	}

	@Override
	public AnimationData getAnimationData(ItemStack key) {
		return factory.getOrCreateAnimationData(GeckoLibUtil.getIDFromStack(key));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if (!world.isClientSide) {
			// Gets the item that the player is holding, should be a JackInTheBoxItem
			final ItemStack stack = player.getItemInHand(hand);
			final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerLevel) world);
			// Tell all nearby clients to trigger this JackInTheBoxItem
			final PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player);
			GeckoLibNetwork.syncAnimation(target, this, id, ANIM_OPEN);
		}
		return super.use(world, player, hand);
	}

	@Override
	public void onAnimationSync(int id, int state) {
		if (state == ANIM_OPEN) {
			// Always use GeckoLibUtil to get AnimationControllers when you don't have
			// access to an AnimationEvent
			final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, CONTROLLER_NAME);

			if (controller.getAnimationState() == AnimationState.Stopped) {
				final LocalPlayer player = Minecraft.getInstance().player;
				if (player != null) {
					player.displayClientMessage(new TextComponent("Opening the jack in the box!"), true);
				}
				// Set the animation to open the JackInTheBoxItem which will start playing music
				// and
				// eventually do the actual animation. Also sets it to not loop
				controller.setAnimation(new AnimationBuilder().addAnimation("Soaryn_chest_popup", false));
			}
		}
	}
}
