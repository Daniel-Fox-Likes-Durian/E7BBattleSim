public class SnowCrystal extends Artifact
{
    public SnowCrystal()
    {
        critResBonus = 20;
    }
    @Override
    public void HandleEvent(BattleManager.EventParams eventParams)
    {
        super.HandleEvent(eventParams);
        switch (eventParams.eventKey)
        {
            case ON_ATTACKED:
                if ((eventParams.priority == 1) && (eventParams.targetHero == equippingHero) && (eventParams.hitType != Skill.HitTypes.CRIT))
                {
                    eventParams.targetHero.addCombatReadiness(20);
                }
                break;
        }
    }
}
