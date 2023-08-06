package ua.edu.ontu.wdt.layer

import ua.edu.ontu.wdt.EmptyUiObserver
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.ConfirmFileDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import java.util.concurrent.atomic.AtomicBoolean

class EmptyUiObserverAndMessageConfigurationImpl: IUiObserverAndMessageConfiguration {

    override fun createProgressObserverForSendFileRule(): IUiGenericObserver<FileProgressDto> = EmptyUiObserver()

    override fun createFinishObserverForSendFileRule(): IUiObserver = EmptyUiObserver<Any>()

    override fun createProblemObserverForSendFileRule(): IUiGenericObserver<String> = EmptyUiObserver()

    override fun createCancelObserverForSendFileRule(): IUiGenericObserver<AtomicBoolean> = EmptyUiObserver()

    override fun createConfirmFileMessage(): IUiGenericConfirmMessage<ConfirmFileDto> = EmptyUiObserver()

    override fun createBeforeSendCommonObserver(): IUiGenericObserver<GetInfoDto> = EmptyUiObserver()
}