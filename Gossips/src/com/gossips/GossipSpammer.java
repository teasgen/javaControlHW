package com.gossips;

import java.util.ArrayList;
import java.util.Random;

public final class GossipSpammer extends Gossip {
    private int cntMessages = 0;
    public static final Random rand = new Random();
    public GossipSpammer(String name, String type, int m) {
        super(name, type, m);
    }

    @Override
    public void sendMessage(String message, ArrayList<Gossip> chain, Gossip parent) {
        if (!this.isOk) {
            return;
        }

        chain.add(this);
        cntMessages = printMessageAndCheckCount(cntMessages, message, parent);
        int howManyTimesSpam = rand.nextInt(3) + 2;

        for (var follower: followers) {
            if (!chain.contains(follower)) {
                for (int i = 0; i < howManyTimesSpam; i++) {
                    follower.sendMessage(message, new ArrayList<Gossip>(chain), this);
                }
            }
        }
    }
}
