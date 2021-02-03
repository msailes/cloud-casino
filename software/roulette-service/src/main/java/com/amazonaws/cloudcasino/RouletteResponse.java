package com.amazonaws.cloudcasino;

public class RouletteResponse {

    private String betId;
    private int returns;

    public RouletteResponse(String betId, int returns) {
        this.betId = betId;
        this.returns = returns;
    }

    public RouletteResponse() {
    }

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public int getReturns() {
        return returns;
    }

    public void setReturns(int returns) {
        this.returns = returns;
    }
}
