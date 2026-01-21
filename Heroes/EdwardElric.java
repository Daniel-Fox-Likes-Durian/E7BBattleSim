import java.util.concurrent.ThreadLocalRandom;

public class EdwardElric extends  Hero
{
    private int s3Stacks;
    private boolean skill2Proc = false;
    public EdwardElric()
    {
        super();
        name = "Edward Elric";
        imagePath = "Images/edward-elric-icon.jpg";
        element = Element.FIRE;
        maxHealth = 20000;
        attack = 1800;
        originalMaxHealth = maxHealth;
        defense = 1400;
        currentHealth = maxHealth;
        speed = 200;
        critChance = 100;
        critDamage = 250;
        soulburnSkill = SkillKeys.SKILL3;
        s3Stacks = 0;
        BuildSkills();
    }

    @Override
    public void UseSkill(Hero target, SkillKeys skillKey, boolean soulburn, boolean isCounter, boolean isExtraAttack) {
        super.UseSkill(target, skillKey, soulburn, isCounter, isExtraAttack);
        Attack(target, skillKey, isCounter, isExtraAttack);
        if (skillKey == SkillKeys.SOULBURN) {skills.get(SkillKeys.SKILL3).reduceCooldown(2);}
    }

    protected void HandleEvent(BattleManager.EventParams eventParams)
    {
        switch (eventParams.eventKey)
        {
            case ON_ATTACK:
                switch (eventParams.priority)
                {
                    case 1:
                        if (this == eventParams.trigerringHero && eventParams.triggeringSkill == skills.get(SkillKeys.SKILL1))
                        {
                            StatusEffect shield = new StatusEffect(StatusEffect.StatusEffectName.SHIELD, 2);
                            shield.setShieldValue(0.08 * maxHealth);
                            AddStatusEffect(shield);
                        }
                        if (this == eventParams.trigerringHero && eventParams.triggeringSkill == skills.get(SkillKeys.SKILL2))
                        {
                            if (!ResistCheck(eventParams.targetHero)) {eventParams.targetHero.RemoveBuffs(2);}
                            TryToDebuff(eventParams.targetHero, 100, Skill2RandomDebuff(),2, false);
                            addCombatReadiness(20);
                        }
                        if (this == eventParams.trigerringHero
                                && (eventParams.triggeringSkill == skills.get(SkillKeys.SKILL3)
                                || eventParams.triggeringSkill == skills.get(SkillKeys.SOULBURN)))
                        {
                            s3Stacks = Math.min(s3Stacks + 1, 3);
                            addCombatReadiness(50);
                        }
                        break;
                }
                break;
            case ON_ATTACKED:
                switch (eventParams.priority)
                {
                    case 1:
                        if (this == eventParams.targetHero && !HasStatusEffect(StatusEffect.StatusEffectName.SEAL))
                        {
                            if (skills.get(SkillKeys.SKILL2).IsAvailable() && getDebuffCount() > 0)
                            {
                                skill2Proc = true;
                            }
                            RemoveDebuffs(1);
                        }
                        break;
                    case 2:
                        if (skill2Proc)
                        {
                            UseSkill(eventParams.trigerringHero, SkillKeys.SKILL2, false, false, true);
                            skills.get(SkillKeys.SKILL2).resetCooldown();
                            skill2Proc = false;
                        }
                        break;
                }
        }
        super.HandleEvent(eventParams);
    }

    @Override
    protected double GetDamageMod(SkillKeys skillKey, Hero target, Skill.HitTypes hitType)
    {
        double dmgBonus = super.GetDamageMod(skillKey, target, hitType);
        if (skillKey == SkillKeys.SKILL3)
        {
            dmgBonus += (0.2 * s3Stacks);
        }
        return dmgBonus;
    }

    private void BuildSkills()
    {

        Skill skill1 = new Skill();
        skill1.setName("I'll Take You On!");
        skill1.setAttRate(1.0);
        skill1.setHpRate(0.1);
        skill1.setSkillType(Skill.SkillTypes.SINGLE);
        skill1.setCanTriggerDual(true);
        skills.put(SkillKeys.SKILL1, skill1);

        Skill skill2 = new Skill();
        skill2.setName("Rise!");
        skill2.setAttRate(0.7);
        skill2.setHpRate(0.08);
        skill2.setMaxCooldown(1);
        skill2.setSkillType(Skill.SkillTypes.AOE);
        skill2.setProcOnly(true);
        skill2.setSoulGain(0);
        skills.put(SkillKeys.SKILL2, skill2);

        Skill skill3 = new Skill();
        skill3.setName("I'll Show You Our Difference");
        skill3.setMaxCooldown(4);
        skill3.setAttRate(1.2);
        skill3.setHpRate(0.25);
        skill3.setSkillType(Skill.SkillTypes.SINGLE);
        skill3.setSoulGain(3);
        skills.put(SkillKeys.SKILL3, skill3);

        Skill soulburn = new Skill();
        soulburn.setName("I'll Show You Our Difference");
        soulburn.setAttRate(1.2);
        soulburn.setHpRate(0.25);
        soulburn.setSkillType(Skill.SkillTypes.SINGLE);
        soulburn.setSoulGain(3);
        skills.put(SkillKeys.SOULBURN, soulburn);

    }

    private StatusEffect.StatusEffectName Skill2RandomDebuff()
    {
        double debuffRoll = ThreadLocalRandom.current().nextDouble() * 100.0;
        StatusEffect.StatusEffectName effectName;
        if (debuffRoll <= 20.0) {effectName = StatusEffect.StatusEffectName.BLIND;}
        else if (debuffRoll <= 40.0) {effectName = StatusEffect.StatusEffectName.ATK_DOWN;}
        else if (debuffRoll <= 60.0) {effectName = StatusEffect.StatusEffectName.PROVOKE;}
        else if (debuffRoll <= 80.0) {effectName = StatusEffect.StatusEffectName.SILENCE;}
        else {effectName = StatusEffect.StatusEffectName.RESTRICT;}

        return effectName;
    }
}
