import org.example.FileManager
import kotlin.test.Test


class FileManagerTest {



    @Test
    fun test() {
        val f = FileManager("C:\\Users\\Gonçalo\\Desktop\\projeto de licenciatura\\projeto-semestral\\final_project_repo\\trivagoBot\\src\\test\\kotlin\\css selectors.json")
        f.extractCssSelectors()
    }
}