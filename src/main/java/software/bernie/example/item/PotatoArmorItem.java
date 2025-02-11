package software.bernie.example.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;

public class PotatoArmorItem extends GeoArmorItem {
	private final AnimationFactory<ItemStack> factory = new AnimationFactory<>(this::createAnimationData);

	public PotatoArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
		super(materialIn, slot, builder.tab(GeckoLibMod.geckolibItemGroup));
	}

	// Predicate runs every frame
	@SuppressWarnings("unused")
	private AnimationBuilder predicate(AnimationController<PotatoArmorItem> controller,
			AnimationEvent<PotatoArmorItem> event) {
		// TODO: item animation refactor
		return null;
//		// This is all the extradata this event carries. The livingentity is the entity
//		// that's wearing the armor. The itemstack and equipmentslottype are self
//		// explanatory.
//		List<EquipmentSlot> slotData = event.getExtraDataOfType(EquipmentSlot.class);
//		List<ItemStack> stackData = event.getExtraDataOfType(ItemStack.class);
//		LivingEntity livingEntity = event.getExtraDataOfType(LivingEntity.class).get(0);
//
//		// Always loop the animation but later on in this method we'll decide whether or
//		// not to actually play it
//		controller.setAnimation(new AnimationBuilder().addAnimation("animation.potato_armor.new", true));
//
//		// If the living entity is an armorstand just play the animation nonstop
//		if (livingEntity instanceof ArmorStand) {
//			return PlayState.CONTINUE;
//		}
//
//		// The entity is a player, so we want to only play if the player is wearing the
//		// full set of armor
//		else if (livingEntity instanceof Player) {
//			Player player = (Player) livingEntity;
//
//			// Get all the equipment, aka the armor, currently held item, and offhand item
//			List<Item> equipmentList = new ArrayList<>();
//			player.getAllSlots().forEach((x) -> equipmentList.add(x.getItem()));
//
//			// elements 2 to 6 are the armor so we take the sublist. Armorlist now only
//			// contains the 4 armor slots
//			List<Item> armorList = equipmentList.subList(2, 6);
//
//			// Make sure the player is wearing all the armor. If they are, continue playing
//			// the animation, otherwise stop
//			boolean isWearingAll = armorList.containsAll(Arrays.asList(ItemRegistry.POTATO_BOOTS.get(), ItemRegistry.POTATO_LEGGINGS.get(), ItemRegistry.POTATO_CHEST.get(), ItemRegistry.POTATO_HEAD.get()));
//			return isWearingAll ? PlayState.CONTINUE : PlayState.STOP;
//		}
//		return PlayState.STOP;
	}

	// All you need to do here is add your animation controllers to the
	// AnimationData
	public AnimationData createAnimationData(ItemStack stack) {
		AnimationData data = new AnimationData();
		data.addAnimationController(new AnimationController<>(this, "controller", 20, this::predicate));
		return data;
	}

	@Override
	public AnimationData getAnimationData(ItemStack key) {
		return factory.getOrCreateAnimationData(key);
	}
}
