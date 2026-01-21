public class DamageReduction
{
    public enum DamageReductionCondition
    {
        SINGLE_ATK, AOE_ATK, CRIT, COUNTER_OR_EXTRA
    }
    public DamageReductionCondition condition;
    public double value;
    public String source;
    public DamageReduction(DamageReductionCondition condition, double value, String source)
    {
        this.condition = condition;
        this.value = value;
        this.source = source;
    }
}
