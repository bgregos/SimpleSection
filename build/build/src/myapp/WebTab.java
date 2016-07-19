package myapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class WebTab {
	@FXML private WebView webview;
	@FXML private Button backButton;
	@FXML private Button forwardButton;
	private WebEngine webEngine;

	public WebTab(){
		webEngine=webview.getEngine();
		webEngine.load("https://www.koofers.com/virginia-tech-vt/professors");
	}

	public void onBack(){
		webEngine.getHistory().go(-1);
	}

	public void onForward(){
		webEngine.getHistory().go(1);
	}
}
