package com.commander.eventeditor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件数据模型 - 对应HTML中的事件配置
 */
public class Event {
    private int Id;
    private int ConquerId;
    private int EventBuffId1;
    private int Round1;
    private List<Integer> CountryId1 = new ArrayList<>();
    private int EventBuffId2;
    private int Round2;
    private List<Integer> CountryId2 = new ArrayList<>();
    private int Trigger;
    private List<Integer> TriggerValue = new ArrayList<>();
    private List<Integer> Location = new ArrayList<>();
    private int Chance;

    // Getters and Setters
    public int getId() { return Id; }
    public void setId(int id) { Id = id; }

    public int getConquerId() { return ConquerId; }
    public void setConquerId(int conquerId) { ConquerId = conquerId; }

    public int getEventBuffId1() { return EventBuffId1; }
    public void setEventBuffId1(int eventBuffId1) { EventBuffId1 = eventBuffId1; }

    public int getRound1() { return Round1; }
    public void setRound1(int round1) { Round1 = round1; }

    public List<Integer> getCountryId1() { return CountryId1; }
    public void setCountryId1(List<Integer> countryId1) { CountryId1 = countryId1; }

    public int getEventBuffId2() { return EventBuffId2; }
    public void setEventBuffId2(int eventBuffId2) { EventBuffId2 = eventBuffId2; }

    public int getRound2() { return Round2; }
    public void setRound2(int round2) { Round2 = round2; }

    public List<Integer> getCountryId2() { return CountryId2; }
    public void setCountryId2(List<Integer> countryId2) { CountryId2 = countryId2; }

    public int getTrigger() { return Trigger; }
    public void setTrigger(int trigger) { Trigger = trigger; }

    public List<Integer> getTriggerValue() { return TriggerValue; }
    public void setTriggerValue(List<Integer> triggerValue) { TriggerValue = triggerValue; }

    public List<Integer> getLocation() { return Location; }
    public void setLocation(List<Integer> location) { Location = location; }

    public int getChance() { return Chance; }
    public void setChance(int chance) { Chance = chance; }

    /**
     * 获取触发类型名称
     */
    public static String getTriggerName(int triggerId) {
        switch (triggerId) {
            case 1: return "回合触发";
            case 2: return "占领触发";
            case 3: return "攻击触发";
            case 4: return "空袭触发";
            case 5: return "连带触发";
            case 6: return "击败将领";
            case 7: return "核弹触发";
            case 8: return "城市数量";
            case 9: return "概率平分";
            default: return "未知(" + triggerId + ")";
        }
    }

    /**
     * 获取触发说明
     */
    public static String getTriggerDescription(int triggerId, List<Integer> triggerValue) {
        switch (triggerId) {
            case 1:
                if (triggerValue != null && triggerValue.size() >= 2) {
                    return "在 " + triggerValue.get(0) + " 到 " + triggerValue.get(1) + " 回合范围内每回合有概率触发事件";
                }
                return "回合触发需要两个参数: [起始回合, 结束回合]";
            case 2:
                if (triggerValue != null && !triggerValue.isEmpty()) {
                    return "当国家 " + formatList(triggerValue) + " 占领指定地块时触发事件";
                }
                return "当任何国家占领指定地块时触发事件";
            case 3:
                if (triggerValue != null && !triggerValue.isEmpty()) {
                    return "当国家 " + formatList(triggerValue) + " 发起攻击时触发事件";
                }
                return "当任何国家发起攻击时触发事件";
            case 4:
                if (triggerValue != null && !triggerValue.isEmpty()) {
                    return "当国家 " + formatList(triggerValue) + " 发起空袭时触发事件";
                }
                return "当任何国家发起空袭时触发事件";
            case 5:
                return "上一个事件触发后连带触发此事件";
            case 6:
                if (triggerValue != null && !triggerValue.isEmpty()) {
                    return "击败将领 " + formatList(triggerValue) + " 后触发事件";
                }
                return "击败指定将领后触发事件";
            case 7:
                if (triggerValue != null && !triggerValue.isEmpty()) {
                    return "当国家 " + formatList(triggerValue) + " 使用核弹时触发事件";
                }
                return "当任何国家使用核弹时触发事件";
            case 8:
                if (triggerValue != null && !triggerValue.isEmpty()) {
                    return "当城市数量达到 " + triggerValue.get(0) + " 时触发事件";
                }
                return "当城市数量达到指定数量时触发事件";
            case 9:
                if (triggerValue != null && triggerValue.size() >= 2) {
                    return "在 " + triggerValue.get(0) + " 到 " + triggerValue.get(1) + " 回合范围内，每回合有概率触发";
                }
                return "概率平分触发需要两个参数: [起始回合, 结束回合]";
            default:
                return "未知触发类型";
        }
    }

    private static String formatList(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i));
        }
        return sb.toString();
    }
}
