package ua.edu.ontu.wdt.layer.ui

import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.ConfirmFileDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import java.util.concurrent.atomic.AtomicBoolean

interface IUiObserverAndMessageConfiguration {

    fun createProgressObserverForSendFileRule(): IUiGenericObserver<FileProgressDto>

    fun createFinishObserverForSendFileRule(): IUiObserver

    fun createProblemObserverForSendFileRule(): IUiGenericObserver<String>

    fun createCancelObserverForSendFileRule(): IUiGenericObserver<AtomicBoolean>

    fun createConfirmFileMessage(): IUiGenericConfirmMessage<ConfirmFileDto>

    fun createBeforeSendCommonObserver(): IUiGenericObserver<GetInfoDto>
}