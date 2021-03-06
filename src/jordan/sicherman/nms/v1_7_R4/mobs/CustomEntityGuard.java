package jordan.sicherman.nms.v1_7_R4.mobs;

import java.util.Calendar;
import jordan.sicherman.MyZ;
import jordan.sicherman.nms.v1_7_R4.mobs.pathfinders.CustomPathfinderGoalArrowAttack;
import jordan.sicherman.nms.v1_7_R4.mobs.pathfinders.CustomPathfinderGoalHurtByTarget;
import jordan.sicherman.nms.v1_7_R4.mobs.pathfinders.CustomPathfinderGoalLookAtPlayer;
import jordan.sicherman.nms.v1_7_R4.mobs.pathfinders.CustomPathfinderGoalMeleeAttack;
import jordan.sicherman.nms.v1_7_R4.mobs.pathfinders.CustomPathfinderGoalMoveToLocation;
import jordan.sicherman.nms.v1_7_R4.mobs.pathfinders.CustomPathfinderGoalNearestAttackableTarget;
import jordan.sicherman.nms.v1_7_R4.mobs.pathfinders.CustomPathfinderGoalRandomLookaround;
import jordan.sicherman.nms.v1_7_R4.mobs.pathfinders.CustomPathfinderGoalRandomStroll;
import jordan.sicherman.utilities.configuration.ConfigEntries;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import net.minecraft.server.v1_7_R4.AttributeModifier;
import net.minecraft.server.v1_7_R4.Block;
import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.EntityCreature;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntitySkeleton;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import net.minecraft.server.v1_7_R4.GroupDataEntity;
import net.minecraft.server.v1_7_R4.Item;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.Items;
import net.minecraft.server.v1_7_R4.MathHelper;
import net.minecraft.server.v1_7_R4.MobEffectList;
import net.minecraft.server.v1_7_R4.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R4.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_7_R4.StepSound;
import net.minecraft.server.v1_7_R4.World;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_7_R4.util.UnsafeList;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomEntityGuard extends EntitySkeleton implements SmartEntity {

    private Location smartTarget;
    private final CustomEntityGuard.GuardType type;
    private final CustomPathfinderGoalMeleeAttack humanAttack;
    private final CustomPathfinderGoalMeleeAttack zombieAttack;
    private final CustomPathfinderGoalMeleeAttack giantAttack;
    private final CustomPathfinderGoalMeleeAttack pigmanAttack;
    private final CustomPathfinderGoalArrowAttack rangedAttack;

    public void setSmartTarget(Location inLoc, long duration) {
        this.smartTarget = inLoc;
        (new BukkitRunnable() {
            public void run() {
                CustomEntityGuard.this.smartTarget = null;
            }
        }).runTaskLater(MyZ.instance, duration);
    }

    public Location getSmartTarget() {
        return this.smartTarget;
    }

    public EntityCreature getEntity() {
        return this;
    }

    protected void bj() {
        this.motY = 0.46D * ((Double) ConfigEntries.GUARD_JUMP_MULTIPLIER.getValue()).doubleValue();
        if (this.hasEffect(MobEffectList.JUMP)) {
            this.motY += (double) ((float) (this.getEffect(MobEffectList.JUMP).getAmplifier() + 1) * 0.1F);
        }

        if (this.isSprinting()) {
            float f = this.yaw * 0.01745329F;

            this.motX -= (double) (MathHelper.sin(f) * 0.2F);
            this.motZ += (double) (MathHelper.cos(f) * 0.2F);
        }

        this.al = true;
    }

    public CustomEntityGuard(World world) {
        super(world);
        this.humanAttack = new CustomPathfinderGoalMeleeAttack(this, EntityHuman.class, ((Double) ConfigEntries.GUARD_SPEED_TARGET.getValue()).doubleValue(), false);
        this.zombieAttack = new CustomPathfinderGoalMeleeAttack(this, CustomEntityZombie.class, ((Double) ConfigEntries.GUARD_SPEED_TARGET.getValue()).doubleValue(), true);
        this.giantAttack = new CustomPathfinderGoalMeleeAttack(this, CustomEntityGiantZombie.class, ((Double) ConfigEntries.GUARD_SPEED_TARGET.getValue()).doubleValue(), true);
        this.pigmanAttack = new CustomPathfinderGoalMeleeAttack(this, CustomEntityPigZombie.class, ((Double) ConfigEntries.GUARD_SPEED_TARGET.getValue()).doubleValue(), true);
        this.rangedAttack = new CustomPathfinderGoalArrowAttack(this, ((Double) ConfigEntries.GUARD_SPEED_TARGET.getValue()).doubleValue(), 20, 50, 15.0F);
        this.type = CustomEntityGuard.GuardType.values()[this.aI().nextInt(CustomEntityGuard.GuardType.values().length)];
        DisguiseAPI.disguiseToAll(this.getBukkitEntity(), new PlayerDisguise(ChatColor.translateAlternateColorCodes('&', (String) ConfigEntries.GUARD_NAME.getValue())));

        try {
            CommonMobUtilities.bField.set(this.goalSelector, new UnsafeList());
            CommonMobUtilities.bField.set(this.targetSelector, new UnsafeList());
            CommonMobUtilities.cField.set(this.goalSelector, new UnsafeList());
            CommonMobUtilities.cField.set(this.targetSelector, new UnsafeList());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(7, new CustomPathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(8, new CustomPathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new CustomPathfinderGoalLookAtPlayer(this, CustomEntityZombie.class, 8.0F));
        this.goalSelector.a(8, new CustomPathfinderGoalLookAtPlayer(this, CustomEntityPigZombie.class, 8.0F));
        this.goalSelector.a(8, new CustomPathfinderGoalLookAtPlayer(this, CustomEntityGiantZombie.class, 8.0F));
        this.goalSelector.a(4, new CustomPathfinderGoalMoveToLocation(this, 1.2D));
        this.goalSelector.a(8, new CustomPathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new CustomPathfinderGoalHurtByTarget(this, true, new Class[] { CustomEntityZombie.class, CustomEntityPigZombie.class, CustomEntityGiantZombie.class, EntityHuman.class}));
        this.targetSelector.a(2, new CustomPathfinderGoalNearestAttackableTarget(this, CustomEntityZombie.class, 0, true));
        this.targetSelector.a(2, new CustomPathfinderGoalNearestAttackableTarget(this, CustomEntityPigZombie.class, 0, true));
        this.targetSelector.a(2, new CustomPathfinderGoalNearestAttackableTarget(this, CustomEntityGiantZombie.class, 0, true));
    }

    public boolean canSpawn() {
        return this.world.a(this.boundingBox, this) && this.world.getCubes(this, this.boundingBox).isEmpty() && !this.world.containsLiquid(this.boundingBox);
    }

    public void a(EntityLiving entityliving, float f) {
        if (this.type == CustomEntityGuard.GuardType.RANGED) {
            super.a(entityliving, f);
        }
    }

    public GroupDataEntity prepare(GroupDataEntity groupdataentity) {
        this.getAttributeInstance(GenericAttributes.b).b(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05D, 1));
        this.a();
        this.h(this.random.nextFloat() < 0.55F * this.world.b(this.locX, this.locY, this.locZ));
        if (this.getEquipment(4) == null) {
            Calendar calendar = this.world.V();

            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.random.nextFloat() < 0.25F) {
                this.setEquipment(4, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.PUMPKIN));
                this.dropChances[4] = 0.0F;
            }
        }

        return groupdataentity;
    }

    protected void a() {
        switch (CustomEntityGuard.SyntheticClass_1.$SwitchMap$jordan$sicherman$nms$v1_7_R4$mobs$CustomEntityGuard$GuardType[this.type.ordinal()]) {
        case 1:
            this.setEquipment(0, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.getMaterial((String) ConfigEntries.GUARD_MELEE_ITEM.getValue()))));
            break;

        case 2:
            this.setEquipment(0, new ItemStack(Items.BOW));
        }

        this.setEquipment(1, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.getMaterial((String) ConfigEntries.GUARD_BOOTS_ITEM.getValue()))));
        this.setEquipment(2, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.getMaterial((String) ConfigEntries.GUARD_LEGGINGS_ITEM.getValue()))));
        this.setEquipment(3, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.getMaterial((String) ConfigEntries.GUARD_CHESTPLATE_ITEM.getValue()))));
        this.setEquipment(4, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.getMaterial((String) ConfigEntries.GUARD_HELMET_ITEM.getValue()))));
    }

    protected String t() {
        return "mob.villager.idle";
    }

    protected String aT() {
        return "game.player.hurt";
    }

    protected String aU() {
        return "game.player.die";
    }

    protected void a(int i, int j, int k, Block block) {
        StepSound stepsound = block.stepSound;

        this.makeSound(stepsound.getStepSound(), stepsound.getVolume1() * 0.15F, stepsound.getVolume2());
    }

    public void die() {
        if (((Boolean) ConfigEntries.GUARD_ZOMBIE_DEATH.getValue()).booleanValue()) {
            CustomEntityZombie zombie = new CustomEntityZombie(this.world);

            zombie.setEquipment(0, this.getEquipment(0));
            zombie.setEquipment(1, this.getEquipment(1));
            zombie.setEquipment(2, this.getEquipment(2));
            zombie.setEquipment(3, this.getEquipment(3));
            zombie.setEquipment(4, this.getEquipment(4));
            zombie.setLocation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            this.world.addEntity(zombie);
        }

        super.die();
    }

    protected Item getLoot() {
        return null;
    }

    protected void getRareDrop(int i) {}

    public void bZ() {
        if (this.type != null) {
            this.goalSelector.a(this.humanAttack);
            this.goalSelector.a(this.zombieAttack);
            this.goalSelector.a(this.giantAttack);
            this.goalSelector.a(this.pigmanAttack);
            this.goalSelector.a(this.rangedAttack);
            switch (CustomEntityGuard.SyntheticClass_1.$SwitchMap$jordan$sicherman$nms$v1_7_R4$mobs$CustomEntityGuard$GuardType[this.type.ordinal()]) {
            case 1:
                this.goalSelector.a(4, this.humanAttack);
                this.goalSelector.a(4, this.zombieAttack);
                this.goalSelector.a(2, this.zombieAttack);
                this.goalSelector.a(4, this.pigmanAttack);
                break;

            case 2:
                this.goalSelector.a(4, this.rangedAttack);
            }

        }
    }

    protected void aD() {
        super.aD();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(((Double) ConfigEntries.GUARD_HEALTH.getValue()).doubleValue());
        this.getAttributeInstance(GenericAttributes.c).setValue(((Double) ConfigEntries.GUARD_KNOCKBACK_RESIST.getValue()).doubleValue());
        this.getAttributeInstance(GenericAttributes.d).setValue(((Double) ConfigEntries.GUARD_SPEED.getValue()).doubleValue());
        this.getAttributeInstance(GenericAttributes.e).setValue(((Double) ConfigEntries.GUARD_DAMAGE.getValue()).doubleValue());
    }

    static class SyntheticClass_1 {

        static final int[] $SwitchMap$jordan$sicherman$nms$v1_7_R4$mobs$CustomEntityGuard$GuardType = new int[CustomEntityGuard.GuardType.values().length];

        static {
            try {
                CustomEntityGuard.SyntheticClass_1.$SwitchMap$jordan$sicherman$nms$v1_7_R4$mobs$CustomEntityGuard$GuardType[CustomEntityGuard.GuardType.MELEE.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                CustomEntityGuard.SyntheticClass_1.$SwitchMap$jordan$sicherman$nms$v1_7_R4$mobs$CustomEntityGuard$GuardType[CustomEntityGuard.GuardType.RANGED.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

        }
    }

    private static enum GuardType {

        MELEE, RANGED;
    }
}
