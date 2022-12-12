package com.gossips;

import java.util.ArrayList;

/**
 * Class of gossips with type Simple
 * Extends abstract class Gossip and must override its method sendMessage due to Homework description:
 * Instance should print and send received message without changes
 * @author Vlad Smirnov
 */
public final class GossipSimple extends Gossip {
    /**
     * {cntMessages}: The number of received messages
     */
    private int cntMessages = 0;

    /**
     * Constructor, which calls Gossip class constructor with taken params
     * @param name  gossips' name
     * @param type  gossips' type
     * @param m     the maximum possible amount of given messages for this gossip
     */
    public GossipSimple(String name, String type, int m) {
        super(name, type, m);
    }

    /**
     * This method strictly implements description in base class
     * @param message   message, which we should process
     * @param chain     array of gossips, who have already got this message
     * @param parent    gossip, from whom current instance got this message (null, if it doesn't exist)
     */
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
