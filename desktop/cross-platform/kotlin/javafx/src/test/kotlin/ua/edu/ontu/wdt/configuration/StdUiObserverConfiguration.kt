package ua.edu.ontu.wdt.configuration

import ua.edu.ontu.wdt.EmptyUiObserver
import ua.edu.ontu.wdt.layer.IUiGenericConfirmMessage
import ua.edu.ontu.wdt.layer.IUiGenericObserver
import ua.edu.ontu.wdt.layer.IUiObserver
import ua.edu.ontu.wdt.layer.IUiObserverAndMessageConfiguration
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.ConfirmFileDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import java.util.concurrent.atomic.AtomicBoolean

class StdUiObserverConfiguration(
    private val stdLogger: StdLogger,
): IUiObserverAndMessageConfiguration {

    override fun createProgressObserverForSendFileRule(): IUiGenericObserver<FileProgressDto> = IUiGenericObserver { dto -> run {
        this.stdLogger.info(dto.toString())
    } }

    override fun createFinishObserverForSendFileRule(): IUiObserver  = EmptyUiObserver<Any>()

    override fun createProblemObserverForSendFileRule(): IUiGenericObserver<String> = IUiGenericObserver { dto -> this.stdLogger.info("Problem: $dto") }

    override fun createCancelObserverForSendFileRule(): IUiGenericObserver<AtomicBoolean> = EmptyUiObserver()

    override fun createConfirmFileMessage(): IUiGenericConfirmMessage<ConfirmFileDto> = IUiGenericConfirmMessage { dto, onAccept, _ -> run {
        this.stdLogger.info("Confirm window")
        this.stdLogger.info(dto.toString())
        onAccept()
    } }

    override fun createBeforeSendCommonObserver(): IUiGenericObserver<GetInfoDto> = EmptyUiObserver()
}