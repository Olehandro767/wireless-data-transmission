package ua.edu.ontu.wdt.configuration.wdt

import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.ConfirmFileDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import ua.edu.ontu.wdt.layer.ui.IUiGenericConfirmMessage
import ua.edu.ontu.wdt.layer.ui.IUiGenericObserver
import ua.edu.ontu.wdt.layer.ui.IUiObserver
import ua.edu.ontu.wdt.layer.ui.IUiObserverAndMessageConfiguration
import java.util.concurrent.atomic.AtomicBoolean

class UiConfiguration : IUiObserverAndMessageConfiguration {

    var onCancel = IUiGenericObserver<AtomicBoolean> {}
    var onNewDeviceObserver = IUiGenericObserver<GetInfoDto> {}
    var onDeviceSearchProgressObserver = IUiGenericObserver<Byte> {}

    override fun createProgressObserverForSendFileRule(): IUiGenericObserver<FileProgressDto> {
        TODO()
    }

    override fun createFinishObserverForSendFileRule(): IUiObserver {
        TODO("Not yet implemented")
    }

    override fun createProblemObserverForSendFileRule(): IUiGenericObserver<String> {
        TODO("Not yet implemented")
    }

    override fun createCancelObserverForSendFileRule(): IUiGenericObserver<AtomicBoolean> {
        TODO("Not yet implemented")
    }

    override fun createConfirmFileMessage(): IUiGenericConfirmMessage<ConfirmFileDto> {
        TODO("Not yet implemented")
    }

    override fun createBeforeSendCommonObserver(): IUiGenericObserver<GetInfoDto> {
        TODO("Not yet implemented")
    }

    override fun createCancelObserver(): IUiGenericObserver<AtomicBoolean> = this.onCancel

    override fun createUiDeviceSearchProgressObserver(): IUiGenericObserver<Byte> =
        this.onDeviceSearchProgressObserver

    override fun createUiNewDeviceInfoObserver(): IUiGenericObserver<GetInfoDto> =
        this.onNewDeviceObserver
}