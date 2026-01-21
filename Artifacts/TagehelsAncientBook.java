public class TagehelsAncientBook extends Artifact
{
    public TagehelsAncientBook()
    {
        name = "Adamant Shield";
    }
    @Override
    public void HandleEvent(BattleManager.EventParams eventParams)
    {
        super.HandleEvent(eventParams);
        switch (eventParams.eventKey)
        {
            case BATTLE_START:
                equippingHero.team.addSouls(20);
                break;
        }
    }
}
