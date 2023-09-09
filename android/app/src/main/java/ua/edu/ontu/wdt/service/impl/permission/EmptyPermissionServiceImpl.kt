package ua.edu.ontu.wdt.service.impl.permission

import ua.edu.ontu.wdt.service.IPermissionService

class EmptyPermissionServiceImpl : IPermissionService {

    override fun showPermissionsDialogIfTheyNotAcceptedAndRunCommand(run: () -> Unit) = run()
}