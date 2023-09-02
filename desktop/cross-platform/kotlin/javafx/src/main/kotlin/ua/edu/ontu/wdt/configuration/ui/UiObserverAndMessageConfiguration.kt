package ua.edu.ontu.wdt.configuration.ui

import javafx.application.Platform.runLater
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.*
import javafx.scene.control.ButtonBar.ButtonData.NO
import javafx.scene.control.ButtonBar.ButtonData.YES
import javafx.scene.control.ButtonType
import javafx.stage.StageStyle.TRANSPARENT
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.ConfirmFileDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import ua.edu.ontu.wdt.layer.ui.IUiGenericConfirmMessage
import ua.edu.ontu.wdt.layer.ui.IUiGenericObserver
import ua.edu.ontu.wdt.layer.ui.IUiObserver
import ua.edu.ontu.wdt.layer.ui.IUiObserverAndMessageConfiguration
import ua.edu.ontu.wdt.service.UiLocalizationService
import java.util.concurrent.atomic.AtomicBoolean

class UiObserverAndMessageConfiguration(
    private val localizationService: UiLocalizationService,
) : IUiObserverAndMessageConfiguration {

    var onNewDevice: IUiGenericObserver<GetInfoDto> = IUiGenericObserver {}
    var onProgressObserver: IUiGenericObserver<Byte> = IUiGenericObserver {}
    var onCancelObserver: IUiGenericObserver<AtomicBoolean> = IUiGenericObserver {}

    private fun defaultAlertConfig(alert: Alert, title: String, contentText: String) {
        alert.initStyle(TRANSPARENT)
        alert.title = title
        alert.contentText = contentText
    }

    private fun createConfirmAlert(title: String, contentText: String, onAccept: () -> Unit, onCancel: () -> Unit) =
        runLater {
            Alert(CONFIRMATION).let { alert ->
                this.defaultAlertConfig(alert, title, contentText)
                alert.buttonTypes.addAll(
                    ButtonType(this.localizationService.getByKey("ui.word.yes"), YES),
                    ButtonType(this.localizationService.getByKey("ui.word.no"), NO)
                )
                alert.showAndWait().ifPresent {
                    when (it.buttonData) {
                        YES -> onAccept()
                        else -> onCancel()
                    }
                }
            }
        }


    private fun createErrorAlert(title: String, contentText: String) = runLater {
        Alert(ERROR).let { alert ->
            this.defaultAlertConfig(alert, title, contentText)
            alert.show()
        }
    }

    private fun createInfoAlert(title: String, contentText: String) = runLater {
        Alert(INFORMATION).let { alert ->
            this.defaultAlertConfig(alert, title, contentText)
            alert.show()
        }
    }

    override fun createProgressObserverForSendFileRule(): IUiGenericObserver<FileProgressDto> = IUiGenericObserver {
//        TODO()
    }

    override fun createFinishObserverForSendFileRule(): IUiObserver = IUiObserver {
        this.createInfoAlert(
            this.localizationService.getByKey("ui.info.sending-files-are-finished"),
            this.localizationService.getByKey("ui.info.you-sent-all-data")
        )
    }

    override fun createProblemObserverForSendFileRule(): IUiGenericObserver<String> = IUiGenericObserver {
//        TODO()
    }

    override fun createCancelObserverForSendFileRule(): IUiGenericObserver<AtomicBoolean> = IUiGenericObserver {
//        TODO()
    }

    override fun createConfirmFileMessage(): IUiGenericConfirmMessage<ConfirmFileDto> =
        IUiGenericConfirmMessage { dto, onAccept, onCancel ->
            val contentText = "${dto.files} ${this.localizationService.getByKey("ui.word.file")}, " + "${dto.folders} ${
                this.localizationService.getByKey("ui.word.folder")
            }"
            this.createConfirmAlert(
                this.localizationService.getByKey("ui.confirm.accept-file"), contentText, onAccept, onCancel
            )
        }

    override fun createBeforeSendCommonObserver(): IUiGenericObserver<GetInfoDto> = IUiGenericObserver {
//        TODO()
    }

    override fun createCancelObserver(): IUiGenericObserver<AtomicBoolean> = this.onCancelObserver

    @Synchronized
    override fun createUiProgressObserver(): IUiGenericObserver<Byte> = this.onProgressObserver

    override fun createUiNewDeviceInfoObserver(): IUiGenericObserver<GetInfoDto> = this.onNewDevice
}