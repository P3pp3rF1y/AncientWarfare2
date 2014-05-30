package net.shadowmage.ancientwarfare.npc.entity.faction;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import cpw.mods.fml.common.network.ByteBufUtils;

public abstract class NpcFaction extends NpcBase
{

String subType = "";

public NpcFaction(World par1World)
  {
  super(par1World);
  }

@Override
public boolean attackEntityAsMob(Entity target)
  {
  float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
  int knockback = 0;
  if(target instanceof EntityLivingBase)
    {
    damage += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)target);
    knockback += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)target);
    }
  boolean targetHit = target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
  if(targetHit)
    {
    if(knockback > 0)
      {
      target.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)knockback * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)knockback * 0.5F));
      this.motionX *= 0.6D;
      this.motionZ *= 0.6D;
      }
    int fireDamage = EnchantmentHelper.getFireAspectModifier(this);

    if(fireDamage > 0)
      {
      target.setFire(fireDamage * 4);
      }
    if(target instanceof EntityLivingBase)
      {
      EnchantmentHelper.func_151384_a((EntityLivingBase)target, this);
      }
    EnchantmentHelper.func_151385_b(this, target);
    }
  return targetHit;
  }

@Override
public boolean canBeCommandedBy(String playerName)
  {
  return false;
  }

@Override
public boolean isHostileTowards(Entity e)
  {
  if(e instanceof EntityPlayer)
    {
    int standing = FactionTracker.INSTANCE.getStandingFor(worldObj, e.getCommandSenderName(), getFaction());
    if("elite".equals(subType)){standing-=50;}
    return standing<0;
    }
  else if(e instanceof NpcPlayerOwned)
    {
    NpcBase npc = (NpcBase)e;
    int standing = FactionTracker.INSTANCE.getStandingFor(worldObj, npc.getOwnerName(), getFaction());
    if("elite".equals(subType)){standing-=50;}
    return standing<0;
    }
  else
    {
    List <String> targets = AncientWarfareNPC.statics.getValidTargetsFor(getNpcType(), "");
    String t = EntityList.getEntityString(e);
    if(targets.contains(t))
      {
      return true;
      }
    }
  return false;
  }

@Override
public void onDeath(DamageSource damageSource)
  {  
  super.onDeath(damageSource);
  if(damageSource.getEntity() instanceof EntityPlayer)
    {
    String faction = getFaction();
    EntityPlayer player = (EntityPlayer)damageSource.getEntity();
    FactionTracker.INSTANCE.adjustStandingFor(worldObj, player.getCommandSenderName(), faction, -AWNPCStatics.factionLossOnDeath);
    }  
  else if(damageSource.getEntity() instanceof NpcPlayerOwned)
    {
    NpcBase npc = (NpcBase)damageSource.getEntity();
    String faction = getFaction();
    String playerName = npc.getOwnerName();
    if(!playerName.isEmpty())
      {
      FactionTracker.INSTANCE.adjustStandingFor(worldObj, playerName, faction, -AWNPCStatics.factionLossOnDeath);
      }
    }
  }

public void setSubtype(String subtype)
  {
  if(subtype==null){subtype="";}
  this.subType = subtype;
  }

@Override
public String getNpcSubType()
  {
  return subType;
  }

public String getFaction()
  {
  String type = getNpcType();
  String faction = type.substring(0, type.indexOf("."));
  return faction;
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {
  super.writeEntityToNBT(tag);
  tag.setString("subType", subType);
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {  
  super.readEntityFromNBT(tag);
  subType = tag.getString("subType");
  }

@Override
public void writeSpawnData(ByteBuf buffer)
  {  
  super.writeSpawnData(buffer);
  ByteBufUtils.writeUTF8String(buffer, subType);
  }

@Override
public void readSpawnData(ByteBuf additionalData)
  {
  super.readSpawnData(additionalData);
  subType = ByteBufUtils.readUTF8String(additionalData);
  this.updateTexture();
  }
  
@Override
public void readAdditionalItemData(NBTTagCompound tag)
  {
  //TODO
  }

@Override
public void writeAdditionalItemData(NBTTagCompound tag)
  {
   //TODO
  }

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  //noop, no orders
  return false;
  }

@Override
public void onOrdersInventoryChanged()
  {
  //noop, no orders
  }

@Override
public void onWeaponInventoryChanged()
  {
  //noop, no inventory changing
  }

}
