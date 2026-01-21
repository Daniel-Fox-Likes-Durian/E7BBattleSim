public class AlexasBasket extends Artifact
{
    public void HandleEvent(BattleManager.EventParams eventParams)
    {
        super.HandleEvent(eventParams);
        switch (eventParams.eventKey)
        {
            case TURN_START:
                if (eventParams.trigerringHero == equippingHero)
                {
                    if (BattleManager.TryRng(40))
                    {
                        equippingHero.AddStatusEffect(new StatusEffect(StatusEffect.StatusEffectName.ATK_UP_GREATER, 1));
                        equippingHero.team.getBattleManager().displayMessage(equippingHero.name + " procced GAB (cringe)");
                    }
                    if (BattleManager.TryRng(40))
                    {
                        equippingHero.AddStatusEffect(new StatusEffect(StatusEffect.StatusEffectName.CRIT_CHANCE_UP, 1));
                    }
                }
                break;
        }
    }
}
