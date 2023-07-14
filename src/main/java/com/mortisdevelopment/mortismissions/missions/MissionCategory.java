package com.mortisdevelopment.mortismissions.missions;

import java.util.List;
import java.util.Random;

public class MissionCategory {

    private final String id;
    private final List<String> missions;

    public MissionCategory(String id, List<String> missions) {
        this.id = id;
        this.missions = missions;
    }

    public String getRandom() {
        Random random = new Random();
        return missions.get(random.nextInt(0, missions.size()));
    }

    public String getId() {
        return id;
    }

    public List<String> getMissions() {
        return missions;
    }
}
