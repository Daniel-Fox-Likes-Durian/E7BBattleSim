public class Artifact
{
    protected Hero equippingHero;
    protected int level = 30;
    protected double attackMod = 0;
    protected double defenseMod = 0;
    protected double damageBonus = 0;
    protected double hitChanceBonus = 0;
    protected double critChanceBonus = 0;
    protected double critResBonus = 0;
    protected double damageReduction = 0;
    protected String name;

    public void HandleEvent(BattleManager.EventParams eventParams)
    {

    }
    public void OnEquip() {}
    public double getHitChanceBonus()
    {
        return hitChanceBonus;
    }
    public double getCritChanceBonus()
    {
        return critChanceBonus;
    }
    public double getDamageBonus()
    {
        return damageBonus;
    }

    public double getAttackMod()
    {
        return attackMod;
    }

    public double getDamageReduction(Hero.SkillKeys skillKey, Hero target, double damage, Skill.HitTypes hitType, boolean isCounter, boolean isExtraAttack)
    {
        return damageReduction;
    }

    public double getCritResBonus()
    {
        return critResBonus;
    }

    public void setEquippingHero(Hero equippingHero)
    {
        this.equippingHero = equippingHero;
    }
}
