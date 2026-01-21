import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Hero
{
    public enum Position
    {
        FRONT, BACK, TOP, BOTTOM
    }

    public enum Element
    {
        ICE, EARTH, FIRE, LIGHT, DARK
    }

    public enum Class
    {
        KNIGHT, MAGE, RANGER, SOULWEAVER, WARRIOR, THIEF
    }

    public enum SkillKeys
    {
        SKILL1, SKILL1_PROC, SKILL2, SKILL2_ALT, SKILL3, SOULBURN
    }
    public enum ElementalMatchup
    {
        DISADVANTAGE, NORMAL, ADVANTAGE
    }

    private double DAMAGE_MULTIPLIER = 1.871;
    public String name;
    protected double maxHealth;
    protected double defense;
    protected double attack;
    protected double effectResistance;
    protected double effectiveness;
    protected double critChance;
    protected double critDamage;
    protected double speed;
    protected double dualAttackChance = 3.0;
    protected double combatReadiness;
    protected double originalMaxHealth;
    protected double currentHealth;
    protected double shield;
    protected int skillNullifier = 0;
    protected double lifesteal;
    protected double skillAttackMod = 0;
    protected double skillDefenseMod = 0;
    protected double skillSpeedMod = 0;
    protected double hitChanceBonus = 0;
    protected double evasion = 0;
    protected double critResistance;
    protected Hero damageSharingTo;
    public double damageSharingValue;
    protected List<DamageShare> damageSharing;
    protected double damageReduction = 0;
    protected boolean alreadyCountered = false;
    protected boolean injurySet = false;
    protected boolean counterSet = false;
    protected boolean penSet = false;
    protected int exclusiveEquipment = 1;
    protected String imagePath;
    protected ArrayList<StatusEffect> statusEffects;
    protected Position position;
    protected Element element;
    protected HashMap<SkillKeys, Skill> skills;
    protected Team team;
    protected Team opposingTeam;
    protected Artifact artifact;
    protected int heroId;
    protected int soulCost = 10;
    protected int unitySetCount = 0;
    protected SkillKeys soulburnSkill = SkillKeys.SKILL1;
    protected boolean isActiveHero = false;
    private boolean isUnbuffable = false;
    private boolean isImmune = false;
    public boolean hasStealth = false;
    private boolean alreadyAddedSouls = false;
    protected int extraTurns = 0;
    protected Hero provokeTarget;
    public Hero()
    {
        combatReadiness = 0.0;
        skills = new HashMap<>();
        statusEffects = new ArrayList<>();
        damageSharing = new ArrayList<>();
    }

    public void setActiveHero(boolean activeHero)
    {
        isActiveHero = activeHero;
    }

    public void setTeam(Team teamToSet)
    {
        team = teamToSet;
    }
    public void setOpposingTeam(Team teamToSet)
    {
        opposingTeam = teamToSet;
    }

    public Artifact getArtifact()
    {
        return artifact;
    }

    public void setArtifact(Artifact artifact)
    {
        this.artifact = artifact;
        artifact.setEquippingHero(this);
    }

    public boolean IsDead()
    {
        return currentHealth <= 0.0;
    }
    public boolean deathEventAlreadyTriggered = false;
    public boolean IsActiveHero()
    {
        return isActiveHero;
    }

    public double getMaxHealth()
    {
        return maxHealth;
    }

    public double getLifesteal()
    {
        return lifesteal;
    }

    public void setLifesteal(double lifesteal)
    {
        this.lifesteal = lifesteal;
    }

    public void addCombatReadiness(double amount)
    {
        if (HasStatusEffect(StatusEffect.StatusEffectName.IMMUNITY) && amount < 0) {return;}
        if (!HasStatusEffect(StatusEffect.StatusEffectName.RESTRICT))
        {
            if (amount > 0 && HasStatusEffect(StatusEffect.StatusEffectName.STIGMA)) { amount /= 2; }
            combatReadiness = Math.max(0, combatReadiness + amount);
        }
    }

    public int getExtraTurns()
    {
        return extraTurns;
    }
    public void addExtraTurn()
    {
        extraTurns++;
    }
    public void subtractExtraTurn()
    {
        extraTurns--;
    }

    // subtract from current health
    public void takeDamage(double damage)
    {
        double remainingDamage = damage;
        if (damage >= shield) {
            remainingDamage -= shield;
            shield = 0;
            currentHealth -= remainingDamage;
            RemoveBarrier();
        }
        else { shield -= damage; }
    }

    private void RemoveBarrier()
    {
        StatusEffect toDelete = null;
        for (StatusEffect statusEffect : statusEffects)
        {
            if (statusEffect.getStatusEffectName() == StatusEffect.StatusEffectName.SHIELD)
            {
                shield = 0;
                toDelete = statusEffect;
            }
        }
        if (toDelete != null) { statusEffects.remove(toDelete); }
    }
    private void RemoveUniqueStatusEffect(StatusEffect.StatusEffectName statusEffectName)
    {
        StatusEffect toDelete = null;
        for (StatusEffect statusEffect : statusEffects)
        {
            if (statusEffect.getStatusEffectName() == statusEffectName)
            {
                toDelete = statusEffect;
            }
        }
        if (toDelete != null) { statusEffects.remove(toDelete); }
    }

    public void takeInjury(double amount)
    {
        maxHealth -= amount;
    }

    public boolean HasStatusEffect(StatusEffect.StatusEffectName statusEffect)
    {
        for (StatusEffect effect : statusEffects)
        {
            if (effect.getStatusEffectName() == statusEffect) { return true; }
        }
        return false;
    }

    private StatusEffect GetEffectFromList(StatusEffect.StatusEffectName statusEffectName)
    {
        for (StatusEffect effect : statusEffects)
        {
            if (effect.getStatusEffectName() == statusEffectName) { return effect; }
        }
        return null;
    }
    public void AddStatusEffect(StatusEffect statusEffect)
    {
        AddStatusEffect(statusEffect, false, true);
    }
    public void AddStatusEffect(StatusEffect statusEffect, boolean startOfTurn, boolean addedThisAttack)
    {
        if (IsDead()) {return;}
        if (isActiveHero) {statusEffect.setAddedThisTurn(!startOfTurn);}
        if (!statusEffect.IsUnique())
        {
            if (statusEffects.size() < 10)
            {
                statusEffects.add(statusEffect);
            }
        }
        else
        {
            StatusEffect existingEffect = GetEffectFromList(statusEffect.getStatusEffectName());
            if (existingEffect != null)
            {
                existingEffect.setRemainingDuration(Math.max(statusEffect.getRemainingDuration(), existingEffect.getRemainingDuration()));
                if (statusEffect.getStatusEffectName() == StatusEffect.StatusEffectName.SHIELD)
                {
                    double newShieldValue = Math.max(statusEffect.getShieldValue(), existingEffect.getShieldValue());
                    existingEffect.setShieldValue(newShieldValue);
                    shield = newShieldValue;
                }
            }
            else
            {
                if (statusEffects.size() < 10)
                {
                    statusEffects.add(statusEffect);
                    if (statusEffect.getStatusEffectName() == StatusEffect.StatusEffectName.SHIELD)
                    {
                        shield = statusEffect.getShieldValue();
                    } else if (statusEffect.getStatusEffectName() == StatusEffect.StatusEffectName.SKILL_NULLIFIER)
                    {
                        skillNullifier = 1;
                    }
                }
            }
        }
    }

    // get the current status of the hero
    public String toString()
    {
        StringBuilder status = new StringBuilder();
        status.append(name);
        status.append("\nHEALTH: " + currentHealth + "/" + maxHealth);
        status.append("\nCR: " + combatReadiness);
        status.append("\nBuffs/Debuffs: ");
        for (StatusEffect statusEffect : statusEffects)
        {
            status.append(statusEffect.getDisplayName() + ", ");
        }
        return status.toString();
    }

    public double getTimeToNextTurn()
    {
        if (combatReadiness >= 100.0)
        {
            return 0.0;
        }
        return (100.0 - combatReadiness) / speed;
    }

    public void advanceCR(double time)
    {
        combatReadiness += time * speed;
    }

    // Attack the target Hero with the given skill
    public void Attack(Hero target, SkillKeys skillKey, boolean isCounter, boolean isExtraAttack)
    {
        ClearAddedThisAttack();
        Skill skill = skills.get(skillKey);
        switch (skill.getSkillType())
        {
            case SINGLE -> SingleAttack(target, skillKey, isCounter, isExtraAttack);
            case AOE -> AoeAttack(target, skillKey, isCounter, isExtraAttack);
        }
        // trigger one-time events after attacking
        BattleManager.EventParams eventParams = new BattleManager.EventParams();
        eventParams.eventKey = BattleManager.EventKeys.ON_ATTACK_USED_ENEMY;
        eventParams.trigerringHero = this;
        eventParams.targetHero = target;
        eventParams.triggeringSkill = skill;
        eventParams.isCounter = isCounter;
        eventParams.isExtraAttack = isExtraAttack;
        opposingTeam.TriggerEvent(eventParams);

        eventParams.eventKey = BattleManager.EventKeys.ON_ATTACK_USED_ALLY;
        team.TriggerEvent(eventParams);

    }
    private void SingleAttack(Hero target, SkillKeys skillKey, boolean isCounter, boolean isExtraAttack)
    {
        Skill skill = skills.get(skillKey);

        // handle damage and injury
        ElementalMatchup elementalMatchup = CalculateElementalAdvantage(skillKey, target);
        Skill.HitTypes hitType = CalculateHitType(skillKey, target, elementalMatchup);

        target.GetDamageSharing();
        double rawDamage = CalculateDamage(skillKey, target, hitType, elementalMatchup, isCounter, isExtraAttack);
        double damageReduction = GetLargestDamageReduction(skillKey, target, rawDamage, hitType, isCounter, isExtraAttack);
        double damage = rawDamage * (1-damageReduction);
        double damageToShare = target.damageSharingValue * rawDamage;
        double damageToMain = damage - damageToShare;
        double damageToMainAfterShield = Math.max(damageToMain - shield, 0);
        double maxInjuryPercent = 0.0;
        maxInjuryPercent += skill.getInnateInjury();

        if (injurySet) { maxInjuryPercent += 0.12; }

        if (target.skillNullifier == 0)
        {
            double maxInjuryAmount = maxInjuryPercent * target.getMaxHealth();
            target.takeDamage(damageToMain);
            if (target.damageSharingTo != null)
            {
                target.damageSharingTo.takeDamage(damageToShare);
            }
            target.takeInjury(Math.min(damageToMainAfterShield, maxInjuryAmount));
        }
        else
        {
            target.skillNullifier -= 1;
            if (target.skillNullifier == 0)
            {
                target.RemoveUniqueStatusEffect(StatusEffect.StatusEffectName.SKILL_NULLIFIER);
            }
        }

        // damage message
        DisplayAttackInfo(damageToMain, skill, hitType, target);

        // trigger events caused by this attack
        BattleManager.EventParams eventParams = new BattleManager.EventParams();
        eventParams.trigerringHero = this;
        eventParams.targetHero = target;
        eventParams.triggeringSkill = skill;
        eventParams.isCounter = isCounter;
        eventParams.isExtraAttack = isExtraAttack;
        eventParams.damageDealt = damageToMain;
        eventParams.hitType = hitType;

        for (int i = 1; i <= 10; i++)
        {
            eventParams.priority = i;
            eventParams.eventKey = BattleManager.EventKeys.ON_ATTACK;
            team.TriggerEvent(eventParams);
            eventParams.eventKey = BattleManager.EventKeys.ON_ATTACKED;
            opposingTeam.TriggerEvent(eventParams);
            team.CheckForDeaths();
            opposingTeam.CheckForDeaths();
        }
    }

    private void AoeAttack(Hero target, SkillKeys skillKey, boolean isCounter, boolean isExtraAttack)
    {
        Skill skill = skills.get(skillKey);
        List<Hero> attackedHeroes = new ArrayList<>();
        Map<Hero, Double> damageDealt = new HashMap<>();
        Map<Hero, Skill.HitTypes> hitTypes = new HashMap<>();
        for (Hero hero : opposingTeam.heroes.values()) {
            if (hero.IsDead()) { continue; }
            attackedHeroes.add(hero);
            // handle damage and injury
            ElementalMatchup elementalMatchup = CalculateElementalAdvantage(skillKey, hero);
            Skill.HitTypes hitType = CalculateHitType(skillKey, hero, elementalMatchup);

            hero.GetDamageSharing();
            double rawDamage = CalculateDamage(skillKey, hero, hitType, elementalMatchup, isCounter, isExtraAttack);
            double damageReduction = GetLargestDamageReduction(skillKey, target, rawDamage, hitType, isCounter, isExtraAttack);
            double damage = rawDamage * (1-damageReduction);
            double damageToShare = hero.damageSharingValue * rawDamage;
            double damageToMain = damage - damageToShare;
            double damageToMainAfterShield = Math.max(damageToMain - shield, 0);
            double maxInjuryPercent = 0.0;
            maxInjuryPercent += skill.getInnateInjury();
            damageDealt.put(hero, damageToMain);
            hitTypes.put(hero, hitType);

            if (injurySet) {
                maxInjuryPercent += 0.06;
            }

            if (hero.skillNullifier == 0)
            {
                double maxInjuryAmount = maxInjuryPercent * hero.getMaxHealth();
                hero.takeDamage(damageToMain);
                if (hero.damageSharingTo != null)
                {
                    hero.damageSharingTo.takeDamage(damageToShare);
                }
                hero.takeInjury(Math.min(damageToMainAfterShield, maxInjuryAmount));
            }
            else
            {
                hero.skillNullifier -= 1;
                if (skillNullifier == 0)
                {
                    hero.RemoveUniqueStatusEffect(StatusEffect.StatusEffectName.SKILL_NULLIFIER);
                }
            }

            DisplayAttackInfo(damageToMain, skill, hitType, hero);
        }

        // trigger events caused by this attack
        BattleManager.EventParams eventParams = new BattleManager.EventParams();
        eventParams.trigerringHero = this;
        eventParams.triggeringSkill = skill;
        eventParams.isCounter = isCounter;
        eventParams.isExtraAttack = isExtraAttack;

        for (int i = 1; i <= 10; i++)
        {
            eventParams.priority = i;
            eventParams.eventKey = BattleManager.EventKeys.ON_ATTACK;
            for (Hero hero : attackedHeroes)
            {
                eventParams.targetHero = hero;
                eventParams.damageDealt = damageDealt.get(hero);
                eventParams.hitType = hitTypes.get(hero);
                team.TriggerEvent(eventParams);
            }

            eventParams.eventKey = BattleManager.EventKeys.ON_ATTACKED;
            for (Hero hero : attackedHeroes) {
                eventParams.targetHero = hero;
                eventParams.damageDealt = damageDealt.get(hero);
                eventParams.hitType = hitTypes.get(hero);
                opposingTeam.TriggerEvent(eventParams);
            }

            if (i == 1 && skill.canTriggerDual() && !isCounter && !isExtraAttack)
            {
                Hero dualAttacker = team.GetDualAttacker(eventParams.triggeringSkill);
                if (dualAttacker != null)
                {
                    dualAttacker.Attack(target, SkillKeys.SKILL1, false, true);
                }
            }
        }
    }
    public double GetCurrentHealthRatio()
    {
        return currentHealth / maxHealth;
    }
    private void DisplayAttackInfo(double damage, Skill skill, Skill.HitTypes hitType, Hero target)
    {
        StringBuilder message = new StringBuilder();
        message.append("\n");
        message.append(name);
        message.append(" used ");
        message.append(skill.getName());
        message.append(" [");
        message.append(hitType);
        message.append("] -> ");
        message.append(target.name);
        message.append(" -> ");
        message.append(Math.floor(damage));
        message.append(" damage");
        team.getBattleManager().displayMessage(message.toString());
        System.out.println(message);
    }

    public void UseNonAttackSkill(Hero target, SkillKeys skillKey)
    {
        BattleManager.EventParams eventParams = new BattleManager.EventParams();
        eventParams.targetHero = target;
        eventParams.triggeringSkill = skills.get(skillKey);
        for (int i = 1; i <= 10; i++)
        {
            eventParams.eventKey = BattleManager.EventKeys.NON_ATK_USED_ALLY;
            eventParams.priority = i;
            team.TriggerEvent(eventParams);
            eventParams.eventKey = BattleManager.EventKeys.NON_ATK_USED_ENEMY;
            opposingTeam.TriggerEvent(eventParams);
        }
    }
    private Skill.HitTypes CalculateHitType(SkillKeys skillKey, Hero target, ElementalMatchup elementalMatchup)
    {
        double hitChance = 100.0;
        if (elementalMatchup == ElementalMatchup.DISADVANTAGE) { hitChance -= 50; }
        hitChance += hitChanceBonus;
        if (artifact != null) { hitChance += artifact.getHitChanceBonus(); }
        hitChance -= target.evasion;

        double hitRoll = ThreadLocalRandom.current().nextDouble() * 100.0;
        if (hitChance < hitRoll)
        {
            return Skill.HitTypes.MISS;
        }

        // skills that cannot crit also cannot land Crushing Hits, so it must be a normal hit
        if (!skills.get(skillKey).CanCrit())
        {
            return  Skill.HitTypes.HIT;
        }

        double effectiveCrit = CalculateEffectiveCrit(skillKey, elementalMatchup);
        double critRes = target.CalculateCritRes();
        double critRoll = ThreadLocalRandom.current().nextDouble() * 100.0;

        if (effectiveCrit >= critRoll + critRes)
        {
            return Skill.HitTypes.CRIT;
        }

        double crushingRoll = ThreadLocalRandom.current().nextDouble() * 100.0;
        if (elementalMatchup == ElementalMatchup.ADVANTAGE && crushingRoll <= 80.0)
        {
            return Skill.HitTypes.CRUSH;
        }
        if (crushingRoll <= 30.0)
        {
            return Skill.HitTypes.CRUSH;
        }

        return Skill.HitTypes.HIT;
    }

    private double CalculateEffectiveCrit(SkillKeys skillKey, ElementalMatchup elementalMatchup)
    {
        double baseCrit = critChance;
        if (artifact != null)
        {
            baseCrit = Math.max(baseCrit + artifact.getCritChanceBonus(), 100.0);
        }
        if (elementalMatchup == ElementalMatchup.ADVANTAGE)
        {
            baseCrit += 15;
        }
        baseCrit += skills.get(skillKey).getCritChanceBonus();
        return baseCrit;
    }
    public double CalculateCritRes()
    {
        double critRes = 0;
        critRes += critResistance;
        if (HasStatusEffect(StatusEffect.StatusEffectName.CRIT_RES_UP))
        {
            critRes += 50.0;
        }
        if (artifact != null)
        {
            critRes += artifact.getCritResBonus();
        }
        return critRes;
    }
    public int getDebuffCount()
    {
        int count = 0;
        for (StatusEffect statusEffect : statusEffects)
        {
            if (statusEffect.getType() == StatusEffect.Type.Debuff) {count++;}
        }
        return count;
    }

    public int getBuffCount()
    {
        int count = 0;
        for (StatusEffect statusEffect : statusEffects)
        {
            if (statusEffect.getType() == StatusEffect.Type.Buff) {count++;}
        }
        return count;
    }

    private ElementalMatchup CalculateElementalAdvantage(SkillKeys skillKey, Hero target)
    {
        Skill skill = skills.get(skillKey);
        ElementalMatchup elementalMatchup = ElementalMatchup.NORMAL;
        if (skill.AlwaysAdvantage()) { return ElementalMatchup.ADVANTAGE; }
        switch (this.element)
        {
            case ICE:
                switch (target.element)
                {
                    case DARK:
                    case ICE:
                    case LIGHT:
                        elementalMatchup = ElementalMatchup.NORMAL;
                        break;
                    case FIRE:
                        elementalMatchup = ElementalMatchup.ADVANTAGE;
                        break;
                    case EARTH:
                        elementalMatchup = ElementalMatchup.DISADVANTAGE;
                        break;
                }
                break;
            case EARTH:
                switch (target.element)
                {
                    case DARK:
                    case EARTH:
                    case LIGHT:
                        elementalMatchup = ElementalMatchup.NORMAL;
                        break;
                    case ICE:
                        elementalMatchup = ElementalMatchup.ADVANTAGE;
                        break;
                    case FIRE:
                        elementalMatchup = ElementalMatchup.DISADVANTAGE;
                        break;
                }
                break;
            case FIRE:
                switch (target.element)
                {
                    case DARK:
                    case FIRE:
                    case LIGHT:
                        elementalMatchup = ElementalMatchup.NORMAL;
                        break;
                    case EARTH:
                        elementalMatchup = ElementalMatchup.ADVANTAGE;
                        break;
                    case ICE:
                        elementalMatchup = ElementalMatchup.DISADVANTAGE;
                        break;
                }
                break;
            case LIGHT:
                switch (target.element)
                {
                    case LIGHT:
                    case FIRE:
                    case EARTH:
                    case ICE:
                        elementalMatchup = ElementalMatchup.NORMAL;
                        break;
                    case DARK:
                        elementalMatchup = ElementalMatchup.ADVANTAGE;
                        break;
                }
                break;
            case DARK:
                switch (target.element)
                {
                    case DARK:
                    case FIRE:
                    case EARTH:
                    case ICE:
                        elementalMatchup = ElementalMatchup.NORMAL;
                        break;
                    case LIGHT:
                        elementalMatchup = ElementalMatchup.ADVANTAGE;
                        break;
                }
                break;
        }
        if (skill.IgnoreDisadvantage() && elementalMatchup == ElementalMatchup.DISADVANTAGE) {elementalMatchup = ElementalMatchup.NORMAL;}
        return elementalMatchup;
    }

    public void UseSkill(Hero target, SkillKeys skillKey, boolean soulburn, boolean isCounter, boolean isExtraAttack)
    {
       if (!isCounter && !isExtraAttack && !soulburn) { skills.get(skillKey).resetCooldown(); }
       if (soulburn)
       {
           skills.get(soulburnSkill).resetCooldown();
       }
    }
    private void DecrementCooldowns()
    {
        for (Skill skill : skills.values())
        {
            skill.decrementCooldown();
        }
    }

    protected void IncrementCooldowns()
    {
        for (Skill skill : skills.values())
        {
            skill.incrementCooldown();
        }
    }

    protected void HandleEvent(BattleManager.EventParams eventParams)
    {
        switch (eventParams.eventKey)
        {
            case TURN_START:
                if (isActiveHero)
                {
                    combatReadiness = 0.0;
                }
                break;
            case TURN_END:
                if (isActiveHero) {
                    decrementStatusEffects();
                    DecrementCooldowns();
                }
                alreadyCountered = false;
                if (IsDead() && HasStatusEffect(StatusEffect.StatusEffectName.REVIVE))
                {
                    RemoveBuffs(10);
                    RemoveDebuffs(10);
                    currentHealth = maxHealth * 0.2;
                    deathEventAlreadyTriggered = false;
                }
                ClearAddedThisAttack();
                break;
            case NON_ATK_USED_ALLY:
                if (isActiveHero && eventParams.priority == 1)
                {
                    team.addSouls(eventParams.triggeringSkill.getSoulGain());
                }
                break;
            case ON_ATTACK:
                if (isActiveHero && eventParams.priority == 1)
                {
                    double lifestealAmount = lifesteal * eventParams.damageDealt;
                    restoreHealth(lifestealAmount);
                    if (!alreadyAddedSouls)
                    {
                        team.addSouls(eventParams.triggeringSkill.getSoulGain());
                        alreadyAddedSouls = true;
                    }
                }
                if (eventParams.priority == 2 && alreadyAddedSouls)
                {
                    alreadyAddedSouls = false;
                }
                break;
            case ON_ATTACKED:
                switch (eventParams.priority)
                {
                    case 2:
                        if (this == eventParams.trigerringHero && !alreadyCountered && BattleManager.TryRng(GetCounterChance()))
                        {
                            UseSkill(this, SkillKeys.SKILL1,false, true, false);
                            alreadyCountered = true;
                        }
                }
                break;
        }
        if (artifact != null)
        {
            artifact.HandleEvent(eventParams);
        }
        if (eventParams.eventKey == BattleManager.EventKeys.ON_DEATH) {deathEventAlreadyTriggered = true;}
    }
    protected void restoreHealth(double amount)
    {
        if (!IsDead() && !HasStatusEffect(StatusEffect.StatusEffectName.UNHEALABLE))
        {
            if (HasStatusEffect(StatusEffect.StatusEffectName.STIGMA)) {amount /= 2;}
            currentHealth = Math.min(currentHealth + amount, maxHealth);
        }
    }
    public void RemoveDebuffs(int amount)
    {
        int toRemoveCount = amount;
        ArrayList<StatusEffect> toRemove = new ArrayList<>();
        for (StatusEffect statusEffect : statusEffects)
        {
            if (statusEffect.getType() == StatusEffect.Type.Debuff)
            {
                toRemove.add(statusEffect);
                toRemoveCount--;
                if (toRemoveCount == 0) {break;}
            }
        }
        for (StatusEffect statusEffect : toRemove)
        {
            if (statusEffect.getStatusEffectName()== StatusEffect.StatusEffectName.PROVOKE) {provokeTarget = null;}
            statusEffects.remove(statusEffect);
        }
    }

    public void RemoveBuffs(int amount)
    {
        int toRemoveCount = amount;
        ArrayList<StatusEffect> toRemove = new ArrayList<>();
        for (StatusEffect statusEffect : statusEffects)
        {
            if (statusEffect.getType() == StatusEffect.Type.Buff)
            {
                toRemove.add(statusEffect);
                toRemoveCount--;
                if (toRemoveCount == 0) {break;}
            }
        }
        for (StatusEffect statusEffect : toRemove)
        {
            statusEffects.remove(statusEffect);
        }
    }
    protected double GetCounterChance()
    {
        if (counterSet)
        {
            return 30;
        }
        else
        {
            return 0;
        }
    }

    public void TryToDebuff(Hero target, double effectChance, StatusEffect.StatusEffectName debuff, int duration, boolean ignoreEr)
    {
        if (target.HasStatusEffect(StatusEffect.StatusEffectName.IMMUNITY)) {return;}
        double rngRoll = ThreadLocalRandom.current().nextDouble() * 100.0;
        if (rngRoll <= effectChance)
        {
            double chanceToResist = Math.max(15, target.effectResistance - this.effectiveness);
            double resistRoll = ThreadLocalRandom.current().nextDouble() * 100.0;
            if (ignoreEr || resistRoll > chanceToResist)
            {
                String message = name + " successfully applied " + debuff + " to " + target.name;
                System.out.println(message);
                StatusEffect effect = new StatusEffect(debuff, duration);
                target.AddStatusEffect(effect);
                if (debuff == StatusEffect.StatusEffectName.PROVOKE && target.HasStatusEffect(StatusEffect.StatusEffectName.PROVOKE))
                {
                    target.provokeTarget=this;
                }
            }
        }
    }

    // Returns true if the target resisted
    public boolean ResistCheck(Hero target)
    {
        double chanceToResist = Math.max(15, target.effectResistance - this.effectiveness);
        double resistRoll = ThreadLocalRandom.current().nextDouble() * 100.0;
        return resistRoll <= chanceToResist;
    }
    protected void displayAvailableSkills()
    {
        StringBuilder message = new StringBuilder();
        message.append("\nThe following skills are available: ");
        for (SkillKeys skillKey : new SkillKeys[]{SkillKeys.SKILL1, SkillKeys.SKILL2, SkillKeys.SKILL3})
        {
            if (!skills.containsKey(skillKey)) { continue; }
            Skill skill = skills.get(skillKey);
            if (skill.IsAvailable() && !skill.getProcOnly())
            {
                message.append(skill.getName());
                switch (skillKey)
                {
                    case SKILL1:
                        message.append(" (1), ");
                        break;
                    case SKILL2:
                        message.append(" (2), ");
                        break;
                    case SKILL3:
                        message.append(" (3)");
                        break;
                }
            }
        }
        System.out.println(message);
    }
    public void decrementStatusEffects(boolean buffsOnly, boolean debuffsOnly)
    {
        ArrayList<StatusEffect> toRemove = new ArrayList<>();
        for (StatusEffect statusEffect : statusEffects)
        {
            if (debuffsOnly && statusEffect.getType() != StatusEffect.Type.Debuff) { continue; }
            if (buffsOnly && statusEffect.getType() != StatusEffect.Type.Buff) { continue; }
            if (!statusEffect.addedThisTurn())
            {
                statusEffect.reduceDuration(1);
            } else
            {
                statusEffect.setAddedThisTurn(false);
            }
            if (statusEffect.IsExpired()){ toRemove.add(statusEffect); }
        }
        for (StatusEffect statusEffect : toRemove)
        {
            if (statusEffect.getStatusEffectName()== StatusEffect.StatusEffectName.PROVOKE) {provokeTarget = null;}
            statusEffects.remove(statusEffect);
        }
    }
    public void decrementStatusEffects()
    {
        decrementStatusEffects(false, false);
    }
    private void ClearAddedThisAttack()
    {
        for (StatusEffect statusEffect : statusEffects)
        {
            statusEffect.setAddedThisAttack(false);
        }
    }

    private double CalculateDamage(SkillKeys skillKey, Hero target, Skill.HitTypes hitType, ElementalMatchup elementalMatchup, boolean isCounter, boolean isExtraAttack)
    {
        Skill skill = skills.get(skillKey);
        double elemAdvMod = 1.0;
        if (elementalMatchup == ElementalMatchup.ADVANTAGE) { elemAdvMod = 1.1; }
        double flatDmg = skill.getTargetMaxHpRate() * target.maxHealth;
        double flatDmg2 = 0;
        double damageMod = GetDamageMod(skillKey, target, hitType);
        double defPen = GetEffectivePen(skill, target);
        double defensePower = (1) / ((target.defense * target.GetDefenseMod() * (1-defPen)) / 300 + 1);
        double damage = (DAMAGE_MULTIPLIER * (attack * GetAttackMod() * skill.getAttRate() + maxHealth *
                skill.getHpRate() + defense * skill.getDefRate() + skill.getSpeedRate() * GetEffectiveSpeed() +
                skill.getTargetSpeedRate() * target.GetEffectiveSpeed() + flatDmg) + flatDmg2) * (skill.getEnhancementMulti()
                + skill.getPow()) * GetHitTypeMod(hitType) * elemAdvMod * damageMod * defensePower;
        return damage;
    }
    protected double GetDamageMod(SkillKeys skillKey, Hero target, Skill.HitTypes hitType)
    {
        return 1.0 + artifact.getDamageBonus();
    }

    private double GetHitTypeMod(Skill.HitTypes hitType)
    {
        double hitMod = 1.0;
        switch (hitType)
        {
            case MISS -> hitMod = 0.75;
            case CRIT -> hitMod = GetCritDamageMod();
            case CRUSH -> hitMod = 1.3;
            case HIT -> hitMod = 1.0;
        }
        return hitMod;
    }
    private double GetCritDamageMod()
    {
        double effectiveCritDamage = critDamage;
        if (HasStatusEffect(StatusEffect.StatusEffectName.CRIT_DMG_UP))
        {
            effectiveCritDamage = Math.max(350.0, effectiveCritDamage + 50.0);
        }
        return effectiveCritDamage / 100.0;
    }

    private double GetDefenseMod()
    {
        double defenseMod = 1.0;
        defenseMod += skillDefenseMod;
        defenseMod += team.getTeamDefMod();
        if (HasStatusEffect(StatusEffect.StatusEffectName.DEF_DOWN))
        {
            defenseMod -= 0.7;
        }
        if (HasStatusEffect(StatusEffect.StatusEffectName.DEF_UP))
        {
            defenseMod += 0.6;
        }
        return defenseMod;
    }
    private void GetDamageSharing()
    {
        double maxValue = 0.0;
        Hero damageShareTo = null;
        for (DamageShare damageShare : damageSharing)
        {
            if (damageShare.value > maxValue)
            {
                maxValue = damageShare.value;
                damageShareTo = damageShare.hero;
            }
        }
        this.damageSharingTo = damageShareTo;
        this.damageSharingValue = maxValue;
    }

    public void AddDamageSharing(DamageShare damageShare)
    {
        damageSharing.add(damageShare);
    }

    public void ClearDamageSharing(Hero hero)
    {
        List<DamageShare> toRemove = new ArrayList<>();
        for (DamageShare damageShare : damageSharing)
        {
            if (damageShare.hero == hero)
            {
                toRemove.add(damageShare);
            }
        }

        for (DamageShare damageShare : toRemove)
        {
            damageSharing.remove(damageShare);
        }
    }
    protected boolean IsBuffed()
    {
        return getBuffCount() >= 1;
    }
    private double GetLargestDamageReduction(SkillKeys skillKey, Hero target, double damage, Skill.HitTypes hitType, boolean isCounter, boolean isExtraAttack)
    {
        double damageReduction = 0;
        damageReduction = Math.max(damageReduction, target.getArtifact().getDamageReduction(skillKey, target, damage, hitType, isCounter, isExtraAttack));
        damageReduction = Math.max(damageReduction, target.team.GetLargestDamageReduction(skillKey, this, target, damage, hitType, isCounter, isExtraAttack));
        damageReduction = Math.max(damageReduction, target.damageReduction);
        return damageReduction;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    protected double GetEffectivePen(Skill skill, Hero target)
    {
        double effectiveDef = 1.0;
        if (penSet && skill.getSkillType() == Skill.SkillTypes.SINGLE)
        {
            effectiveDef *= 0.85;
        }
        double skillPen = skill.getInnatePen(target);
        if (skillPen > 0) { effectiveDef *= (1 - skillPen); }
        return (1 - effectiveDef);
    }
    private double GetAttackMod()
    {
        double attackMod = 1.0;
        attackMod += skillAttackMod;
        attackMod += team.getTeamAtkMod();
        attackMod += artifact.getAttackMod();
        if (HasStatusEffect(StatusEffect.StatusEffectName.ATK_DOWN))
        {
            attackMod -= 0.5;
        }
        if (HasStatusEffect(StatusEffect.StatusEffectName.ATK_UP))
        {
            attackMod += 0.5;
        } else if (HasStatusEffect(StatusEffect.StatusEffectName.ATK_UP_GREATER))
        {
            attackMod += 0.75;
        }
        return attackMod;
    }

    public double GetDualAttackChance()
    {
        double chance = dualAttackChance;
        chance += unitySetCount * 8.0;

        return chance;
    }
    public double GetEffectiveAttack()
    {
        return attack * GetAttackMod();
    }

    private double GetEffectiveSpeed()
    {
        double speedMod = 1.0;
        speedMod += team.getTeamSpeedMod();
        speedMod += skillSpeedMod;
        if (HasStatusEffect(StatusEffect.StatusEffectName.SPEED_DOWN))
        {
            speedMod -= 0.3;
        }
        if (HasStatusEffect(StatusEffect.StatusEffectName.SPEED_UP))
        {
            speedMod += 0.3;
        }
        return speedMod * speed;
    }
}
