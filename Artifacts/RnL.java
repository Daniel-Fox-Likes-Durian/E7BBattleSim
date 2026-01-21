public class RnL extends Artifact
{
    private boolean onCooldown = false;
    public void HandleEvent(BattleManager.EventParams eventParams)
    {
        super.HandleEvent(eventParams);
        switch (eventParams.eventKey)
        {
            case TURN_END:
                if (eventParams.trigerringHero == equippingHero)
                {
                    if (!onCooldown && BattleManager.TryRng(20))
                    {
                        equippingHero.addExtraTurn();
                        onCooldown = true;
                        equippingHero.team.getBattleManager().displayMessage(equippingHero.name + " procced RnL (cringe)");
                    } else
                    {
                        onCooldown = false;
                    }
                }
                break;
        }
    }
}
