package ua.edu.ontu.wdt.service

interface IPermissionService {

    fun showPermissionsDialogIfTheyNotAcceptedAndRunCommand(run: () -> Unit)
}