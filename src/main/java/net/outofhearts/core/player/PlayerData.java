package net.outofhearts.core.player;

public class PlayerData {

    public Rank rank;
    public long balance;

    public PlayerData(Rank rank, long balance) {
        this.rank = rank;
        this.balance = balance;
    }
}