import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SplashScreen extends Level{

	private final double FONT_SIZE = 25;
	String mainText;

	public SplashScreen(Stage stage, String mainText){
		super(stage);
		this.mainText = mainText;
		setupSplash(mainText);
	}

	public void setupSplash(String mainText){
		VBox layout = makeLayout(mainText);
		getRoot().getChildren().add(layout);
	}

	public VBox makeLayout(String mainText){
		VBox layout = new VBox();
		layout.setPrefSize(getSize(), getSize());
		layout.setStyle("-fx-border-color: black");
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().add(makeBanner(FONT_SIZE));
		layout.getChildren().add(makeMain(FONT_SIZE*.8, mainText));
		layout.getChildren().add(makePlay());
		return layout;
	}

	public HBox makeBanner(double fontSize){
		Text Duke = makeText("Duke", fontSize, getDukeBlue());
		Text vs = makeText("Vs.", fontSize, Color.BLACK);
		Text UNC = makeText("UNC", fontSize, getUNCBlue());
		Text Title = makeText("Bots Attack!", fontSize, Color.BLACK);
		HBox text = new HBox(Duke, vs, UNC, Title);
		text.setSpacing(10);
		text.setAlignment(Pos.CENTER);
		text.setPrefWidth(getSize()*.6);

		double width = getSize()*.2;
		ImageView Duke_Logo = makeImage(new ImageView("/images/Duke_Logo.png"), width);
		ImageView UNC_Logo = makeImage(new ImageView("/images/UNC_Logo.png"), width);
		HBox banner = new HBox(Duke_Logo, text, UNC_Logo);
		banner.setPadding(new Insets(10));
		banner.setPrefWidth(getSize());
		banner.setAlignment(Pos.CENTER);
		return banner;
	}

	public HBox makeMain(double size, String mainText){
		Text text = makeText(mainText, size, Color.BLACK);
		HBox center = new HBox(text);
		center.setPadding(new Insets(20));
		center.setAlignment(Pos.CENTER);
		return center;
	}

	public HBox makePlay(){
		Button play = new Button("Play Now");
		play.setStyle("-fx-font-family: 'Ubuntu'; -fx-font-size:"+FONT_SIZE);
		play.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event){
				if(!mainText.equals("You won!")){
					Level one = new LevelOne(getStage());
				}
				else{
					Level two = new LevelTwo(getStage());
				}
			}
		});
		HBox button = new HBox(play);
		button.setAlignment(Pos.CENTER);
		return button;
	}
}