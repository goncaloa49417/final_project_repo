import org.example.CssSelectors
import org.example.FileManager
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test
import kotlin.test.assertTrue


class FileManagerTest {

    @Test
    fun `fromList maps values correctly when list has 9 valid strings`() {

        val selectors = listOf(
            "shadow-root-selector",
            "deny-cookies-selector",
            "writing-box-selector",
            "confirm-selector",
            "date-selector",
            "search-selector",
            "last-page-number-selector",
            "hotel-names-selector",
            "hotel-prices-selector",
            "next-page-selector"
        )

        val cssSelectors = CssSelectors.fromList(selectors)

        assertEquals("shadow-root-selector", cssSelectors.cookies.shadowRoot)
        assertEquals("deny-cookies-selector", cssSelectors.cookies.denyCookies)

        assertEquals("writing-box-selector", cssSelectors.pageOne.writingBox)
        assertEquals("confirm-selector", cssSelectors.pageOne.confirm)
        assertEquals("date-selector", cssSelectors.pageOne.date)
        assertEquals("search-selector", cssSelectors.pageOne.search)

        assertEquals("last-page-number-selector", cssSelectors.pageTwo.numberOfPages)
        assertEquals("hotel-names-selector", cssSelectors.pageTwo.hotelNames)
        assertEquals("hotel-prices-selector", cssSelectors.pageTwo.hotelPrices)
        assertEquals("next-page-selector", cssSelectors.pageTwo.nextPage)
    }

}