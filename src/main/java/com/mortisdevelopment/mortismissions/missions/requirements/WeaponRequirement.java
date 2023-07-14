package com.mortisdevelopment.mortismissions.missions.requirements;

import java.util.List;

public class WeaponRequirement extends MissionRequirement {

    private final List<String> weaponTitles;

    public WeaponRequirement(List<String> weaponTitles) {
        this.weaponTitles = weaponTitles;
    }

    @Override
    public boolean isRequirement(Object object) {
        if (!(object instanceof String)) {
            return false;
        }
        String weaponTitle = (String) object;
        return weaponTitles.contains(weaponTitle);
    }

    public List<String> getWeaponTitles() {
        return weaponTitles;
    }
}
