public class AlencinoxsWrath extends Artifact
{
    private boolean alreadyProc = false;
    public AlencinoxsWrath()
    {
        critChanceBonus = 15;
    }
    @Override
    public void HandleEvent(BattleManager.EventParams eventParams)
    {
        super.HandleEvent(eventParams);
        switch (eventParams.eventKey)
        {
            case ON_ATTACK:
                if (eventParams.isExtraAttack && eventParams.trigerringHero.IsActiveHero() && eventParams.priority == 1)
                {
                    eventParams.trigerringHero.addCombatReadiness(20);
                    alreadyProc = true;
                }
                if (eventParams.priority == 2)
                {
                    alreadyProc = false;
                }
        }
    }
}
