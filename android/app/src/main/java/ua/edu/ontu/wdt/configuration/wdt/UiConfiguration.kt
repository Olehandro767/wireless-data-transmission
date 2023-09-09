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

    override fun createProgressObserverForSendFileRule(): IUiGenericObserver<FileProgressDto> =
        IUiGenericObserver { }

    override fun createFinishObserverForSendFileRule(): IUiObserver = IUiObserver { }

    override fun createProblemObserverForSendFileRule(): IUiGenericObserver<String> =
        IUiGenericObserver { }

    override fun createCancelObserverForSendFileRule(): IUiGenericObserver<AtomicBoolean> =
        IUiGenericObserver { }

    override fun createConfirmFileMessage(): IUiGenericConfirmMessage<ConfirmFileDto> =
        IUiGenericConfirmMessage { dto, onAccept, onCancel -> }

    override fun createBeforeSendCommonObserver(): IUiGenericObserver<GetInfoDto> =
        IUiGenericObserver { }

    override fun createCancelObserver(): IUiGenericObserver<AtomicBoolean> = this.onCancel

    override fun createUiDeviceSearchProgressObserver(): IUiGenericObserver<Byte> =
        this.onDeviceSearchProgressObserver

    override fun createUiNewDeviceInfoObserver(): IUiGenericObserver<GetInfoDto> =
        this.onNewDeviceObserver

    override fun createFinishObserverForSendClipboardRule(): IUiObserver = IUiObserver { }
    override fun createProblemObserverForClientListener(): IUiGenericObserver<Exception> =
        IUiGenericObserver { }
}