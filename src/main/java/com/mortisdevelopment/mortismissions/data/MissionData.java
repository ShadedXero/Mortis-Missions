package com.mortisdevelopment.mortismissions.data;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MissionData extends ItemData {

    private final String uuidKey = "Mission";
    private final String idKey = "MissionId";
    private final String maxKey = "MissionMax";
    private final String currentKey = "MissionCurrent";
    private final String completeKey = "MissionComplete";

    public MissionData(@NotNull ItemMeta meta) {
        super(meta);
    }

    public void create(String id, int max, int current, boolean complete) {
        setUUID(UUID.randomUUID());
        setId(id);
        setMax(max);
        setCurrent(current);
        setComplete(complete);
    }

    public void setUUID(UUID uuid) {
        if (uuid == null) {
            setString(uuidKey, null);
            return;
        }
        setString(uuidKey, uuid.toString());
    }

    public UUID getUUID() {
        String value = getString(uuidKey);
        if (value == null) {
            return null;
        }
        return UUID.fromString(value);
    }

    public void setId(String id) {
        if (id == null) {
            setString(idKey, null);
            return;
        }
        setString(idKey, id);
    }

    public String getId() {
        return getString(idKey);
    }

    public void setMax(int max) {
        setInt(maxKey, max);
    }

    public int getMax() {
        return getInt(maxKey);
    }

    public void setCurrent(int current) {
        setInt(currentKey, current);
    }

    public int getCurrent() {
        return getInt(currentKey);
    }

    public void setComplete(boolean complete) {
        setString(completeKey, Boolean.toString(complete));
    }

    public boolean isComplete() {
        String value = getString(completeKey);
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    public String getUuidKey() {
        return uuidKey;
    }

    public String getIdKey() {
        return idKey;
    }

    public String getMaxKey() {
        return maxKey;
    }

    public String getCurrentKey() {
        return currentKey;
    }

    public String getCompleteKey() {
        return completeKey;
    }
}
