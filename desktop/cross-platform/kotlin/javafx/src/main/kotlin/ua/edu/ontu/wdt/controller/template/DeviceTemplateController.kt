package ua.edu.ontu.wdt.controller.template

import javafx.fxml.FXML
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.text.Text
import ua.edu.ontu.wdt.layer.DeviceType.MOBILE
import ua.edu.ontu.wdt.layer.dto.GetInfoDto

class DeviceTemplateController(
    private val deviceInfo: GetInfoDto,
    private val onClick: (event: MouseEvent, deviceInfo: GetInfoDto) -> Unit,
) {

    @FXML
    private lateinit var imageView: ImageView

    @FXML
    private lateinit var textArea: Text

    @FXML
    fun onClick(event: MouseEvent) {
        this.onClick(event, this.deviceInfo)
    }

    @FXML
    fun initialize() {
        this.textArea.text = this.deviceInfo.deviceName
        this.imageView.image = Image(
            this.javaClass.classLoader.getResourceAsStream(
                if (this.deviceInfo.deviceType == MOBILE) "phone.png" else "computer-laptop.png"
            )
        )
    }
}