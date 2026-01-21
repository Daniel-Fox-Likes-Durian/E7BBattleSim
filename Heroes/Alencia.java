import javax.swing.plaf.synth.SynthOptionPaneUI;

public class Alencia extends Hero
{
    boolean hasMindsEye;
    public Alencia()
    {
        super();
        name = "Alencia";
        imagePath = "Images/alencia.png";
        element = Element.EARTH;
        maxHealth = 25000;
        attack = 1800;
        originalMaxHealth = maxHealth;
        defense = 1500;
        currentHealth = maxHealth;
        speed = 185;
        critChance = 85.0;
        critDamage = 270;
        injurySet = true;
        penSet = true;
        BuildSkills();
    }

    @Override
    public void UseSkill(Hero target, SkillKeys skillKey, boolean soulburn, boolean isCounter, boolean isExtraAttack)
    {
        super.UseSkill(target, skillKey, soulburn, isCounter, isExtraAttack);
        switch (skillKey)
        {
            case SKILL1:
                Attack(target, skillKey, isCounter, isExtraAttack);
                break;
            case SOULBURN:
                Attack(target, SkillKeys.SOULBURN, false, false);
                break;
            case SKILL3:
                Attack(target, skillKey, isCounter, isExtraAttack);
                addCombatReadiness(50);
                break;
        }
    }

    protected void HandleEvent(BattleManager.EventParams eventParams)
    {
        switch (eventParams.eventKey)
        {
            case TURN_START:
                if (this == eventParams.trigerringHero)
                {
                    statusEffects.add(new StatusEffect(StatusEffect.StatusEffectName.MINDS_EYE, 1));
                    hasMindsEye = true;
                }
                break;
            case ON_ATTACK:
                if (this == eventParams.trigerringHero &&
                        (eventParams.triggeringSkill == skills.get(SkillKeys.SKILL1))
                         || eventParams.triggeringSkill == skills.get(SkillKeys.SOULBURN))
                {
                    switch (eventParams.priority)
                    {
                        case 1:
                            if (exclusiveEquipment == 1) { restoreHealth(0.08 * maxHealth); }
                            if (eventParams.hitType != Skill.HitTypes.MISS)
                            {
                                if (eventParams.triggeringSkill==skills.get(SkillKeys.SKILL1))
                                {
                                    TryToDebuff(eventParams.targetHero, 75, StatusEffect.StatusEffectName.DEF_DOWN, 1, false);
                                } else
                                {
                                    TryToDebuff(eventParams.targetHero, 100, StatusEffect.StatusEffectName.DEF_DOWN, 2, false);
                                }
                            }
                            break;
                        case 6:
                            if (hasMindsEye) { Attack(eventParams.targetHero, SkillKeys.SKILL2, false, true); }
                            break;
                    }
                }
                if (this == eventParams.trigerringHero && eventParams.triggeringSkill == skills.get(SkillKeys.SKILL3))
                {
                    switch (eventParams.priority)
                    {
                        case 1:
                            if (eventParams.hitType != Skill.HitTypes.MISS && !ResistCheck(eventParams.targetHero))
                            {
                                eventParams.targetHero.RemoveBuffs(10);
                            }
                            team.AddStatusEffect(StatusEffect.StatusEffectName.DEF_UP, 2);
                            break;
                    }
                }
        }
        super.HandleEvent(eventParams);
    }

    private void BuildSkills()
    {

        Skill skill1 = new Skill();
        skill1.setName("Eradicate");
        skill1.setAttRate(0.5);
        skill1.setHpRate(0.08);
        skill1.setSkillType(Skill.SkillTypes.SINGLE);
        skill1.setCanTriggerDual(true);
        skills.put(SkillKeys.SKILL1, skill1);

        Skill soulburn = new Skill();
        soulburn.setName("Eradicate");
        soulburn.setAttRate(0.5);
        soulburn.setHpRate(0.08);
        soulburn.setSkillType(Skill.SkillTypes.SINGLE);
        soulburn.setCanTriggerDual(true);
        skills.put(SkillKeys.SOULBURN, soulburn);

        Skill skill2 = new Skill();
        skill2.setName("Trample");
        skill2.setAttRate(0.5);
        skill2.setHpRate(0.11);
        skill2.setSkillType(Skill.SkillTypes.SINGLE);
        skill2.setProcOnly(true);
        skill2.setInnateInjury(0.1);
        skill2.setSoulGain(0);
        skills.put(SkillKeys.SKILL2, skill2);

        Skill skill3 = new Skill();
        skill3.setName("Genesis");
        skill3.setMaxCooldown(4);
        skill3.setAttRate(0.5);
        skill3.setHpRate(0.15);
        skill3.setSkillType(Skill.SkillTypes.AOE);
        skill3.setSoulGain(3);
        skills.put(SkillKeys.SKILL3, skill3);

    }
}
