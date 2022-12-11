package com.gossips;

import java.util.ArrayList;

public final class GossipDeduplicator extends Gossip {
    private int cntMessages = 0;
    private ArrayList<String> recievedMessages;

    public GossipDeduplicator(String name, String type, int m) {
        super(name, type, m);
        ArrayList<String> recievedMessages = new ArrayList<String>();
    }

    @Override
    public void sendMessage(String message, ArrayList<Gossip> chain, Gossip parent) {
        if (!this.isOk || this.recievedMessages.contains(message)) {
            return;
        }
        this.recievedMessages.add(message);

        chain.add(this);
        cntMessages = printMessageAndCheckCount(cntMessages, message, parent);

        for (var follower: followers) {
            if (!chain.contains(follower)) {
                follower.sendMessage(message, new ArrayList<Gossip>(chain), this);
            }
        }
    }
}
