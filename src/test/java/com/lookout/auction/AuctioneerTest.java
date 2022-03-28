package com.lookout.auction;

import org.junit.jupiter.api.*;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuctioneerTest {

    Auctioneer auctioneer;
    Auctioneer auctioneer1;
    Auctioneer auctioneer2;
    Auctioneer auctioneer3;
    Auctioneer auctioneer4;
    Auctioneer auctioneer5;
    Auctioneer auctioneer6;
    Auctioneer auctioneer7;
    private static final int ONE_DAY_IN_SECONDS = 86400;

    @BeforeAll
    void setUp() {
        auctioneer = new Auctioneer();
        auctioneer1 = new Auctioneer();
        auctioneer2 = new Auctioneer();
        auctioneer3 = new Auctioneer();
        auctioneer4 = new Auctioneer();
        auctioneer5 = new Auctioneer();
        auctioneer6 = new Auctioneer();
        auctioneer7 = new Auctioneer();
        var item = new Item(1, "Boat");
        var item1 = new Item(2, "Scooter");
        var item2 = new Item(3, "Bicycle");
        var item3 = new Item(4, "Car");
        var item4 = new Item(5, "Motorcycle");
        var item5 = new Item(6, "Caravan");
        var item6 = new Item(7, "MiniVan");
        var item7 = new Item(8, "Yacht");
        auctioneer.createAuction(item);
        auctioneer1.createAuction(item1);
        auctioneer2.createAuction(item2);
        auctioneer3.createAuction(item3);
        auctioneer4.createAuction(item4);
        auctioneer5.createAuction(item5);
        auctioneer6.createAuction(item6);
        auctioneer7.createAuction(item7);
    }

    @Test
    @DisplayName("Determine Winner - Auction still open")
    void shouldDetermineWinnerOpenAuction() {
        Exception exception = assertThrows(RuntimeException.class, () ->
                auctioneer.determineWinner(0, Instant.now().plusSeconds(86399)));
        assertEquals("Auction 0 is still open.", exception.getMessage());
    }

    @Test
    @DisplayName("Determine Winner - Empty Auction")
    void shouldDetermineWinnerEmptyAuction() {
        Exception exception = assertThrows(RuntimeException.class, () ->
                auctioneer.determineWinner(0, Instant.now().plusSeconds(ONE_DAY_IN_SECONDS)));
        assertEquals("No bid provided for the auction 0", exception.getMessage());
    }

    @Test
    @DisplayName("Determine Winner - Single Bidder")
    void shouldDetermineWinnerSingleBidder() {
        var bid1 = new Bid(new Bidder(1, "Alice"), Instant.now(), 250000, 300000, 5000);
        auctioneer.makeBid(bid1, 0);
        var result = auctioneer.determineWinner(0, Instant.now().plusSeconds(ONE_DAY_IN_SECONDS));
        assertEquals(250000, result.getWinningBid());
    }

    @Test
    @DisplayName("Determine Winner - Multiple Bidder - First Auction")
    void shouldDetermineWinnerMultipleBidderFirst() {
        var bid1 = new Bid(new Bidder(1, "Alice"), Instant.now(), 250000, 300000, 5000);
        var bid2 = new Bid(new Bidder(2, "Aaron"), Instant.now(), 280000, 310000, 20100);
        var bid3 = new Bid(new Bidder(3, "Amanda"), Instant.now(), 250100, 320000, 24700);
        auctioneer6.makeBid(bid1, 6);
        auctioneer6.makeBid(bid2, 6);
        auctioneer6.makeBid(bid3, 6);
        var result = auctioneer.determineWinner(6, Instant.now().plusSeconds(ONE_DAY_IN_SECONDS));
        assertEquals(300100, result.getWinningBid());
    }

    @Test
    @DisplayName("Determine Winner - Multiple Bidder - Second Auction")
    void shouldDetermineWinnerMultipleBidderSecond() {
        var bid1 = new Bid(new Bidder(1, "Alice"), Instant.now(), 70000, 72500, 200);
        var bid2 = new Bid(new Bidder(2, "Aaron"), Instant.now(), 59900, 72500, 1500);
        var bid3 = new Bid(new Bidder(3, "Amanda"), Instant.now(), 62500, 72500, 800);
        auctioneer1.makeBid(bid1, 1);
        auctioneer1.makeBid(bid2, 1);
        auctioneer1.makeBid(bid3, 1);
        var result = auctioneer1.determineWinner(1, Instant.now().plusSeconds(ONE_DAY_IN_SECONDS));
        assertEquals(72200, result.getWinningBid());
    }

    @Test
    @DisplayName("Determine Winner - Multiple Bidder - Third Auction")
    void shouldDetermineWinnerMultipleBidderThird() {
        var bid1 = new Bid(new Bidder(1, "Alice"), Instant.now(), 5000, 8000, 300);
        var bid2 = new Bid(new Bidder(2, "Aaron"), Instant.now(), 6000, 8200, 200);
        var bid3 = new Bid(new Bidder(3, "Amanda"), Instant.now(), 5500, 8500, 500);
        auctioneer2.makeBid(bid1, 2);
        auctioneer2.makeBid(bid2, 2);
        auctioneer2.makeBid(bid3, 2);
        var result = auctioneer2.determineWinner(2, Instant.now().plusSeconds(ONE_DAY_IN_SECONDS));
        assertEquals(8500, result.getWinningBid());
    }

    @Test
    @DisplayName("Determine Winner - Multiple Bidder - Tie")
    void shouldDetermineWinnerMultipleBidderTie() {
        var bid1 = new Bid(new Bidder(1, "Alice"), Instant.now(), 5000, 8000, 300);
        var bid2 = new Bid(new Bidder(2, "Aaron"), Instant.now(), 6000, 8000, 200);
        var bid3 = new Bid(new Bidder(3, "Amanda"), Instant.now(), 5500, 8000, 500);
        auctioneer3.makeBid(bid1, 3);
        auctioneer3.makeBid(bid2, 3);
        auctioneer3.makeBid(bid3, 3);
        var result = auctioneer3.determineWinner(3, Instant.now().plusSeconds(ONE_DAY_IN_SECONDS));
        assertEquals(8000, result.getWinningBid());
        assertEquals("Alice", result.getBidder().getName());
    }

    @Test
    @DisplayName("Determine Winner - Multiple Bidder - Possible Min")
    void shouldDetermineWinnerMultipleBidderPossibleMin() {
        var bid1 = new Bid(new Bidder(1, "Alice"), Instant.now(), 5000, 8000, 300);
        var bid2 = new Bid(new Bidder(2, "Aaron"), Instant.now(), 6000, 6400, 200);
        var bid3 = new Bid(new Bidder(3, "Amanda"), Instant.now(), 5500, 6000, 500);
        auctioneer4.makeBid(bid1, 4);
        auctioneer4.makeBid(bid2, 4);
        auctioneer4.makeBid(bid3, 4);
        var result = auctioneer4.determineWinner(4, Instant.now().plusSeconds(ONE_DAY_IN_SECONDS));
        assertEquals(6500, result.getWinningBid());
        assertEquals("Alice", result.getBidder().getName());
    }

    @Test
    @DisplayName("Determine Winner - Multiple Bidder - Possible Min - Equality Crash")
    void shouldDetermineWinnerMultipleBidderPossibleEquality() {
        var bid1 = new Bid(new Bidder(1, "Alice"), Instant.now(), 5000, 8000, 200);
        var bid2 = new Bid(new Bidder(2, "Aaron"), Instant.now(), 6000, 6400, 200);
        var bid3 = new Bid(new Bidder(3, "Amanda"), Instant.now(), 5500, 6000, 500);
        auctioneer5.makeBid(bid1, 5);
        auctioneer5.makeBid(bid2, 5);
        auctioneer5.makeBid(bid3, 5);
        var result = auctioneer5.determineWinner(5, Instant.now().plusSeconds(ONE_DAY_IN_SECONDS));
        assertEquals(6600, result.getWinningBid());
        assertEquals("Alice", result.getBidder().getName());
    }

    @Test
    @DisplayName("Determine Winner - Multiple Bidder - Possible Min - Diff is equal to increment amount")
    void shouldDetermineWinnerMultipleBidderSameDiff() {
        var bid1 = new Bid(new Bidder(1, "Alice"), Instant.now(), 5000, 7100, 500);
        var bid2 = new Bid(new Bidder(2, "Aaron"), Instant.now(), 6000, 6400, 200);
        var bid3 = new Bid(new Bidder(3, "Amanda"), Instant.now(), 5500, 6500, 500);
        auctioneer7.makeBid(bid1, 7);
        auctioneer7.makeBid(bid2, 7);
        auctioneer7.makeBid(bid3, 7);
        var result = auctioneer7.determineWinner(7, Instant.now().plusSeconds(ONE_DAY_IN_SECONDS));
        assertEquals(7000, result.getWinningBid());
        assertEquals("Alice", result.getBidder().getName());
    }

    @Test
    @DisplayName("Make a bid - Closed Auction")
    void shouldMakeABidClosedAuction() {
        var bid1 = new Bid(new Bidder(1, "Alice"), Instant.now().plusSeconds(ONE_DAY_IN_SECONDS), 5000, 7100, 500);
        Exception exception = assertThrows(RuntimeException.class, () ->
                auctioneer7.makeBid(bid1, 7));
        assertEquals("Auction 7 is closed", exception.getMessage());
    }

    @Test
    @DisplayName("Make a bid - Max Bid is less then starting Bid")
    void shouldMakeABidValueMismatch() {
        var bid1 = new Bid(new Bidder(1, "Alice"), Instant.now(), 7101, 7100, 500);
        Exception exception = assertThrows(RuntimeException.class, () ->
                auctioneer7.makeBid(bid1, 7));
        assertEquals("Starting Bid should be less than Max Bid.", exception.getMessage());
    }
}
