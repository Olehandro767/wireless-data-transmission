package ua.edu.ontu.wdt.helpful.factory

import ua.edu.ontu.wdt.service.IPermissionService
import ua.edu.ontu.wdt.service.impl.permission.EmptyPermissionServiceImpl

object PermissionServiceFactory {

    fun createPermissionService(): IPermissionService {
        return EmptyPermissionServiceImpl()
    }
}