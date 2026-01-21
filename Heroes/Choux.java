import java.util.concurrent.ThreadLocalRandom;

public class Choux extends Hero
{
    private int currentFocus = 0;
    public Choux()
    {
        super();
        name = "Choux";
        imagePath = "Images/Choux.png";
        element = Element.ICE;
        maxHealth = 26000;
        attack = 1700;
        originalMaxHealth = maxHealth;
        defense = 1300;
        critChance = 100;
        critDamage = 270;
        currentHealth = maxHealth;
        speed = 190;
        counterSet = true;
        soulCost = 20;
        BuildSkills();
    }

    @Override
    public void UseSkill(Hero target, SkillKeys skillKey, boolean soulburn, boolean isCounter, boolean isExtraAttack)
    {
        super.UseSkill(target, skillKey, soulburn, isCounter, isExtraAttack);
        switch (skillKey)
        {
            case SKILL1:
            case SOULBURN:
                Attack(target, skillKey, isCounter, isExtraAttack);
                if (currentFocus < 5) {currentFocus += 1;}
                break;
            case SKILL2:
                if (currentFocus == 5)
                {
                    Attack(target, SkillKeys.SKILL2_ALT, isCounter, isExtraAttack);
                    currentFocus = 0;
                    skills.get(skillKey).setCurrentCooldown(0);
                }
                else Attack(target, skillKey, isCounter, isExtraAttack);
                break;
            case SKILL3:
                Attack(target, skillKey, isCounter, isExtraAttack);
                currentFocus = Math.min(currentFocus + 3, 5);
                break;
        }
    }

    protected void HandleEvent(BattleManager.EventParams eventParams)
    {
        switch (eventParams.eventKey)
        {
            case ON_ATTACK:
                if (this == eventParams.trigerringHero && (eventParams.triggeringSkill == skills.get(SkillKeys.SKILL2) || eventParams.triggeringSkill == skills.get(SkillKeys.SKILL2_ALT)))
                {
                    switch (eventParams.priority)
                    {
                        case 1:
                            restoreHealth(0.25 * eventParams.damageDealt);
                            break;
                    }
                }
                if ((this == eventParams.trigerringHero)
                        && (eventParams.triggeringSkill == skills.get(SkillKeys.SKILL1))
                        || eventParams.triggeringSkill == skills.get(SkillKeys.SOULBURN))
                {
                    switch (eventParams.priority)
                    {
                        case 1:
                            if (eventParams.triggeringSkill == skills.get(SkillKeys.SOULBURN))
                            {
                                addExtraTurn();
                            }
                            break;
                        case 6:
                            addCombatReadiness(20);
                            if ((eventParams.hitType == Skill.HitTypes.CRIT) && (exclusiveEquipment == 1))
                            {
                                double fwoooshRoll = ThreadLocalRandom.current().nextDouble() * 100.0;
                                if (fwoooshRoll <= 40.0)
                                {
                                    UseSkill(eventParams.targetHero, SkillKeys.SKILL2, false, false, true);
                                }
                            }
                            break;
                    }
                }
                if ((this == eventParams.trigerringHero) && (eventParams.triggeringSkill == skills.get(SkillKeys.SKILL3)))
                {
                    switch (eventParams.priority)
                    {
                        case 1:
                            team.AddStatusEffect(StatusEffect.StatusEffectName.CRIT_RES_UP, 3);
                            break;
                    }
                }
                break;
            case ON_ATTACKED:
                if (eventParams.priority == 2 && (this != eventParams.targetHero) && (eventParams.triggeringSkill.CanBeCountered()) && (skills.get(SkillKeys.SKILL3).getCurrentCooldown() > 0) && !alreadyCountered && !eventParams.isCounter && !eventParams.isExtraAttack)
                {
                    double counterRoll = ThreadLocalRandom.current().nextDouble() * 100.0;
                    if (counterRoll <= 20.0)
                    {
                        UseSkill(eventParams.trigerringHero, SkillKeys.SKILL1, false, true, false);
                        alreadyCountered = true;
                    }
                }
                break;
        }
        super.HandleEvent(eventParams);
    }

    private void BuildSkills()
    {

        Skill skill1 = new Skill();
        skill1.setName("Chop");
        skill1.setAttRate(0.5);
        skill1.setHpRate(0.1);
        skill1.setSkillType(Skill.SkillTypes.SINGLE);
        skill1.setCanTriggerDual(true);
        skills.put(SkillKeys.SKILL1, skill1);

        Skill soulburn = new Skill();
        soulburn.setName("Chop (Soulburn)");
        soulburn.setAttRate(0.5);
        soulburn.setHpRate(0.1);
        soulburn.setSkillType(Skill.SkillTypes.SINGLE);
        soulburn.setCanTriggerDual(true);
        skills.put(SkillKeys.SOULBURN, soulburn);

        Skill skill2 = new Skill();
        skill2.setName("Fwoooosh!");
        skill2.setMaxCooldown(4);
        skill2.setAttRate(0.5);
        skill2.setHpRate(0.11);
        skill2.setInnatePen(0.7);
        skill2.setSkillType(Skill.SkillTypes.SINGLE);
        skills.put(SkillKeys.SKILL2, skill2);

        Skill skill2Alt = new Skill();
        skill2Alt.setName("Fwoooosh! (5 focus)");
        skill2Alt.setAttRate(0.5);
        skill2Alt.setHpRate(0.15);
        skill2Alt.setInnatePen(0.7);
        skill2Alt.setSkillType(Skill.SkillTypes.SINGLE);
        skill2Alt.setProcOnly(true);
        skills.put(SkillKeys.SKILL2_ALT, skill2Alt);

        Skill skill3 = new Skill();
        skill3.setName("Help Me, Cream!");
        skill3.setMaxCooldown(4);
        skill3.setAttRate(0.5);
        skill3.setHpRate(0.15);
        skill3.setSkillType(Skill.SkillTypes.AOE);
        skill3.setSoulGain(3);
        skills.put(SkillKeys.SKILL3, skill3);

    }
}
