import javafx.stage.Stage;

public class LevelTwo extends Level{
	
	private final int NUM_BOTS = 15;
	private final int BOSS_HEALTH = 100;
	private final double BOSS_RADIUS = 100;

	// Creates boss level with NUM_BOTS normal bots then a boss and plays the level
	public LevelTwo(Stage stage){
		super(stage);
		makeCoachK();
		addKeysController();
		addMouseController();
		generateBots(NUM_BOTS, getTeamUNC(), getVertical(), getBotRadius(), getBotHealth());
		generateBoss(BOSS_HEALTH, BOSS_RADIUS);
		play();
	}
	
}