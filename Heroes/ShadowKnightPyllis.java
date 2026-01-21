public class ShadowKnightPyllis extends Hero
{
    private int s2Stacks;
    private int defBuffStacks;
    private DamageReduction s2Reduction;
    public ShadowKnightPyllis()
    {
        super();
        name = "Shadow Knight Pyllis";
        imagePath = "Images/Shadow-Knight-Pyllis-icon.png";
        element = Element.DARK;
        maxHealth = 25000;
        originalMaxHealth = maxHealth;
        defense = 2000;
        currentHealth = maxHealth;
        speed = 150;
        attack = 1300;
        s2Stacks = 0;
        critChance = 15;
        critDamage = 150;
        s2Reduction = new DamageReduction(DamageReduction.DamageReductionCondition.COUNTER_OR_EXTRA, 0.3, name);
        soulburnSkill = SkillKeys.SKILL3;
        BuildSkills();
    }

    @Override
    public void UseSkill(Hero target, Hero.SkillKeys skillKey, boolean soulburn, boolean isCounter, boolean isExtraAttack)
    {
        super.UseSkill(target, skillKey, soulburn, isCounter, isExtraAttack);
        switch (skillKey)
        {
            case SKILL1:
                Attack(target, skillKey, isCounter, isExtraAttack);
                break;
            case SOULBURN:
            case SKILL3:
                team.RemoveDebuffs(1);
                Attack(target, skillKey, isCounter, isExtraAttack);
                team.AddShield(defense * 0.8,2);
                if (target.HasStatusEffect(StatusEffect.StatusEffectName.PROVOKE))
                {
                    team.AddCombatReadiness(15);
                }
                if (skillKey == SkillKeys.SOULBURN) {skills.get(SkillKeys.SKILL3).reduceCooldown(2);}
                break;
        }
    }

    protected void HandleEvent(BattleManager.EventParams eventParams)
    {
        switch (eventParams.eventKey)
        {
            case BATTLE_START:
                team.AddDamageReduction(s2Reduction);
            case ON_ATTACK:
                if (this == eventParams.trigerringHero
                        && eventParams.triggeringSkill == skills.get(Hero.SkillKeys.SKILL1)
                        && eventParams.priority == 1)
                {
                    if (!ResistCheck(eventParams.targetHero) && BattleManager.TryRng(60))
                    {
                        eventParams.targetHero.RemoveBuffs(1);
                    }
                    TryToDebuff(eventParams.targetHero,75, StatusEffect.StatusEffectName.PROVOKE, 1, false);
                }
                if (this == eventParams.trigerringHero
                        && eventParams.triggeringSkill == skills.get(Hero.SkillKeys.SKILL3)
                        && eventParams.priority == 1)
                {
                    TryToDebuff(eventParams.targetHero,100, StatusEffect.StatusEffectName.PROVOKE, 1, false);
                }
                break;
            case ON_ATTACKED:
                switch (eventParams.priority)
                {
                    case 1:
                        if (this == eventParams.targetHero && !HasStatusEffect(StatusEffect.StatusEffectName.SEAL))
                        {
                            s2Stacks = Math.max(s2Stacks + 1, 3);
                            skillDefenseMod = s2Stacks * 0.1;
                            defBuffStacks += 1;
                            if (defBuffStacks == 4)
                            {
                                defBuffStacks = 0;
                                team.AddStatusEffect(StatusEffect.StatusEffectName.DEF_UP,2);
                            }
                        }
                        break;
                }
            case ON_DEATH:
                if (this == eventParams.trigerringHero)
                {
                    team.RemoveDamageReduction(s2Reduction);
                }
        }
        super.HandleEvent(eventParams);
    }

    private void BuildSkills()
    {
        Skill skill1 = new Skill();
        skill1.setName("Weakening Blow");
        skill1.setAttRate(0.7);
        skill1.setDefRate(0.5);
        skill1.setSkillType(Skill.SkillTypes.SINGLE);
        skill1.setCanTriggerDual(true);
        skills.put(Hero.SkillKeys.SKILL1, skill1);

        Skill skill2 = new Skill();
        skill2.setName("Will");
        skill2.setSkillType(Skill.SkillTypes.PASSIVE);
        skills.put(Hero.SkillKeys.SKILL2, skill2);

        Skill skill3 = new Skill();
        skill3.setName("Perceptive Slash");
        skill3.setAttRate(1.3);
        skill3.setDefRate(0.7);
        skill3.setMaxCooldown(3);
        skill3.setSkillType(Skill.SkillTypes.SINGLE);
        skill3.setSoulGain(2);
        skills.put(Hero.SkillKeys.SKILL3, skill3);

        Skill soulburn = new Skill();
        soulburn.setName("Perceptive Slash (Soulburn)");
        soulburn.setAttRate(1.3);
        soulburn.setDefRate(0.7);
        soulburn.setSkillType(Skill.SkillTypes.SINGLE);
        soulburn.setSoulGain(2);
        skills.put(SkillKeys.SOULBURN, soulburn);
    }
}
