package com.amazonaws.cloudcasino;

public class RouletteRequest {

    private int stakeAmount;
    private int betNumber;
    private int playerId;

    public RouletteRequest(int stakeAmount, int betNumber, int playerId) {
        this.stakeAmount = stakeAmount;
        this.betNumber = betNumber;
        this.playerId = playerId;
    }

    public RouletteRequest() {
    }

    public int getStakeAmount() {
        return stakeAmount;
    }

    public void setStakeAmount(int stakeAmount) {
        this.stakeAmount = stakeAmount;
    }

    public int getBetNumber() {
        return betNumber;
    }

    public void setBetNumber(int betNumber) {
        this.betNumber = betNumber;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
