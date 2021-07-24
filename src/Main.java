import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Deck deck = new Deck();
        deck.shuffle();
        Player[] players = new Player[]{
                new Player("Trevor"),
                new Player("Franklin"),
                new Player("Michael")
        };
        Game game = new Game(players);
        game.dealCards(deck);
        game.setCommunityCards(deck);
        System.out.println("Карты на столе:" + Arrays.toString(game.getCommunityCards()));
        game.reveal();
        game.initRankPokerHand7();
        game.evaluatePlayers();
    }
}