import java.util.*;

public class Deck {
    public static final int CARDS_NUMBER = 52;
    private static final String[] SUITS = {"Пики", "Крести", "Черви", "Буби"};
    private static final String[] RANKS = {"Двойка", "Тройка", "Четверка", "Пятерка", "Шестерка",
            "Семерка", "Восьмерка", "Девятка", "Десятка", "Валет", "Дама", "Король", "Туз"};
    private final List<String> deck;

    public Deck() {
        deck = new ArrayList<>();
        createDeck();
    }

    public List<String> getDeck() {
        return deck;
    }

    public void removeTwoCards() {
        this.deck.remove(1);
        this.deck.remove(0);
    }

    public void shuffle() {
        String temp;

        //FisherYates algorithm
        for (int i = CARDS_NUMBER - 1; i > 0; i--) {
            int j = (int) (Math.random() * i + 1);
            temp = deck.get(j);
            deck.set(j, deck.get(i));
            deck.set(i, temp);
        }
    }

    private void createDeck() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                deck.add(RANKS[j] + " " + SUITS[i]);
            }
        }
    }
}