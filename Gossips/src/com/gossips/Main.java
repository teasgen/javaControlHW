package com.gossips;

import java.util.*;

/**
 * Class for convenient storage of a pair {gossips' name, order number}
 * @author Vlad Smirnov
 */
class PairNameInx {
    /**
     * The private name of a gossip and her order number
     */
    String name;
    int index;

    /**
     * Pair constructor, gets {gossips' name, order number} and copy this values accordingly
     * @param name name
     * @param x index
     */
    PairNameInx(String name, int x) {
        this.name = name;
        this.index = x;
    }
}

/**
 * Class for storing the names of two different gossips and their indexes
 * @author Vlad Smirnov
 */
class PairID {
    /**
     * Two pairs for two gossips
     */
    PairNameInx first, second;

    /**
     * @param a first gossip
     * @param b second gossip
     */
    PairID(PairNameInx a, PairNameInx b) {
        this.first = a;
        this.second = b;
    }

    /**
     * Check if current pair is correct
     * @return
     *  {true} if
     *      indexes are correct (gossips exists)
     *      gossips are not equal to each other
     *  {false} otherwise
     */
    public boolean correct() {
        return this.first.index != -1 && this.second.index != -1 && this.first.index != this.second.index;
    }
}

/**
 * Comparator for lexicographical sorting of gossips
 * @author Vlad Smirnov
 */
class GossipComparator implements Comparator<Gossip> {
    @Override
    public int compare(Gossip o1, Gossip o2) {
        return o1.getName().compareTo(o2.getName());
    }
}

public class Main {
    /**
     * The public static array with all possible types of gossips
     */
    public static final ArrayList<String> allTypes = new ArrayList<String>(
            Arrays.asList(
                    "null",
                    "censor",
                    "spammer",
                    "simple",
                    "deduplicator"
            )
    );

    /**
     * Public static method, which creates Gossip with given name and type
     * @param name gossips' name
     * @param type gossips' type
     * @param m    the maximum possible amount of given messages for this gossip
     * @return     new Gossip according to {type}
     */
    public static Gossip createGossip(String name, String type, int m) {
        return switch (type) {
            case "simple" -> new GossipSimple(name, type, m);
            case "censor" -> new GossipCensor(name, type, m);
            case "null" -> new GossipNull(name, type, m);
            case "spammer" -> new GossipSpammer(name, type, m);
            default -> new GossipDeduplicator(name, type, m);
        };
    }

    /**
     * Public static method for getting index by given name
     * @param name      gossips' name
     * @param gossips   current array of gossips
     * @return          the order number of gossip with name {name}
     */
    public static int getIndexOfGossipByName(String name, ArrayList<Gossip> gossips) {
        for (int i = 0; i < gossips.size(); ++i) {
            if (gossips.get(i).getName().equals(name))
                return i;
        }
        return -1;
    }

    /**
     * Prints error: unknown name
     * @param name gossips' name
     */
    public static void unknownName(String name) {
        System.out.println("Error: unknown gossip " + name);
    }

    /**
     * This public static method reads two names, check if they exist
     * In positive case returns structure, contains scanned names and corresponding indexes
     * @param sc        console scanner
     * @param gossips   current array of gossips
     * @return          PairID object, contains names and their indexes
     */
    public static PairID readLink(Scanner sc, ArrayList<Gossip> gossips) {
        String name1, name2;
        name1 = sc.next();
        name2 = sc.next();
        int inxName1 = getIndexOfGossipByName(name1, gossips);
        int inxName2 = getIndexOfGossipByName(name2, gossips);
        if (inxName1 == -1) {
            unknownName(name1);
        }
        if (inxName2 == -1) {
            unknownName(name2);
        }
        if (inxName1 == inxName2) {
            System.out.println("Error: gossip can't subscribe to herself");
        }
        return new PairID(new PairNameInx(name1, inxName1), new PairNameInx(name2, inxName2));
    }

    /**
     * Main method, which manages the whole program
     * <p>
     * It provides the communication between user and program
     * Scans new action and works according to Homework "Сплетницы"
     * <p>
     * Informs user, if something went wrong
     * @param args console arguments
     */
    public static void main(String[] args) {
        int m = Integer.parseInt(args[0]);
        Scanner sc = new Scanner(System.in);
        ArrayList<Gossip> gossips = new ArrayList<>();
        String actionType, name, type, message;
        System.out.println("""
                Welcome to gossips area!!!
                Good luck!
                Or write help, to get manual""");
        for (;;) {
            System.out.print("\n> ");
            actionType = sc.next();
            switch (actionType) {
                case "create" -> {
                    if (gossips.size() == 100) {
                        System.out.println("Can't create new gossip");
                        continue;
                    }
                    name = sc.next();
                    type = sc.next();
                    if (!allTypes.contains(type)) {
                        System.out.println("Error: unknown type " + type);
                        continue;
                    }
                    boolean isNameAlreadyExists = false;
                    for (var gossip : gossips)
                        isNameAlreadyExists |= (gossip.getName().equals(name));
                    if (isNameAlreadyExists) {
                        System.out.println("Error: this name: '" + name + "' is already used");
                        continue;
                    }
                    gossips.add(createGossip(name, type, m));
                    System.out.println("Ok, " + name + " gossip created");
                }
                case "link" -> {
                    var nowLink = readLink(sc, gossips);
                    if (nowLink.correct()) {
                        if (gossips.get(nowLink.first.index).addFollower(gossips.get(nowLink.second.index)))
                            System.out.println("Ok, " + nowLink.second.name + " followed " + nowLink.first.name);
                    }
                }
                case "unlink" -> {
                    var nowLink = readLink(sc, gossips);
                    if (nowLink.correct()) {
                        if (gossips.get(nowLink.first.index).removeFollower(gossips.get(nowLink.second.index)))
                            System.out.println("Ok, " + nowLink.second.name + " unfollowed " + nowLink.first.name);
                    }
                }
                case "gossips" -> {
                    gossips.sort(new GossipComparator());
                    for (int i = 0; i < gossips.size(); i++) {
                        System.out.println("Gossip " + (i + 1) + ": " + gossips.get(i).getName());
                    }
                }
                case "listeners" -> {
                    name = sc.next();
                    int inx = getIndexOfGossipByName(name, gossips);
                    if (inx == -1)
                        unknownName(name);
                    else {
                        gossips.get(inx).printListeners();
                    }
                }
                case "message" -> {
                    name = sc.next();
                    message = sc.next();
                    gossips.get(getIndexOfGossipByName(name, gossips)).sendMessage(message, new ArrayList<>(), null);
                }
                case "about" -> {
                    System.out.println(
                            """
                                    Автор: Смирнов Владислав, БПИ211
                                    Если Вы нашли ошибку или есть вопросы, то напишите мне:
                                        Телеграм: @teasgen
                                        Почта: vmsmirnov@edu.hse.ru""");
                }
                case "help" -> {
                    System.out.println("""
                            1. create <name> <type> - создает сплетницу с именем name и типом type
                                Возможные типы:
                                    null,
                                    censor,
                                    spammer,
                                    simple,
                                    deduplicator
                            2. link <name1> <name2> - подписывает сплетницу name2 на сплетницу name1
                            3. unlink <name1> <name2> - отписывает сплетницу name2 от сплетницы name1
                            4. message <name> <message>" - отправляется сообщение message сплетнице name
                            5. gossips - печатает всех сплетниц в алфавитном порядке
                            6. listeners <name> - печатает имена всех слушателей сплетницы name в алфавитном порядке
                            7. about - об авторе
                            8. quit - выйти из приложения""");
                }
                case "quit" -> {
                    System.out.println("See you soon later!");
                    return;
                }
                default -> {
                    System.out.println("It seems, I don't know command: '" + actionType + "', please check spelling or write help");
                    sc.nextLine();
                }
            }
        }
    }
}