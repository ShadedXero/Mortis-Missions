package me.none030.mortismissions.utils;

public class MissionData {

    private final String id;
    private final String type;
    private final Integer max;
    private final Integer current;
    private final boolean complete;

    public MissionData(String id, String type, Integer max, Integer current, boolean complete) {
        this.id = id;
        this.type = type;
        this.max = max;
        this.current = current;
        this.complete = complete;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Integer getMax() {
        return max;
    }

    public Integer getCurrent() {
        return current;
    }

    public boolean isComplete() {
        return complete;
    }
}
