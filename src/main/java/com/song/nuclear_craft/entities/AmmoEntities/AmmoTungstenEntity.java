package com.song.nuclear_craft.entities.AmmoEntities;

import com.song.nuclear_craft.entities.AbstractAmmoEntity;
import com.song.nuclear_craft.misc.ConfigCommon;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class AmmoTungstenEntity extends AbstractAmmoEntity {

    public AmmoTungstenEntity(EntityType<? extends AbstractAmmoEntity> type, World world) {
        super(type, world);
    }

    public AmmoTungstenEntity(FMLPlayMessages.SpawnEntity entity, World world) {
        super(entity, world);
    }

    public AmmoTungstenEntity(double x, double y, double z, World world, ItemStack itemStack, PlayerEntity shooter) {
        super(x, y, z, world, itemStack, shooter);
    }

    @Override
    public double getBlockBreakThreshold() {
        return ConfigCommon.AMMO_TUNGSTEN_BLOCK_BREAK_THRESHOLD.get();
    }

    @Override
    public double getRicochetEnergyLoss() {
        return ConfigCommon.AMMO_TUNGSTEN_RICOCHET_LOSS.get();
    }
}
