package com.gossips;

import java.util.ArrayList;

public final class GossipSimple extends Gossip {
    private int cntMessages = 0;

    public GossipSimple(String name, String type, int m) {
        super(name, type, m);
    }

    @Override
    public void sendMessage(String message, ArrayList<Gossip> chain, Gossip parent) {
        if (!this.isOk) {
            return;
        }

        chain.add(this);
        cntMessages = printMessageAndCheckCount(cntMessages, message, parent);

        for (var follower: followers) {
            if (!chain.contains(follower)) {
                follower.sendMessage(message, new ArrayList<Gossip>(chain), this);
            }
        }
    }
}
