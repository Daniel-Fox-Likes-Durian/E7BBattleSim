import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame
{
    private BattleManager battleManager;
    private JPanel infoPanel;
    private JLayeredPane crPanel;
    private JPanel mainPanel;
    private JPanel team1Panel;
    private JPanel team2Panel;
    private JPanel hero1Panel;
    private JPanel hero23Panel;
    private JPanel hero4Panel;
    private JPanel hero5Panel;
    private JPanel hero67Panel;
    private JPanel hero8Panel;
    private JPanel hero1Box;
    private JPanel hero2Box;
    private JPanel hero3Box;
    private JPanel hero4Box;
    private JPanel hero5Box;
    private JPanel hero6Box;
    private JPanel hero7Box;
    private JPanel hero8Box;
    private JLabel hero1Portrait;
    private JLabel hero1CrPortrait;
    private JLabel hero2Portrait;
    private JLabel hero2CrPortrait;
    private JLabel hero3Portrait;
    private JLabel hero3CrPortrait;
    private JLabel hero4Portrait;
    private JLabel hero4CrPortrait;
    private JLabel hero5Portrait;
    private JLabel hero5CrPortrait;
    private JLabel hero6Portrait;
    private JLabel hero6CrPortrait;
    private JLabel hero7Portrait;
    private JLabel hero7CrPortrait;
    private JLabel hero8Portrait;
    private JLabel hero8CrPortrait;
    private HealthBar hero1HealthBar;
    private HealthBar hero2HealthBar;
    private HealthBar hero3HealthBar;
    private HealthBar hero4HealthBar;
    private HealthBar hero5HealthBar;
    private HealthBar hero6HealthBar;
    private HealthBar hero7HealthBar;
    private HealthBar hero8HealthBar;
    private JPanel hero1StatusFlowLayout;
    private JPanel hero2StatusFlowLayout;
    private JPanel hero3StatusFlowLayout;
    private JPanel hero4StatusFlowLayout;
    private JPanel hero5StatusFlowLayout;
    private JPanel hero6StatusFlowLayout;
    private JPanel hero7StatusFlowLayout;
    private JPanel hero8StatusFlowLayout;
    private JPanel skillButtonPanel;
    private JButton s1Button;
    private JButton s2Button;
    private JButton s3Button;
    private JLabel s1Cooldown;
    private JLabel s2Cooldown;
    private JLabel s3Cooldown;
    private JPanel s1Box;
    private JPanel s2Box;
    private JPanel s3Box;
    private JPanel soulInfoBox;
    private JButton soulButton;
    private JLabel soulCount;
    public MainFrame(BattleManager battleManager)
    {
        super("BattleSim");
        this.battleManager = battleManager;
        battleManager.battleFrame = this;

        mainPanel = new JPanel(new BorderLayout());
        crPanel = new JLayeredPane();
        crPanel.setPreferredSize(new Dimension(100, 1000));

        InitHealthBars();

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(true);
        infoPanel.setBackground(Color.DARK_GRAY);

        team1Panel = new JPanel();
        team1Panel.setLayout(new BoxLayout(team1Panel, BoxLayout.Y_AXIS));
        team1Panel.setOpaque(true);
        team1Panel.setBackground(Color.DARK_GRAY);
        team2Panel = new JPanel();
        team2Panel.setLayout(new BoxLayout(team2Panel, BoxLayout.Y_AXIS));
        team2Panel.setOpaque(true);
        team2Panel.setBackground(Color.DARK_GRAY);

        skillButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20,0));
        skillButtonPanel.setOpaque(true);
        skillButtonPanel.setBackground(Color.DARK_GRAY);

        InitHeroBoxes();
        InitHeroFlowLayouts();

        JLabel label = new JLabel("Choose Skill: ");
        label.setOpaque(true);
        label.setForeground(Color.WHITE);
        label.setBackground(Color.DARK_GRAY);

        InitSkillBoxes();
        InitSoulsInfo();

        InitHeroPortraits();

        AddComponentsToHeroBoxes();

        hero1Panel.add(hero1Box);
        hero23Panel.add(hero2Box);
        hero23Panel.add(hero3Box);
        hero4Panel.add(hero4Box);
        hero5Panel.add(hero5Box);
        hero67Panel.add(hero6Box);
        hero67Panel.add(hero7Box);
        hero8Panel.add(hero8Box);

        skillButtonPanel.add(soulInfoBox);
        skillButtonPanel.add(label);
        skillButtonPanel.add(s1Box);
        skillButtonPanel.add(s2Box);
        skillButtonPanel.add(s3Box);

        team1Panel.add(hero1Panel);
        team1Panel.add(hero23Panel);
        team1Panel.add(hero4Panel);

        team2Panel.add(hero5Panel);
        team2Panel.add(hero67Panel);
        team2Panel.add(hero8Panel);

        mainPanel.add(skillButtonPanel, BorderLayout.SOUTH);
        mainPanel.add(team1Panel, BorderLayout.WEST);
        mainPanel.add(team2Panel, BorderLayout.EAST);
        mainPanel.add(infoPanel, BorderLayout.CENTER);

        crPanel.setOpaque(true);
        mainPanel.setOpaque(true);
        crPanel.setBackground(Color.DARK_GRAY);
        mainPanel.setBackground(Color.DARK_GRAY);
        add(crPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        InitBoard();
    }

    private void InitSkillBoxes()
    {
        s1Box = new JPanel();
        s2Box = new JPanel();
        s3Box = new JPanel();

        s1Button = new JButton("Skill 1");
        s2Button = new JButton("Skill 2");
        s3Button = new JButton("Skill 3");

        s1Cooldown = new JLabel("N/A");
        s2Cooldown = new JLabel("N/A");
        s3Cooldown = new JLabel("N/A");

        Border inactiveBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);

        for (int skillNumber = 1; skillNumber <= 3; skillNumber++)
        {
            JPanel skillBox = getSkillBox(skillNumber);
            JButton skillButton = getSkillButton(skillNumber);
            JLabel skillCooldown = getSkillCooldown(skillNumber);
            Hero.SkillKeys skillKey = getSkillKey(skillNumber);

            skillBox.setLayout(new BoxLayout(skillBox, BoxLayout.Y_AXIS));
            skillBox.setOpaque(true);
            skillButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    battleManager.setSelectedSkill(skillKey);
                }
            });
            skillButton.setBorder(inactiveBorder);
            skillCooldown.setOpaque(true);
            skillCooldown.setForeground(Color.WHITE);
            skillCooldown.setBackground(Color.DARK_GRAY);
            skillBox.add(skillCooldown);
            skillBox.add(skillButton);
            skillBox.setBackground(Color.DARK_GRAY);
        }
    }
    public void UpdateSkillButtons(Hero.SkillKeys skillKey)
    {
        Border inactiveBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
        Border activeBorder = BorderFactory.createLineBorder(Color.red, 3);

        s1Button.setBorder(inactiveBorder);
        s2Button.setBorder(inactiveBorder);
        s3Button.setBorder(inactiveBorder);
        switch (skillKey)
        {
            case SKILL1 -> s1Button.setBorder(activeBorder);
            case SKILL2 -> s2Button.setBorder(activeBorder);
            case SKILL3 -> s3Button.setBorder(activeBorder);
        }
    }
    private void InitHeroBoxes()
    {
        hero1Box = new JPanel();
        hero2Box = new JPanel();
        hero3Box = new JPanel();
        hero4Box = new JPanel();
        hero5Box = new JPanel();
        hero6Box = new JPanel();
        hero7Box = new JPanel();
        hero8Box = new JPanel();

        JPanel heroBox;
        for (int heroId = 1; heroId <= 8; heroId++)
        {
            heroBox = findHeroBoxById(heroId);
            heroBox.setLayout(new BoxLayout(heroBox, BoxLayout.Y_AXIS));
            if (heroId == 1 || heroId == 5) { heroBox.add(Box.createRigidArea(new Dimension(0, 40))); }
            heroBox.setOpaque(true);
            heroBox.setBackground(Color.DARK_GRAY);
        }
    }

    public void AddComponentsToHeroBoxes()
    {
        hero1Box.add(hero1StatusFlowLayout);
        hero1Box.add(hero1HealthBar);
        hero1Box.add(Box.createRigidArea(new Dimension(0, 10)));
        hero1Box.add(hero1Portrait);
        crPanel.add(hero1CrPortrait, Integer.valueOf(1));

        hero2Box.add(hero2StatusFlowLayout);
        hero2Box.add(hero2HealthBar);
        hero2Box.add(Box.createRigidArea(new Dimension(0, 10)));
        hero2Box.add(hero2Portrait);
        crPanel.add(hero2CrPortrait, Integer.valueOf(2));

        hero3Box.add(hero3StatusFlowLayout);
        hero3Box.add(hero3HealthBar);
        hero3Box.add(Box.createRigidArea(new Dimension(0, 10)));
        hero3Box.add(hero3Portrait);
        crPanel.add(hero3CrPortrait, Integer.valueOf(3));

        hero4Box.add(hero4StatusFlowLayout);
        hero4Box.add(hero4HealthBar);
        hero4Box.add(Box.createRigidArea(new Dimension(0, 10)));
        hero4Box.add(hero4Portrait);
        crPanel.add(hero4CrPortrait, Integer.valueOf(4));

        hero5Box.add(hero5StatusFlowLayout);
        hero5Box.add(hero5HealthBar);
        hero5Box.add(Box.createRigidArea(new Dimension(0, 10)));
        hero5Box.add(hero5Portrait);
        crPanel.add(hero5CrPortrait, Integer.valueOf(5));

        hero6Box.add(hero6StatusFlowLayout);
        hero6Box.add(hero6HealthBar);
        hero6Box.add(Box.createRigidArea(new Dimension(0, 10)));
        hero6Box.add(hero6Portrait);
        crPanel.add(hero6CrPortrait, Integer.valueOf(6));

        hero7Box.add(hero7StatusFlowLayout);
        hero7Box.add(hero7HealthBar);
        hero7Box.add(Box.createRigidArea(new Dimension(0, 10)));
        hero7Box.add(hero7Portrait);
        crPanel.add(hero7CrPortrait, Integer.valueOf(7));

        hero8Box.add(hero8StatusFlowLayout);
        hero8Box.add(hero8HealthBar);
        hero8Box.add(Box.createRigidArea(new Dimension(0, 10)));
        hero8Box.add(hero8Portrait);
        crPanel.add(hero8CrPortrait, Integer.valueOf(8));
    }

    private void InitHeroFlowLayouts()
    {
        hero1Panel = new JPanel(new FlowLayout());
        hero23Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100,0));
        hero4Panel = new JPanel(new FlowLayout());
        hero5Panel = new JPanel(new FlowLayout());
        hero67Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100,0));
        hero8Panel = new JPanel(new FlowLayout());

        hero1StatusFlowLayout = new JPanel(new FlowLayout());
        hero2StatusFlowLayout = new JPanel(new FlowLayout());
        hero3StatusFlowLayout = new JPanel(new FlowLayout());
        hero4StatusFlowLayout = new JPanel(new FlowLayout());
        hero5StatusFlowLayout = new JPanel(new FlowLayout());
        hero6StatusFlowLayout = new JPanel(new FlowLayout());
        hero7StatusFlowLayout = new JPanel(new FlowLayout());
        hero8StatusFlowLayout = new JPanel(new FlowLayout());

        JPanel heroStatusFlowLayout;
        JPanel heroPanel;
        for (int heroId = 1; heroId <= 8; heroId++)
        {
            heroStatusFlowLayout = findStatusPanelById(heroId);
            heroStatusFlowLayout.setOpaque(true);
            heroStatusFlowLayout.setBackground(Color.DARK_GRAY);

            //if (heroId == 3 || heroId == 7) { continue; }
            heroPanel = findHeroPanelById(heroId);
            heroPanel.setOpaque(true);
            heroPanel.setBackground(Color.DARK_GRAY);
        }

    }

    private void InitBoard()
    {
        for (Hero hero : battleManager.team1.heroes.values())
        {
            switch(hero.position)
            {
                case FRONT -> InitSingleHero(hero, hero3Portrait, hero3CrPortrait);
                case BOTTOM -> InitSingleHero(hero, hero4Portrait, hero4CrPortrait);
                case BACK -> InitSingleHero(hero, hero2Portrait, hero2CrPortrait);
                case TOP -> InitSingleHero(hero, hero1Portrait, hero1CrPortrait);
            }
        }
        for (Hero hero : battleManager.team2.heroes.values())
        {
            switch(hero.position)
            {
                case FRONT -> InitSingleHero(hero, hero6Portrait, hero6CrPortrait);
                case BOTTOM -> InitSingleHero(hero, hero8Portrait, hero8CrPortrait);
                case BACK -> InitSingleHero(hero, hero7Portrait, hero7CrPortrait);
                case TOP -> InitSingleHero(hero, hero5Portrait, hero5CrPortrait);
            }
        }
    }
    public void UpdateBoard()
    {
        UpdateSouls();
        for (Hero hero : battleManager.team1.heroes.values())
        {
            switch(hero.position)
            {
                case FRONT -> UpdateSingleHero(hero, hero3Portrait, hero3CrPortrait, hero3StatusFlowLayout, hero3HealthBar);
                case BOTTOM -> UpdateSingleHero(hero, hero4Portrait, hero4CrPortrait, hero4StatusFlowLayout, hero4HealthBar);
                case BACK -> UpdateSingleHero(hero, hero2Portrait, hero2CrPortrait, hero2StatusFlowLayout, hero2HealthBar);
                case TOP -> UpdateSingleHero(hero, hero1Portrait, hero1CrPortrait, hero1StatusFlowLayout, hero1HealthBar);
            }
        }
        for (Hero hero : battleManager.team2.heroes.values())
        {
            switch(hero.position)
            {
                case FRONT -> UpdateSingleHero(hero, hero6Portrait, hero6CrPortrait, hero6StatusFlowLayout, hero6HealthBar);
                case BOTTOM -> UpdateSingleHero(hero, hero8Portrait, hero8CrPortrait, hero8StatusFlowLayout, hero8HealthBar);
                case BACK -> UpdateSingleHero(hero, hero7Portrait, hero7CrPortrait, hero7StatusFlowLayout, hero7HealthBar);
                case TOP -> UpdateSingleHero(hero, hero5Portrait, hero5CrPortrait, hero5StatusFlowLayout, hero5HealthBar);
            }
        }
        revalidate();
    }

    private void InitHealthBars()
    {
        hero1HealthBar = new HealthBar();
        hero2HealthBar = new HealthBar();
        hero3HealthBar = new HealthBar();
        hero4HealthBar = new HealthBar();
        hero5HealthBar = new HealthBar();
        hero6HealthBar = new HealthBar();
        hero7HealthBar = new HealthBar();
        hero8HealthBar = new HealthBar();
    }
    private void InitSoulsInfo()
    {
        soulInfoBox = new JPanel();
        soulButton = new JButton("BURN");
        soulCount = new JLabel("0");

        soulInfoBox.setLayout(new BoxLayout(soulInfoBox, BoxLayout.Y_AXIS));
        soulInfoBox.setOpaque(true);
        soulCount.setOpaque(true);

        soulCount.setBackground(Color.DARK_GRAY);
        soulCount.setForeground(Color.WHITE);
        soulInfoBox.setBackground(Color.DARK_GRAY);
        soulButton.addActionListener(e -> battleManager.toggleSoulburn());

        Border inactiveBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
        soulButton.setBorder(inactiveBorder);

        soulInfoBox.add(soulCount);
        soulInfoBox.add(soulButton);
    }
    // update the information related to souls and soulburn skills
    public void UpdateSouls()
    {
        Hero activeHero = battleManager.activeHero;
        Team activeTeam = activeHero.team;
        int souls = activeTeam.getSouls();
        soulCount.setText(String.valueOf(souls));

        boolean soulBurnAvailable = activeHero.skills.get(activeHero.soulburnSkill).IsAvailable() && souls >= activeHero.soulCost;
        soulButton.setEnabled(soulBurnAvailable);
        if (battleManager.soulburnSelected)
        {
            Border activeBorder = BorderFactory.createLineBorder(Color.blue, 3);
            soulButton.setBorder(activeBorder);
        } else
        {
            Border inactiveBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
            soulButton.setBorder(inactiveBorder);
        }
    }
    private void InitHeroPortraits()
    {
        ImageIcon emptyImage = new ImageIcon("Images/empty.png");
        hero1Portrait = new JLabel(emptyImage);
        hero1CrPortrait = new JLabel(emptyImage);

        hero2Portrait = new JLabel(emptyImage);
        hero2CrPortrait = new JLabel(emptyImage);

        hero3Portrait = new JLabel(emptyImage);
        hero3CrPortrait = new JLabel(emptyImage);

        hero4Portrait = new JLabel(emptyImage);
        hero4CrPortrait = new JLabel(emptyImage);

        hero5Portrait = new JLabel(emptyImage);
        hero5CrPortrait = new JLabel(emptyImage);

        hero6Portrait = new JLabel(emptyImage);
        hero6CrPortrait = new JLabel(emptyImage);

        hero7Portrait = new JLabel(emptyImage);
        hero7CrPortrait = new JLabel(emptyImage);

        hero8Portrait = new JLabel(emptyImage);
        hero8CrPortrait = new JLabel(emptyImage);

        JLabel heroPortrait;
        for (int heroId = 1; heroId <= 8; heroId++)
        {
            heroPortrait = findHeroPortraitById(heroId);
            int finalHeroId = heroId;
            heroPortrait.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mousePressed(MouseEvent e)
                {
                    super.mousePressed(e);
                    battleManager.selectTarget(finalHeroId);
                }
            });
        }
    }
    private void InitSingleHero(Hero hero, JLabel portrait, JLabel crPortrait)
    {
        BufferedImage heroImage = null;
        try
        {
            heroImage = ImageIO.read(new File(hero.getImagePath()));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        portrait.setIcon(new ImageIcon(heroImage));
        if (crPortrait != null)
        {
            crPortrait.setIcon(new ImageIcon(heroImage.getScaledInstance(heroImage.getWidth() / 2, heroImage.getHeight() / 2, Image.SCALE_DEFAULT)));
            crPortrait.setBounds(0, 100, crPortrait.getIcon().getIconWidth(), crPortrait.getIcon().getIconHeight());
            crPortrait.setBackground(null);
            crPanel.invalidate();
        }
        if (hero.isActiveHero)
        {
            Border activeBorder = BorderFactory.createLineBorder(Color.red, 3);
            portrait.setBorder(activeBorder);
        }
    }

    private void UpdateSingleHero(Hero hero, JLabel portrait, JLabel crPortrait, JPanel statFlowLayout, HealthBar healthBar)
    {
        if (hero.isActiveHero)
        {
            Border activeBorder = BorderFactory.createLineBorder(Color.red, 3);
            portrait.setBorder(activeBorder);
            for (Hero.SkillKeys skillKey : hero.skills.keySet())
            {
                Skill skill = hero.skills.get(skillKey);
                boolean skillAvailable = skill.IsAvailable();
                switch (skillKey)
                {
                    case SKILL1:
                        s1Button.setEnabled(skillAvailable);
                        s1Cooldown.setText(String.valueOf(skill.getCurrentCooldown()));
                        break;
                    case SKILL2:
                        if (skill.getProcOnly() || skill.getSkillType() == Skill.SkillTypes.PASSIVE)
                        {
                            s2Button.setEnabled(false);
                        } else
                        {
                            s2Button.setEnabled(skillAvailable);
                        }
                        s2Cooldown.setText(String.valueOf(skill.getCurrentCooldown()));
                        break;
                    case SKILL3:
                        s3Button.setEnabled(skillAvailable);
                        s3Cooldown.setText(String.valueOf(skill.getCurrentCooldown()));
                        break;
                }
            }
        } else
        {
            Border inactiveBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
            portrait.setBorder(inactiveBorder);
        }
        if (hero.IsDead())
        {
            portrait.setIcon(new ImageIcon("Images/empty.png"));
            crPortrait.setVisible(false);
            statFlowLayout.setVisible(false);
            healthBar.setVisible(false);
        } else
        {
            float crRatio = (float) hero.combatReadiness / 100f;
            if (crPortrait != null && crPortrait.getParent() != null)
            {
                crPortrait.setOpaque(true);
                int yPos = (int) ((crPortrait.getParent().getHeight()-50) * crRatio);
                crPortrait.setBounds(0, yPos, crPortrait.getIcon().getIconWidth(), crPortrait.getIcon().getIconHeight());
                crPanel.invalidate();
            }
        }
        statFlowLayout.removeAll();
        for (StatusEffect statusEffect : hero.statusEffects)
        {
            statFlowLayout.add(new JLabel(new ImageIcon(statusEffect.getImagePath())));
            statFlowLayout.revalidate();
            statFlowLayout.repaint();
        }
        if (healthBar != null)
        {
            healthBar.setCurrentHealth(hero.currentHealth, hero.maxHealth, hero.originalMaxHealth, hero.shield);
        }
    }
    public void UpdateBorders()
    {
        Border inactiveBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
        Border validBorder = BorderFactory.createLineBorder(Color.green, 3);
        if (battleManager.getSelectedSkill() == null){ClearSkillBorders();}
        for (int heroId = 1; heroId <= 8; heroId++)
        {
            Hero hero = battleManager.findHeroById(heroId);
            if (hero == null) { continue; }
            JLabel heroPortrait = findHeroPortraitById(heroId);
            if (battleManager.isValidTarget(heroId))
            {
                heroPortrait.setBorder(validBorder);
            } else if (!hero.isActiveHero)
            {
                heroPortrait.setBorder(inactiveBorder);
            }
            heroPortrait.revalidate();
            heroPortrait.repaint();
        }
    }
    private JLabel findHeroPortraitById(int heroId)
    {
        JLabel portrait = null;
        switch (heroId)
        {
            case 1 -> portrait = hero1Portrait;
            case 2 -> portrait = hero2Portrait;
            case 3 -> portrait = hero3Portrait;
            case 4 -> portrait = hero4Portrait;
            case 5 -> portrait = hero5Portrait;
            case 6 -> portrait = hero6Portrait;
            case 7 -> portrait = hero7Portrait;
            case 8 -> portrait = hero8Portrait;
        }
        return portrait;
    }

    private JLabel findHeroCrPortraitById(int heroId)
    {
        JLabel portrait = null;
        switch (heroId)
        {
            case 1 -> portrait = hero1CrPortrait;
            case 2 -> portrait = hero2Portrait;
            case 3 -> portrait = hero3Portrait;
            case 4 -> portrait = hero4Portrait;
            case 5 -> portrait = hero5Portrait;
            case 6 -> portrait = hero6Portrait;
            case 7 -> portrait = hero7Portrait;
            case 8 -> portrait = hero8Portrait;
        }
        return portrait;
    }
    private JPanel findHeroBoxById(int heroId)
    {
        JPanel heroBox = null;
        switch (heroId)
        {
            case 1 -> heroBox = hero1Box;
            case 2 -> heroBox = hero2Box;
            case 3 -> heroBox = hero3Box;
            case 4 -> heroBox = hero4Box;
            case 5 -> heroBox = hero5Box;
            case 6 -> heroBox = hero6Box;
            case 7 -> heroBox = hero7Box;
            case 8 -> heroBox = hero8Box;
        }
        return heroBox;
    }
    private JPanel getSkillBox(int skillNumber)
    {
        JPanel skillBox = null;
        switch (skillNumber)
        {
            case 1 -> skillBox = s1Box;
            case 2 -> skillBox = s2Box;
            case 3 -> skillBox = s3Box;
        }
        return skillBox;
    }
    private JLabel getSkillCooldown(int skillNumber)
    {
        JLabel skillCooldown = null;
        switch(skillNumber)
        {
            case 1 -> skillCooldown = s1Cooldown;
            case 2 -> skillCooldown = s2Cooldown;
            case 3 -> skillCooldown = s3Cooldown;
        }
        return skillCooldown;
    }
    private JButton getSkillButton(int skillNumber)
    {
        JButton skillButton = null;
        switch (skillNumber)
        {
            case 1 -> skillButton = s1Button;
            case 2 -> skillButton = s2Button;
            case 3 -> skillButton = s3Button;
        }
        return skillButton;
    }
    private void ClearSkillBorders()
    {
        Border inactiveBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
        s1Button.setBorder(inactiveBorder);
        s2Button.setBorder(inactiveBorder);
        s3Button.setBorder(inactiveBorder);
    }

    private Hero.SkillKeys getSkillKey(int skillNumber)
    {
        Hero.SkillKeys skillKey = null;
        switch (skillNumber)
        {
            case 1 -> skillKey = Hero.SkillKeys.SKILL1;
            case 2 -> skillKey = Hero.SkillKeys.SKILL2;
            case 3 -> skillKey = Hero.SkillKeys.SKILL3;
        }
        return skillKey;
    }

    private HealthBar findHealthBarById(int heroId)
    {
        HealthBar healthBar = null;
        switch (heroId)
        {
            case 1 -> healthBar = hero1HealthBar;
            case 2 -> healthBar = hero2HealthBar;
            case 3 -> healthBar = hero3HealthBar;
            case 4 -> healthBar = hero4HealthBar;
            case 5 -> healthBar = hero5HealthBar;
            case 6 -> healthBar = hero6HealthBar;
            case 7 -> healthBar = hero7HealthBar;
            case 8 -> healthBar = hero8HealthBar;
        }
        return healthBar;
    }
    private JPanel findHeroPanelById(int heroId)
    {
        JPanel heroPanel = null;
        switch (heroId)
        {
            case 1 -> heroPanel = hero1Panel;
            case 2, 3 -> heroPanel = hero23Panel;
            case 4 -> heroPanel = hero4Panel;
            case 5 -> heroPanel = hero5Panel;
            case 6, 7 -> heroPanel = hero67Panel;
            case 8 -> heroPanel = hero8Panel;
        }
        return heroPanel;
    }

    private JPanel findStatusPanelById(int heroId)
    {
        JPanel statusPanel = null;
        switch (heroId)
        {
            case 1 -> statusPanel = hero1StatusFlowLayout;
            case 2 -> statusPanel = hero2StatusFlowLayout;
            case 3 -> statusPanel = hero3StatusFlowLayout;
            case 4 -> statusPanel = hero4StatusFlowLayout;
            case 5 -> statusPanel = hero5StatusFlowLayout;
            case 6 -> statusPanel = hero6StatusFlowLayout;
            case 7 -> statusPanel = hero7StatusFlowLayout;
            case 8 -> statusPanel = hero8StatusFlowLayout;
        }
        return statusPanel;
    }

    public void displayMessage(String message)
    {
        JLabel messageLabel = new JLabel(message);
        messageLabel.setOpaque(true);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBackground(Color.DARK_GRAY);
        infoPanel.add(messageLabel);
        if (infoPanel.getComponentCount() > 10)
        {
            infoPanel.remove(0);
        }
        infoPanel.revalidate();
    }
}
