import java.util.EnumSet;
import java.util.HashMap;

public enum Combination {

    ROYAL_FLUSH(11),
    STRAIGHT_FLUSH(10),
    SKIP_STRAIGHT_FLUSH_ACE_LOW_TMP(9),
    FOUR_OF_A_KIND(8),
    FULL_HOUSE(7),
    FLUSH(6),
    STRAIGHT(5),
    SKIP_STRAIGHT_ACE_LOW_TMP(4),
    THREE_OF_A_KIND(3),
    TWO_PAIR(2),
    PAIR(1),
    HIGH_CARD(0);

    private final static HashMap<Integer, Combination> fromRank = new HashMap<>();

    static {
        for (Combination combination : EnumSet.allOf(Combination.class)) {
            fromRank.put(combination.rank(), combination);
        }
    }

    private final int rank;

    Combination(int rank) {
        this.rank = rank;
    }

    public static Combination fromRank(int rank) {
        return fromRank.get(rank);
    }

    public int rank() {
        return rank;
    }
}