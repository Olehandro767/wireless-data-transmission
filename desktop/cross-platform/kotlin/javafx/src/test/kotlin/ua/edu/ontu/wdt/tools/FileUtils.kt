package ua.edu.ontu.wdt.tools

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object FileUtils {

    private fun doTask(fileDto: TestFileDto) {
        if (fileDto.innerFiles != null) {
            fileDto.file.mkdirs()
            for (item in fileDto.innerFiles) {
                doTask(item)
            }
        } else if (fileDto.content != null) {
            fileDto.file.createNewFile()
            Files.write(Path.of(fileDto.file.toURI()), fileDto.content.toByteArray())
        }
    }

    fun createTestFiles() {
        val testFolderPath = "./test_folder"
        val testFolder = File(testFolderPath)

        if (testFolder.exists()) {
            testFolder.deleteRecursively()
            println("Del test folder")
        }

        testFolder.mkdirs()
        arrayOf(
            TestFileDto("$testFolderPath/dir1", arrayOf(
                TestFileDto("$testFolderPath/dir1/f1.txt", null, "test file 1"),
                TestFileDto("$testFolderPath/dir1/in_dir", arrayOf(
                    TestFileDto("$testFolderPath/dir1/in_dir/in_file.txt", null, "in file")
                )),
            )),
            TestFileDto("$testFolderPath/file1.txt", null, "test file 2"),
            TestFileDto("$testFolderPath/dir2", emptyArray()),
        ).forEach { doTask(it) }
    }

    data class TestFileDto(
        val path: String,
        val innerFiles: Array<TestFileDto>? = null,
        val content: String? = null,
        val file: File = File(path),
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TestFileDto

            if (file != other.file) return false
            if (innerFiles != null) {
                if (other.innerFiles == null) return false
                if (!innerFiles.contentEquals(other.innerFiles)) return false
            } else if (other.innerFiles != null) return false
            if (content != other.content) return false

            return true
        }

        override fun hashCode(): Int {
            var result = file.hashCode()
            result = 31 * result + (innerFiles?.contentHashCode() ?: 0)
            result = 31 * result + (content?.hashCode() ?: 0)
            return result
        }
    }
}