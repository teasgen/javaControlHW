package com.gossips.GossipsArea.smirnov.vladislav;

import java.util.ArrayList;

/**
 * Class of gossips with type Null
 * <p>Extends abstract class Gossip and must override its method sendMessage due to Homework description:</p>
 * <p>Instance should only print received message without changes</p>
 * @author Vlad Smirnov
 */
public final class GossipNull extends Gossip {
    /**
     * The number of received messages;
     */
    private int cntMessages = 0;

    /**
     * Constructor, which calls Gossip class constructor with taken params
     * @param name  gossip's name
     * @param type  gossip's type
     * @param m     the maximum possible amount of given messages for this gossip
     */
    public GossipNull(String name, String type, int m) {
        super(name, type, m);
    }

    /**
     * If gossip is available for communication prints received message
     * @param message   message, which we should process
     * @param chain     array of gossips, who have already got this message
     * @param parent    gossip, from whom current instance got this message (null, if it doesn't exist)
     */
    @Override
    public void sendMessage(String message, ArrayList<Gossip> chain, Gossip parent) {
        if (!this.isOk) {
            return;
        }
        cntMessages = printMessageAndCheckCount(cntMessages, message, parent);
    }
}
