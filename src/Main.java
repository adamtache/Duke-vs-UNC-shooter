import javafx.stage.Stage;
import javafx.application.Application;

public class Main extends Application{
	private final String TITLE = "Duke vs. UNC: Bots Attack";
	private final String BACK_STORY = "Duke University and University of North Carolina Chapel-Hill,"
			+ "\ndue to expansions in Robotics funding, both built fleets of war robots."
			+ "\nEquipped with advanced weaponry for mass destruction."
			+ "\nUNC's robots were somehow hacked and escaped the facilities. They're "
			+ "\nnearly to Duke University's campus in Durham. This is where we need you."
			+ "\nYou'll control Coach K and try to lead Duke's bots to victory."
			+ "\nAs a 68 year old, Coach K is still strong as steel and can take some damage "
			+ "\nfrom UNC robots. Unfortunately, Duke's bots are not fully built. Every time"
			+ "\na UNC bot is destroyed, however, Duke alumni donate money to finish "
			+ "\na Duke bot. Objective: Survive as long as possible. GOOD LUCK!"
			+ "\n\nDirections:\n\nMove: Arrow Keys (up, down, left, right) or WASD\nShoot: SPACE BAR to shoot straight or CLICK to aim\nBuy Bot: B";
	
	public static void main(String[] args){
		launch(args);
	}

	// Starts game with title and splashscreen with back story
	public void start(Stage s) throws Exception{
		s.setTitle(TITLE);
		Level splash = new SplashScreen(s, BACK_STORY);
		s.show();
	}
}