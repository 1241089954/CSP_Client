package com.zjicm.csp;

import java.io.Serializable;

public class PlayerAction implements Serializable {
    private ACTION action;
    private boolean intoGame;
    private int position;
    private int seat;
    private int csp;

    enum ACTION{
        InOrOut,
        CSP;
    }

    public PlayerAction(ACTION action,int csp){
        this.action = action;
        this.csp = csp;
    }

    public int getCsp() {
        return csp;
    }

    public void setCsp(int csp) {
        this.csp = csp;
    }

    public PlayerAction(ACTION action, boolean intoGame, int position, int seat) {
        this.action = action;
        this.intoGame = intoGame;
        this.position = position;
        this.seat = seat;
    }

    public ACTION getAction() {
        return action;
    }

    public void setAction(ACTION action) {
        this.action = action;
    }

    public boolean isIntoGame() {
        return intoGame;
    }

    public void setIntoGame(boolean intoGame) {
        this.intoGame = intoGame;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }
}
