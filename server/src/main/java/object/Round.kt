package `object`

data class Round(val hands: Map<Int, List<String>>, val bidHistory: Map<Int, BidHistory>)
