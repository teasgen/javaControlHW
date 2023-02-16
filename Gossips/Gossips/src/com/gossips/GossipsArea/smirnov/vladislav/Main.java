package com.gossips.GossipsArea.smirnov.vladislav;

import java.util.*;

/**
 * Class for convenient storage of a pair {gossip's name, order number}
 * @author Vlad Smirnov
 */
class PairNameInx {
    /**
     * The private name of a gossip
     */
    String name;
    /**
     * The private order of a gossip
     */
    int index;

    /**
     * Pair constructor, gets {gossip's name, order number} and copy this values accordingly
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
     *  True if
     *      <pre>1. indexes are correct (gossips exists)</pre>
     *      <pre>2. gossips are not equal to each other</pre>
     *  <p>False otherwise</p>
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

/**
 * Main class for program execution
 */
public class Main {
    /**
     * The public static array with all possible types of gossips
     */
    public static final ArrayList<String> allTypes = new ArrayList<>(
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
     * @param name gossip's name
     * @param type gossip's type
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
     * @param name      gossip's name
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
     * @param name gossip's name
     */
    public static void unknownName(String name) {
        System.out.println("Error: unknown gossip " + name);
    }

    /**
     * Checks whether scanner can read next
     * @param sc Scanner instance
     * @return In positive case {true}, otherwise {false} and prints Error
     */
    public static boolean checkNextAndThrowBadCommand(Scanner sc) {
        if (sc.hasNext())
            return true;
        System.out.println("Error: bad command structure, please check spelling or write help");
        return false;
    }
    /**
     * This public static method reads two names, check if they exist
     * <p>In positive case returns structure, contains scanned names and corresponding indexes</p>
     * @param sc        console scanner
     * @param gossips   current array of gossips
     * @return          PairID object, contains names and their indexes
     */
    public static PairID readLink(Scanner sc, ArrayList<Gossip> gossips) {
        String name1, name2;
        if (!checkNextAndThrowBadCommand(sc)) return null;
        name1 = sc.next();
        if (!checkNextAndThrowBadCommand(sc)) return null;
        name2 = sc.next();
        int inxName1 = getIndexOfGossipByName(name1, gossips);
        int inxName2 = getIndexOfGossipByName(name2, gossips);
        if (inxName1 == -1)
            unknownName(name1);
        else if (inxName2 == -1)
            unknownName(name2);
        else if (inxName1 == inxName2)
            System.out.println("Error: gossip can't subscribe to herself");
        return new PairID(new PairNameInx(name1, inxName1), new PairNameInx(name2, inxName2));
    }

    /**
     * Main method, which manages the whole program
     * <p>It provides the communication between user and program</p>
     * <p>Scans new action and works according to Homework "Сплетницы"</p>
     * <p>Informs user, if something went wrong</p>
     * @param args console arguments
     */
    public static void main(String[] args) {
        int m;
        try {
            m = Integer.parseInt(args[0]);
            if (m < 0)
                throw new Exception();
        } catch (Exception e) {
            System.out.println("Bad console argument, expected int");
            return;
        }
        Scanner sc = new Scanner(System.in);
        ArrayList<Gossip> gossips = new ArrayList<>();
        String actionType, name, type, message;
        System.out.println("""
                Welcome to gossips area!!!
                Good luck!
                Or write help, to get manual""");
        System.out.print("\n> ");
        while (checkNextAndThrowBadCommand(sc)) {
            boolean isMessageType = false;
            actionType = sc.next();
            switch (actionType) {
                case "create" -> {
                    if (gossips.size() == 100) {
                        System.out.println("Can't create new gossip");
                        break;
                    }
                    if (!checkNextAndThrowBadCommand(sc)) break;
                    name = sc.next();
                    if (!checkNextAndThrowBadCommand(sc)) break;
                    type = sc.next();
                    if (!allTypes.contains(type)) {
                        System.out.println("Error: unknown type " + type);
                        break;
                    }
                    boolean isNameAlreadyExists = false;
                    for (var gossip : gossips)
                        isNameAlreadyExists |= (gossip.getName().equals(name));
                    if (isNameAlreadyExists) {
                        System.out.println("Error: this name: '" + name + "' is already used");
                        break;
                    }
                    gossips.add(createGossip(name, type, m));
                    System.out.println("Ok, " + name + " gossip created");
                }
                case "link" -> {
                    var nowLink = readLink(sc, gossips);
                    if (nowLink != null && nowLink.correct()) {
                        if (gossips.get(nowLink.first.index).addFollower(gossips.get(nowLink.second.index))) {
                            System.out.println("Ok, " + nowLink.second.name + " followed " + nowLink.first.name);
                        }
                    }
                }
                case "unlink" -> {
                    var nowLink = readLink(sc, gossips);
                    if (nowLink != null && nowLink.correct()) {
                        if (gossips.get(nowLink.first.index).removeFollower(gossips.get(nowLink.second.index))) {
                            System.out.println("Ok, " + nowLink.second.name + " unfollowed " + nowLink.first.name);
                        }
                    }
                }
                case "gossips" -> {
                    gossips.sort(new GossipComparator());
                    for (int i = 0; i < gossips.size(); i++) {
                        System.out.println("Gossip " + (i + 1) + ": " + gossips.get(i).getName());
                    }
                }
                case "listeners" -> {
                    if (!checkNextAndThrowBadCommand(sc)) break;
                    name = sc.next();
                    int inx = getIndexOfGossipByName(name, gossips);
                    if (inx == -1) {
                        unknownName(name);
                    }
                    else {
                        gossips.get(inx).printListeners();
                    }
                }
                case "message" -> {
                    isMessageType = true;
                    if (!checkNextAndThrowBadCommand(sc)) break;
                    name = sc.next();
                    if (!checkNextAndThrowBadCommand(sc)) break;
                    message = sc.nextLine().replaceFirst("^\\s*", "");
                    int inx = getIndexOfGossipByName(name, gossips);
                    if (inx == -1) {
                        unknownName(name);
                    } else {
                        gossips.get(inx).sendMessage(message, new ArrayList<>(), null);
                    }
                }
                case "about" -> System.out.println(
                        """
                                Автор: Смирнов Владислав, БПИ211
                                Если Вы нашли ошибку или есть вопросы, то напишите мне:
                                    Телеграм: @teasgen
                                    Почта: vmsmirnov@edu.hse.ru""");
                case "help" -> System.out.println("""
                        create <name> <type> - создает сплетницу с именем name и типом type
                            Возможные типы:
                                censor:
                                    Сплетница-цензор печатает все сообщения, но передает своим подписчикам
                                    только те сообщения, где есть подстрока «Java», независимо от регистра
                                deduplicator:
                                    Сплетница-deduplicator печатает и передает сообщения, которые не встречались
                                    ей ранее. Уникальность сообщения определяется по его тексту
                                null:
                                    Сплетница-null печатает полученные ею сообщения, но не передаёт их дальше
                                spammer:
                                    Сплетница-спамер печатает полученное сообщение один раз, а передает
                                    сообщение своим подписчикам от двух до пяти раз. Количество раз генерируется
                                    случайно с помощью экземпляра класса java.util.Random
                                simple:
                                    Сплетница-simple: печатает и передает сообщение слушателям без изменений.
                                    
                        2. link <name1> <name2> - подписывает сплетницу name2 на сплетницу name1
                        
                        3. unlink <name1> <name2> - отписывает сплетницу name2 от сплетницы name1
                        
                        4. message <name> <message>" - отправляется сообщение message сплетнице name
                        
                        5. gossips - печатает всех сплетниц в алфавитном порядке
                        
                        6. listeners <name> - печатает имена всех слушателей сплетницы name в алфавитном порядке
                        
                        7. about - об авторе
                        
                        8. quit - выйти из приложения""");
                case "quit" -> {
                    System.out.println("See you soon later!");
                    return;
                }
                default -> System.out.println("It seems, I don't know command: '" + actionType + "', please check spelling or write help");
            }
            if (!isMessageType)
                sc.nextLine();
            System.out.print("\n> ");
        }
    }
}
