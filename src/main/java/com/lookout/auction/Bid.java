package com.lookout.auction;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Bid {
    private final Bidder bidder;
    private final Instant bidTime;
    private final int startingBid;
    private final int maxBid;
    private final int incrementAmount;
    @Setter(AccessLevel.NONE)
    private int winnerCandidateBid;

    void calculateAndSetWinnerCandidateBid() {
        var startingBid = this.getStartingBid();
        var incrementAmount = this.getIncrementAmount();
        var maxBid = this.getMaxBid();
        var diff = maxBid - startingBid;
        var remainingAmount = diff % incrementAmount;
        var addedValue = diff - remainingAmount;
        this.winnerCandidateBid = startingBid + addedValue;
    }
}
