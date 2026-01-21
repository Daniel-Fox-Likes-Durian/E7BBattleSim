public class Kise extends Hero
{
    public Kise()
    {
        super();
        name = "Kise";
        imagePath = "Images/kise.png";
        element = Element.ICE;
        attack = 4500;
        maxHealth = 9000;
        originalMaxHealth = maxHealth;
        defense = 900;
        currentHealth = maxHealth;
        speed = 220;
        critChance = 100;
        critDamage = 320;
        exclusiveEquipment = 3;
        penSet = true;
        BuildSkills();
    }

    @Override
    public void UseSkill(Hero target, SkillKeys skillKey, boolean soulburn, boolean isCounter, boolean isExtraAttack)
    {
        super.UseSkill(target, skillKey, soulburn, isCounter, isExtraAttack);
        Attack(target, skillKey, isCounter, isExtraAttack);
    }

    protected void HandleEvent(BattleManager.EventParams eventParams)
    {
        switch (eventParams.eventKey)
        {
            case ON_ATTACK:
                if (eventParams.priority == 1 && eventParams.triggeringSkill == skills.get(SkillKeys.SKILL1) && exclusiveEquipment == 1)
                {
                    addCombatReadiness(15);
                } else if (eventParams.priority == 1 && eventParams.triggeringSkill == skills.get(SkillKeys.SKILL3))
                {
                    if (eventParams.hitType != Skill.HitTypes.MISS)
                    {
                        if (!ResistCheck(eventParams.targetHero) && exclusiveEquipment == 3)
                        {
                            eventParams.targetHero.decrementStatusEffects(true, false);
                        }
                        if (!ResistCheck(eventParams.targetHero)) { eventParams.targetHero.IncrementCooldowns(); }
                        if (!ResistCheck(eventParams.targetHero)) { eventParams.targetHero.IncrementCooldowns(); }
                    }
                    StatusEffect shield = new StatusEffect(StatusEffect.StatusEffectName.SHIELD, 2);
                    shield.setShieldValue(0.65 * attack);
                    AddStatusEffect(shield);
                    AddStatusEffect(new StatusEffect(StatusEffect.StatusEffectName.STEALTH, 2));
                    addCombatReadiness(50);
                }
                break;
        }
        super.HandleEvent(eventParams);
    }
    @Override
    protected double GetDamageMod(SkillKeys skillKey, Hero target, Skill.HitTypes hitType)
    {
        double damageMod = super.GetDamageMod(skillKey, target, hitType);
        switch (skillKey)
        {
            case SKILL1 -> {if (target.IsBuffed()) damageMod += 0.7;}
            case SOULBURN -> {if (target.IsBuffed()) damageMod += 1.0;}
            case SKILL3 -> damageMod += 0.35 * (currentHealth / maxHealth);
        }
        return damageMod;
    }
    @Override
    protected double GetEffectivePen(Skill skill, Hero target)
    {
        double effectiveDef = 1 - super.GetEffectivePen(skill, target);
        if (skill == skills.get(SkillKeys.SKILL2))
        {
            if (HasStatusEffect(StatusEffect.StatusEffectName.STEALTH))
            {
                effectiveDef *= 0.4;
            } else
            {
                effectiveDef *= 0.7;
            }
        }
        return 1 - effectiveDef;
    }

    private void BuildSkills()
    {

        Skill skill1 = new Skill();
        skill1.setName("Full Moon Scythe");
        skill1.setAttRate(1.1);
        skill1.setSkillType(Skill.SkillTypes.SINGLE);
        skill1.setCanTriggerDual(true);
        skills.put(SkillKeys.SKILL1, skill1);

        Skill soulburn = new Skill();
        soulburn.setName("Full Moon Scythe (soulburn)");
        soulburn.setAttRate(1.4);
        soulburn.setSkillType(Skill.SkillTypes.SINGLE);
        soulburn.setCanTriggerDual(true);
        skills.put(SkillKeys.SOULBURN, soulburn);

        Skill skill2 = new Skill();
        skill2.setName("Dark Scar");
        skill2.setMaxCooldown(3);
        skill2.setAttRate(0.9);
        skill2.setSkillType(Skill.SkillTypes.AOE);
        skill2.setSoulGain(2);
        skills.put(SkillKeys.SKILL2, skill2);

        Skill skill3 = new Skill();
        skill3.setName("Nocturne");
        skill3.setMaxCooldown(4);
        skill3.setAttRate(1.6);
        skill3.setSkillType(Skill.SkillTypes.SINGLE);
        skill3.setSoulGain(3);
        skills.put(SkillKeys.SKILL3, skill3);

    }
}
