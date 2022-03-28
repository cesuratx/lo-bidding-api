package com.lookout.auction;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/*
This is the only exposed class to work with the bidding api.
 */
public class Auctioneer {

    private static final AtomicInteger auctionIdCounter = new AtomicInteger(0);
    private static final List<Auction> auctions = new CopyOnWriteArrayList<>();

    public boolean createAuction(Item item) {
        var auction = new Auction(item);
        auction.setAuctionId(auctionIdCounter.getAndIncrement());
        auction.setCreatedAt(Instant.now());
        auction.setBids(new CopyOnWriteArrayList<>());
        return auctions.add(auction);
    }

    public void makeBid(Bid bid, int auctionId) {
        var auction = auctions.get(auctionId);
        auction.makeBid(bid);
    }

    public Result determineWinner(int auctionId, Instant currenTime) {
        var auction = auctions.get(auctionId);
        return auction.determineWinner(currenTime);
    }

    public boolean removeAuction(Auction auction) {
        return auctions.remove(auction);
    }

    public List<Auction> getAuctions() {
        return auctions;
    }

    /*
     * this method is defined but not implemented. The purpose of this method is
     * to achieve fairness. 24 Hours before each auction, users should be notified to be in comply
     * with the business rule : "Should there be a tie between two or more bidders,
     * the first person that entered their information wins".
     */
    public void notifyBidders() {
        //TODO send email to the bidders in the system and let them know about the rules of the bidding.
    }
}
