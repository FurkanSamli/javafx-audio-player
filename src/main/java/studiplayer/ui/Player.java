package studiplayer.ui;

import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import studiplayer.audio.AudioFile;
import studiplayer.audio.PlayList;
import studiplayer.audio.SortCriterion;

public class Player extends Application {
	// constants from pdf
	public static final String DEFAULT_PLAYLIST = "playlists/DefaultPlayList.m3u";
	private static final String PLAYLIST_DIRECTORY = "playlists";
	private static final String INITIAL_PLAY_TIME_LABEL = "00:00";
	private static final String NO_CURRENT_SONG = "-";
	
	private PlayList playList;
	private boolean useCertPlayList = false;
	private boolean isPaused = false;
	
	private Button playButton;
	private Button pauseButton;
	private Button stopButton;
	private Button nextButton;
	private Label playListLabel;
	private Label playTimeLabel;
	private Label currentSongLabel;
	private ChoiceBox<SortCriterion> sortChoiceBox;
	private TextField searchTextField;
	private Button filterButton;
	
	private SongTable table; 
	
	private PlayerThread playerThread;
	private TimerThread timerThread;

	public Player() {
	}

	public void setUseCertPlayList(boolean value) {
		this.useCertPlayList = value;
	}

	public void loadPlayList(String pathname) {
		// load default list if null is passed
		String target = (pathname == null || pathname.isEmpty()) ? DEFAULT_PLAYLIST : pathname;
		
		if (this.playList == null) {
			this.playList = new PlayList(target);
		} else {
			this.playList.loadFromM3U(target); 
		}
		
		// update label safely
		if (this.playListLabel != null) {
			Platform.runLater(() -> this.playListLabel.setText(target));
		}
	}

	// public for junit tests
	public Button createButton(String iconfile) {
		Button button = null;
		try {
			URL url = getClass().getResource("/icons/" + iconfile);
			Image icon = new Image(url.toString());
			ImageView imageView = new ImageView(icon);
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);
			button = new Button("", imageView);
			button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			button.setStyle("-fx-background-color: #fff;");
		} catch (Exception e) {
			System.out.println("Image icons/" + iconfile + " not found!");
			System.exit(-1);
		}
		return button;
	}

	private void updateSongInfo(AudioFile af) {
		Platform.runLater(() -> {
			if (af == null) {
				currentSongLabel.setText(NO_CURRENT_SONG);
				playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			} else {
				currentSongLabel.setText(af.getTitle());
			}
		});
	}

	private void setButtonStates(boolean playBtn, boolean pauseBtn, boolean stopBtn, boolean nextBtn) {
		Platform.runLater(() -> {
			playButton.setDisable(playBtn);
			pauseButton.setDisable(pauseBtn);
			stopButton.setDisable(stopBtn);
			nextButton.setDisable(nextBtn);
		});
	}

	private void startThreads(boolean onlyTimer) {
		if (timerThread == null) {
			timerThread = new TimerThread();
			timerThread.start();
		}
		if (!onlyTimer) {
			if (playerThread == null) {
				playerThread = new PlayerThread();
				playerThread.start();
			}
		}
	}

	private void terminateThreads(boolean onlyTimer) {
		if (timerThread != null) {
			timerThread.terminate();
			timerThread = null;
		}
		if (!onlyTimer) {
			if (playerThread != null) {
				playerThread.terminate();
				playerThread = null;
			}
		}
	}

	private void playCurrentSong() {
		AudioFile af = playList.currentAudioFile();
		if (af != null) {
			isPaused = false;
			updateSongInfo(af);
			setButtonStates(true, false, false, false);
			startThreads(false); 
		}
	}

	private void pauseCurrentSong() {
		AudioFile af = playList.currentAudioFile();
		if (af != null) {
			af.togglePause(); 
			if (!isPaused) {
				isPaused = true;
				// disable play while paused
				setButtonStates(true, false, false, false); 
				terminateThreads(true); 
			} else {
				isPaused = false;
				setButtonStates(true, false, false, false);
				startThreads(true); 
			}
		}
	}

	private void stopCurrentSong() {
		AudioFile af = playList.currentAudioFile();
		if (af != null) {
			isPaused = false;
			setButtonStates(false, true, true, false);
			terminateThreads(false);
			af.stop();
			Platform.runLater(() -> playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL));
		}
	}

	private void nextSong() {
		stopCurrentSong();
		playList.nextSong();
		playCurrentSong();
	}

	@Override
	public void start(Stage stage) throws Exception {
		// load initial list
		loadPlayList(null); 

		playButton = createButton("play.jpg");
		pauseButton = createButton("pause.jpg");
		stopButton = createButton("stop.jpg");
		nextButton = createButton("next.jpg");
		
		playListLabel = new Label(DEFAULT_PLAYLIST);
		playTimeLabel = new Label(INITIAL_PLAY_TIME_LABEL);
		currentSongLabel = new Label(NO_CURRENT_SONG);
		
		sortChoiceBox = new ChoiceBox<>();
		sortChoiceBox.getItems().addAll(SortCriterion.values());
		sortChoiceBox.setValue(sortChoiceBox.getItems().get(0));
		
		searchTextField = new TextField();
		filterButton = new Button("display");

		filterButton.setOnAction(e -> {
			playList.setSearch(searchTextField.getText());
			playList.setSortCriterion(sortChoiceBox.getValue());
			table.refreshSongs();
		});

		table = new SongTable(playList);

		playButton.setOnAction(e -> playCurrentSong());
		pauseButton.setOnAction(e -> pauseCurrentSong());
		stopButton.setOnAction(e -> stopCurrentSong());
		nextButton.setOnAction(e -> nextSong());

		table.setRowSelectionHandler(e -> {
			Song selectedRow = table.getSelectionModel().getSelectedItem();
			if (selectedRow != null) {
				AudioFile af = selectedRow.getAudioFile();
				stopCurrentSong();
				playList.jumpToAudioFile(af);
				playCurrentSong();
			}
		});

		// set initial buttons
		setButtonStates(false, true, true, false); 

		BorderPane mainPane = new BorderPane();
		
		// setup top area
		HBox filterBox = new HBox(10);
		filterBox.setAlignment(Pos.CENTER_LEFT);
		filterBox.getChildren().addAll(new Label("Search text"), searchTextField, new Label("Sort by"), sortChoiceBox, filterButton);
		TitledPane filterPane = new TitledPane("Filter", filterBox);
		mainPane.setTop(filterPane);
		
		mainPane.setCenter(table);
		
		// setup bottom area
		GridPane infoGrid = new GridPane();
		infoGrid.setHgap(10);
		infoGrid.setVgap(5);
		infoGrid.add(new Label("Playlist"), 0, 0);
		infoGrid.add(playListLabel, 1, 0);
		infoGrid.add(new Label("Current Song"), 0, 1);
		infoGrid.add(currentSongLabel, 1, 1);
		infoGrid.add(new Label("Playtime"), 0, 2);
		infoGrid.add(playTimeLabel, 1, 2);
		
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(playButton, pauseButton, stopButton, nextButton);
		
		VBox bottomBox = new VBox(10);
		bottomBox.getChildren().addAll(infoGrid, buttonBox);
		mainPane.setBottom(bottomBox);

		Scene scene = new Scene(mainPane, 600, 500);
		stage.setScene(scene);
		stage.setTitle("APA Player");
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	// thread for audio playback
	private class PlayerThread extends Thread {
		private boolean stopped = false;

		public void terminate() {
			stopped = true;
		}

		@Override
		public void run() {
			while (!stopped) {
				AudioFile af = playList.currentAudioFile();
				if (af != null) {
					Platform.runLater(() -> table.selectSong(af));
					try {
						af.play(); 
					} catch (Exception e) {
						e.printStackTrace();
					}
					// move to next song automatically if not stopped
					if (!stopped) {
						playList.nextSong();
						Platform.runLater(() -> updateSongInfo(playList.currentAudioFile()));
					}
				} else {
					stopped = true;
				}
			}
		}
	}

	// thread for updating play time
	private class TimerThread extends Thread {
		private boolean stopped = false;

		public void terminate() {
			stopped = true;
		}

		@Override
		public void run() {
			while (!stopped) {
				AudioFile af = playList.currentAudioFile();
				if (af != null) {
					Platform.runLater(() -> {
						try {
							playTimeLabel.setText(af.formatPosition());
						} catch (Exception e) {
						}
					});
				}
				try {
					Thread.sleep(100); 
				} catch (InterruptedException e) {
				}
			}
		}
	}
}