package com.commander.eventeditor.model;

/**
 * 条件数据模型
 */
public class Condition {
    private String ConditionType;
    private int ConditionValue;
    private String ConditionCompare;

    public Condition() {}

    public Condition(String conditionType, int conditionValue, String conditionCompare) {
        ConditionType = conditionType;
        ConditionValue = conditionValue;
        ConditionCompare = conditionCompare;
    }

    public String getConditionType() { return ConditionType; }
    public void setConditionType(String conditionType) { ConditionType = conditionType; }

    public int getConditionValue() { return ConditionValue; }
    public void setConditionValue(int conditionValue) { ConditionValue = conditionValue; }

    public String getConditionCompare() { return ConditionCompare; }
    public void setConditionCompare(String conditionCompare) { ConditionCompare = conditionCompare; }
}
