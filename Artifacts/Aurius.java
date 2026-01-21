public class Aurius extends Artifact
{
    public Aurius()
    {
    }
    @Override
    public void HandleEvent(BattleManager.EventParams eventParams)
    {
        super.HandleEvent(eventParams);
        switch (eventParams.eventKey)
        {
            case ON_REVIVE:
            case BATTLE_START:
                equippingHero.team.AddDamageShare(new DamageShare(equippingHero, 0.2));
                equippingHero.team.addDefMod(0.1);
                break;
            case ON_DEATH:
                if (eventParams.trigerringHero == equippingHero && !equippingHero.deathEventAlreadyTriggered)
                {
                    equippingHero.team.ClearDamageShare(equippingHero);
                    equippingHero.team.addDefMod(-0.1);
                }
                break;
        }
    }
}

