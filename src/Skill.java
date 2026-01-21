import java.util.ArrayList;

public class Skill
{
    public enum SkillTypes
    {
        AOE, NON_ATK, SINGLE, TWO_TARGETS, PASSIVE
    }
    public enum TargetTypes
    {
        ENEMY, ALLY
    }
    public enum HitTypes
    {
        MISS, HIT, CRUSH, CRIT
    }

    public enum DualAttackConditions
    {
        HIGHEST_ATK, RANDOM
    }
    private String name;
    private int maxCooldown = 0;
    private int currentCooldown = 0;
    private double enhancementMulti = 0.3;
    private double attRate = 0;
    private double defRate = 0;
    private double hpRate = 0;
    private double speedRate = 0;
    private double targetSpeedRate = 0;
    private double targetMaxHpRate = 0;
    private double pow = 1;
    private double critChanceBonus = 0;
    private double innateInjury = 0;
    protected double innatePen = 0;
    private boolean procOnly = false;
    private boolean canCrit = true;
    private boolean alwaysAdvantage = false;
    private boolean ignoreDisadvantage = false;
    private boolean canBeCountered = true;
    private boolean ignoreDamageSharing = false;
    private DualAttackConditions dualAttackCondition = DualAttackConditions.RANDOM;
    private SkillTypes skillType;
    private boolean isCounter;
    private boolean isExtraAttack;
    private boolean canTriggerDual;
    private boolean guaranteedDual;
    private boolean highestAttackDual;
    private int soulGain = 1;
    public TargetTypes targetType = TargetTypes.ENEMY;

    public Skill()
    {

    }

    public boolean getProcOnly()
    {
        return procOnly;
    }

    public void setName(String name) {
        this.name = name;
    }
    public boolean AlwaysAdvantage()
    {
        return alwaysAdvantage;
    }
    public boolean IgnoreDisadvantage()
    {
        return ignoreDisadvantage;
    }
    public void setMaxCooldown(int maxCooldown) {
        this.maxCooldown = maxCooldown;
    }

    public double getCritChanceBonus()
    {
        return critChanceBonus;
    }

    public DualAttackConditions getDualAttackCondition()
    {
        return dualAttackCondition;
    }

    public void setDualAttackCondition(DualAttackConditions dualAttackCondition)
    {
        this.dualAttackCondition = dualAttackCondition;
    }

    public void setCurrentCooldown(int currentCooldown)
    {
        this.currentCooldown = currentCooldown;
    }

    public int getCurrentCooldown()
    {
        return currentCooldown;
    }

    public void setEnhancementMulti(double enhancementMulti) {
        this.enhancementMulti = enhancementMulti;
    }
    public boolean CanBeCountered()
    {
        return canBeCountered;
    }

    public void setAttRate(double attRate) {
        this.attRate = attRate;
    }

    public void setDefRate(double defRate) {
        this.defRate = defRate;
    }

    public void setHpRate(double hpRate) {
        this.hpRate = hpRate;
    }


    public void setPow(double pow) {
        this.pow = pow;
    }

    public void setProcOnly(boolean procOnly) {
        this.procOnly = procOnly;
    }
    public void setSkillType(SkillTypes skillType) {
        this.skillType = skillType;
    }

    public SkillTypes getSkillType() {
        return skillType;
    }

    public double getAttRate()
    {
        return attRate;
    }

    public double getDefRate()
    {
        return defRate;
    }

    public double getHpRate()
    {
        return hpRate;
    }

    public double getSpeedRate()
    {
        return speedRate;
    }

    public void setSpeedRate(double speedRate)
    {
        this.speedRate = speedRate;
    }

    public double getTargetSpeedRate()
    {
        return targetSpeedRate;
    }
    public void reduceCooldown(int amount)
    {
        currentCooldown = Math.max(0, currentCooldown - amount);
    }

    public void setTargetSpeedRate(double enemySpeedRate)
    {
        this.targetSpeedRate = enemySpeedRate;
    }
    public void SetIgnoreDamageSharing(boolean ignoreDamageSharing)
    {
        this.ignoreDamageSharing = ignoreDamageSharing;
    }
    public boolean ignoreDamageSharing()
    {
        return ignoreDamageSharing;
    }
    public double getTargetMaxHpRate()
    {
        return targetMaxHpRate;
    }

    public void setTargetMaxHpRate(double targetMaxHpRate)
    {
        this.targetMaxHpRate = targetMaxHpRate;
    }

    public double getEnhancementMulti()
    {
        return enhancementMulti;
    }

    public double getPow()
    {
        return pow;
    }
    public boolean CanCrit()
    {
        return canCrit;
    }
    public boolean isCounter() {
        return isCounter;
    }

    public double getInnateInjury()
    {
        return innateInjury;
    }

    public void setInnateInjury(double innateInjury)
    {
        this.innateInjury = innateInjury;
    }

    public void setCounter(boolean counter) {
        isCounter = counter;
    }

    public boolean isExtraAttack() {
        return isExtraAttack;
    }

    public void setExtraAttack(boolean extraAttack) {
        isExtraAttack = extraAttack;
    }

    public boolean canTriggerDual() {
        return canTriggerDual;
    }

    public void setCanTriggerDual(boolean canTriggerDual) {
        this.canTriggerDual = canTriggerDual;
    }

    public boolean isGuaranteedDual() {
        return guaranteedDual;
    }

    public void setGuaranteedDual(boolean guaranteedDual) {
        this.guaranteedDual = guaranteedDual;
    }

    public boolean isHighestAttackDual() {
        return highestAttackDual;
    }

    public void setHighestAttackDual(boolean highestAttackDual) {
        this.highestAttackDual = highestAttackDual;
    }

    public TargetTypes getTargetType()
    {
        return targetType;
    }

    public void setTargetType(TargetTypes targetType)
    {
        this.targetType = targetType;
    }

    public double getInnatePen(Hero target)
    {
        return innatePen;
    }
    public void resetCooldown()
    {
        currentCooldown = maxCooldown;
    }

    public void decrementCooldown()
    {
        currentCooldown = Math.max(0, currentCooldown - 1);
    }
    public void incrementCooldown()
    {
        if (skillType != SkillTypes.PASSIVE)
        {
            currentCooldown = Math.min(maxCooldown, currentCooldown + 1);
        }
    }

    public void setInnatePen(double pen)
    {
        innatePen = pen;
    }

    public String getName()
    {
        return name;
    }

    public boolean IsAvailable()
    {
        return currentCooldown == 0;
    }

    public int getSoulGain()
    {
        return soulGain;
    }

    public void setSoulGain(int soulGain)
    {
        this.soulGain = soulGain;
    }
}
