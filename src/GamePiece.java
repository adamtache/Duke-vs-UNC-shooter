import javafx.animation.TranslateTransition;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class GamePiece {

	private int team; // -1=UNC, 1=Duke
	private Circle circle;
	private Level level;
	private boolean isPlayer;
	private boolean isBullet;
	private double health;
	private double BULLET_INTERVAL = 1000;
	private double age = 0;

// Creates game piece and adjusts color accordingly to team then adds to scene graph
	public GamePiece(Level level, int team, double xCoor, double yCoor, double width, boolean player, boolean bullet, double health){
		this.team = team;
		this.level = level;
		this.isPlayer = player;
		this.isBullet = bullet;
		this.health = health;
		circle = new Circle(width);
		circle.setCenterX(xCoor);
		circle.setCenterY(yCoor);
		if(team == level.getTeamDuke())
			circle.setFill(level.getDukeBlue());
		else
			circle.setFill(level.getUNCBlue());
		level.getRoot().getChildren().add(circle);
		level.getNodeList().add(this);
		if(isBullet)
			circle.toBack();
	}

// Checks age of bullet and if time to fire
	public void step(){
		if(isBullet && age > BULLET_INTERVAL){
			level.remove(this);
		}
		if(health>0 && !isBullet() && !isPlayer()){
			if(age%BULLET_INTERVAL==0)
				fireStraight();
			if(circle.getRadius() == level.getBossRadius()){
				for(int x=10; x<100; x+=10){
					if((age-x*level.getStepTime())%BULLET_INTERVAL==0){
						fireStraight();
					}
				}
			}
		}

	}

	public boolean isPlayer(){
		return isPlayer;
	}

	public boolean isBullet(){
		return isBullet;
	}

	public void addToBack(){
		getCircle().toBack();
	}

	public void addToFront(){
		getCircle().toFront();
	}

	public int getTeam(){
		return team;
	}

	public Circle getCircle(){
		return circle;
	}

	public Level getLevel(){
		return level;
	}

	public double getHealth(){
		return health;
	}

	public void updateHealth(double incr){
		health += incr;
	}

	public void updateAge(double incr){
		age += incr;
	}

// Fires bullet straight in direction depending on team
	public void fireStraight(){
		double X = circle.getBoundsInParent().getMinX();
		double Y = circle.getBoundsInParent().getMinY();
		double bulletX = X + circle.getRadius();
		double bulletY = Y - Math.signum(team)*circle.getRadius();
		GamePiece bullet = new GamePiece(level, team, bulletX, bulletY, level.getBulletSize(), false, true, 1);
		TranslateTransition transition = new TranslateTransition(new Duration(BULLET_INTERVAL), bullet.getCircle());
		transition.setByY(level.getCanvas().getHeight()*-1*Math.signum(team));
		transition.playFromStart();
	}

// Checks if piece is in bounds
	public boolean isInBounds(){
		double X = circle.getCenterX();
		double Y = circle.getCenterY();
		double buffer = 200;
		double low = 0 - buffer;
		double xHigh = level.getCanvas().getWidth() + buffer;
		double yHigh = level.getCanvas().getHeight() + buffer;
		if(X < low || Y < low || X > xHigh || Y > yHigh){
			return false;
		}
		return true;
	}

// Removes piece from game/scene
	public void remove(){
		if(health <= 0 || !isInBounds()){
			level.remove(this);
		}
	}

}