package com.song.nuclear_craft.entities;

import com.google.common.collect.Lists;
import com.song.nuclear_craft.misc.ConfigCommon;
import com.song.nuclear_craft.misc.NukeExplosion;
import com.song.nuclear_craft.network.MySExplosionPacket;
import com.song.nuclear_craft.network.NuclearCraftPacketHandler;
import com.song.nuclear_craft.particles.ParticleRegister;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.Random;

public class ExplosionUtils{
    public static final float NUKE_RADIUS = ConfigCommon.NUKE_RADIUS.get().floatValue();
    public static final double MAX_BLAST_POWER = ConfigCommon.NUKE_BLAST_POWER.get();

    public static final float Y_SHORTEN=1.5f;

    public static double getBlastPower(double dist, double radius){
        double decay_rd = radius * 0.95;
        if(dist < decay_rd){
            return 1.1d;
        }
        else {
            return -(1/(radius-decay_rd))*(dist-decay_rd) + 1;
        }
    }

    public static List<BlockPos> getAffectedBlockPositions(World world, double x, double y, double z, float radius, double max_blast_power){
        List<BlockPos> affectedBlockPositions = Lists.newArrayList();
        int radius_int = (int) Math.ceil(radius);
        for (int dx = -radius_int; dx < radius_int + 1; dx++) {
            // fast calculate affected blocks
            int y_lim = (int) (Math.sqrt(radius_int*radius_int-dx*dx)/Y_SHORTEN);
            for (int dy = -y_lim; dy < y_lim + 1; dy++) {
                int z_lim = (int) Math.sqrt(radius_int*radius_int-dx*dx-dy*dy*Y_SHORTEN*Y_SHORTEN);
                for (int dz = -z_lim; dz < z_lim + 1; dz++) {
                    BlockPos blockPos = new BlockPos(x + dx, y + dy, z + dz);
                    BlockState blockState = world.getBlockState(blockPos);
                    double power = getBlastPower(Math.sqrt(dx*dx+dy*dy*Y_SHORTEN*Y_SHORTEN+dz*dz), radius);
                    if (blockState!= Blocks.AIR.getDefaultState() && ((power>1) ||(power > new Random().nextDouble()))){
                        float resistance = blockState.getBlock().getExplosionResistance();
                        if (resistance < max_blast_power) {
                            affectedBlockPositions.add(blockPos);
                        }
                    }
                }
            }
        }
        return affectedBlockPositions;
    }

    public static Explosion oldNukeExplode(World world, Entity entity, double x, double y, double z, float radius, boolean spawnCloud, double max_blast_power) {
        List<BlockPos> affectedBlockPositions = getAffectedBlockPositions(world, x, y, z, radius, max_blast_power);
        NukeExplosion nukeExplosion = new NukeExplosion(world, entity, x, y, z, radius, affectedBlockPositions);
        nukeExplosion.doExplosionA();
        nukeExplosion.doExplosionB(false);
        for (PlayerEntity playerentity : (world.getPlayers())) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) playerentity;
            if (serverplayerentity.getDistanceSq(x, y, z) < 65536D) {
                serverplayerentity.connection.sendPacket(new SExplosionPacket(x, y, z, radius, nukeExplosion.getAffectedBlockPositions(), nukeExplosion.getPlayerKnockbackMap().get(serverplayerentity)));
                if(spawnCloud){
                    // Send custom packet to handle nuke explosion
                    NuclearCraftPacketHandler.EXPLOSION_CHANNEL.send(PacketDistributor.PLAYER.with(()->serverplayerentity), new MySExplosionPacket(radius, x, y, z));
                }

            }
        }
        playNukeSound(world, x, y, z, radius);
        // normal explosion
        world.createExplosion(entity, x, y, z, radius*0.8f, Explosion.Mode.BREAK);
        return nukeExplosion;
    }

    public static Explosion oldNukeExplode(World world, Entity entity, double x, double y, double z, float radius, boolean spawnCloud){
        return oldNukeExplode(world, entity, x, y, z, radius, spawnCloud, MAX_BLAST_POWER);
    }

    public static void playNukeSound(World world, double x, double y, double z, double radius){
        world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, (float) (40.0F*radius/NUKE_RADIUS), (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
    }

    @Deprecated
    public static void multiExplode(World world, Entity entity, double x, double y, double z, float radius, int intensity){
        float hf_rd = radius / 4;
        for(int i = -intensity; i<intensity+1; i++){
            for(int j=-intensity; j<intensity+1; j++){
                for(int k=-intensity; k<1; k++){
                    if(Math.sqrt(i*i+j*j+k*k) < (double)intensity +0.1){
                        world.createExplosion(entity, x + hf_rd * i, y + hf_rd * k, z + hf_rd * j, radius, Explosion.Mode.BREAK);
                    }
                }
            }
        }
    }

    public static void mushroomCloud(double x, double y, double z, double radius){
        ClientWorld world = Minecraft.getInstance().world;
        if(world.isRemote){
            int MAX_STEP_Y = (int)Math.ceil(1.25*radius);
            double step_y = 2.986 * (radius) / MAX_STEP_Y;
            for(int step=0; step<MAX_STEP_Y; step ++){
                double this_r = 0d;
                double delta_y = step * step_y;
                if(delta_y < radius / 3){
                    this_r = radius - delta_y * 2;
                }
                else if(delta_y < radius){
                    this_r = radius / 3;
                }
                else if(delta_y < 2.98 * radius){
                    this_r = Math.sqrt(radius*radius-(1.986*radius-delta_y)*(1.986*radius-delta_y));
                }
                int n_render = (int) Math.ceil(0.75*this_r);
                double theta_step = 6.28 / n_render;
                for (int i=0; i<n_render; i++){
                    double theta = theta_step * i;
                    double delta_x = this_r * Math.cos(theta);
                    double delta_z = this_r * Math.sin(theta);
                    if (delta_y > radius){
                        world.addParticle((IParticleData) ParticleRegister.NUKE_PARTICLE_FIRE.get(), true, x+delta_x, y+delta_y, z+delta_z, 0d, 0.07d, 0d);
                    }
                    else {
                        world.addParticle((IParticleData) ParticleRegister.NUKE_PARTICLE_SMOKE.get(), true, x+delta_x, y+delta_y, z+delta_z, 0d, 0.07d, 0d);
                    }
                }
            }
        }
    }
}
