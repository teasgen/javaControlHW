package com.gossips.smirnov.vladislav;

import java.util.ArrayList;

/**
 * Class of gossips with type Censor
 * Extends abstract class Gossip and must override its method sendMessage due to Homework description:
 * Instance should print and send received message only if it contains 'Java' as a suabstring without case sensivity
 * @author Vlad Smirnov
 */
public final class GossipCensor extends Gossip {
    /**
     * {cntMessages}: The number of received messages
     */
    private int cntMessages = 0;

    /**
     * Constructor, which calls Gossip class constructor with taken params
     * @param name  gossip's name
     * @param type  gossip's type
     * @param m     the maximum possible amount of given messages for this gossip
     */
    public GossipCensor(String name, String type, int m) {
        super(name, type, m);
    }
    
    /**
     * This method implements description in base class
     * Program prints received message without changes
     * If message contains 'java' (without case sensivity), program send it to followers 
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

        if (message.toLowerCase().contains("java"))
            return;

        for (var follower: followers) {
            if (!chain.contains(follower)) {
                follower.sendMessage(message, new ArrayList<Gossip>(chain), this);
            }
        }
    }
}
