package ua.edu.ontu.wdt.controller

import javafx.fxml.FXML
import javafx.scene.control.Label

@Deprecated("hc")
class HelloController {
    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private fun onHelloButtonClick() {
        welcomeText.text = "Welcome to JavaFX Application!"
    }
}