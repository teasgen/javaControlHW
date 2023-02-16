package com.gossips.GossipsArea.smirnov.vladislav;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class of gossips with type Deduplicator
 * <p>Extends abstract class Gossip and must override its method sendMessage due to Homework description:</p>
 * <p>Instance should print and send message without changes if only it has never ever received</p>
 * <p>message with the similar text</p>
 * @author Vlad Smirnov
 */
public final class GossipDeduplicator extends Gossip {
    /**
     * The number of received messages;
     */
    private int cntMessages = 0;
    /**
     * HashSet with all messages, which instance has already got
     */
    private final HashSet<String> receivedMessages;

    /**
     * Constructor, which calls Gossip class constructor with taken params
     * Also it sets {receivedMessages} as an empty ArrayList
     * @param name  gossip's name
     * @param type  gossip's type
     * @param m     the maximum possible amount of given messages for this gossip
     */
    public GossipDeduplicator(String name, String type, int m) {
        super(name, type, m);
        receivedMessages = new HashSet<>();
    }

    /**
     * This method implements description in base class, but sends and prints message if only it has never ever received
     * message with the similar text
     * If message satisfies this condition, program will add it in receivedMessages
     * @param message   message, which we should process
     * @param chain     array of gossips, who have already got this message
     * @param parent    gossip, from whom current instance got this message (null, if it doesn't exist)
     */
    @Override
    public void sendMessage(String message, ArrayList<Gossip> chain, Gossip parent) {
        if (!this.isOk)
            return;
        cntMessages = printMessageAndCheckCount(cntMessages, message, parent);
        if (receivedMessages.contains(message))
            return;
        receivedMessages.add(message);

        chain.add(this);

        for (var follower: followers) {
            if (!chain.contains(follower)) {
                follower.sendMessage(message, new ArrayList<>(chain), this);
            }
        }
    }
}
