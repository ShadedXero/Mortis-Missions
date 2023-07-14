package com.mortisdevelopment.mortismissions.missions.requirements;

import io.lumine.mythic.api.mobs.MythicMob;

import java.util.List;

public class MythicMobRequirement extends MissionRequirement {

    private final List<MythicMob> mythicMobs;

    public MythicMobRequirement(List<MythicMob> mythicMobs) {
        this.mythicMobs = mythicMobs;
    }

    @Override
    public boolean isRequirement(Object object) {
        if (!(object instanceof MythicMob)) {
            return false;
        }
        MythicMob mythicMob = (MythicMob) object;
        return mythicMobs.contains(mythicMob);
    }

    public List<MythicMob> getMythicMobs() {
        return mythicMobs;
    }
}
