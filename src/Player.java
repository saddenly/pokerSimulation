import java.util.Arrays;

public class Player {
    public static final int CARDS_IN_HAND = 2;
    private String[] cards;
    private String name;

    public Player(String name) {
        cards = new String[CARDS_IN_HAND];
        this.name = name;
    }

    public String showCards() {
        return Arrays.toString(cards);
    }

    public void addCards(String card1, String card2) {
        cards[0] = card1;
        cards[1] = card2;
    }

    public String[] getCards() {
        return cards;
    }

    public void setCards(String[] cards) {
        this.cards = cards;
    }

    public String getName() {
        return name;
    }
}