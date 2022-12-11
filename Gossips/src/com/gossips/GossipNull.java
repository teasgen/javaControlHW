package com.gossips;

import java.util.ArrayList;

public final class GossipNull extends Gossip {
    private int cntMessages = 0;

    public GossipNull(String name, String type, int m) {
        super(name, type, m);
    }

    @Override
    public void sendMessage(String message, ArrayList<Gossip> chain, Gossip parent) {
        if (!this.isOk) {
            return;
        }
        cntMessages = printMessageAndCheckCount(cntMessages, message, parent);
    }
}
