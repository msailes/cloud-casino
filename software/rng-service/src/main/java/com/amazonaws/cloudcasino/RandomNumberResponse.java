package com.amazonaws.cloudcasino;

public class RandomNumberResponse {

    public int randomNumber;

    public RandomNumberResponse(int randomNumber) {
        this.randomNumber = randomNumber;
    }

    public RandomNumberResponse() {
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }
}
