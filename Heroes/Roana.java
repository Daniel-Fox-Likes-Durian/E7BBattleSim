public class Roana extends Hero
{
    public Roana()
    {
        super();
        name = "Roana";
        imagePath = "Images/roana.png";
        element = Element.EARTH;
        maxHealth = 25000;
        originalMaxHealth = maxHealth;
        defense = 1800;
        currentHealth = maxHealth;
        speed = 180;
        attack = 1300;
        critChance = 15;
        critDamage = 150;
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
                Attack(target, skillKey, isCounter, isExtraAttack);
                break;
            case SKILL3:
                team.AddShield(maxHealth * 0.15,2);
                team.AddStatusEffect(StatusEffect.StatusEffectName.REVIVE, 2);

                team.restoreHealth(maxHealth * 0.25);

                UseNonAttackSkill(target, skillKey);
                break;
        }
    }

    protected void HandleEvent(BattleManager.EventParams eventParams)
    {
        switch (eventParams.eventKey)
        {
            case ON_ATTACK:
                if (this == eventParams.trigerringHero
                        && eventParams.triggeringSkill == skills.get(SkillKeys.SKILL1)
                        && eventParams.priority == 1)
                {
                    StatusEffect shield = new StatusEffect(StatusEffect.StatusEffectName.SHIELD, 2);
                    shield.setShieldValue(maxHealth * 0.125);
                    team.LowestHero().AddStatusEffect(shield);
                }
                break;
            case ON_ATTACKED:
                if (eventParams.priority == 1
                        && (eventParams.isExtraAttack || eventParams.isCounter)
                        && !HasStatusEffect(StatusEffect.StatusEffectName.SEAL))
                {
                    team.restoreHealth(maxHealth * 0.0775);
                }
        }
        super.HandleEvent(eventParams);
    }

    private void BuildSkills()
    {
        Skill skill1 = new Skill();
        skill1.setName("Soul Purification");
        skill1.setAttRate(1);
        skill1.setSkillType(Skill.SkillTypes.SINGLE);
        skill1.setCanTriggerDual(true);
        skills.put(SkillKeys.SKILL1, skill1);

        Skill skill2 = new Skill();
        skill2.setName("Vigilant Eye");
        skill2.setSkillType(Skill.SkillTypes.PASSIVE);
        skills.put(SkillKeys.SKILL2, skill2);

        Skill skill3 = new Skill();
        skill3.setName("Noble Rekos");
        skill3.setMaxCooldown(4);
        skill3.setSkillType(Skill.SkillTypes.NON_ATK);
        skill3.setTargetType(Skill.TargetTypes.ALLY);
        skill3.setSoulGain(3);
        skills.put(SkillKeys.SKILL3, skill3);
    }
}
