import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

// Manages the process of the battle.
public class BattleManager
{
    public enum EventKeys
    {
        BATTLE_START, TURN_END, TURN_START, ON_ATTACK_USED_ENEMY, ON_ATTACK_USED_ALLY, ON_ATTACK, ON_ATTACKED,
        NON_ATK_USED_ENEMY, NON_ATK_USED_ALLY, ON_DEATH, ON_REVIVE
    }
    Team team1;
    Team team2;
    Hero activeHero;
    List<Hero> heroes;
    public MainFrame battleFrame;

    private Hero.SkillKeys selectedSkill;
    public boolean soulburnSelected = false;

    public BattleManager()
    {
        heroes = new ArrayList<>();
    }

    public static class EventParams
    {
        public BattleManager.EventKeys eventKey;
        public Hero trigerringHero;
        public Hero targetHero;
        public Hero respondingHero;
        public Skill triggeringSkill;
        public boolean isCounter;
        public boolean isExtraAttack;
        public double damageDealt;
        public Skill.HitTypes hitType;
        public int priority;
        public boolean canTriggerDual;
        public boolean guarenteedDual;
        public boolean highestAttackDual;
        public boolean alreadyDualAttacked;
    }
    public void TriggerEvent(EventParams eventParams)
    {
        for (Hero hero : heroes)
        {
            hero.HandleEvent(eventParams);
        }
    }

    // if chance = 60, this will return true 60% of the time
    public static boolean TryRng(double chance)
    {
        double rngRoll = ThreadLocalRandom.current().nextDouble() * 100.0;
        return rngRoll <= chance;
    }

    // print a string that shows the current state of battle
    public String getCurrentState()
    {
        StringBuilder status = new StringBuilder();
        status.append("It is now ").append(activeHero.name).append("'s turn.\n");
        status.append(team1.toString()).append("\n");
        status.append(team2.toString());
        return status.toString();
    }

    public void addStartingCR()
    {
        for (Hero hero : heroes)
        {
            double startingCR = ThreadLocalRandom.current().nextDouble() * 5;
            hero.addCombatReadiness(startingCR);
        }
    }

    // time: for every 1 speed, a Hero gains 1 CR per unit of time
    public double getTimeToNextTurn()
    {
        double remainingTime;
        double minTime = 1000.0;
        for (Hero hero : heroes)
        {
            if (hero.IsDead()) { continue; }
            remainingTime = hero.getTimeToNextTurn();
            if (remainingTime < minTime) { minTime=remainingTime; }
        }
        return minTime;
    }

    public void advanceCR(double time)
    {
        for (Hero hero : heroes)
        {
            hero.advanceCR(time);
        }
    }
    private void startBattle()
    {
        EventParams eventParams = new EventParams();
        eventParams.eventKey = EventKeys.BATTLE_START;
        TriggerEvent(eventParams);
        addStartingCR();
    }

    // if no Heroes are currently at 100 CR, advance CR based on speed until one Hero has 100 CR
    private void advanceToNextTurn()
    {
        double timeToNextTurn = getTimeToNextTurn();
        if (timeToNextTurn > 0.0)
        {
            advanceCR(timeToNextTurn);
        }
    }
    private void findAndSetActiveHero()
    {
        if (activeHero != null)
        {
            if (activeHero.getExtraTurns() > 0)
            {
                activeHero.subtractExtraTurn();
                return;
            }
            activeHero.setActiveHero(false);
        }

        double highestCr = -1.0;
        for (Hero hero : heroes)
        {
            if (hero.IsDead()) {continue;}
            if (hero.combatReadiness > highestCr)
            {
                highestCr = hero.combatReadiness;
                activeHero = hero;
            }
        }
        activeHero.setActiveHero(true);
    }
    public void addTeam(Team teamToAdd, String teamKey)
    {
        teamToAdd.setBattleManager(this);
        teamToAdd.addHeroesToBattleManager();
        switch (teamKey)
        {
            case "team1":
                team1 = teamToAdd;
                break;
            case "team2":
                team2 = teamToAdd;
                break;
        }
    }
    public void addHero(Hero heroToAdd)
    {
        heroes.add(heroToAdd);
    }
    private boolean isGameOver()
    {
        if (team1.IsDead()) return true;
        if (team2.IsDead()) return true;
        return false;
    }

    // Starts a battle using the Java Swift UI kit
    public void startManualBattle()
    {
        startBattle();
        advanceToNextTurn();
        findAndSetActiveHero();
        EventParams eventParams = new EventParams();
        eventParams.eventKey = EventKeys.TURN_START;
        eventParams.trigerringHero = activeHero;
        TriggerEvent(eventParams);

        battleFrame.UpdateBoard();

        System.out.println(getCurrentState());
        activeHero.displayAvailableSkills();
        System.out.println("Choose a skill: ");
    }

    // Processes all events that occur during one turn, after selecting the skill to use
    public void processOneTurn(Hero.SkillKeys skillToUse, Hero target, boolean dryRun)
    {
        if (isGameOver()) {return;}
        boolean soulburn = false;
        if (soulburnSelected)
        {
            toggleSoulburn();
            activeHero.team.addSouls(-1 * activeHero.soulCost);
            skillToUse = Hero.SkillKeys.SOULBURN;
            soulburn = true;
        }
        activeHero.UseSkill(target, skillToUse, soulburn, false, false);

        EventParams eventParams = new EventParams();
        eventParams.eventKey = EventKeys.TURN_END;
        eventParams.trigerringHero = activeHero;
        TriggerEvent(eventParams);

        if (isGameOver())
        {
            return;
        }

        battleFrame.UpdateSouls();

        advanceToNextTurn();
        findAndSetActiveHero();

        eventParams = new EventParams();
        eventParams.eventKey = EventKeys.TURN_START;
        eventParams.trigerringHero = activeHero;
        TriggerEvent(eventParams);

        System.out.println(getCurrentState());
        activeHero.displayAvailableSkills();
        battleFrame.UpdateBoard();

        if (activeHero.provokeTarget != null)
        {
            processOneTurn(Hero.SkillKeys.SKILL1, activeHero.provokeTarget, false);
        }
    }

    public void setSelectedSkill(Hero.SkillKeys skillKey)
    {
        selectedSkill = skillKey;
        if (soulburnSelected && skillKey != activeHero.soulburnSkill) {soulburnSelected = false;}
        battleFrame.UpdateSouls();
        battleFrame.UpdateSkillButtons(skillKey);
        battleFrame.UpdateBorders();
    }
    public void toggleSoulburn()
    {
        soulburnSelected = !soulburnSelected;
        if (soulburnSelected)
        {
            setSelectedSkill(activeHero.soulburnSkill);
            battleFrame.UpdateSkillButtons(activeHero.soulburnSkill);
        }
        battleFrame.UpdateSouls();
        battleFrame.UpdateBorders();
    }
    public Hero.SkillKeys getSelectedSkill()
    {
        return selectedSkill;
    }
    public void selectTarget(int heroId)
    {
        if (selectedSkill == null) { return; }
        if (isValidTarget(heroId))
        {
            processOneTurn(selectedSkill, findHeroById(heroId), false);
            selectedSkill = null;
            battleFrame.UpdateBorders();
            battleFrame.UpdateSouls();
        }
    }

    public void displayMessage(String message)
    {
        battleFrame.displayMessage(message);
    }
    public boolean isValidTarget(int heroId)
    {
        Hero target = findHeroById(heroId);
        if (target == null || selectedSkill == null)
        {
            return false;
        }
        if (target.IsDead())
        {
            return false;
        }
        Skill skill = activeHero.skills.get(selectedSkill);
        if (skill.targetType == Skill.TargetTypes.ENEMY && target.HasStatusEffect(StatusEffect.StatusEffectName.STEALTH))
        {
            return false;
        }
        if (skill.targetType == Skill.TargetTypes.ENEMY && target.team == activeHero.team)
        {
            return false;
        }
        if (skill.targetType == Skill.TargetTypes.ALLY && target.team != activeHero.team)
        {
            return false;
        }
        return true;
    }
    public Hero findHeroById(int heroId)
    {
        Hero hero = null;
        switch (heroId)
        {
            case 1 -> hero = team1.heroes.get(Hero.Position.TOP);
            case 2 -> hero = team1.heroes.get(Hero.Position.BACK);
            case 3 -> hero = team1.heroes.get(Hero.Position.FRONT);
            case 4 -> hero = team1.heroes.get(Hero.Position.BOTTOM);
            case 5 -> hero = team2.heroes.get(Hero.Position.TOP);
            case 6 -> hero = team2.heroes.get(Hero.Position.FRONT);
            case 7 -> hero = team2.heroes.get(Hero.Position.BACK);
            case 8 -> hero = team2.heroes.get(Hero.Position.BOTTOM);
        }

        return hero;
    }

    public void doBattle()
    {
        Scanner battleInput = new Scanner(System.in);
        String input;
        startBattle();
        while (!isGameOver())
        {
            advanceToNextTurn();
            findAndSetActiveHero();

            EventParams eventParams = new EventParams();
            eventParams.eventKey = EventKeys.TURN_START;
            eventParams.trigerringHero = activeHero;
            TriggerEvent(eventParams);

            System.out.println(getCurrentState());
            activeHero.displayAvailableSkills();
            System.out.println("Choose a skill: ");
            input = battleInput.nextLine();
            Hero.SkillKeys skillToUse;
            switch (input)
            {
                case "1":
                    skillToUse = Hero.SkillKeys.SKILL1;
                    break;
                case "2":
                    skillToUse = Hero.SkillKeys.SKILL2;
                    break;
                case "3":
                    skillToUse = Hero.SkillKeys.SKILL3;
                    break;
                default:
                    skillToUse = Hero.SkillKeys.SKILL1;
                    break;
            }
            activeHero.UseSkill(activeHero.opposingTeam.getHeroAtPostion(Hero.Position.FRONT), skillToUse,false, false, false);

            eventParams = new EventParams();
            eventParams.eventKey = EventKeys.TURN_END;
            eventParams.trigerringHero = activeHero;
            TriggerEvent(eventParams);
        }
    }

}
