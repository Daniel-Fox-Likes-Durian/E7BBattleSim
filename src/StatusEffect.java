public class StatusEffect
{
    public enum Type
    {
        Buff, Debuff
    }
    public enum StatusEffectName
    {
        ATK_UP, ATK_UP_GREATER, DEF_UP, DEF_UP_GREATER, RES_UP, INVINCIBLE, IMMORTAL, IMMUNITY, MINDS_EYE, CRIT_RES_UP,
        CONTINUOUS_HEALING, CRIT_DMG_UP, ATK_DOWN, DEF_DOWN, UNBUFFABLE, PROVOKE, STUN, SLEEP, POISON, BURN, BLEED,
        UNHEALABLE, BLIND, SILENCE, RESTRICT, SHIELD, SEAL, REVIVE, SPEED_UP, SPEED_DOWN, STIGMA, SKILL_NULLIFIER,
        STEALTH, CRIT_CHANCE_UP
    }
    private Type type;
    private StatusEffectName statusEffectName;
    private String displayName;
    private String imagePath;
    private int remainingDuration;
    private boolean addedThisTurn;
    private boolean addedThisAttack;
    private double shieldValue;
    private boolean isDispellable;
    private boolean isUnique;
    public StatusEffect(StatusEffectName effectName, int duration)
    {
        remainingDuration = duration;
        statusEffectName = effectName;
        switch (effectName)
        {
            case ATK_UP:
            case ATK_UP_GREATER:
            case DEF_UP:
            case DEF_UP_GREATER:
            case RES_UP:
            case INVINCIBLE:
            case IMMORTAL:
            case IMMUNITY:
            case CRIT_RES_UP:
            case CONTINUOUS_HEALING:
            case CRIT_DMG_UP:
            case SHIELD:
            case REVIVE:
            case SPEED_UP:
            case SPEED_DOWN:
            case SKILL_NULLIFIER:
            case STEALTH:
            case CRIT_CHANCE_UP:
                isUnique = true;
                isDispellable = true;
                type = Type.Buff;
                break;
            case MINDS_EYE:
                isUnique = true;
                isDispellable = false;
                type = Type.Buff;
                break;
            case ATK_DOWN:
            case DEF_DOWN:
            case UNBUFFABLE:
            case UNHEALABLE:
            case STUN:
            case SLEEP:
            case PROVOKE:
            case SEAL:
            case BLIND:
            case SILENCE:
            case RESTRICT:
            case STIGMA:
                isUnique = true;
                isDispellable = true;
                type = Type.Debuff;
                break;
            case BURN:
            case BLEED:
            case POISON:
                isUnique = false;
                isDispellable = true;
                type = Type.Debuff;
        }
        SetDisplayName();
        SetImagePath();
    }

    private void SetDisplayName()
    {
        switch (statusEffectName)
        {
            case DEF_DOWN -> displayName = "Defense Down";
            case DEF_UP -> displayName = "Defense Up";
            case ATK_UP -> displayName = "Attack Up";
            case IMMUNITY -> displayName = "Immunity";
            case MINDS_EYE -> displayName = "Mind's Eye";
            case CRIT_RES_UP -> displayName = "Crit Res Up";
            case ATK_DOWN -> displayName = "Attack Down";
            case SILENCE -> displayName = "Silence";
            case RESTRICT -> displayName = "Restrict";
            case PROVOKE -> displayName = "Povoke";
            case BLIND -> displayName = "Blind";
            case STIGMA -> displayName = "Stigma";
            case STEALTH -> displayName = "Stealth";
            case SHIELD -> displayName = "Barrier";
            default -> displayName = "Some buff/debuff I haven't implemented yet";
        }
    }
    private void SetImagePath()
    {
        switch (statusEffectName)
        {
            case ATK_UP -> imagePath = "Images/stic_att_up.png";
            case MINDS_EYE -> imagePath = "Images/Minds-Eye.png";
            case DEF_DOWN -> imagePath = "Images/stic_def_dn.png";
            case DEF_UP -> imagePath = "Images/stic_def_up.png";
            case ATK_DOWN -> imagePath = "Images/stic_att_dn.png";
            case SILENCE -> imagePath = "Images/stic_silence.png";
            case PROVOKE -> imagePath = "Images/stic_provoke.png";
            case RESTRICT -> imagePath = "Images/Restrict.png";
            case CRIT_RES_UP -> imagePath = "Images/stic_crires_up.png";
            case SHIELD -> imagePath = "Images/stic_protect.png";
            case BLIND -> imagePath = "Images/stic_blind.png";
            case REVIVE -> imagePath = "Images/stic_bless.png";
            case STIGMA -> imagePath = "Images/Stigma.png";
            case IMMUNITY -> imagePath = "Images/stic_debuf_impossible.png";
            case SKILL_NULLIFIER -> imagePath = "Images/Skill-Nullifier.png";
            case STEALTH -> imagePath = "Images/stic_hide.png";
            default -> imagePath = "Images/random-buff.png";
        }
    }
    public String getImagePath()
    {
        return imagePath;
    }

    public String getDisplayName()
    {
        return displayName;
    }
    public StatusEffectName getStatusEffectName()
    {
        return statusEffectName;
    }

    public void reduceDuration(int amount)
    {
        remainingDuration -= amount;
    }

    public void setRemainingDuration(int remainingDuration)
    {
        this.remainingDuration = remainingDuration;
    }

    public int getRemainingDuration()
    {
        return remainingDuration;
    }
    public Type getType() { return type; }

    public boolean IsExpired()
    {
        return remainingDuration <= 0;
    }

    public double getShieldValue()
    {
        return shieldValue;
    }

    public void setShieldValue(double shieldValue)
    {
        this.shieldValue = shieldValue;
    }

    public boolean addedThisTurn()
    {
        return addedThisTurn;
    }

    public boolean isAddedThisAttack()
    {
        return addedThisAttack;
    }

    public void setAddedThisAttack(boolean addedThisAttack)
    {
        this.addedThisAttack = addedThisAttack;
    }

    public void setAddedThisTurn(boolean addedThisTurn)
    {
        this.addedThisTurn = addedThisTurn;
    }

    public boolean IsUnique()
    {
        return isUnique;
    }
    public boolean IsDispellable()
    {
        return isDispellable;
    }
}
