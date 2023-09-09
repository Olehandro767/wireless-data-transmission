package ua.edu.ontu.wdt.layer.ui

import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.ConfirmFileDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import java.util.concurrent.atomic.AtomicBoolean

interface IUiObserverAndMessageConfiguration {

    fun createProgressObserverForSendFileRule(): IUiGenericObserver<FileProgressDto>

    fun createFinishObserverForSendFileRule(): IUiObserver

    fun createProblemObserverForSendFileRule(): IUiGenericObserver<String>

    @Deprecated("use more universal")
    fun createCancelObserverForSendFileRule(): IUiGenericObserver<AtomicBoolean>

    fun createConfirmFileMessage(): IUiGenericConfirmMessage<ConfirmFileDto>

    @Deprecated("use more universal")
    fun createBeforeSendCommonObserver(): IUiGenericObserver<GetInfoDto>

    fun createCancelObserver(): IUiGenericObserver<AtomicBoolean>

    fun createUiDeviceSearchProgressObserver(): IUiGenericObserver<Byte>

    fun createUiNewDeviceInfoObserver(): IUiGenericObserver<GetInfoDto>

    fun createFinishObserverForSendClipboardRule(): IUiObserver

    @Deprecated("Redundant")
    fun createProblemObserverForClientListener(): IUiGenericObserver<Exception>
}