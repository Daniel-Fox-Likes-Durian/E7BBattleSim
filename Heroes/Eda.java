import java.util.concurrent.ThreadLocalRandom;

public class Eda extends Hero
{
    boolean effectsApplied = false;
    public Eda()
    {
        super();
        name = "Eda";
        imagePath = "Images/Eda-icon.png";
        element = Element.ICE;
        maxHealth = 9000;
        originalMaxHealth = maxHealth;
        defense = 1000;
        attack = 4000;
        currentHealth = maxHealth;
        speed = 250;
        critChance = 100;
        critDamage = 250;
        soulCost = 20;
        soulburnSkill = SkillKeys.SKILL2;
        BuildSkills();
    }

    @Override
    public void UseSkill(Hero target, SkillKeys skillKey, boolean soulburn, boolean isCounter, boolean isExtraAttack) {
        super.UseSkill(target, skillKey, soulburn, isCounter, isExtraAttack);
        Attack(target, skillKey, isCounter, isExtraAttack);
    }

    protected void HandleEvent(BattleManager.EventParams eventParams)
    {
        switch (eventParams.eventKey)
        {
            case ON_ATTACK:
                if (this == eventParams.trigerringHero
                        && eventParams.triggeringSkill == skills.get(SkillKeys.SKILL1)
                        && eventParams.priority == 1
                        && eventParams.hitType != Skill.HitTypes.MISS)
                {
                        TryToDebuff(eventParams.targetHero, 75, StatusEffect.StatusEffectName.DEF_DOWN, 1, false);
                }
                if (this == eventParams.trigerringHero &&
                        (eventParams.triggeringSkill == skills.get(SkillKeys.SKILL2) ||
                         eventParams.triggeringSkill == skills.get(SkillKeys.SOULBURN)))
                {
                    boolean soulburn = eventParams.triggeringSkill == this.skills.get(SkillKeys.SOULBURN);
                    switch (eventParams.priority)
                    {
                        case 1:
                            if (eventParams.hitType != Skill.HitTypes.MISS)
                            {
                                double crDecrease = ThreadLocalRandom.current().nextDouble() * 25.0 + 15;
                                if (!ResistCheck(eventParams.targetHero)) {eventParams.targetHero.RemoveBuffs(2);}
                                if (!ResistCheck(eventParams.targetHero)) {eventParams.targetHero.addCombatReadiness(-1 * crDecrease);}
                            }
                            if (!effectsApplied)
                            {
                                AddStatusEffect(new StatusEffect(StatusEffect.StatusEffectName.SKILL_NULLIFIER, Integer.MAX_VALUE));
                                if (soulburn)
                                {
                                    addExtraTurn();
                                }
                                effectsApplied = true;
                            }
                            break;
                    }
                }
                if (this == eventParams.trigerringHero
                        && eventParams.triggeringSkill == skills.get(SkillKeys.SKILL3)
                        && eventParams.priority == 1)
                {
                    team.AddCombatReadiness(20);
                    if (eventParams.hitType != Skill.HitTypes.MISS)
                    {
                        TryToDebuff(eventParams.targetHero, 85, StatusEffect.StatusEffectName.STUN, 1, false);
                    }
                }
                break;
            case NON_ATK_USED_ALLY:
                if (eventParams.priority == 1 && skills.get(SkillKeys.SKILL3).IsAvailable())
                {
                    addCombatReadiness(20);
                }
                break;
            case TURN_END:
                effectsApplied = false;
        }
        super.HandleEvent(eventParams);
    }

    private void BuildSkills()
    {

        Skill skill1 = new Skill();
        skill1.setName("Icy Impact");
        skill1.setAttRate(1);
        skill1.setSkillType(Skill.SkillTypes.SINGLE);
        skill1.setCanTriggerDual(true);
        skills.put(SkillKeys.SKILL1, skill1);

        Skill skill2 = new Skill();
        skill2.setName("Cold Snap");
        skill2.setAttRate(0.9);
        skill2.setMaxCooldown(3);
        skill2.setSkillType(Skill.SkillTypes.AOE);
        skill2.setSoulGain(2);
        skills.put(SkillKeys.SKILL2, skill2);

        Skill soulburn = new Skill();
        soulburn.setName("Cold Snap (Soulburn)");
        soulburn.setAttRate(0.9);
        soulburn.setSkillType(Skill.SkillTypes.AOE);
        soulburn.setSoulGain(2);
        skills.put(SkillKeys.SOULBURN, soulburn);

        Skill skill3 = new Skill();
        skill3.setName("Absolute Zero");
        skill3.setMaxCooldown(4);
        skill3.setAttRate(1.05);
        skill3.setPow(1.1);
        skill3.setEnhancementMulti(0.2);
        skill3.setSkillType(Skill.SkillTypes.AOE);
        skill3.setSoulGain(3);
        skills.put(SkillKeys.SKILL3, skill3);
    }
}
