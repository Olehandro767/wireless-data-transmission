package ua.edu.ontu.wdt.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.AnchorPane
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import ua.edu.ontu.wdt.service.FileAndFolderRememberService
import java.net.URL
import java.util.*

class MainController {

    @FXML
    private lateinit var resources: ResourceBundle

    @FXML
    private lateinit var location: URL

    @FXML
    private lateinit var anchorPaneForContent: AnchorPane

    @FXML
    private lateinit var backOrClearButton: Button

    @FXML
    private lateinit var getClipboardButton: Button

    @FXML
    private lateinit var nextButton: Button

    @FXML
    private lateinit var progressBar: ProgressBar

    @FXML
    private lateinit var rescanButton: Button

    @FXML
    private lateinit var sendDataButton: Button

    private val fileAndFolderRememberService = FileAndFolderRememberService()

    @FXML
    fun anchorPaneMouseClicked(event: MouseEvent) {
        if (this.fileAndFolderRememberService.isEmpty()) {
            val fileChooser = FileChooser()
            fileChooser.title = "Choose files"
            fileChooser.extensionFilters.addAll(ExtensionFilter("All Files", "*.*"))
            val chosenFiles = fileChooser.showOpenMultipleDialog(this.anchorPaneForContent.scene.window)
            chosenFiles?.let {
                this.fileAndFolderRememberService.rememberFiles(*it.toTypedArray())
            }
        }
    }

    @FXML
    fun dragDropped(event: DragEvent) {
        if (event.dragboard.hasFiles()) {
            event.dragboard.files?.let {
                this.fileAndFolderRememberService.rememberFiles(*it.toTypedArray())
            }
        }
    }

    @FXML
    fun dragOver(event: DragEvent) {
        if (event.dragboard.hasFiles()) {
            event.acceptTransferModes(*TransferMode.ANY)
        }
    }

    @FXML
    fun getClipboardAction(event: ActionEvent) {
    }

    @FXML
    fun sendDataAction(event: ActionEvent) {
    }

    @FXML
    fun initialize() {
    }
}
