package com.lookout.auction;

import lombok.Data;

@Data
public class Result {
    private final Bidder bidder;
    private final int winningBid;
}
