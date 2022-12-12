package com.gossips;
import java.util.ArrayList;

/**
 * Abstract class that describes common features between all gossip types
 * @author Vlad Smirnov
 */
public abstract class Gossip {
    /**
     * {isOk}:          Bool variable that shows whether gossip instance is currently available for communication
     * {followers}:     Array of all listeners
     * {name, type}:    Gossips' name & type
     * {m}:             The maximum possible amount of given messages for this gossip
     */
    protected String name, type;
    protected boolean isOk;
    protected ArrayList<Gossip> followers;
    protected int m;
    /**
     * Constructor, which initialize fields due to taken params accordingly
     * @param name  gossips' name
     * @param type  gossips' type
     * @param m     the maximum possible amount of given messages for this gossip
     */
    public Gossip(String name, String type, int m) {
        this.name = name;
        this.type = type;
        this.isOk = true;
        this.m = m;
        followers = new ArrayList<Gossip>();
    }

    /**
     * Returns current name of the gossip
     * @return String name
     */
    public String getName() { return this.name; }

    /**
     * This method adds new listener to the current follower list
     * @param newFollower  new Gossip listener, whom user want to add
     * @return {true}, if newFollower hasn't been followed to current instance yet,
     *         {false} otherwise, also program informs user about it
     */
    public boolean addFollower(Gossip newFollower) {
        if (followers.contains(newFollower)) {
            System.out.println("Warning: " + newFollower.getName() + " is already followed " + this.getName());
            return false;
        }
        followers.add(newFollower);
        return true;
    }

    /**
     * This method adds removes listener from the current follower list
     * @param oldFollower Gossip listener, whom user want to remove
     * @return  {true}, if oldFollower has already been followed to current instance,
     *          {false} otherwise, also program informs user about it
     */
    public boolean removeFollower(Gossip oldFollower) {
        if (!followers.contains(oldFollower)) {
            System.out.println("Error: " + oldFollower.getName() + " is not followed " + this.getName());
            return false;
        }
        followers.remove(oldFollower);
        return true;
    }

    /**
     * Prints all listeners of the current instance
     */
    public void printListeners() {
        followers.sort(new GossipComparator());
        for (int i = 0; i < followers.size(); i++) {
            System.out.println("Listener " + (i + 1) + ": " + followers.get(i).getName());
        }
    }

    /**
     * This method prints message, which gossip got, increase counter of received messages and returns it
     * If amount of received messages becomes equal to m, {isOk} sets false and programs prints ending message
     * @param cntMessages   counter of received messages
     * @param message       message that we want to print
     * @param parent        from whom instance got the message (if parent exists: reference to Gossip, otherwise null )
     * @return  correct counter of received messages
     */
    public int printMessageAndCheckCount(int cntMessages, String message, Gossip parent) {
        ++cntMessages;
        String par = (parent == null ? "" : ", from: " + parent.getName());
        System.out.println(
                this.getName() +
                        ", message number = " +
                        cntMessages +
                        ", message: \"" +
                        message + "\"" + par);
        if (cntMessages == m) {
            this.isOk = false;
            System.out.println(this.getName() + ": I'm tired. Goodbye!");
        }
        return cntMessages;
    }

    /**
     * Abstract method, which describes what every gossip type should do, if it has got a message:
     *      1. check if instance is available for communication
     *      2. in positive case prints messages and add current instance to chain (to prevent infinite messages exchange)
     *      3. sends message to all followers due to gossips' type
     *<p>
     * @param message   message, which we should process
     * @param chain     array of gossips, who have already got this message
     * @param parent    gossip, from whom current instance got this message (null, if it doesn't exist)
     */
    public abstract void sendMessage(String message, ArrayList<Gossip> chain, Gossip parent);
}
