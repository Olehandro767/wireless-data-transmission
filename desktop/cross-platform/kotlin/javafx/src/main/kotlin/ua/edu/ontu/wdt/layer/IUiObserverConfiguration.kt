package ua.edu.ontu.wdt.layer

import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.SendFileProgressDto

interface IUiObserverConfiguration {

    fun createProgressObserverForSendFileRule(): IUiGenericObserver<SendFileProgressDto>

    fun createFinishObserverForSendFileRule(): IUiObserver

    fun createProblemObserverForSendFileRule(): IUiGenericObserver<String>

    fun createBeforeSendCommonObserver(): IUiGenericObserver<GetInfoDto>
}