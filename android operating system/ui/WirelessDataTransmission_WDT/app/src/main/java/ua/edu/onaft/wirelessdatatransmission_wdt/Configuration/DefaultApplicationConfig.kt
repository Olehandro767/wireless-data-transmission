package ua.edu.onaft.wirelessdatatransmission_wdt.Configuration

import android.os.Build
import com.WDTComponents.Action.SendType.ActionForSendType
import com.WDTComponents.AppConfig
import com.WDTComponents.AppOption
import com.WDTComponents.DataBase.ModelDAO.*
import com.WDTComponents.IPWork.IPV4.PackageIPVersion4
import com.WDTComponents.ServerControll.Server
import com.WDTComponents.TypeDeviceENUM
import com.WDTComponents.WorkingWithClient.StartForWorkingWithClient
import ua.edu.onaft.wirelessdatatransmission_wdt.Common.Constant
import java.io.File

class DefaultApplicationConfig {

    init {
        /**
         * Config custom classes
         */
        AppConfig.AlertInterface.errorIMessage = ErrorMessageConfiguration()
        AppConfig.AlertInterface.iLoadAlert = LoadAlertConfiguration().javaClass
        AppConfig.AlertInterface.iMessage = MessageConfiguration()
        AppConfig.AlertInterface.littleIMessage = LittleMessageConfiguration()
        AppConfig.AlertInterface.messageForNotifyAboutCompleteDownloadProcess = MessageForNotifyAboutCompleteDownloadProcess()
        AppConfig.SystemClipboard.iSystemClipboard = SystemClipboardConfiguration()
        AppConfig.DataBase.WorkWithDataBaseInterface.iWorkingWithDataBase = WorkingWithDataBaseConfiguration(false)
        AppConfig.OpenDataMethod.iOpenDataMethod = OpenDataMethod()
        Thread {
            AppOption.DEVICE_TYPE = TypeDeviceENUM.PHONE
            AppOption.LOCAL_DEVICE_NAME = "${Build.BRAND} ${Build.MODEL}".intern()
            if (Constant.mainExternalStorageDirectory != null) {
                AppOption.DIRECTORY_FOR_DOWNLOAD_FILES = File(Constant.mainExternalStorageDirectory!!.absolutePath + "/Download/Wireless Data Transmission")
            }
        }.start()
        /**
         * Config default classes
         */
        AppConfig.Action.SendTypeInterface.iActionForSendType = ActionForSendType
        AppConfig.DataBase.ModelDAOInterface.iDeviceModelDAO = DeviceModelDAO(AppConfig.DataBase.WorkWithDataBaseInterface.iWorkingWithDataBase)
        AppConfig.DataBase.ModelDAOInterface.iFileModelDAO = FileModelDAO(AppConfig.DataBase.WorkWithDataBaseInterface.iWorkingWithDataBase)
        AppConfig.DataBase.ModelDAOInterface.iTrustedDeviceModelDAO = TrustedDeviceModelModelDAO(AppConfig.DataBase.WorkWithDataBaseInterface.iWorkingWithDataBase)
        AppConfig.DataBase.ModelDAOInterface.iTransferredFilesHistoryModelDAO = TransferredFilesHistoryModelDAO(AppConfig.DataBase.WorkWithDataBaseInterface.iWorkingWithDataBase)
        AppConfig.DataBase.ModelDAOInterface.iAcceptedFilesHistoryModelDAO = AcceptedFilesHistoryModelDAO(AppConfig.DataBase.WorkWithDataBaseInterface.iWorkingWithDataBase)
        AppConfig.IPWorkInterface.IPV4.iIP = PackageIPVersion4()
        AppConfig.WorkingWithClientInterface.iWorkingWithClient = StartForWorkingWithClient()
        AppConfig.ServerControllInterface.iServer = Server()
        /**
         * Start WDT Components
         */
        AppConfig.IPWorkInterface.IPV4.iIP.getListOfIP()
        //AppConfig.ServerControllInterface.iServer.startServerSocket()
    }

}