public class Robot extends GamePiece{
	
	private boolean auto_fire;

	public Robot(Level level, int team, double xCoor, double yCoor, double width, boolean isPlayer, boolean isBullet, double health){
		super(level, team, xCoor, yCoor, width, isPlayer, isBullet, health);
	}

}