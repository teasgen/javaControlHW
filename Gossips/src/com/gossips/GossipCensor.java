package com.gossips;

import java.util.ArrayList;

public final class GossipCensor extends Gossip{
    private int cntMessages = 0;

    public GossipCensor(String name, String type, int m) {
        super(name, type, m);
    }

    @Override
    public void sendMessage(String message, ArrayList<Gossip> chain, Gossip parent) {
        if (!this.isOk) {
            return;
        }

        chain.add(this);
        cntMessages = printMessageAndCheckCount(cntMessages, message, parent);

        if (message.toLowerCase().contains("java"))
            return;

        for (var follower: followers) {
            if (!chain.contains(follower)) {
                follower.sendMessage(message, new ArrayList<Gossip>(chain), this);
            }
        }
    }
}
