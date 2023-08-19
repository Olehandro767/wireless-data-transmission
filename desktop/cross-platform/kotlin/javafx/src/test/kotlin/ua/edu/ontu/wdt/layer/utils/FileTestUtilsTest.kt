package ua.edu.ontu.wdt.layer.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ua.edu.ontu.wdt.tools.FileTestUtils
import java.io.File

class FileTestUtilsTest {

    @Test
    fun separateFilesAndFolders() {
        FileTestUtils.createTestFiles()
        val dto = FileUtils.separateFilesAndFolders(
                File("./test_folder/dir1"),
                File("./test_folder/file1.txt")
        )

        assertEquals(2, dto.folders.size)
        assertEquals(3, dto.files.size)
    }
}