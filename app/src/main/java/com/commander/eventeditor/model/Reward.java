package com.commander.eventeditor.model;

/**
 * 奖励数据模型
 */
public class Reward {
    private String RewardType;
    private int RewardValue;
    private int RewardAmount;

    public Reward() {}

    public Reward(String rewardType, int rewardValue, int rewardAmount) {
        RewardType = rewardType;
        RewardValue = rewardValue;
        RewardAmount = rewardAmount;
    }

    public String getRewardType() { return RewardType; }
    public void setRewardType(String rewardType) { RewardType = rewardType; }

    public int getRewardValue() { return RewardValue; }
    public void setRewardValue(int rewardValue) { RewardValue = rewardValue; }

    public int getRewardAmount() { return RewardAmount; }
    public void setRewardAmount(int rewardAmount) { RewardAmount = rewardAmount; }
}
