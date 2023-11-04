package ua.edu.ontu.wdt.layer.file

interface IContentResolver {

    fun prepareFile(path: String): IFileContext

    fun readFile(): IFileContext
}