package ua.edu.ontu.wdt.controller.template

import javafx.fxml.FXML
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.text.Text

class FileOrFolderTemplateController(
    private val text: String,
    private val isFolder: Boolean,
) {

    @FXML
    private lateinit var imageView: ImageView

    @FXML
    private lateinit var textArea: Text

    @FXML
    fun initialize() {
        this.textArea.text = text
        this.imageView.image =
            Image(this.javaClass.classLoader.getResourceAsStream(if (this.isFolder) "folder.png" else "document.png"))
    }
}