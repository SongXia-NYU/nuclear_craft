package com.song.nuclear_craft.particles;

import com.song.nuclear_craft.NuclearCraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleRegister {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, NuclearCraft.MODID);

    public static final RegistryObject<ParticleType<BasicParticleType>> NUKE_PARTICLE_SMOKE = PARTICLES.register("nuke_particle_smoke", ()->new BasicParticleType(true));
    public static final RegistryObject<ParticleType<BasicParticleType>> NUKE_PARTICLE_FIRE = PARTICLES.register("nuke_particle_fire", ()->new BasicParticleType(true));
    public static final RegistryObject<ParticleType<BasicParticleType>> BIG_SMOKE = PARTICLES.register("big_smoke", ()->new BasicParticleType(true));
    public static final RegistryObject<ParticleType<BasicParticleType>> RESTRICTED_HEIGHT_SMOKE_PARTICLE = PARTICLES.register("restricted_height_smoke_particle", ()->new BasicParticleType(true));
    public static final RegistryObject<ParticleType<BasicParticleType>> MUSHROOM_SMOKE_PARTICLE = PARTICLES.register("mushroom_smoke_particle", ()->new BasicParticleType(true));
    public static final RegistryObject<ParticleType<BasicParticleType>> SHOCK_WAVE = PARTICLES.register("down_smoke", ()->new BasicParticleType(true));

    public static final RegistryObject<ParticleType<BasicParticleType>> EXPLODE_CORE = PARTICLES.register("explode_core", ()->new BasicParticleType(true));
//
//    public static BasicParticleType NUKE_PARTICLE_FIRE = new BasicParticleType(true);
//    public static BasicParticleType BIG_SMOKE = new BasicParticleType(true);
//    static {
//        NUKE_PARTICLE_FIRE.setRegistryName("nuke_particle_fire");
//        BIG_SMOKE.setRegistryName("big_smoke");
//    }
}
