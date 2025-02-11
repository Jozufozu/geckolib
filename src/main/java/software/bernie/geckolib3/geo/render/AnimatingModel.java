package software.bernie.geckolib3.geo.render;

import java.util.*;

import com.google.common.collect.ImmutableList;

import software.bernie.geckolib3.core.processor.BoneTree;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class AnimatingModel implements BoneTree<AnimatingBone> {

	private final GeoModel model;

	private final List<AnimatingBone> topLevelBones;

	private final List<AnimatingBone> allBones;

	private final Map<String, AnimatingBone> boneLookup = new HashMap<>();

	public AnimatingModel(GeoModel model) {
		this.model = model;

		ImmutableList.Builder<AnimatingBone> topLevel = ImmutableList.builder();
		for (GeoBone topLevelBone : model.topLevelBones) {
			topLevel.add(new AnimatingBone(this, topLevelBone));
		}
		topLevelBones = topLevel.build();

		ImmutableList.Builder<AnimatingBone> all = ImmutableList.builder();
		for (AnimatingBone bone : topLevelBones) {
			bone.walkDepthFirst(e -> {
				all.add(e);
				boneLookup.put(e.getName(), e);
			});
		}
		allBones = all.build();
	}

	@Override
	public List<AnimatingBone> getTopLevelBones() {
		return topLevelBones;
	}

	@Override
	public List<AnimatingBone> getAllBones() {
		return allBones;
	}

	@Override
	public AnimatingBone getBoneByName(String name) {
		return boneLookup.get(name);
	}
}
