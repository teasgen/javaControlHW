package com.gossips.smirnov.vladislav;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class of gossips with type Spammer
 * Extends abstract class Gossip and must override its method sendMessage due to Homework description:
 * Instance should print and send received message without changes n times, where
 * n - random int in [3, 5]
 * @author Vlad Smirnov
 */  
public final class GossipSpammer extends Gossip {
    /**
     * {cntMessages}:   The number of received messages
     * {rand}: 		Random generator
     */
    private int cntMessages = 0;
    public static final Random rand = new Random();
    /**
     * Constructor, which calls Gossip class constructor with taken params
     * @param name  gossip's name
     * @param type  gossip's type
     * @param m     the maximum possible amount of given messages for this gossip
     */
    public GossipSpammer(String name, String type, int m) {
        super(name, type, m);
    }
    /**
     * This method implements description in base class, but sends message to followers n times, where
     * n - random int in [3, 5]
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
