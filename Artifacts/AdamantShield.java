public class AdamantShield extends Artifact
{
    private DamageReduction teamReduction;
    public AdamantShield()
    {
        name = "Adamant Shield";
        teamReduction = new DamageReduction(DamageReduction.DamageReductionCondition.CRIT, 0.16, name);
    }
    @Override
    public void HandleEvent(BattleManager.EventParams eventParams)
    {
        super.HandleEvent(eventParams);
        switch (eventParams.eventKey)
        {
            case ON_REVIVE:
            case BATTLE_START:
                equippingHero.team.AddDamageReduction(teamReduction);
                break;
            case ON_DEATH:
                if (eventParams.trigerringHero == equippingHero && !equippingHero.deathEventAlreadyTriggered)
                {
                    equippingHero.team.RemoveDamageReduction(teamReduction);
                }
                break;
        }
    }
}
