import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HealthBar extends JLabel
{
    private int currentHealthXCutoff;
    private int maxHealthXCutoff;
    private int injuryXCutoff;
    private int width = 118;

    public HealthBar()
    {
        super();
    }

    public void setCurrentHealth(double currentHealth, double maxHealth, double originalMaxHealth, double shield)
    {
        currentHealthXCutoff = (int) Math.ceil(width * (currentHealth / (originalMaxHealth + shield)));
        maxHealthXCutoff = (int) Math.ceil(width * (maxHealth / (originalMaxHealth + shield)));
        injuryXCutoff = (int) Math.floor(width * (originalMaxHealth / (originalMaxHealth + shield)));
        UpdateHealthBarImage();
    }

    private void UpdateHealthBarImage()
    {
        BufferedImage healthBar = new BufferedImage(width, 10, BufferedImage.TYPE_INT_BGR);
        for (int x = 0; x < healthBar.getWidth(); x++)
        {
            int color = GetRgbForXValue(x);
            for (int y = 0; y < healthBar.getHeight(); y++)
            {
                healthBar.setRGB(x, y, color);
            }
        }
        setIcon(new ImageIcon(healthBar));
        Border border = BorderFactory.createLineBorder(Color.black, 1);
        setBorder(border);
    }
    public void setWidth(int width)
    {
        this.width = width;
        UpdateHealthBarImage();
    }
    private int GetRgbForXValue(int x)
    {
        if (x <= currentHealthXCutoff)
        {
            return Color.green.getRGB();
        } else if (x <= maxHealthXCutoff)
        {
            return Color.darkGray.getRGB();
        } else if (x <= injuryXCutoff)
        {
            return  Color.red.getRGB();
        } else
        {
            return Color.lightGray.getRGB();
        }
    }
}
