package twopiradians.minewatch.common.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import twopiradians.minewatch.common.Minewatch;
import twopiradians.minewatch.common.item.weapon.ItemMWWeapon;
import twopiradians.minewatch.common.sound.ModSoundEvents;

public class EntityReaperBullet extends EntityMWThrowable {

	public EntityReaperBullet(World worldIn) {
		super(worldIn);
		this.setSize(0.1f, 0.1f);
	}

	public EntityReaperBullet(World worldIn, EntityLivingBase throwerIn, EnumHand hand) {
		super(worldIn, throwerIn);
		this.setNoGravity(true);
		this.lifetime = 5;
	}

	@Override
	public void onUpdate() {		
		super.onUpdate();

		if (this.world.isRemote) {
			int numParticles = (int) ((Math.abs(motionX)+Math.abs(motionY)+Math.abs(motionZ))*10d);
			for (int i=0; i<numParticles; ++i)
				Minewatch.proxy.spawnParticlesTrail(this.world, 
						this.posX+(this.prevPosX-this.posX)*i/numParticles+world.rand.nextDouble()*0.05d, 
						this.posY+(this.prevPosY-this.posY)*i/numParticles+world.rand.nextDouble()*0.05d, 
						this.posZ+(this.prevPosZ-this.posZ)*i/numParticles+world.rand.nextDouble()*0.05d, 
						0, 0, 0, 0xAF371E, 0xFFC26E, 0.3f, 1, 1);
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		super.onImpact(result);

		if (result.entityHit instanceof EntityLivingBase && this.getThrower() instanceof EntityPlayer &&
				result.entityHit != this.getThrower() && ((EntityLivingBase)result.entityHit).getHealth() > 0) {
			if (!this.world.isRemote) {
				float damage = 7 - (7 - 2) * ((float)this.ticksExisted / lifetime);
				((EntityLivingBase)result.entityHit).attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) this.getThrower()), damage*ItemMWWeapon.damageScale);
				((EntityLivingBase)result.entityHit).hurtResistantTime = 0;
			}
			else
				this.getThrower().playSound(ModSoundEvents.hurt, 0.05f, result.entityHit.world.rand.nextFloat()/2+0.75f);
			this.setDead();
		}
	}
}
