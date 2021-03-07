package com.WDTlib.AlertInterface

interface ILoadAlert {

    fun showAlert()
    fun closeAlert()
    fun setPercentageOfDownload(percent: Double)
    fun setTitleAlert(title: String)
    fun setContentText(text: String)

}