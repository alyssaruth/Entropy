package screen;

import javax.swing.ImageIcon;

import object.AchievementBadge;
import util.AchievementsUtil;
import util.Registry;

public interface AchievementBadges extends Registry
{
	//Icons
	public static final ImageIcon lockedIcon = AchievementsUtil.getIconForAchievement("locked");
	
	//Badges
	public final AchievementBadge cavemanBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_CAVEMAN);
	public final AchievementBadge burglarBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_BURGLAR);
	public final AchievementBadge lionBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_LION);
	public final AchievementBadge gardenerBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_GARDENER);
	public final AchievementBadge psychicBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_PSYCHIC);
	public final AchievementBadge fiveMinutesBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_FIVE_MINUTES);
	public final AchievementBadge fifteenMinutesBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_FIFTEEN_MINUTES);
	public final AchievementBadge thirtyMinutesBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_THIRTY_MINUTES);
	public final AchievementBadge sixtyMinutesBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_SIXTY_MINUTES);
	public final AchievementBadge twoHoursBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_TWO_HOURS);
	public final AchievementBadge cowardBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_COWARD);
	public final AchievementBadge spectatorBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_SPECTATOR);
	public final AchievementBadge vanityBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_VANITY);
	public final AchievementBadge unscathedBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_UNSCATHED);
	public final AchievementBadge bulletproofBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_BULLETPROOF);
	public final AchievementBadge superhumanBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_SUPERHUMAN);
	public final AchievementBadge momentumBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_MOMENTUM);
	public final AchievementBadge chainReactionBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_CHAIN_REACTION);
	public final AchievementBadge perpetualMotionBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_PERPETUAL_MOTION);
	public final AchievementBadge participantBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_PARTICIPANT);
	public final AchievementBadge hobbyistBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_HOBBYIST);
	public final AchievementBadge enthusiastBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_ENTHUSIAST);
	public final AchievementBadge professionalBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_PROFESSIONAL);
	public final AchievementBadge veteranBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_VETERAN);
	public final AchievementBadge firstTimerBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_FIRST_TIMER);
	public final AchievementBadge casualStrategistBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_CASUAL_STRATEGIST);
	public final AchievementBadge consistentWinnerBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_CONSISTENT_WINNER);
	public final AchievementBadge dominantForceBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_DOMINANT_FORCE);
	public final AchievementBadge secondThoughtsBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_SECOND_THOUGHTS);
	public final AchievementBadge fullBlindTwoBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_FULL_BLIND_TWO);
	public final AchievementBadge fullBlindThreeBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_FULL_BLIND_THREE);
	public final AchievementBadge fullBlindFourBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_FULL_BLIND_FOUR);
	public final AchievementBadge chimeraBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_CHIMERA);
	public final AchievementBadge precisionBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_PRECISION);
	public final AchievementBadge mathematicianBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_MATHEMATICIAN);
	public final AchievementBadge nuclearStrikeBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_NUCLEAR_STRIKE);
	public final AchievementBadge handicapTwoBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_HANDICAP_TWO);
	public final AchievementBadge handicapThreeBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_HANDICAP_THREE);
	public final AchievementBadge handicapFourBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_HANDICAP_FOUR);
	public final AchievementBadge vectropyOneBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_VECTROPY_ONE);
	public final AchievementBadge vectropyTenBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_VECTROPY_TEN);
	public final AchievementBadge vectropyTwentyFiveBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_VECTROPY_TWENTY_FIVE);
	public final AchievementBadge vectropyFiftyBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_VECTROPY_FIFTY);
	public final AchievementBadge distractedBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_DISTRACTED);
	public final AchievementBadge citizensArrestBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_CITIZENS_ARREST);
	public final AchievementBadge connectedBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_CONNECTED);
	public final AchievementBadge railbirdBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_RAILBIRD);
	public final AchievementBadge socialBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_SOCIAL);
	public final AchievementBadge blueScreenOfDeathBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_BLUE_SCREEN_OF_DEATH);
	public final AchievementBadge werewolfBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_WEREWOLF);
	public final AchievementBadge spacemanBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_SPACEMAN);
	public final AchievementBadge konamiCodeBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_KONAMI_CODE);
	
	public final AchievementBadge bookwormBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_BOOKWORM);
	public final AchievementBadge chattyBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_CHATTY);
	public final AchievementBadge deceitfulBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_DECEITFUL);
	public final AchievementBadge honestBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_HONEST);
	public final AchievementBadge lookAtMeBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_LOOK_AT_ME);
	public final AchievementBadge monotoneBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_MONOTONE);
	public final AchievementBadge omniscientBadge = new AchievementBadge(ACHIEVEMENTS_BOOLEAN_OMNISCIENT);
}
