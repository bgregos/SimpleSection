package myapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class RatingsTab {
	@FXML private WebView webview;
	@FXML private Button backButton;
	@FXML private Button forwardButton;
	@FXML private Button refreshButton;
	private WebEngine webEngine;

	public void initialize(){
		webEngine = webview.getEngine();
		webEngine.load("https://www.koofers.com/virginia-tech-vt/professors");
	}

	public void onBack() {
		webEngine.getHistory().go(-1);
	}

	public void onForward() {
		webEngine.getHistory().go(1);
	}

	public void refresh(){
		webEngine.reload();
	}
}