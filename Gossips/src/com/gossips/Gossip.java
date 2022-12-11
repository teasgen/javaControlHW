package com.gossips;
import java.util.ArrayList;
public abstract class Gossip {
    protected boolean isOk;
    protected ArrayList<Gossip> followers;
    protected String name, type;
    protected int m;
    public Gossip(String name, String type, int m) {
        this.name = name;
        this.type = type;
        this.isOk = true;
        this.m = m;
        followers = new ArrayList<Gossip>();
    }
    public String getName() { return this.name; }
    public String getType() { return this.type; }
    public boolean addFollower(Gossip newFollower) {
        if (followers.contains(newFollower)) {
            System.out.println("Warning: " + newFollower.getName() + " is already followed " + this.getName());
            return false;
        }
        followers.add(newFollower);
        return true;
    }
    public boolean removeFollower(Gossip oldFollower) {
        if (!followers.contains(oldFollower)) {
            System.out.println("Error: " + oldFollower.getName() + " is not followed " + this.getName());
            return false;
        }
        followers.remove(oldFollower);
        return true;
    }
    public void printListeners() {
        followers.sort(new GossipComparator());
        for (int i = 0; i < followers.size(); i++) {
            System.out.println("Listener " + String.valueOf(i + 1) + ": " + followers.get(i).getName());
        }
    }
    public int printMessageAndCheckCount(int cntMessages, String message, Gossip parent) {
        ++cntMessages;
        String par = (parent == null ? "" : ", from: " + parent.getName());
        System.out.println(
                this.getName() +
                        ", message number = " +
                        String.valueOf(cntMessages) +
                        ", message: \"" +
                        message + "\"" + par);
        if (cntMessages == m) {
            this.isOk = false;
            System.out.println(this.getName() + ": I'm tired. Goodbye!");
        }
        return cntMessages;
    }
    public abstract void sendMessage(String message, ArrayList<Gossip> chain, Gossip parent);
}
