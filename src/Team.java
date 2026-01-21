import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Team
{
    String name;
    Map<Hero.Position, Hero> heroes;
    private int souls;
    private BattleManager battleManager;
    private double teamDefMod = 0;
    private double teamAtkMod = 0;
    private double teamSpeedMod = 0;
    public Team opposingTeam;
    private ArrayList<DamageReduction> damageReductions;
    public Team()
    {
        heroes = new HashMap<>();
        damageReductions = new ArrayList<>();
    }
    public void TriggerEvent(BattleManager.EventParams eventParams)
    {
        for (Hero hero : heroes.values())
        {
            hero.HandleEvent(eventParams);
        }
    }
    public void AddDamageReduction(DamageReduction damageReduction)
    {
        damageReductions.add(damageReduction);
    }
    public double GetLargestDamageReduction(Hero.SkillKeys skillKey, Hero attacker, Hero target, double damage, Skill.HitTypes hitType, boolean isCounter, boolean isExtraAttack)
    {
        double damageReductionValue = 0;
        for (DamageReduction damageReduction : damageReductions)
        {
            switch (damageReduction.condition)
            {
                case COUNTER_OR_EXTRA:
                    if (!(isExtraAttack || isCounter)) {continue;}
                    break;
                case AOE_ATK:
                    if (attacker.skills.get(skillKey).getSkillType() != Skill.SkillTypes.AOE) {continue;}
                    break;
                case SINGLE_ATK:
                    if (attacker.skills.get(skillKey).getSkillType() != Skill.SkillTypes.SINGLE) {continue;}
                    break;
                case CRIT:
                    if (hitType != Skill.HitTypes.CRIT) {continue;}
                    break;
            }
            if (damageReduction.value > damageReductionValue) {damageReductionValue = damageReduction.value;}
        }
        return damageReductionValue;
    }
    public void AddStatusEffect(StatusEffect.StatusEffectName statusEffectName, int duration)
    {
        AddStatusEffect(statusEffectName, duration, false, true);
    }
    public void AddStatusEffect(StatusEffect.StatusEffectName statusEffectName, int duration, boolean startOfTurn, boolean addedThisAttack)
    {
        for (Hero hero : heroes.values())
        {
            hero.AddStatusEffect(new StatusEffect(statusEffectName, duration), startOfTurn, addedThisAttack);
        }
    }
    public void AddShield(double shieldValue, int duration)
    {
        for (Hero hero : heroes.values())
        {
            StatusEffect shield = new StatusEffect(StatusEffect.StatusEffectName.SHIELD, duration);
            shield.setShieldValue(shieldValue);
            hero.AddStatusEffect(shield);
        }
    }
    public void AddDamageShare(DamageShare damageShare)
    {
        for (Hero hero : heroes.values())
        {
            if (hero != damageShare.hero)
            {
                hero.AddDamageSharing(damageShare);
            }
        }
    }

    public Hero GetDualAttacker(Skill triggeringSkill)
    {
        Hero dualAttacker = null;
        if (triggeringSkill.isGuaranteedDual())
        {
            switch (triggeringSkill.getDualAttackCondition())
            {
                case HIGHEST_ATK -> dualAttacker = GetHighestAttackHero();
                default -> dualAttacker = GetRandomHero();
            }
        }

        for (Hero hero : heroes.values())
        {
            if (BattleManager.TryRng(hero.GetDualAttackChance()))
            {
                dualAttacker = hero;
            }
        }
        return dualAttacker;
    }
    public Hero GetRandomHero()
    {
        double rngRoll = ThreadLocalRandom.current().nextDouble() * 100.0;
        if (rngRoll <= 25.0) { return heroes.get(Hero.Position.FRONT); }
        else if (rngRoll <= 50) { return heroes.get(Hero.Position.TOP); }
        else if (rngRoll <= 75) { return heroes.get(Hero.Position.BACK); }
        else { return heroes.get(Hero.Position.BOTTOM); }
    }

    public Hero GetHighestAttackHero()
    {
        Hero highestAtkHero = null;
        double highestAttack = 0;
        for (Hero hero : heroes.values())
        {
            if (hero.GetEffectiveAttack() > highestAttack)
            {
                highestAtkHero = hero;
            }
        }
        return highestAtkHero;
    }
    public void addDefMod(double amount)
    {
        teamDefMod += amount;
    }

    public double getTeamDefMod()
    {
        return teamDefMod;
    }
    public void addAtkMod(double amount)
    {
        teamAtkMod += amount;
    }

    public double getTeamAtkMod()
    {
        return teamAtkMod;
    }
    public void addSpeedMod(double amount)
    {
        teamSpeedMod += amount;
    }

    public double getTeamSpeedMod()
    {
        return teamSpeedMod;
    }
    public void RemoveDamageReduction(DamageReduction toRemove)
    {
        damageReductions.remove(toRemove);
    }

    public int getSouls()
    {
        return souls;
    }

    public void setOpposingTeam(Team opposingTeam)
    {
        this.opposingTeam = opposingTeam;
    }

    public void CheckForDeaths()
    {
        for (Hero hero : heroes.values())
        {
            if (hero.IsDead())
            {
                BattleManager.EventParams deathParams = new BattleManager.EventParams();
                deathParams.trigerringHero = hero;
                deathParams.eventKey = BattleManager.EventKeys.ON_DEATH;
                hero.HandleEvent(deathParams);
            }
        }
    }

    public void ClearDamageShare(Hero heroToClear)
    {
        for (Hero hero : heroes.values())
        {
            hero.ClearDamageSharing(heroToClear);
        }
    }

    public void RemoveDebuffs(int amount)
    {
        for (Hero hero : heroes.values())
        {
            hero.RemoveDebuffs(amount);
        }
    }
    public void AddCombatReadiness(double amount)
    {
        for (Hero hero : heroes.values())
        {
            hero.addCombatReadiness(amount);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addHero(Hero heroToAdd, Hero.Position position)
    {
        heroes.put(position, heroToAdd);
        heroToAdd.position = position;
        heroToAdd.setTeam(this);
        heroToAdd.setOpposingTeam(opposingTeam);
    }
    public void setBattleManager(BattleManager battleManagerToSet)
    {
        battleManager = battleManagerToSet;
    }

    public BattleManager getBattleManager()
    {
        return battleManager;
    }

    public Hero getHeroAtPostion(Hero.Position position)
    {
        return heroes.get(position);
    }
    public void addHeroesToBattleManager()
    {
        for (Hero hero : heroes.values())
        {
            battleManager.addHero(hero);
        }
    }
    public boolean IsDead()
    {
        for (Hero hero : heroes.values())
        {
            if (!hero.IsDead())
            {
                return false;
            }
        }

        return true;
    }

    public void addSouls(int amount)
    {
        souls = Math.min(80, souls + amount);
    }

    public void subtractSouls(int amount)
    {
        souls = Math.max(0, souls - amount);
    }

    public void restoreHealth(double amount)
    {
        for (Hero hero : heroes.values())
        {
            hero.restoreHealth(amount);
        }
    }

    public Hero LowestHero()
    {
        double lowestHp = 2;
        Hero lowestHero = null;
        double hpRatio;
        for (Hero hero : heroes.values())
        {
            hpRatio = hero.currentHealth/hero.maxHealth;
            if (hpRatio < lowestHp && !hero.IsDead())
            {
                lowestHero = hero;
                lowestHp = hpRatio;
            }
        }
        return lowestHero;
    }

    public String toString()
    {
        String status = new String();
        status += name;
        status += "\n";
        for (Hero hero : heroes.values())
        {
            status += hero.toString();
            status += "\n";
        }
        return status;
    }
}
