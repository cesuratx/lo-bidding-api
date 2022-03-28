package com.lookout.auction;

import lombok.*;

import java.time.Instant;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

@Getter
@Setter(AccessLevel.PACKAGE)
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class Auction {
    private List<Bid> bids;
    @EqualsAndHashCode.Include
    private final Item item;
    private Instant createdAt;
    @EqualsAndHashCode.Include
    private int auctionId;

    private static final int ONE_DAY_IN_SECONDS = 86400;
    private static final int WINNING_BID = 0;
    private static final int SECOND_BID = 1;

    boolean makeBid(Bid bid) {
        //assuming that auction is visible to bidder, but still during submission time will pass.
        int comparator = this.createdAt.plusSeconds(ONE_DAY_IN_SECONDS).compareTo(bid.getBidTime());
        if (comparator < 0) {
            throw new RuntimeException("Auction " + this.auctionId + " is closed");
        }
        if (bid.getStartingBid() > bid.getMaxBid()) {
            throw new IllegalArgumentException("Starting Bid should be less than Max Bid.");
        }
        return bids.add(bid);
    }

    Result determineWinner(Instant currenTime) {
        var comparator = currenTime.minusSeconds(ONE_DAY_IN_SECONDS).compareTo(this.createdAt);
        if (comparator < 0) {
            throw new RuntimeException("Auction " + this.auctionId + " is still open.");
        }
        if (bids.isEmpty()) {
            throw new RuntimeException("No bid provided for the auction " + this.auctionId);
        }
        if (bids.size() == 1) {
            return new Result(bids.get(WINNING_BID).getBidder(), bids.get(WINNING_BID).getStartingBid());
        }
        bids.forEach(Bid::calculateAndSetWinnerCandidateBid);
        var sortedBids = bids
                .stream()
                .sorted(comparingInt(Bid::getWinnerCandidateBid)
                        .reversed())
                .collect(toList());
        var winnerCandidate = sortedBids.get(WINNING_BID);
        var winnerCandidateBid = winnerCandidate.getWinnerCandidateBid();
        var tieBids = sortedBids
                .stream()
                .takeWhile(e -> e.getWinnerCandidateBid() == winnerCandidateBid)
                .collect(toList());
        if (tieBids.size() == 1) {
            var modifiableWinnerCandidateBid = winnerCandidateBid;
            var secondWinnerCandidateBid = sortedBids.get(SECOND_BID).getWinnerCandidateBid();
            var diff = modifiableWinnerCandidateBid - secondWinnerCandidateBid;
            var winnerCandidateIncrementAmount = winnerCandidate.getIncrementAmount();
            if (diff < winnerCandidateIncrementAmount || diff == winnerCandidateIncrementAmount) {
                return new Result(winnerCandidate.getBidder(), modifiableWinnerCandidateBid);
            } else {
                var times = (diff / winnerCandidateIncrementAmount);
                var remainder = (diff % winnerCandidateIncrementAmount);
                if (remainder > 0) {
                    modifiableWinnerCandidateBid = modifiableWinnerCandidateBid - (winnerCandidateIncrementAmount * times);
                    return new Result(winnerCandidate.getBidder(), modifiableWinnerCandidateBid);
                } else {
                    modifiableWinnerCandidateBid = modifiableWinnerCandidateBid - (winnerCandidateIncrementAmount * (times-1));
                    return new Result(winnerCandidate.getBidder(), modifiableWinnerCandidateBid);
                }
            }
        } else {
            winnerCandidate = tieBids.stream().min(comparing(Bid::getBidTime)).get();
            return new Result(winnerCandidate.getBidder(), winnerCandidate.getWinnerCandidateBid());
        }
    }
}
