import javafx.stage.Stage;

public class LevelOne extends Level{

	private final int NUM_BOTS = 10;

	// Creates bot level with NUM_BOTS generated and activates user key/mouse movement then plays level
	public LevelOne(Stage stage){
		super(stage);
		makeCoachK();
		addKeysController();
		addMouseController();
		generateBots(NUM_BOTS, getTeamUNC(), getVertical(), getBotRadius(), getBotHealth());
		play();
	}
	
}