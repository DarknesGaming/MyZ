package jordan.sicherman.nms.v1_7_R4.mobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jordan.sicherman.nms.utilities.NMS;
import jordan.sicherman.utilities.configuration.ConfigEntries;
import net.minecraft.server.v1_7_R4.BiomeBase;
import net.minecraft.server.v1_7_R4.BiomeMeta;
import net.minecraft.server.v1_7_R4.EntityGiantZombie;
import net.minecraft.server.v1_7_R4.EntityPigZombie;
import net.minecraft.server.v1_7_R4.EntitySkeleton;
import net.minecraft.server.v1_7_R4.EntityTypes;
import net.minecraft.server.v1_7_R4.EntityZombie;
import org.bukkit.entity.EntityType;

public enum CustomEntityType {

    PIG_ZOMBIE("PigZombie", 57, EntityType.PIG_ZOMBIE, new CustomEntityType.CustomBiomeMeta(EntityPigZombie.class, CustomEntityPigZombie.class, ((Integer) ConfigEntries.PIGMAN_PACK_SPAWN.getValue()).intValue(), ((Integer) ConfigEntries.PIGMAN_PACK_MIN.getValue()).intValue(), ((Integer) ConfigEntries.PIGMAN_PACK_MAX.getValue()).intValue())), GUARD("Skeleton", 51, EntityType.SKELETON, new CustomEntityType.CustomBiomeMeta(EntitySkeleton.class, CustomEntityGuard.class, ((Integer) ConfigEntries.GUARD_PACK_SPAWN.getValue()).intValue(), ((Integer) ConfigEntries.GUARD_PACK_MIN.getValue()).intValue(), ((Integer) ConfigEntries.GUARD_PACK_MAX.getValue()).intValue())), ZOMBIE("Zombie", 54, EntityType.ZOMBIE, new CustomEntityType.CustomBiomeMeta(EntityZombie.class, CustomEntityZombie.class, ((Integer) ConfigEntries.ZOMBIE_PACK_SPAWN.getValue()).intValue(), ((Integer) ConfigEntries.ZOMBIE_PACK_MIN.getValue()).intValue(), ((Integer) ConfigEntries.ZOMBIE_PACK_MAX.getValue()).intValue())), GIANT_ZOMBIE("Giant", 53, EntityType.GIANT, new CustomEntityType.CustomBiomeMeta(EntityGiantZombie.class, CustomEntityGiantZombie.class, ((Integer) ConfigEntries.GIANT_PACK_SPAWN.getValue()).intValue(), ((Integer) ConfigEntries.GIANT_PACK_MIN.getValue()).intValue(), ((Integer) ConfigEntries.GIANT_PACK_MAX.getValue()).intValue()));

    private static final Map cache = new HashMap();
    private String name;
    private int id;
    private EntityType entityType;
    private CustomEntityType.CustomBiomeMeta meta;

    private CustomEntityType(String name, int id, EntityType entityType, CustomEntityType.CustomBiomeMeta meta) {
        this.name = name;
        this.id = id;
        this.entityType = entityType;
        this.meta = meta;
    }

    public String getName() {
        return this.name;
    }

    public int getID() {
        return this.id;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public Class getNMSClass() {
        return this.meta.nms;
    }

    public Class getCustomClass() {
        return this.meta.custom;
    }

    public static void registerEntities() {
        CustomEntityType[] biomes = values();
        int zombieExcluded = biomes.length;

        for (int pigmanExcluded = 0; pigmanExcluded < zombieExcluded; ++pigmanExcluded) {
            CustomEntityType guardExcluded = biomes[pigmanExcluded];

            a(guardExcluded.getCustomClass(), guardExcluded.getName(), guardExcluded.getID());
        }

        BiomeBase[] abiomebase;

        try {
            abiomebase = (BiomeBase[]) ((BiomeBase[]) NMS.getPrivateStatic(BiomeBase.class, "biomes"));
        } catch (Exception exception) {
            return;
        }

        List list = (List) ConfigEntries.ZOMBIE_EXCLUDES.getValue();
        List list1 = (List) ConfigEntries.PIGMAN_EXCLUDES.getValue();
        List list2 = (List) ConfigEntries.GUARD_EXCLUDES.getValue();
        List generalExcluded = (List) ConfigEntries.GENERAL_EXCLUDES.getValue();
        BiomeBase[] abiomebase1 = abiomebase;
        int i = abiomebase.length;

        for (int j = 0; j < i; ++j) {
            BiomeBase biomeBase = abiomebase1[j];

            if (biomeBase == null) {
                break;
            }

            if (biomeBase != BiomeBase.HELL) {
                CustomEntityType.cache.put(biomeBase, new CustomEntityType.DefaultCache(biomeBase));
                String[] list10 = CustomEntityType.DefaultCache.keys;
                int k = list10.length;

                for (int l = 0; l < k; ++l) {
                    String key = list10[l];
                    List list11 = (List) NMS.getDeclaredField(biomeBase, key);

                    if (list11 != null) {
                        list11.clear();
                    }
                }

                List list3;

                if (!generalExcluded.contains(Integer.valueOf(biomeBase.ah))) {
                    if (CustomEntityType.PIG_ZOMBIE.meta.enabled && !list1.contains(Integer.valueOf(biomeBase.ah)) && !((CustomEntityType.DefaultCache) CustomEntityType.cache.get(biomeBase)).as.contains(CustomEntityType.PIG_ZOMBIE.meta.toBiomeMeta())) {
                        list3 = (List) NMS.getDeclaredField(biomeBase, CustomEntityType.DefaultCache.keys[0]);
                        if (list3 != null) {
                            list3.add(CustomEntityType.PIG_ZOMBIE.meta.toBiomeMeta());
                        }
                    }

                    if (CustomEntityType.ZOMBIE.meta.enabled && !list10.contains(Integer.valueOf(biomeBase.ah)) && !((CustomEntityType.DefaultCache) CustomEntityType.cache.get(biomeBase)).as.contains(CustomEntityType.ZOMBIE.meta.toBiomeMeta())) {
                        list3 = (List) NMS.getDeclaredField(biomeBase, CustomEntityType.DefaultCache.keys[0]);
                        if (list3 != null) {
                            list3.add(CustomEntityType.ZOMBIE.meta.toBiomeMeta());
                        }
                    }

                    if (CustomEntityType.GUARD.meta.enabled && !list2.contains(Integer.valueOf(biomeBase.ah)) && !((CustomEntityType.DefaultCache) CustomEntityType.cache.get(biomeBase)).as.contains(CustomEntityType.GUARD.meta.toBiomeMeta())) {
                        list3 = (List) NMS.getDeclaredField(biomeBase, CustomEntityType.DefaultCache.keys[0]);
                        if (list3 != null) {
                            list3.add(CustomEntityType.GUARD.meta.toBiomeMeta());
                        }
                    }
                }

                if (CustomEntityType.GIANT_ZOMBIE.meta.enabled && ((List) ConfigEntries.GIANT_INCLUDES.getValue()).contains(Integer.valueOf(biomeBase.ah)) && !((CustomEntityType.DefaultCache) CustomEntityType.cache.get(biomeBase)).as.contains(CustomEntityType.GIANT_ZOMBIE.meta.toBiomeMeta())) {
                    list3 = (List) NMS.getDeclaredField(biomeBase, CustomEntityType.DefaultCache.keys[0]);
                    if (list3 != null) {
                        list3.add(CustomEntityType.GIANT_ZOMBIE.meta.toBiomeMeta());
                    }
                }
            }
        }

    }

    public static void unregisterEntities() {
        CustomEntityType[] biomes = values();
        int exc = biomes.length;

        int i;
        CustomEntityType entity;

        for (i = 0; i < exc; ++i) {
            entity = biomes[i];

            try {
                ((Map) NMS.getPrivateStatic(EntityTypes.class, "c")).remove(entity.getCustomClass());
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            try {
                ((Map) NMS.getPrivateStatic(EntityTypes.class, "e")).remove(entity.getCustomClass());
            } catch (Exception exception1) {
                exception1.printStackTrace();
            }
        }

        biomes = values();
        exc = biomes.length;

        for (i = 0; i < exc; ++i) {
            entity = biomes[i];
            a(entity.getNMSClass(), entity.getName(), entity.getID());
        }

        BiomeBase[] abiomebase;

        try {
            abiomebase = (BiomeBase[]) ((BiomeBase[]) NMS.getPrivateStatic(BiomeBase.class, "biomes"));
        } catch (Exception exception2) {
            return;
        }

        BiomeBase[] abiomebase1 = abiomebase;

        i = abiomebase.length;

        for (int j = 0; j < i; ++j) {
            BiomeBase biomeBase = abiomebase1[j];

            if (biomeBase == null) {
                break;
            }

            if (biomeBase != BiomeBase.HELL) {
                NMS.setDeclaredField(biomeBase, CustomEntityType.DefaultCache.keys[0], ((CustomEntityType.DefaultCache) CustomEntityType.cache.get(biomeBase)).as);
                NMS.setDeclaredField(biomeBase, CustomEntityType.DefaultCache.keys[1], ((CustomEntityType.DefaultCache) CustomEntityType.cache.get(biomeBase)).at);
                NMS.setDeclaredField(biomeBase, CustomEntityType.DefaultCache.keys[2], ((CustomEntityType.DefaultCache) CustomEntityType.cache.get(biomeBase)).au);
                NMS.setDeclaredField(biomeBase, CustomEntityType.DefaultCache.keys[3], ((CustomEntityType.DefaultCache) CustomEntityType.cache.get(biomeBase)).av);
            }
        }

        CustomEntityType.cache.clear();
    }

    private static void a(Class paramClass, String paramString, int paramInt) {
        try {
            ((Map) NMS.getPrivateStatic(EntityTypes.class, "c")).put(paramString, paramClass);
            ((Map) NMS.getPrivateStatic(EntityTypes.class, "d")).put(paramClass, paramString);
            ((Map) NMS.getPrivateStatic(EntityTypes.class, "e")).put(Integer.valueOf(paramInt), paramClass);
            ((Map) NMS.getPrivateStatic(EntityTypes.class, "f")).put(paramClass, Integer.valueOf(paramInt));
            ((Map) NMS.getPrivateStatic(EntityTypes.class, "g")).put(paramString, Integer.valueOf(paramInt));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    private static class DefaultCache {

        List as;
        List at;
        List au;
        List av;
        private static final String[] keys = new String[] { "as", "at", "au", "av"};

        public DefaultCache(BiomeBase baseFor) {
            for (int i = 0; i < CustomEntityType.DefaultCache.keys.length; ++i) {
                switch (i) {
                case 0:
                    this.as = new ArrayList((List) NMS.getDeclaredField(baseFor, CustomEntityType.DefaultCache.keys[i]));
                    break;

                case 1:
                    this.at = new ArrayList((List) NMS.getDeclaredField(baseFor, CustomEntityType.DefaultCache.keys[i]));
                    break;

                case 2:
                    this.au = new ArrayList((List) NMS.getDeclaredField(baseFor, CustomEntityType.DefaultCache.keys[i]));
                    break;

                case 3:
                    this.av = new ArrayList((List) NMS.getDeclaredField(baseFor, CustomEntityType.DefaultCache.keys[i]));
                }
            }

        }
    }

    private static class CustomBiomeMeta {

        private final Class nms;
        private final Class custom;
        private final BiomeMeta meta;
        private final boolean enabled;

        public CustomBiomeMeta(Class nms, Class custom, int spawn_chance, int range_min, int range_max) {
            this.nms = nms;
            this.custom = custom;
            this.enabled = spawn_chance > 0;
            this.meta = new BiomeMeta(custom, spawn_chance, range_min, range_max);
        }

        public BiomeMeta toBiomeMeta() {
            return this.meta;
        }
    }
}
