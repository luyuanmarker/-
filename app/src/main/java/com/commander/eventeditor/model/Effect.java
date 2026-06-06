package com.commander.eventeditor.model;

/**
 * 效果数据模型
 */
public class Effect {
    private int EffectId;
    private String EffectType;
    private int EffectValue;
    private String TargetType;
    private int TargetValue;

    public Effect() {}

    public Effect(int effectId, String effectType, int effectValue, String targetType, int targetValue) {
        EffectId = effectId;
        EffectType = effectType;
        EffectValue = effectValue;
        TargetType = targetType;
        TargetValue = targetValue;
    }

    public int getEffectId() { return EffectId; }
    public void setEffectId(int effectId) { EffectId = effectId; }

    public String getEffectType() { return EffectType; }
    public void setEffectType(String effectType) { EffectType = effectType; }

    public int getEffectValue() { return EffectValue; }
    public void setEffectValue(int effectValue) { EffectValue = effectValue; }

    public String getTargetType() { return TargetType; }
    public void setTargetType(String targetType) { TargetType = targetType; }

    public int getTargetValue() { return TargetValue; }
    public void setTargetValue(int targetValue) { TargetValue = targetValue; }
}
