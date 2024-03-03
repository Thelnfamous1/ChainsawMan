package me.Thelnfamous1.chainsaw_man.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class ConstantVelocityProjectile extends ProjectileEntity implements IEntityAdditionalSpawnData {
    public double xStep;
    public double yStep;
    public double zStep;

    protected ConstantVelocityProjectile(EntityType<? extends ConstantVelocityProjectile> type, World world) {
        super(type, world);
    }

    public ConstantVelocityProjectile(EntityType<? extends ConstantVelocityProjectile> type, double x, double y, double z, double xDist, double yDist, double zDist, World p_i50174_14_) {
        this(type, p_i50174_14_);
        this.moveTo(x, y, z, this.yRot, this.xRot);
        this.reapplyPosition();
        double distance = MathHelper.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
        if (distance != 0.0D) {
            this.xStep = xDist / distance;
            this.yStep = yDist / distance;
            this.zStep = zDist / distance;
        }

    }

    public ConstantVelocityProjectile(EntityType<? extends ConstantVelocityProjectile> type, LivingEntity shooter, double xDist, double yDist, double zDist, World world) {
        this(type, shooter.getX(), shooter.getY(), shooter.getZ(), xDist, yDist, zDist, world);
        this.setOwner(shooter);
        this.setRot(shooter.yRot, shooter.xRot);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double size = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(size)) {
            size = 4.0D;
        }

        size = size * 64.0D;
        return pDistance < size * size;
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();
        if (this.level.isClientSide || (owner == null || !owner.removed) && this.level.hasChunkAt(this.blockPosition())) {
            super.tick();

            RayTraceResult hitResult = ProjectileHelper.getHitResult(this, this::canHitEntity);
            if (hitResult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitResult)) {
                this.onHit(hitResult);
            }

            this.checkInsideBlocks();
            Vector3d movement = this.getDeltaMovement();
            double x = this.getX() + movement.x;
            double y = this.getY() + movement.y;
            double z = this.getZ() + movement.z;
            ProjectileHelper.rotateTowardsMovement(this, 0.2F);
            float inertia = this.getInertia();
            if (this.isInWater()) {
                for(int i = 0; i < 4; ++i) {
                    float f1 = 0.25F;
                    this.level.addParticle(ParticleTypes.BUBBLE, x - movement.x * 0.25D, y - movement.y * 0.25D, z - movement.z * 0.25D, movement.x, movement.y, movement.z);
                }

                inertia = 0.8F;
            }

            this.setDeltaMovement(new Vector3d(this.xStep, this.yStep, this.zStep).scale(inertia));
            this.level.addParticle(this.getTrailParticle(), x, y + 0.5D, z, 0.0D, 0.0D, 0.0D);
            this.setPos(x, y, z);
        } else {
            this.remove();
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !entity.noPhysics;
    }

    protected IParticleData getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    protected float getInertia() {
        return 0.95F;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("step", this.newDoubleList(new double[]{this.xStep, this.yStep, this.zStep}));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("step", 9)) {
            ListNBT listnbt = pCompound.getList("step", 6);
            if (listnbt.size() == 3) {
                this.xStep = listnbt.getDouble(0);
                this.yStep = listnbt.getDouble(1);
                this.zStep = listnbt.getDouble(2);
            }
        }

    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public float getPickRadius() {
        return 1.0F;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            this.markHurt();
            Entity entity = pSource.getEntity();
            if (entity != null) {
                Vector3d vector3d = entity.getLookAngle();
                this.setDeltaMovement(vector3d);
                this.xStep = vector3d.x * 0.1D;
                this.yStep = vector3d.y * 0.1D;
                this.zStep = vector3d.z * 0.1D;
                this.setOwner(entity);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        Entity owner = this.getOwner();
        buffer.writeVarInt(owner != null ? owner.getId() : 0);
        buffer.writeShort((int)(MathHelper.clamp(this.xStep, -3.9D, 3.9D) * 8000.0D));
        buffer.writeShort((int)(MathHelper.clamp(this.yStep, -3.9D, 3.9D) * 8000.0D));
        buffer.writeShort((int)(MathHelper.clamp(this.zStep, -3.9D, 3.9D) * 8000.0D));
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        int ownerId = additionalData.readVarInt();
        if(ownerId > 0){
            this.setOwner(this.level.getEntity(ownerId));
        }
        this.xStep = additionalData.readShort() / 8000.0D;
        this.yStep = additionalData.readShort() / 8000.0D;
        this.zStep = additionalData.readShort() / 8000.0D;
    }
}
