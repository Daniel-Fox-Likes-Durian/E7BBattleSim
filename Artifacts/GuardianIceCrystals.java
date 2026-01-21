public class GuardianIceCrystals extends Artifact
{
    private boolean alreadyProc = false;
    private double healRate = 20;
    public GuardianIceCrystals(int level)
    {
        healRate = 10.0 + (Math.floor(level/3) * 1.0);
    }
    @Override
    public void HandleEvent(BattleManager.EventParams eventParams)
    {
        super.HandleEvent(eventParams);
        switch (eventParams.eventKey)
        {
            case ON_ATTACKED:
                if (eventParams.priority == 1 && !alreadyProc && eventParams.targetHero.GetCurrentHealthRatio() < 0.5 && !eventParams.targetHero.IsDead())
                {
                    alreadyProc = true;
                    eventParams.targetHero.restoreHealth((healRate/100)*eventParams.targetHero.maxHealth);
                }
            case TURN_START:
                if (equippingHero.IsActiveHero())
                {
                    alreadyProc = false;
                }
        }
    }
}