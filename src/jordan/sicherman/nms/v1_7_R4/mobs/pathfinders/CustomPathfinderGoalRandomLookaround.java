package jordan.sicherman.nms.v1_7_R4.mobs.pathfinders;

import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.PathfinderGoal;

public class CustomPathfinderGoalRandomLookaround extends PathfinderGoal {

    private final EntityInsentient creature;
    private double xOffset;
    private double zOffset;
    private int lookAway;

    public CustomPathfinderGoalRandomLookaround(EntityInsentient creature) {
        this.creature = creature;
        this.a(3);
    }

    public boolean a() {
        return this.creature.aI().nextFloat() < 0.02F;
    }

    public boolean b() {
        return this.lookAway >= 0;
    }

    public void c() {
        double d1 = 6.283185307179586D * this.creature.aI().nextDouble();

        this.xOffset = Math.cos(d1);
        this.zOffset = Math.sin(d1);
        this.lookAway = 20 + this.creature.aI().nextInt(20);
    }

    public void e() {
        --this.lookAway;
        this.creature.getControllerLook().a(this.creature.locX + this.xOffset, this.creature.locY + (double) this.creature.getHeadHeight(), this.creature.locZ + this.zOffset, 10.0F, (float) this.creature.x());
    }
}
