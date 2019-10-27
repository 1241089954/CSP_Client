package com.zjicm.csp;

import java.io.Serializable;

public class Table implements Serializable {

    private boolean isInGame;
    private String tableName;
    private boolean isPlayer1Online = false;
    private String player1IP = "";
    private boolean isPlayer2Online = false;
    private String player2IP = "";

    public Table() {
    }

    public Table(String tableName) {
        this.tableName = tableName;
    }

    public Table(String tableName, boolean isPlayer1Online, String player1IP,
                 boolean isPlayer2Online, String player2IP) {
        this.tableName = tableName;
        this.isPlayer1Online = isPlayer1Online;
        this.player1IP = player1IP;
        this.isPlayer2Online = isPlayer2Online;
        this.player2IP = player2IP;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isPlayer1Online() {
        return isPlayer1Online;
    }

    public void setPlayer1Online(boolean player1Online) {
        isPlayer1Online = player1Online;
    }

    public String getPlayer1IP() {
        return player1IP;
    }

    public void setPlayer1IP(String player1IP) {
        this.player1IP = player1IP;
    }

    public boolean isPlayer2Online() {
        return isPlayer2Online;
    }

    public void setPlayer2Online(boolean player2Online) {
        isPlayer2Online = player2Online;
    }

    public String getPlayer2IP() {
        return player2IP;
    }

    public void setPlayer2IP(String player2IP) {
        this.player2IP = player2IP;
    }

    public String toString(){
        return tableName+"  "+isPlayer1Online+"  "+player1IP+"  "+isPlayer2Online+"  "+player2IP;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public void setInGame(boolean inGame) {
        isInGame = inGame;
    }
}
