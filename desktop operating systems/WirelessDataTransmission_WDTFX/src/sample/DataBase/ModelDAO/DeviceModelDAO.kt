package sample.DataBase.ModelDAO

import sample.DataBase.FeedReaderContract
import sample.DataBase.IWorkingWithDataBase
import sample.DataBase.Model.DeviceModel
import sample.WDTComponents.IPWork.IPV4.StaticFunctionsEnumerableIPVersion4

interface IDeviceModelDAO: IDAO {

    fun addNewDeviceToDatabaseWithUsingFilter(internetProtocolAddress: String, nameDevice: String, typeDevice: String)
    fun selectWhereIPLike(ip4Str: String): ArrayList<Array<String>>

}

class DeviceModelDAO(iWorkingWithDataBase: IWorkingWithDataBase): IDAO, IDeviceModelDAO {

    private val iWorkingWithDataBase: IWorkingWithDataBase = iWorkingWithDataBase
    private val deviceTable: FeedReaderContract.FeedDevice = FeedReaderContract.FeedDevice

    init {
        if (!iWorkingWithDataBase.createDataBase()) println("Database didn't create")
        if (!iWorkingWithDataBase.executeQuery(FeedReaderContract.SQL_CREATE_DEVICE)) println("Table in Database didn't create")
    }

    fun insert(deviceModel: DeviceModel) {
        if (deviceModel.ipAddress != null && deviceModel.deviceType != null && deviceModel.deviceName != null) {
            if (deviceModel.id != null)
                iWorkingWithDataBase.executeQuery("INSERT INTO ${deviceTable.TABLE_NAME} " +
                        "(${deviceTable.ID}, ${deviceTable.DEVICE_NAME}, ${deviceTable.DEVICE_TYPE}, ${deviceTable.IP_ADDRESS}) " +
                        "VALUES ('${deviceModel.id}', '${deviceModel.deviceName}', '${deviceModel.deviceType}', '${deviceModel.ipAddress}');")
            else iWorkingWithDataBase.executeQuery("INSERT INTO ${deviceTable.TABLE_NAME} " +
                    "(${deviceTable.DEVICE_NAME}, ${deviceTable.DEVICE_TYPE}, ${deviceTable.IP_ADDRESS}) " +
                    "VALUES ('${deviceModel.deviceName}', '${deviceModel.deviceType}', '${deviceModel.ipAddress}');")
        }
    }

    fun update(deviceModel: DeviceModel) {
        iWorkingWithDataBase.executeQuery("UPDATE ${deviceTable.TABLE_NAME} " +
                "SET ${deviceTable.DEVICE_NAME} = '${deviceModel.deviceName}', " +
                "${deviceTable.DEVICE_TYPE} = '${deviceModel.deviceType}', " +
                "${deviceTable.IP_ADDRESS} = '${deviceModel.ipAddress}' " +
                "WHERE ${deviceTable.ID} = '${deviceModel.id}';")
    }

    fun delete(deviceModel: DeviceModel) {
        iWorkingWithDataBase.executeQuery("DELETE FROM ${deviceTable.TABLE_NAME} WHERE ${deviceTable.ID} = '${deviceModel.id}';")
    }

    override fun selectWhereIPLike(ip4Str: String): ArrayList<Array<String>> = iWorkingWithDataBase.executeRowQuery("SELECT * FROM ${deviceTable.TABLE_NAME} WHERE ${deviceTable.IP_ADDRESS} LIKE '$ip4Str%'")

    override fun addNewDeviceToDatabaseWithUsingFilter(internetProtocolAddress: String, nameDevice: String, typeDevice: String) {
//        println(internetProtocolAddress)  // maybe delete
//        println(nameDevice)
        var isIpExistsInDatabase = false
        try {
            for (strings in this.selectWhereIPLike(StaticFunctionsEnumerableIPVersion4.enumerableIPVersion4(internetProtocolAddress)))
                if (strings[3].equals(internetProtocolAddress)) {
                    isIpExistsInDatabase = true
                    break
                }
        } catch (E: Exception) {
            isIpExistsInDatabase = true
        }
        if (!isIpExistsInDatabase) {
            this.insert(DeviceModel(nameDevice, typeDevice, internetProtocolAddress))
            println("add new device to database")
        }
    }

    override fun deleteTable() {
        iWorkingWithDataBase.executeQuery(FeedReaderContract.SQL_DROP_DEVICE)
    }

    override fun selectAll(): ArrayList<Array<String>> = iWorkingWithDataBase.executeRowQuery("SELECT * FROM ${deviceTable.TABLE_NAME};")

    override fun selectAllWithRowId(): ArrayList<Array<String>> = iWorkingWithDataBase.executeRowQuery("SELECT keyid, ${deviceTable.ID}, ${deviceTable.DEVICE_NAME}, ${deviceTable.DEVICE_TYPE}, ${deviceTable.IP_ADDRESS} FROM ${deviceTable.TABLE_NAME};")

}