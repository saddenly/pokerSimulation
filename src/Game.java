public class Game {
    private static final int[] recurrence = {
            0, 1, 5, 24, 106, 472, 2058, 8768, 29048, 70347, 233028, 583164, 1472911
    };
    private final static int[] hands = {Combination.FOUR_OF_A_KIND.rank(), Combination.STRAIGHT_FLUSH.rank(), Combination.STRAIGHT.rank(),
            Combination.FLUSH.rank(), Combination.HIGH_CARD.rank(), Combination.PAIR.rank(), Combination.TWO_PAIR.rank(),
            Combination.ROYAL_FLUSH.rank(), Combination.THREE_OF_A_KIND.rank(), Combination.FULL_HOUSE.rank()};
    private final static long MASK4_1 = 0b11110000_11110000_11110000_11110000_11110000_11110000_11110000_11110000L;
    private final static long MASK4_2 = 0b00001111_00001111_00001111_00001111_00001111_00001111_00001111_00001111L;
    private final Player[] players;
    private final int[] straight = new int[4731770];
    private final int[] flush = new int[32768];
    private final int[] straightFlush = new int[32768];
    private final int[] lookup = new int[4731770];
    private String[] communityCards;

    public Game(Player[] players) throws InvalidNumberOfPlayersException {
        setPlayersNumber(players.length);
        this.players = players;
    }

    private static int getUpperFive(int cards) {
        cards = Integer.bitCount(cards) > 5 ? cards & -(1 << Integer.numberOfTrailingZeros(cards) + 1) : cards;
        cards = Integer.bitCount(cards) > 5 ? cards & -(1 << Integer.numberOfTrailingZeros(cards) + 1) : cards;
        return cards;
    }

    public static int[][] get5HandsFrom7(int[] nrs) {
        int[][] result = new int[21][];
        int index = 0;
        for (int x1 = 0; x1 < 7; x1++) {
            for (int x2 = x1 + 1; x2 < 7; x2++) {
                for (int x3 = x2 + 1; x3 < 7; x3++) {
                    for (int x4 = x3 + 1; x4 < 7; x4++) {
                        for (int x5 = x4 + 1; x5 < 7; x5++) {
                            result[index] = new int[]{nrs[x1], nrs[x2], nrs[x3], nrs[x4], nrs[x5]};
                            index++;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static boolean hasStraight(int set) {
        return 0 != (set & (set >> 1) & (set >> 2) & (set >> 3) & (set >> 4));
    }

    private static long minPos(long a, long b) {
        return a == 0 ? b : b == 0 ? a : Math.min(a, b);
    }

    private static long max(long a, long b) {
        return Math.max(a, b);
    }

    public void dealCards(Deck deck) {
        for (Player player : players) {
            player.addCards(deck.getDeck().get(0), deck.getDeck().get(1));
            deck.removeTwoCards();
        }
    }

    public void reveal() {
        for (int i = 0; i < players.length; i++) {
            System.out.println("Игрок №" + (i + 1) + ":" + players[i].showCards());
        }
    }

    public void setPlayersNumber(int playersNumber) throws InvalidNumberOfPlayersException {
        if (playersNumber < 2 || playersNumber > 10) {
            throw new InvalidNumberOfPlayersException("Invalid number of players. Number should be between 2 and 10");
        }
    }

    private int getStraight(int set) {
        return set & (set << 1) & (set << 2) & (set << 3) & (set << 4);
    }

    public void evaluatePlayers() {
        int greatestValue = 0;
        int playerNumber = 0;
        int[] buffer = new int[4];
        for (int i = 0; i < players.length; i++) {
            int value = rankPokerHand7(players[i], buffer);
            if (value > greatestValue) {
                greatestValue = value;
                playerNumber = i;
            }
        }
        System.out.println("Выйграл игрок " + players[playerNumber].getName());
    }

    public String[] getCommunityCards() {
        return communityCards;
    }

    public void setCommunityCards(Deck deck) {
        communityCards = new String[5];
        for (int i = 0; i < 5; i++) {
            communityCards[i] = deck.getDeck().get(0);
            deck.getDeck().remove(0);
        }
    }

    private int[][] convertCards(String[] cards) {
        int[] ranks = new int[7];
        int[] suits = new int[7];
        for (int i = 0; i < 7; i++) {
            String[] segments = cards[i].split(" ");
            switch (segments[0]) {
                case "Двойка" -> ranks[i] = 0;
                case "Тройка" -> ranks[i] = 1;
                case "Четверка" -> ranks[i] = 2;
                case "Пятерка" -> ranks[i] = 3;
                case "Шестерка" -> ranks[i] = 4;
                case "Семерка" -> ranks[i] = 5;
                case "Восьмерка" -> ranks[i] = 6;
                case "Девятка" -> ranks[i] = 7;
                case "Десятка" -> ranks[i] = 8;
                case "Валет" -> ranks[i] = 9;
                case "Дама" -> ranks[i] = 10;
                case "Король" -> ranks[i] = 11;
                case "Туз" -> ranks[i] = 12;
            }
            switch (segments[1]) {
                case "Пики" -> suits[i] = 0;
                case "Крести" -> suits[i] = 1;
                case "Черви" -> suits[i] = 2;
                case "Буби" -> suits[i] = 3;
            }
        }
        return new int[][]{ranks, suits};
    }

    public int rankPokerHand7(Player player, int[] buffer) {
        String[] handCards = new String[7];

        System.arraycopy(communityCards, 0, handCards, 0, communityCards.length);
        System.arraycopy(player.getCards(), 0, handCards, communityCards.length, player.getCards().length);

        int[] nr = convertCards(handCards)[0];
        int[] suit = convertCards(handCards)[1];
        int index = 0;

        for (int i = 0; i < 4; i++) {
            buffer[i] = 0;
        }

        for (int i = 0; i < 7; i++) {
            buffer[suit[i]] |= 1L << nr[i];
            index += recurrence[nr[i]];
        }

        index = index % 4731770;
        int value = lookup[index];
        int fl = 0;

        for (int i = 0; i < 4; i++) {
            fl |= flush[buffer[i]];
        }
        int str = straight[index];

        int straightFl = fl == 0 ? 0 :
                (straightFlush[str & buffer[0]] | straightFlush[str & buffer[1]] | straightFlush[str & buffer[2]] | straightFlush[str & buffer[3]]);

        straightFl = Integer.highestOneBit(straightFl);

        return straightFl == 1 << 12 ? (Combination.ROYAL_FLUSH.rank() << 26)
                : straightFl != 0 ? (Combination.STRAIGHT_FLUSH.rank() << 26) | straightFl
                : fl != 0 ? (Combination.FLUSH.rank() << 26) | fl
                : value;
    }

    public int rankPokerHand5(int[] nr, int[] suit) {
        long v = 0L;
        int set = 0;
        for (int i = 0; i < 5; i++) {
            v += (v & (15L << (nr[i] * 4))) + (1L << (nr[i] * 4));
            set |= 1 << (nr[i] - 2);
        }
        int value = (int) (v % 15L - ((hasStraight(set)) || (set == 0x403c / 4) ? 3L : 1L)); // keep the v value at this point
        value -= (suit[0] == (suit[1] | suit[2] | suit[3] | suit[4]) ? 1 : 0) * ((set == 0x7c00 / 4) ? -5 : 1);
        value = hands[value];

        // break ties
        value = value << 26;
        value |= value == Combination.FULL_HOUSE.rank() << 26 ? 64 - Long.numberOfLeadingZeros(v & (v << 1) & (v << 2)) << 20
                : set == 0x403c / 4 ? 0 // Ace low straights
                : ((64 - Long.numberOfLeadingZeros(
                max((v & MASK4_1) & ((v & MASK4_1) << 1), (v & MASK4_2) & ((v & MASK4_2) << 1))) << 20) |
                (Long.numberOfTrailingZeros(
                        minPos((v & MASK4_1) & ((v & MASK4_1) << 1), (v & MASK4_2) & ((v & MASK4_2) << 1))) << 14));
        value |= set;
        return value;
    }

    public void initRankPokerHand7() {
        for (int i = 0; i < 32768; i++) {
            straightFlush[i] = getStraight(i);
            if ((i & (0x403c / 4)) == 0x403c / 4) {
                straightFlush[i] |= 1 << 3;
            }
            flush[i] = Integer.bitCount(i) >= 5 ? getUpperFive(i) : 0;
        }
        int max = 0;
        int count = 0;
        int[] nrs = {1, 2, 4, 8, 1};
        for (int nr1 = 2; nr1 <= 14; nr1++) {
            for (int nr2 = nr1; nr2 <= 14; nr2++) {
                for (int nr3 = nr2; nr3 <= 14; nr3++) {
                    for (int nr4 = nr3; nr4 <= 14; nr4++) {
                        for (int nr5 = nr4; nr5 <= 14; nr5++) {
                            if (nr1 == nr5) {
                                continue;
                            }
                            for (int nr6 = nr5; nr6 <= 14; nr6++) {
                                if (nr2 == nr6) {
                                    continue;
                                }
                                for (int nr7 = nr6; nr7 <= 14; nr7++) {
                                    if (nr3 == nr7) {
                                        continue;
                                    }
                                    int[][] hands = get5HandsFrom7(new int[]{nr1, nr2, nr3, nr4, nr5, nr6, nr7});
                                    int maxValue = 0;
                                    int straight = 0;
                                    for (int[] hand : hands) {
                                        int value = rankPokerHand5(hand, nrs);
                                        int set = value & ((1 << 14) - 1);
                                        straight |= (hasStraight(set) || set == 0x403c / 4) ? set : 0;
                                        maxValue = Math.max(value, maxValue);
                                    }
                                    int sum = recurrence[nr1 - 2] + recurrence[nr2 - 2] + recurrence[nr3 - 2] + recurrence[nr4 - 2] + recurrence[nr5 - 2] + recurrence[nr6 - 2] + recurrence[nr7 - 2];
                                    count++;
                                    lookup[sum % 4731770] = maxValue;
                                    if (sum % 4731770 > max) {
                                        max = sum % 4731770; //7561824
                                    }
                                    this.straight[sum % 4731770] = straight;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}