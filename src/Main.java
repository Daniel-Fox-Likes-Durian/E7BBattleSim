import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args)
    {
        BattleManager battleManager = new BattleManager();

        Team team1 = new Team();
        Team team2 = new Team();
        team1.setName("TEAM 1");
        team2.setName("TEAM 2");
        team1.setOpposingTeam(team2);
        team2.setOpposingTeam(team1);
        AlencinoxsWrath alencinoxsWrath = new AlencinoxsWrath();
        SnowCrystal snowCrystal = new SnowCrystal();
        AlencinoxsWrath alencinoxsWrath2 = new AlencinoxsWrath();
        SnowCrystal snowCrystal2 = new SnowCrystal();

        Alencia alencia = new Alencia();
        alencia.setArtifact(alencinoxsWrath);
        Choux choux = new Choux();
        choux.setArtifact(snowCrystal);

        AlencinoxsWrath alencinoxsWrath3 = new AlencinoxsWrath();
        EdwardElric edwardElric = new EdwardElric();
        edwardElric.setArtifact(alencinoxsWrath3);

        Roana roana = new Roana();
        GuardianIceCrystals iceCrystals = new GuardianIceCrystals(30);
        roana.setArtifact(iceCrystals);

        ShadowKnightPyllis shadowKnightPyllis = new ShadowKnightPyllis();
        Aurius aurius = new Aurius();
        shadowKnightPyllis.setArtifact(aurius);

        Ran ran = new Ran();
        RnL rnL = new RnL();
        ran.setArtifact(rnL);

        Eda eda = new Eda();
        TagehelsAncientBook book = new TagehelsAncientBook();
        eda.setArtifact(book);

        Kise kise = new Kise();
        AlexasBasket alexasBasket = new AlexasBasket();
        kise.setArtifact(alexasBasket);

        team1.addHero(alencia, Hero.Position.BACK);
        team1.addHero(roana, Hero.Position.FRONT);
        team1.addHero(shadowKnightPyllis, Hero.Position.BOTTOM);
        team1.addHero(kise, Hero.Position.TOP);

        team2.addHero(edwardElric, Hero.Position.BOTTOM);
        team2.addHero(choux, Hero.Position.FRONT);
        team2.addHero(ran, Hero.Position.BACK);
        team2.addHero(eda, Hero.Position.TOP);

        battleManager.addTeam(team1, "team1");
        battleManager.addTeam(team2, "team2");

        //Creating the Frame
        JFrame frame = new MainFrame(battleManager);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        battleManager.startManualBattle();

        frame.setVisible(true);
    }
}