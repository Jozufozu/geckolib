package software.bernie.geckolib3.item;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import software.bernie.geckolib3.core.IAnimatableSingleton;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public abstract class GeoArmorItem extends ArmorItem implements IAnimatableSingleton<ItemStack> {
	public GeoArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
		super(materialIn, slot, builder);
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IItemRenderProperties() {

			@Override
			public HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack,
					EquipmentSlot armorSlot, HumanoidModel<?> _default) {
				return GeoArmorRenderer.getRenderer(GeoArmorItem.this.getClass())
						.setCurrentItem(entityLiving, itemStack, armorSlot).applyEntityStats(_default)
						.applySlot(armorSlot);
			}
		});
	}

	@Nullable
	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		Class<? extends GeoArmorItem> clazz = this.getClass();
		GeoArmorRenderer<? super GeoArmorItem> renderer = GeoArmorRenderer.getRenderer(clazz);
		return renderer.getTextureLocation((GeoArmorItem) stack.getItem()).toString();
	}
}
