package ua.edu.ontu.wdt

import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import java.awt.Toolkit
import java.nio.file.Files.readAllBytes
import java.nio.file.Path.of

class Main : Application() {

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(Main::class.java.classLoader.getResource("main.fxml"))
        val screen = Toolkit.getDefaultToolkit().screenSize
        val scene = Scene(fxmlLoader.load(), screen.width / 2.7, screen.height / 2.7)
        stage.title = "Wireless data transmission"
        stage.scene = scene
        stage.icons.add(Image(Main::class.java.classLoader.getResourceAsStream("logo.png")))
        stage.show()
    }
}

fun main(vararg args: String) {
    val logo = String(readAllBytes(of(Main::class.java.classLoader.getResource("ascii-logo.txt")!!.toURI())))
    println("\u001b[36m${logo}\u001b[0m")
    launch(Main::class.java, *args)
}