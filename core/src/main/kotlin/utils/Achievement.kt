package utils

import util.Registry.achievements

enum class Achievement(
    val registryLocation: String,
    val title: String,
    val explanation: String,
    val description: String
) {
    Caveman("caveman", "The Caveman", "Make a perfect bid in Clubs", "Clobbered 'em!"),
    Burglar("burglar", "The Burglar", "Make a perfect bid in Diamonds", "Dazzling."),
    Lion("lion", "The Lion", "Make a perfect bid in Hearts", "A bid with some real heart."),
    Gardener("gardener", "The Gardener", "Make a perfect bid in Spades", "Textbook digging!"),
    Psychic(
        "psychic",
        "The Psychic",
        "Make a perfect bid whilst blind",
        "You totally *knew* it was there, didn't you?"
    ),
    Sluggish(
        "fiveMinutes",
        "Sluggish",
        "Play the game for 5 minutes",
        "Kudos, you stuck with this for five minutes. If it helps, that's the same as a slow 1500m run..."
    ),
    WarmingUp(
        "fifteenMinutes",
        "Warming Up",
        "Play the game for 15 minutes",
        "Still not setting the world alight, you've progressed to a medium-paced 5000m. Keep trying."
    ),
    BreakingASweat(
        "thirtyMinutes",
        "Breaking a Sweat",
        "Play the game for 30 minutes",
        "Now the blood's pumping! 10,000m and a new World Record (assuming you're playing pre-1938)."
    ),
    WorldClass(
        "sixtyMinutes",
        "World Class",
        "Play the game for 1 hour",
        "<HTML>All that training is finally paying off - you* just ran a half marathon!<br><br>*Not really you. You're still inside, staring at a screen.</HTML>"
    ),
    RecordBreaker(
        "twoHours",
        "Record Breaker",
        "Play the game for 2 hours",
        "<HTML>You could have run a marathon in the time you've been playing this. Where 'could have' assumes<br>" +
            "your name to be Haile Gebrselassie and that you chose to run with a strong following wind.</HTML>"
    ),
    Coward(
        "coward",
        "Coward",
        "Quit a game mid-way through",
        "At least I assume it was cowardice. Maybe you just had better things to do."
    ),
    Spectator(
        "spectator",
        "Spectator",
        "Watch a game through to the end after you've been knocked out",
        "Bet that was fun. Did the best random number generator win?"
    ),
    Vanity(
        "vanity",
        "Vanity",
        "View your achievements and/or statistics 20 times",
        "Your own biggest fan!"
    ),
    Unscathed(
        "perfectTwoPlayerGame",
        "Unscathed",
        "Win a full two player game without losing a single round",
        "Five whole rounds and not a foot wrong."
    ),
    Bulletproof(
        "perfectThreePlayerGame",
        "Bulletproof",
        "Win a full three player game without losing a single round",
        "I can dodge bullets, baby!"
    ),
    Superhuman(
        "perfectFourPlayerGame",
        "Superhuman",
        "Win a full four player game without losing a single round",
        "No sarcastic remark for this one - just a sincere pat on the back. Well played!"
    ),
    Momentum(
        "momentum",
        "Momentum",
        "Win 3 games in a row",
        "Hey look at that! You're almost on a roll!"
    ),
    ChainReaction(
        "chainReaction",
        "Chain Reaction",
        "Win 6 games in a row",
        "This is actually pretty good."
    ),
    PerpetualMotion(
        "perpetualMotion",
        "Perpetual Motion",
        "Win 10 games in a row",
        "Tough, even with just two players. You'll have needed some luck for this one."
    ),
    Participant(
        "participant",
        "Participant",
        "Play 10 games (includes online)",
        "Well you've given it a try. Just how into card games are you?"
    ),
    Hobbyist(
        "hobbyist",
        "Hobbyist",
        "Play 25 games (includes online)",
        "Meh, somewhat entertaining. Aren't you curious about some of those achievements?"
    ),
    Enthusiast(
        "enthusiast",
        "Enthusiast",
        "Play 50 games (includes online)",
        "It's more than just a time-waster now, isn't it?"
    ),
    Professional(
        "professional",
        "Professional",
        "Play 100 games (includes online)",
        "How many hours must you have racked up by now...?"
    ),
    Veteran("veteran", "Veteran", "Play 200 games (includes online)", "Please go outside."),
    FirstTimer(
        "firstTimer",
        "First-Timer",
        "Win a game of Entropy (includes online)",
        "Hoorah! You're a success, and here's a medal to prove it!"
    ),
    CasualStrategist(
        "casualStrategist",
        "Casual Strategist",
        "Win 10 games of Entropy (includes online)",
        "You might just be starting to get the hang of this."
    ),
    ConsistentWinner(
        "consistentWinner",
        "Consistent Winner",
        "Win 25 games of Entropy (includes online)",
        "Definitely getting the hang of it now, depending on your loss statistics."
    ),
    DominantForce(
        "dominantForce",
        "Dominant Force",
        "Win 50 games of Entropy (includes online)",
        "Fine, fine. You're a competent Entropy player. Happy now?"
    ),
    SecondThoughts(
        "secondThoughts",
        "Second Thoughts",
        "Look at your cards after bidding blind",
        "'Fraidy-cat."
    ),
    BlindTwo(
        "fullBlindTwo",
        "Blind Luck",
        "Win a full two player game entirely blind",
        "Just clicking buttons would get you this - a highly skilled pursuit indeed."
    ),
    BlindThree(
        "fullBlindThree",
        "Against the Odds",
        "Win a full three player game entirely blind",
        "You would've had to think about what you were doing a bit for this one."
    ),
    BlindFour(
        "fullBlindFour",
        "Lottery Winner",
        "Win a full four player game entirely blind",
        "Impressive. Wasn't it just a little tempting to sneak a peek near the end?"
    ),
    Chimera(
        "chimera",
        "The Chimera",
        "Make perfect bids in 3 or more different suits during one game",
        "One of the hardest achievements in the game. Also a badass creature from Greek mythology."
    ),
    Precision(
        "precision",
        "Precision",
        "Win a game of 5 rounds or more without ever making an overbid",
        "There's a fine line between precise and overly cautious, but this time you nailed it."
    ),
    Mathematician(
        "mathematician",
        "The Mathematician",
        "Make a perfect bid in Vectropy",
        "Perfect in all four suits at once, like only a mathematician could."
    ),
    NuclearStrike(
        "nuclearStrike",
        "Nuclear Strike",
        "Win a full four player game starting with one card and playing entirely blind",
        "NUCLEAR STRRRRRRRIKE!"
    ),
    HandicapTwo(
        "handicapTwo",
        "Underdog I",
        "Win a two player game with a handicap of 2 or more",
        "I wonder what the others could be..."
    ),
    HandicapThree(
        "handicapThree",
        "Underdog II",
        "Win a three player game with a handicap of 2 or more",
        "Hey, that picture looks familiar!"
    ),
    HandicapFour(
        "handicapFour",
        "Underdog III",
        "Win a four player game with a handicap of 2 or more",
        "Yeah, I wasn't at my most imaginative when I made these."
    ),
    VectropyOne(
        "vectropyOne",
        "Easy as 1, 2, 3",
        "Win a game of Vectropy (includes online)",
        "Maybe not that easy at first, but you get used to it I swear."
    ),
    VectropyTen(
        "vectropyTen",
        "Base Ten",
        "Win 10 games of Vectropy (includes online)",
        "It's a good job it's not binary, otherwise you'd have only won twice."
    ),
    VectropyTwentyFive(
        "vectropyTwentyFive",
        "Five Squared",
        "Win 25 games of Vectropy (includes online)",
        "I've got a formula for you: this badge * 2 = next badge. Get on it."
    ),
    VectropyFifty(
        "vectropyFifty",
        "Half-Century",
        "Win 50 games of Vectropy (includes online)",
        "The big 5-0, as they say. Congratulations."
    ),
    Distracted(
        "distracted",
        "Distracted",
        "Take longer than 3 minutes to act on your turn",
        "Hey, it's your turn! Do something already!"
    ),
    CitizensArrest(
        "citizensArrest",
        "Citizen's Arrest",
        "Call 'Illegal!' correctly",
        "Aren't you glad the computer opponents can't do this to you?"
    ),
    Connected(
        "connected",
        "Connected",
        "Connect to the Entropy Server",
        "It's a real hive of activity..."
    ),
    Railbird(
        "railbird",
        "Railbird",
        "Observe at least one round online",
        "I'm surprised there were enough people online for you to accomplish this."
    ),
    Social("social", "Social", "Play with 5 or more individuals online", "Awesome!"),
    Werewolf("werewolf", "The Werewolf", "Make a perfect bid in Moons", "Harooooo!"),
    Spaceman(
        "spaceman",
        "The Spaceman",
        "Make a perfect bid in Stars",
        "A bid so great it's taken you to outer space!"
    ),
    Bookworm(
        "bookworm",
        "Bookworm",
        "Spend 5 minutes on the Help dialog",
        "Well at least someone reads the help."
    ),
    Chatty(
        "chatty",
        "Chatty",
        "Send 25 online chat messages",
        "Bonus points if there was actually someone else around at the time."
    ),
    Deceitful(
        "deceitful",
        "Deceitful",
        "Win a game never revealing a card of the suit you were bidding (must reveal at least 5)",
        "No double-bluffs from you, just straight up lies."
    ),
    Honest(
        "honest",
        "Honest",
        "Win a game only revealing cards of the suit you were bidding (must reveal at least 5)",
        "Honesty is the best policy. Except it probably isn't."
    ),
    LookAtMe("lookAtMe", "Look At Me!", "Export a replay", "I'm Mr. Meeseeeeks, look at me!"),
    Monotone(
        "monotone",
        "Monotone",
        "Win a 5-card game of Entropy only ever bidding one suit",
        "Sometimes being a one-trick pony has its upsides."
    ),
    Omniscient(
        "omniscient",
        "Omniscient",
        "Play in a round where 10 or more of your opponents' cards are revealed",
        "Not exactly cloak-and-daggers when you can pretty much see everyone's hand..."
    ),
    BlueScreenOfDeath(
        "blueScreenOfDeath",
        "Blue Screen of Death",
        "Cause something to go wrong in the game",
        "Oops! Looks like I'll be getting an alert to fix whatever that was..."
    ),
    KonamiCode(
        "konamiCode",
        "Nintendo",
        "Enter the Konami code in the main Entropy window",
        "I hear if you hold B and Down the opponent will never challenge."
    ),
}

fun Achievement.isUnlocked() = achievements.getBoolean(registryLocation, false)
