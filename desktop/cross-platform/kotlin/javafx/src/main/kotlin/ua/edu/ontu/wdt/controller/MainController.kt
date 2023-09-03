package ua.edu.ontu.wdt.controller

import javafx.application.Platform.runLater
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.TOP_CENTER
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import ua.edu.ontu.wdt.configuration.AsyncConfiguration
import ua.edu.ontu.wdt.configuration.Logger
import ua.edu.ontu.wdt.configuration.ui.UiObserverAndMessageConfiguration
import ua.edu.ontu.wdt.controller.template.DeviceTemplateController
import ua.edu.ontu.wdt.controller.template.FileOrFolderTemplateController
import ua.edu.ontu.wdt.layer.IDeviceSearcher
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener
import ua.edu.ontu.wdt.layer.factory.DeviceRequestAbstractFactory
import ua.edu.ontu.wdt.layer.factory.DeviceRequestListenerFactory
import ua.edu.ontu.wdt.layer.factory.DeviceSearcherFactory
import ua.edu.ontu.wdt.layer.ui.IUiGenericObserver
import ua.edu.ontu.wdt.service.ApplicationContext.Companion.buildContext
import ua.edu.ontu.wdt.service.ApplicationContext.Companion.createApplicationYamlPath
import ua.edu.ontu.wdt.service.ApplicationContext.Companion.readYaml
import ua.edu.ontu.wdt.service.FileAndFolderRememberService
import ua.edu.ontu.wdt.service.UiLocalizationService
import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.util.*

class MainController {

    @FXML
    private lateinit var resources: ResourceBundle

    @FXML
    private lateinit var location: URL

    @FXML
    private lateinit var vBoxForContent: VBox

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

    private val applicationContext = buildContext(readYaml(FileInputStream(createApplicationYamlPath())))
    private val localizationService = UiLocalizationService(applicationContext)
    private val fileAndFolderRememberService = FileAndFolderRememberService()
    private val uiConfig = UiObserverAndMessageConfiguration(this.localizationService)

    private fun updateFilesAndFolders(vararg files: File) {
        this.fileAndFolderRememberService.rememberFiles(*files)
        this.vBoxForContent.children.clear()
        for (file in this.fileAndFolderRememberService.getAllFiles()) {
            runLater {
                val fxml = FXMLLoader()
                fxml.location = MainController::class.java.classLoader.getResource("file-or-folder-template.fxml")!!
                fxml.setController(FileOrFolderTemplateController(file.name, file.isDirectory))
                this.vBoxForContent.alignment = TOP_CENTER
                this.vBoxForContent.children.add(fxml.load())
            }
        }
    }

    @FXML
    fun anchorPaneMouseClicked(event: MouseEvent) {
        if (this.fileAndFolderRememberService.isEmpty()) {
            val fileChooser = FileChooser()
            fileChooser.title = this.localizationService.getByKey("ui.main.file-chooser.title")
            fileChooser.extensionFilters.addAll(ExtensionFilter("All Files", "*.*"))
            val chosenFiles = fileChooser.showOpenMultipleDialog(this.vBoxForContent.scene.window)
            chosenFiles?.let {
                this.updateFilesAndFolders(*it.toTypedArray())
            }
        }
    }

    @FXML
    fun dragDropped(event: DragEvent) {
        if (event.dragboard.hasFiles()) {
            event.dragboard.files?.let {
                this.updateFilesAndFolders(*it.toTypedArray())
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
        TODO()
    }

    @FXML
    fun sendDataAction(event: ActionEvent) {
        this.fileAndFolderRememberService.cleanAllFiles()
        this.vBoxForContent.children.clear()
        this.vBoxForContent.alignment = CENTER
        this.vBoxForContent.children.add(Text("test").run {
            this.textAlignment = TextAlignment.CENTER
            this.fill = Color.web("#0000007e")
            this.layoutX = 137.0
            this.layoutY = 187.0
            this
        })
        this.rescanButton.isDisable = true
        this.rescanButton.isVisible = false
        this.progressBar.progress = 0.0
        this.progressBar.isVisible = false
    }

    @FXML
    fun initialize() {
        val asyncConfiguration = AsyncConfiguration()
        asyncConfiguration.runAsync {
            this.progressBar.progress = 0.0
            DeviceRequestListenerFactory.createDeviceListener(
                this.applicationContext, asyncConfiguration, this.uiConfig, Logger(IDeviceRequestListener::class.java)
            ).serve()
            this.nextButton.setOnAction {
                if (!this.fileAndFolderRememberService.isEmpty()) {
                    runLater {
                        this.vBoxForContent.children.clear()
                    }
                    var maxIndex = 0.toByte()
                    this.uiConfig.onProgressObserver = IUiGenericObserver {
                        if (maxIndex < it) {
                            maxIndex = it
                            runLater {
                                this.progressBar.progress = maxIndex.toDouble() / 100.0
                            }
                        }
                    }
                    this.uiConfig.onNewDevice = IUiGenericObserver {
                        runLater {
                            val fxml = FXMLLoader()
                            fxml.location = MainController::class.java.classLoader.getResource("device-template.fxml")!!
                            fxml.setController(DeviceTemplateController(it) { _, deviceInfo ->
                                runLater {
                                    this.vBoxForContent.children.clear()
                                    this.vBoxForContent.alignment = CENTER
                                    val fileProgressFxml = FXMLLoader()
                                    fileProgressFxml.location =
                                        MainController::class.java.classLoader.getResource("file-progress-template.fxml")!!
                                    this.vBoxForContent.children.add(fileProgressFxml.load())
                                    asyncConfiguration.runAsync {
                                        DeviceRequestAbstractFactory.createDeviceRequestFactory(
                                            this.applicationContext,
                                            asyncConfiguration,
                                            this.uiConfig,
                                            Logger(this.javaClass)
                                        ).createSendFileRequestBuilder()
                                            .files(*this.fileAndFolderRememberService.getAllFiles().toTypedArray())
                                            .ip(deviceInfo.ip).build().doRequest()
                                    }
                                }
                            })
                            this.vBoxForContent.children.add(fxml.load())
                        }
                    }
                    DeviceSearcherFactory.createDeviceSearcher(
                        this.applicationContext, asyncConfiguration, this.uiConfig, Logger(IDeviceSearcher::class.java)
                    ).search()
                }
            }
        }
    }
}
