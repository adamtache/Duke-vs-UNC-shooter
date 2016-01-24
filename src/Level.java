import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Level{
	private final int SIZE = 700;
	private final int TEAM_DUKE = 1;
	private final int TEAM_UNC = -1;

	private final Duration TRANSLATE_DURATION = Duration.seconds(2);

	private Group root;
	private Stage stage;
	private Scene scene;
	private Canvas canvas;
	private ArrayList<GamePiece> nodeList;

	private final double MIN_TRANSLATE_DURATION = 2000;
	private final double MAX_TRANSLATE_DURATION = 10000;

	public final int FRAMES_PER_SECOND = 60;
	private final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;

	private final double BOT_RADIUS = 25;
	private final int BULLET_SIZE = 10;
	private double COACH_K_HEALTH = 5;
	private final double COACH_K_RADIUS = 25;
	private final double BOT_COST = 20;
	private final double BOT_KILL = 10;
	private final int BOT_HEALTH = 1;
	private double BOSS_RADIUS;
	private boolean bossDead = false;
	private double STEP_TIME;

	private Robot CoachK;

	private final boolean IS_PLAYER = true;
	private final boolean IS_BULLET = true;
	private final int VERTICAL = 1;
	private final int HORIZONTAL = 2;
	private double timeElapsed = 0;
	Timeline animation;

	private double numEnemies = 0;
	private double numEnemiesLeft = 0;
	private double money = 0;
	ArrayList<HBox> info = new ArrayList<HBox>();
	HBox bars = new HBox();

	// Level constructed with JavaFX scene graph and scene with font from Google fonts
	// nodeList created to hold the game's nodes (GamePiece objects)
	public Level(Stage stage){
		this.stage = stage;
		root = new Group();
		canvas = new Canvas(SIZE, SIZE);
		root.getChildren().add(canvas);
		scene = new Scene(root, Color.BLANCHEDALMOND);
		scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Ubuntu");
		stage.setScene(scene);
		nodeList = new ArrayList<GamePiece>();
	}

	// Removes out of bounds node from scene graph
	public void garbageCollect(){
		for(int i=0; i<nodeList.size(); i++){
			nodeList.get(i).remove();
		}
	}
	
	// Creates progress bars representing user statistics
	public void createUserInfo(){
		addBar(CoachK.getHealth()/COACH_K_HEALTH, "Health: ");
		addBar(money/125, "Money: ");
		addBar(numEnemiesLeft/numEnemies, "Enemies Left: ");
	}

	
	// Helper method for making generic progress bar with text label
	public HBox makeProgressBar(double initial, String label){
		ProgressBar pb = new ProgressBar(initial);
		Label text = new Label(label);
		HBox progressBar = new HBox(text, pb);
		progressBar.setSpacing(5);
		return progressBar;
	}

	// Removes old progress bars and replaces with new updated ones
	public void updateInfo(){
		if(getRoot().getChildren().contains(bars))
			getRoot().getChildren().remove(bars);
		bars = new HBox();
		info.clear();
		createUserInfo();
		for(int x=0; x<info.size(); x++){
			bars.getChildren().add(info.get(x));
		}
		bars.setSpacing(5);
		bars.setPadding(new Insets(10));
		getRoot().getChildren().add(bars);
	}
	
	// Add progress bar with certain message and progress
	public void addBar(double progress, String message){
		info.add(makeProgressBar(progress, message));
	}

	// Checks for end state in current game
	public void endCheck(Timeline animation){
		if(CoachK.getHealth() <= 0)
			endGame(animation, "Game over.");
		int numUNCLeft = 0;
		for(int x=0; x<nodeList.size(); x++){
			GamePiece current = nodeList.get(x);
			if(current.getTeam() == getTeamUNC() && !current.isBullet())
				numUNCLeft++;
		}
		if(numUNCLeft==0  && !bossDead)
			endGame(animation, "You won!");
		if(numUNCLeft==0 && bossDead)
			endGame(animation, "Congratulations! You won. Play again?");
	}

	public double getTimeElapsed(){
		return timeElapsed;
	}

	public void step(Timeline animation){
		timeElapsed += animation.getCurrentTime().toMillis();
		STEP_TIME = animation.getCurrentTime().toMillis();
		updatePieces(animation);
		endCheck(animation);
		garbageCollect();
		collisionDetection();
	}

	// Updates age of node to be one keyframe later
	public void updatePieces(Timeline animation){
		for(int x=0; x<nodeList.size(); x++){
			nodeList.get(x).updateAge(animation.getCurrentTime().toMillis());
			nodeList.get(x).step();
		}
	}

	public void collisionDetection(){
		for(int x=0; x<nodeList.size(); x++){
			GamePiece current = nodeList.get(x);
			current.step();
			for(int y=x+1; y<nodeList.size(); y++){
				GamePiece next = nodeList.get(y);
				Shape intersect = Shape.intersect(current.getCircle(), next.getCircle());
				if (intersect.getBoundsInLocal().getWidth() != -1) {
					if(current.getTeam() != next.getTeam()){
						if(!next.isPlayer() && next.isBullet())
							current.updateHealth(-1);
						if(!current.isPlayer())
							next.updateHealth(-1);
						if(current.isPlayer() || next.isPlayer())
							updateInfo();
					}
				}
			}
		}
	}

	public void play(){
		animation = new Timeline();
		KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(animation));
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.getKeyFrames().add(frame);	
		animation.play();
		getNumEnemies();
		updateInfo();
	}

	public void getNumEnemies(){
		int numUNC = 0;
		for(int x=0; x<nodeList.size(); x++){
			if(nodeList.get(x).getTeam() == TEAM_UNC)
				numUNC++;
		}
		numEnemies = numUNC;
		numEnemiesLeft = numUNC;
	}

	// Checks if there are any UNC bots left
	public boolean checkFinished(){
		for(int x=0; x<getNodeList().size(); x++){
			GamePiece current = getNodeList().get(x);
			if(!current.isBullet() && !current.isPlayer() && current.getTeam()!=getTeamDuke())
				return false;
		}
		return true;
	}

	public Canvas getCanvas(){
		return canvas;
	}

	public int getBulletSize(){
		return BULLET_SIZE;
	}

	// Removes piece from game and checks if piece is the boss (for level 2)
	public void remove(GamePiece piece){
		getRoot().getChildren().remove(piece.getCircle());
		getNodeList().remove(piece);
		if(piece.getCircle().getRadius() == BOSS_RADIUS)
			bossDead = true;
		if(!piece.isBullet() && piece.getTeam() == TEAM_UNC)
			botKill();
	}

	// If user kills bot, update money and enemies progress bar
	public void botKill(){
		money+= BOT_KILL;
		numEnemiesLeft--;
		updateInfo();
	}

	// End the game and display certain title
	public void endGame(Timeline animation, String title){
		new SplashScreen(stage, title);
		animation.stop();
	}

	// Get width/height of canvas
	public int getSize(){
		return SIZE;
	}
	
	public double getStepTime(){
		return STEP_TIME;
	}

	public Duration getTranslateDuration(){
		return TRANSLATE_DURATION;
	}

	// Returns current list of game nodes
	public ArrayList<GamePiece> getNodeList(){
		return nodeList;
	}

	public Group getRoot(){
		return root;
	}

	public Stage getStage(){
		return stage;
	}

	public Scene getScene(){
		return scene;
	}

	public Color getDukeBlue(){
		return Color.web("#001A57");
	}

	public Color getUNCBlue(){
		return Color.web("#7BAFD4");
	}

	public Text makeText(String title, double size, Color color){
		Text text = new Text(title);
		text.setStyle("-fx-font-family: 'Ubuntu'; -fx-font-size:"+size);
		text.setFill(color);
		text.setEffect(getDropShadow());
		return text;
	}

	public ImageView makeImage(ImageView view, double width){
		view.setFitWidth(width);
		view.setPreserveRatio(true);
		view.setEffect(getDropShadow());
		return view;
	}

	public DropShadow getDropShadow(){
		DropShadow ds = new DropShadow();
		ds.setOffsetY(3.0f);
		ds.setColor(Color.color(0.4f,  0.4f,  0.4f));
		return ds;
	}

	public int getTeamDuke(){
		return TEAM_DUKE;
	}

	public int getTeamUNC(){
		return TEAM_UNC;
	}

	// Checks if user has enough money to buy bot and acts accordingly. If purchased, updates money bar
	public void buyBot(){
		if(money >= BOT_COST){
			makePurchase(BOT_COST);
			generateBots(1, getTeamDuke(), HORIZONTAL, BOT_RADIUS, BOT_HEALTH);
			updateInfo();
		}
	}

	// Creates Coach K user controllable game piece
	public void makeCoachK(){
		CoachK = new Robot(this, getTeamDuke(), canvas.getWidth()/2, canvas.getHeight()-2*COACH_K_RADIUS, COACH_K_RADIUS, IS_PLAYER, !IS_BULLET, COACH_K_HEALTH);
		Image img = new Image("/images/CoachK.png");
		CoachK.getCircle().setFill(new ImagePattern(img));
	}

	// Creates boss for level 2
	public void generateBoss(int BOSS_HEALTH, double BOSS_RADIUS){
		this.BOSS_RADIUS = BOSS_RADIUS;
		generateBots(1, TEAM_UNC, HORIZONTAL, BOSS_RADIUS, BOSS_HEALTH);
	}

	// Creates given number of bots on given team with given movement (horizontal or vertical) with given radius and amount of health
	public void generateBots(int numBots, int team, int movement, double radius, int health){
		for(int x=0; x<numBots; x++){
			double randX = Math.random()*getCanvas().getWidth();
			double randY = -1*BOT_RADIUS*2;
			if(team==getTeamDuke())
				randY = getCanvas().getHeight() - BOT_RADIUS*2;
			Robot bot = new Robot(this, team, randX, randY, radius, false, false, health);
			addBot(bot, movement);
		}
	}
	
	public double getBossRadius(){
		return BOSS_RADIUS;
	}

	public double getBotRadius(){
		return BOT_RADIUS;
	}

	public int getBotHealth(){
		return BOT_HEALTH;
	}

	// Adds bot to game with certain movement determine which type of path it takes once added (vertical or horizontal)
	public void addBot(Robot bot, int movement){
		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(javafx.util.Duration.millis(Math.random()*MAX_TRANSLATE_DURATION + MIN_TRANSLATE_DURATION));
		pathTransition.setPath(createPath(bot, movement));
		pathTransition.setNode(bot.getCircle());
		pathTransition.setOnFinished(e->{
			pathTransition.setPath(createPath(bot, movement));
			pathTransition.play();
		});
		pathTransition.play();
	}

	// Creates horizontal or vertical random path for bot to take automatically 
	public Path createPath(Robot bot, int movement){
		double radius = bot.getCircle().getRadius();
		double sign = Math.signum(bot.getTeam());
		double newX = (int) (Math.random()*getSize());
		double newY = bot.getCircle().getCenterY();;
		Path path = new Path();
		if(movement == VERTICAL){
			double startX = (int) (Math.random()*canvas.getWidth());
			newY = -1*sign*(getSize()+2*radius);
			path.getElements().add(new MoveTo(startX, -1*radius*2));
			path.getElements().add(new LineTo(newX, newY));
		}
		else if(movement == HORIZONTAL){
			double startX = -100;
			double endX = canvas.getWidth() + 100;
			newY = newY-100*Math.signum(bot.getTeam());
			path.getElements().add(new MoveTo(startX, newY));
			path.getElements().add(new LineTo(endX, newY));
		}
		return path;
	}

	public double getMoney(){
		return money;
	}

	public void makePurchase(double cost){
		money -= cost;
	}

	// Allows user to control Coach K with mouse and fire if click
	public void addMouseController(){
		scene.setOnMousePressed(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				clickFire(event);
			}
		});
	}

	// Allows user to control Coach K with keys and act accordingly
	public void addKeysController(){
		scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent key){
				int distance = 7;
				switch(key.getCode()){
				case UP: CoachK.getCircle().setCenterY(CoachK.getCircle().getCenterY()-distance); break;
				case W: CoachK.getCircle().setCenterY(CoachK.getCircle().getCenterY()-distance); break;
				case RIGHT: CoachK.getCircle().setCenterX(CoachK.getCircle().getCenterX()+distance); break;
				case D: CoachK.getCircle().setCenterX(CoachK.getCircle().getCenterX()+distance); break;
				case DOWN: CoachK.getCircle().setCenterY(CoachK.getCircle().getCenterY()+distance); break;
				case S: CoachK.getCircle().setCenterY(CoachK.getCircle().getCenterY()+distance); break;
				case LEFT: CoachK.getCircle().setCenterX(CoachK.getCircle().getCenterX()-distance); break;
				case A: CoachK.getCircle().setCenterX(CoachK.getCircle().getCenterX()-distance); break;
				case SPACE: CoachK.fireStraight(); break;
				case B: buyBot(); break;
				case O: endGame(animation, "You won!"); break;
				case T: endGame(animation, "Congratulations! You won. Play again?"); break;
				default:
					break;
				}
			}
		});
	}

	// Fires bullet in straight line toward clicked spot then extending that line by determining slope
	public void clickFire(MouseEvent e){
		GamePiece bullet = new GamePiece(this, getTeamDuke(), CoachK.getCircle().getCenterX(), CoachK.getCircle().getCenterY(), BULLET_SIZE, !IS_PLAYER, IS_BULLET, 1);
		TranslateTransition transition = new TranslateTransition(TRANSLATE_DURATION, bullet.getCircle());
		double oldX = bullet.getCircle().getCenterX();
		double oldY = bullet.getCircle().getCenterY();
		double clickX = e.getX();
		double clickY = e.getY();
		double slope = (clickY-oldY)/(clickX-oldX);
		double xDiff = clickX - oldX;
		double newX = clickX + Math.signum(xDiff)*getCanvas().getWidth()*5;
		double newY = slope*newX;
		if(Math.abs(slope)==Double.POSITIVE_INFINITY)
			newY = clickY + Math.signum(newY)*getCanvas().getHeight();
		transition.setToX(newX);
		transition.setToY(newY);
		transition.playFromStart();
	}

	// Returns vertical movement (1)
	public int getVertical(){
		return VERTICAL;
	}

	// Returns horizontal movement (2)
	public int getHorizontal(){
		return HORIZONTAL;
	}

}