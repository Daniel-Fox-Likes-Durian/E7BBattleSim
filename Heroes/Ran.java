public class Ran extends Hero
{
    private int s3Stacks;
    private boolean skill2Proc = false;
    public Ran()
    {
        super();
        name = "Ran";
        imagePath = "Images/Ran-icon.png";
        element = Element.ICE;
        maxHealth = 9000;
        originalMaxHealth = maxHealth;
        defense = 1000;
        attack = 3000;
        currentHealth = maxHealth;
        speed = 300;
        critChance = 85.0;
        critDamage = 250;
        soulCost = 20;
        soulburnSkill = SkillKeys.SKILL3;
        BuildSkills();
    }

    @Override
    public void UseSkill(Hero target, SkillKeys skillKey, boolean soulburn, boolean isCounter, boolean isExtraAttack) {
        super.UseSkill(target, skillKey, soulburn, isCounter, isExtraAttack);
        switch (skillKey)
        {
            case SKILL1:
            case SOULBURN:
            case SKILL3:
                Attack(target, skillKey, isCounter, isExtraAttack);
                break;
            case SKILL2:
                AddStatusEffect(new StatusEffect(StatusEffect.StatusEffectName.ATK_UP, 2));
                team.AddStatusEffect(StatusEffect.StatusEffectName.IMMUNITY, 2);
                UseNonAttackSkill(target, skillKey);
                addExtraTurn();
                break;
        }
    }

    protected void HandleEvent(BattleManager.EventParams eventParams)
    {
        switch (eventParams.eventKey)
        {
            case ON_ATTACK:
                if (this == eventParams.trigerringHero &&
                        (eventParams.triggeringSkill == skills.get(SkillKeys.SKILL3)
                        || eventParams.triggeringSkill == skills.get(SkillKeys.SOULBURN)))
                {
                    boolean soulburn = eventParams.triggeringSkill == this.skills.get(SkillKeys.SOULBURN);
                    switch (eventParams.priority)
                    {
                        case 1:
                            if (eventParams.hitType != Skill.HitTypes.MISS && !ResistCheck(eventParams.targetHero))
                            {
                                eventParams.targetHero.RemoveBuffs(2);
                            }
                            if (eventParams.hitType != Skill.HitTypes.MISS)
                            {
                                TryToDebuff(eventParams.targetHero, 100, StatusEffect.StatusEffectName.STIGMA, 2, soulburn);
                                TryToDebuff(eventParams.targetHero, 85, StatusEffect.StatusEffectName.DEF_DOWN, 2, soulburn);
                            }
                            AddStatusEffect(new StatusEffect(StatusEffect.StatusEffectName.SKILL_NULLIFIER, Integer.MAX_VALUE));
                            break;
                    }
                }
        }
        super.HandleEvent(eventParams);
    }

    private void BuildSkills()
    {

        Skill skill1 = new Skill();
        skill1.setName("Tempest");
        skill1.setAttRate(0.9);
        skill1.setPow(0.9);
        skill1.setSpeedRate(0.075);
        skill1.setEnhancementMulti(0.4);
        skill1.setInnatePen(0.2);
        skill1.setSkillType(Skill.SkillTypes.SINGLE);
        skill1.setCanTriggerDual(true);
        skills.put(SkillKeys.SKILL1, skill1);

        Skill skill2 = new Skill();
        skill2.setName("Mental Focus");
        skill2.setMaxCooldown(4);
        skill2.setSkillType(Skill.SkillTypes.NON_ATK);
        skill2.setSoulGain(2);
        skill2.setTargetType(Skill.TargetTypes.ALLY);
        skills.put(SkillKeys.SKILL2, skill2);

        Skill skill3 = new Skill();
        skill3.setName("Instant Blade");
        skill3.setMaxCooldown(4);
        skill3.setAttRate(0.8);
        skill3.setSpeedRate(0.075);
        skill3.setTargetSpeedRate(0.15);
        skill3.setSkillType(Skill.SkillTypes.AOE);
        skill3.setSoulGain(2);
        skills.put(SkillKeys.SKILL3, skill3);

        Skill soulburn = new Skill();
        soulburn.setName("Instant Blade (Soulburn)");
        soulburn.setAttRate(0.8);
        soulburn.setSpeedRate(0.075);
        soulburn.setTargetSpeedRate(0.15);
        soulburn.setSkillType(Skill.SkillTypes.AOE);
        soulburn.setSoulGain(2);
        skills.put(SkillKeys.SOULBURN, soulburn);

    }
}
