package com.mortisdevelopment.mortismissions.missions.requirements;

import org.bukkit.entity.EntityType;

import java.util.List;

public class EntityRequirement extends MissionRequirement {

    private final List<EntityType> entities;

    public EntityRequirement(List<EntityType> entities) {
        this.entities = entities;
    }

    @Override
    public boolean isRequirement(Object object) {
        if (!(object instanceof EntityType)) {
            return false;
        }
        EntityType entity = (EntityType) object;
        return entities.contains(entity);
    }

    public List<EntityType> getEntities() {
        return entities;
    }
}
