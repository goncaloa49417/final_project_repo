package parentDivTest

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.json.Json
import org.example.WEBSITE
import org.example.httpRequests.DivRespFinal
import org.example.httpRequests.ModelAnswerSchemas
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.OllamaRequestBodyFormat
import navigation.ChromeDriverExtension
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


class ParentDivTest {

    // used in gemma3-12b-with-semantic-2
    private val bigPrompt = """"
        ###Task###  
        You are a HTML code analyst expert. Given a description of the element and a list of div elements each rated by level of semantic, decide what div would be the parent of the child element and create a css selector to access it using only it's attributes.
        
        ###Context###  
        A button with the purpose of starting the search after the a location has been selected.
          
        ###Div List###  
        1. <div id="__next"> // low – likely a framework wrapper, no direct semantic role.
        2. <div class="_7Mr3YA"> // low – meaningless string, lacks any semantic indication.
        3. <div data-testid="page-header-wrapper" class=""> // medium – descriptive `data-testid` but empty class attribute.
        4. <div class="FfmyqR e4D1FP jngrXy"> // low – pseudo-random class names with no clear purpose.
        5. <div class="vTDE1M"> // low – purely presentational, lacks any semantic value.
        6. <div class="j4pLyK"> // low – meaningless string, no indication of its purpose.
        7. <div data-testid="desktop-dropdown-menu" class="_4DcEqf"> // medium – descriptive `data-testid` but empty class attribute.
        8. <div class="tbKdsQ"> // low – purely presentational with no semantic clues.
        9. <div class="FfmyqR T99TF6 e4D1FP A5QoPl"> // low – pseudo-random class names, lacks clear purpose.
        10. <div class="jkemPj"> // low – meaningless string, no indication of its purpose.
        11. <div class="FfmyqR e4D1FP jngrXy"> // low – pseudo-random class names with no clear purpose.
        12. <div class="vzC9TR FrYDhH REZdEJ" data-testid="search-form"> // high – descriptive `data-testid` and rich semantic role.
        13. <div class="_3axGO1"> // low – meaningless string, lacks any semantic indication.
        14. <div class=""> // low – empty class attribute with no clear purpose.
        15. <div role="combobox" aria-expanded="false" aria-controls="suggestion-list" class="If79lQ yXXD2G"> // high – explicit role and ARIA attributes provide rich semantic detail.
        16. <div role="button" class="HxkFDQ aaN4L7" tabindex="0"> // medium – suggests interactivity but lacks specific context.
        17. <div class="Z8wU9_"> // low – purely presentational with no semantic clues.
        18. <div class="QpwdOT"> // low – meaningless string, lacks any semantic indication.
        19. <div class="FfmyqR e4D1FP jngrXy"> // low – pseudo-random class names with no clear purpose.
        20. <div data-testid="usp-module" class="ytw8QW"> // medium – descriptive `data-testid` but empty class attribute.
        1. <div class="FfmyqR e4D1FP jngrXy"> // low – repeated, meaningless class names.
        2. <div class="HPeupy"> // medium – somewhat descriptive name but lacks specificity.
        3. <div class="HPeupy"> // medium – same as above, no semantic value added by repetition.
        4. <div class="HPeupy"> // medium – similar to previous, no clear meaning from repeated class names.
        5. <div class="FfmyqR e4D1FP jngrXy"> // low – duplicate of element 1, same reasoning applies.
        6. <div class="ypJHZM"> // low – random class name with no semantic value.
        7. <div class="U5pOi3"> // medium – somewhat descriptive but lacks specificity.
        8. <div class="Q7yG_1"> // low – meaningless string, no semantic indication.
        9. <div class="_TUOwe"> // low – framework wrapper, no direct semantic role.
        10. <div class="_7ITYMW" data-testid="landing-deals-slider-deals-carousel"> // high – descriptive `data-testid` shows component's purpose.
        11. <div class="_1KYTTK"> // medium – somewhat descriptive but lacks specificity.
        12. <div class="_1s9ZIF"> // low – random class name with no semantic value.
        13. <div class="INhdjV" data-action="itemCard" data-testid="deal-item-100/39576"> // high – ARIA and `data-testid` provide clear semantic intent.
        14. <div class="inUGa9 ZhvL_k"> // medium – somewhat descriptive but lacks specificity.
        15. <div class="a2fgmx"> // low – random class name with no semantic value.
        16. <div class="Subl9v"> // low – meaningless string, no semantic indication.
        17. <div class="VGvZ9a"> // low – repeated, meaningless class names.
        18. <div class="VGvZ9a"> // same as above, no clear meaning from repetition.
        19. <div class="Kx9zKW"> // medium – somewhat descriptive but lacks specificity.
        20. <div class="_4R_YLs" data-action="priceBlock"> // high – ARIA role adds rich semantic detail.
        41. <div class="pKqNaZ"> // low – random, non-descriptive class name.
        42. <div class="VqoNUi" data-testid="advertiser-details-2812" data-cos="advertiser" itemprop="offeredBy"> // high – descriptive `data-testid`, ARIA attribute, and schema.org itemprop provide rich semantic information.
        43. <div class="nRdG52 unEo73"> // low – no clear purpose or meaning from class names.
        44. <div data-testid="tooltip-wrapper" class="JRbS_T s87Vds UlUJPV"> // high – descriptive `data-testid` and ARIA attributes indicate a tooltip component.
        45. <div data-testid="tooltip-children-wrapper" aria-describedby="tooltip--__«r1»" class="xs7bm8"> // medium – `aria-describedby` suggests a related element, but lacks explicit role or semantic meaning.
        46. <div class="Eesg2Q"> // low – no clear purpose or meaning from class names.
        47. <div "_2E_0Rq FXJNjg SwVR4I" data-testid="superSavingsPriceBadge" data-cos="superSavingsPrice"> // high – descriptive `data-testid` and ARIA attribute indicate a savings badge component.
        48. <div class="HjOk6Q oVtsoQ ZTIfHR" data-testid="recommended-price" data-cos="recommendedPrice" itemprop="price"> // medium – suggests price information, but lacks explicit role or semantic meaning.
        49. <div class="_4e36Ya"> // low – no clear purpose or meaning from class names.
        50. <div class="_1s9ZIF"> // low – no clear purpose or meaning from class names.
        51. <div class="INhdjV" data-action="itemCard" data-testid="deal-item-100/35030"> // high – descriptive `data-testid` and ARIA attribute indicate a deal item component.
        52. <div class="inUGa9 ZhvL_k"> // low – no clear purpose or meaning from class names.
        53. <div class="a2fgmx"> // low – no clear purpose or meaning from class names.
        54. <div class="Subl9v"> // low – no clear purpose or meaning from class names.
        55. <div class="VGvZ9a"> // low – no clear purpose or meaning from class names ( duplicate).
        56. <div class="VGvZ9a"> // low – no clear purpose or meaning from class names (duplicate).
        57. <div class="Kx9zKW"> // low – no clear purpose or meaning from class names.
        58. <div "_4R_YLs" data-action="priceBlock"> // medium – suggests price block information, but lacks explicit role or semantic meaning.
        59. <div class="pKqNaZ"> // low – random, non-descriptive class name ( duplicate).
        60. <div class="VqoNUi" data-testid="advertiser-details-2626" data-cos="advertiser" itemprop="offeredBy"> // high – descriptive `data-testid`, ARIA attribute, and schema.org itemprop provide rich semantic information.
        61. <div class="nRdG52 unEo73"> // low – purely presentational with no semantic clues.
        62. <div data-testid="tooltip-wrapper" class="JRbS_T s87Vds UlUJPV"> // medium – suggests a tooltip but lacks ARIA or role.
        63. <div data-testid="tooltip-children-wrapper" aria-describedby="tooltip--__«r2»" class="xs7bm8"> // high – descriptive `data-testid` and ARIA attributes indicate a tooltip.
        64. <div class="Eesg2Q"> // low – no semantic meaning, likely used for styling or layout.
        65. <div class="_2E_0Rq FXJNjg SwVR4I" data-testid="superSavingsPriceBadge" data-cos="superSavingsPrice"> // high – descriptive `data-testid` and ARIA attributes indicate a savings price badge.
        66. <div class="HjOk6Q oVtsoQ ZTIfHR" data-testid="recommended-price" data-cos="recommendedPrice" itemprop="price"> // medium – suggests a recommended price but lacks specific context.
        67. <div class="_4e36Ya"> // low – no semantic meaning, likely used for styling or layout.
        68. <div class="_1s9ZIF"> // low – no semantic meaning, likely used for styling or layout.
        69. <div class="INhdjV" data-action="itemCard" data-testid="deal-item-100/932695"> // medium – suggests a deal item but lacks ARIA or role.
        70. <div class="inUGa9 ZhvL_k"> // low – no semantic meaning, likely used for styling or layout.
        71. <div class="a2fgmx"> // low – no semantic meaning, likely used for styling or layout.
        72. <div class="Subl9v"> // low – no semantic meaning, likely used for styling or layout.
        73. <div class="VGvZ9a"> // low – repeated element with no semantic meaning.
        74. <div class="VGvZ9a"> // same as 73, low.
        75. <div class="Kx9zKW"> // low – no semantic meaning, likely used for styling or layout.
        76. <div class="_4R_YLs" data-action="priceBlock"> // high – descriptive `data-testid` and ARIA attributes indicate a price block.
        77. <div class="pKqNaZ"> // low – no semantic meaning, likely used for styling or layout.
        78. <div class="VqoNUi" data-testid="advertiser-details-3640" data-cos="advertiser" itemprop="offeredBy"> // medium – suggests advertiser details but lacks specific context.
        79. <div data-testid="tooltip-wrapper" class="JRbS_T s87Vds UlUJPV"> // same as 62, medium.
        81. <div data-testid="tooltip-children-wrapper" aria-describedby="tooltip--__«r3»" class="xs7bm8"> // high – clear semantic intent with ARIA and `data-testid`.
        82. <div class="Eesg2Q"> // low – no semantic clues, likely used for styling.
        83. <div class="_2E_0Rq FXJNjg SwVR4I" data-testid="superSavingsPriceBadge" data-cos="superSavingsPrice"> // high – descriptive `data-testid` and ARIA attributes.
        84. <div class="HjOk6Q oVtsoQ ZTIfHR" data-testid="recommended-price" data-cos="recommendedPrice" itemprop="price"> // medium – clear semantic intent, but `itemprop` is redundant with `data-testid`.
        85. <div class="_4e36Ya"> // low – no semantic clues.
        86. <div class="_1s9ZIF"> // low – no semantic clues.
        87. <div class="INhdjV" data-action="itemCard" data-testid="deal-item-100/38294"> // medium – suggests interactivity but lacks ARIA or role.
        88. <div class="inUGa9 ZhvL_k"> // low – no semantic clues.
        89. <div class="a2fgmx"> // low – no semantic clues.
        90. <div class="Subl9v"> // low – no semantic clues.
        91. <div class="VGvZ9a"> // high – clear semantic intent with repeated `data-testid`.
        92. <div class="VGvZ9a"> // high – same as above, likely intended to be a duplicate.
        93. <div class="Kx9zKW"> // low – no semantic clues.
        94. <div class="_4R_YLs" data-action="priceBlock"> // medium – suggests interactivity but lacks ARIA or role.
        95. <div class="pKqNaZ"> // low – no semantic clues.
        96. <div class="VqoNUi" data-testid="advertiser-details-3066" data-cos="advertiser" itemprop="offeredBy"> // medium – clear semantic intent, but `itemprop` is redundant with `data-testid`.
        97. <div class="nRdG52 unEo73"> // low – no semantic clues.
        98. <div data-testid="tooltip-wrapper" class="JRbS_T s87Vds UlUJPV"> // high – clear semantic intent with ARIA and `data-testid`.
        99. <div data-testid="tooltip-children-wrapper" aria-describedby="tooltip--__«r4»" class="xs7bm8"> // high – same as above, likely intended to be a duplicate.
        100. <div class="Eesg2Q"> // low – no semantic clues.
        101. <div class="_2E_0Rq FXJNjg SwVR4I" data-testid="superSavingsPriceBadge" data-cos="superSavingsPrice"> // high – clear and descriptive `data-testid` with ARIA attribute.
        102. <div class="HjOk6Q oVtsoQ ZTIfHR" data-testid="recommended-price" data-cos="recommendedPrice" itemprop="price"> // medium – suggests a price element but lacks explicit role or ARIA description.
        103. <div>_4e36Ya_> // low – empty class name with no semantic value.
        104. <div>_1s9ZIF_> // low – empty class name with no semantic value.
        105. <div class="INhdjV" data-action="itemCard" data-testid="deal-item-100/5818068"> // high – descriptive `data-testid` and clear action attribute.
        106. <div class="inUGa9 ZhvL_k"> // low – random-looking class name with no semantic value.
        107. <div class="a2fgmx"> // low – empty class name with no semantic value.
        108. <div class="Subl9v"> // low – empty class name with no semantic value.
        109. <div class="VGvZ9a"> // low – empty class name with no semantic value.
        110. <div class="VGvZ9a"> // duplicate, same rating as 109.
        111. <div class="Kx9zKW"> // low – random-looking class name with no semantic value.
        112. <div class="_4R_YLs" data-action="priceBlock"> // high – clear and descriptive `data-action` attribute.
        113. <div class="pKqNaZ"> // low – empty class name with no semantic value.
        114. <div class="VqoNUi" data-testid="advertiser-details-2268" data-cos="advertiser" itemprop="offeredBy"> // high – clear and descriptive `data-testid` with ARIA attribute.
        115. <div class="nRdG52 unEo73"> // low – random-looking class name with no semantic value.
        116. <div data-testid="tooltip-wrapper" class="JRbS_T s87Vds UlUJPV"> // medium – suggests a tooltip element but lacks explicit role or ARIA description.
        117. <div data-testid="tooltip-children-wrapper" aria-describedby="tooltip--__«r5»" class="xs7bm8"> // high – clear and descriptive `data-testid` with ARIA attribute.
        118. <div class="Eesg2Q"> // low – empty class name with no semantic value.
        119. <div class="_2E_0Rq FXJNjg SwVR4I" data-testid="superSavingsPriceBadge" data-cos="superSavingsPrice"> // duplicate, same rating as 101.
        120. <div class="HjOk6Q oVtsoQ ZTIfHR" data-testid="recommended-price" data-cos="recommendedPrice" itemprop="price"> // duplicate, same rating as 102.
        121. <div class="_4e36Ya"> // low – random, non-descriptive class name.
        122. <div class="U5pOi3"> // low – meaningless string, no semantic indication.
        123. <div class="khSXnt"> // low – unclear purpose without additional context.
        124. <div class="_TUOwe"> // low – lacks any descriptive value or meaning.
        125. <div class="FfmyqR e4D1FP jngrXy"> // medium – some semantic hints (e.g., `jngrXy` might suggest a layout), but overall unclear purpose.
        126. <div class="_4HvnB"> // low – random, non-descriptive class name.
        127. <div class="Xzrp7a"> // low – meaningless string, no semantic indication.
        128. <div class="P357j_" data-testid="horizontal-scroll-row"> // medium – `data-testid` provides some context, but overall unclear purpose.
        129. <div class="_7F2oNl QGI8pF zoE8qq"> // low – random, non-descriptive class name with multiple values.
        130. <div class="rjenyH"> // low – lacks any descriptive value or meaning.
        131. <div class="_2Zs8ez _4kafhd ic1iv2"> // medium – some semantic hints (e.g., `_ic1iv2` might suggest a layout), but overall unclear purpose.
        132. <div role="tablist" tabindex="-1" class="b7lys9"> // high – explicit role and ARIA attributes provide clear meaning for accessibility.
        133-140. <div class="gwQlrp"> // low – identical, presentational class name with no semantic value.
        141. <div class="gwQlrp"> // low – random, non-descriptive class name.
        142. <div class="gwQlrp"> // low – duplicate class name, lacks semantic value.
        143. <div aria-labelledby="tab-list-31720" class="g3BCYF" id="tab-content-31720" role="tabpanel" tabindex="0"> // high – clear ARIA and role definitions for tab content.
        144. <div aria-labelledby="tab-list-31638" class="g3BCYF" hidden="" id="tab-content-31638" role="tabpanel" tabindex="0"> // medium – labeled region, but `hidden` attribute might be misleading.
        145. <div aria-labelledby="tab-list-31630" class="g3BCYF" hidden="" id="tab-content-31630" role="tabpanel" tabindex="0"> // medium – similar to 144, with `hidden` attribute.
        146. <div aria-labelledby="tab-list-31823" class="g3BCYF" hidden="" id="tab-content-31823" role="tabpanel" tabindex="0"> // high – clear ARIA and role definitions for tab content.
        147. <div aria-labelledby="tab-list-16137" class="g3BCYF" hidden="" id="tab-content-16137" role="tabpanel" tabindex="0"> // medium – labeled region, but `hidden` attribute might be misleading.
        148. <div aria-labelledby="tab-list-13443" class="g3BCYF" hidden="" id="tab-content-13443" role="tabpanel" tabindex="0"> // high – clear ARIA and role definitions for tab content.
        149. <div aria-labelledby="tab-list-31722" class="g3BCYF" hidden="" id="tab-content-31722" role="tabpanel" tabindex="0"> // medium – labeled region, but `hidden` attribute might be misleading.
        150. <div aria-labelledby="tab-list-31644" class="g3BCYF" hidden="" id="tab-content-31644" role="tabpanel" tabindex="0"> // high – clear ARIA and role definitions for tab content.
        151. <div aria-labelledby="tab-list-31795" class="g3BCYF" hidden="" id="tab-content-31795" role="tabpanel" tabindex="0"> // medium – labeled region, but `hidden` attribute might be misleading.
        152. <div aria-labelledby="tab-list-13628" class="g3BCYF" hidden="" id="tab-content-13628" role="tabpanel" tabindex="0"> // high – clear ARIA and role definitions for tab content.
        153. <div class="ElEs_f"> // low – non-descriptive class name, lacks semantic value.
        154. <div class="YVmZo_"> // low – non-descriptive class name, lacks semantic value.
        155. <div class="UU2r_m"> // low – non-descriptive class name, lacks semantic value.
        156. <div class="pExfKw nm9SGJ"> // low – non-descriptive class name, lacks semantic value.
        157. <div class="FCe3kX"> // medium – likely used for styling or layout, no clear semantic role.
        158. <div class="FCe3kX"> // same as 157, lacks clear semantic role.
        159. <div class="FCe3kX"> // same as 157 and 158, lacks clear semantic role.
        160. <div class="FCe3kX"> // same as 157, 158, and 159, lacks clear semantic role.
        161. <div class="FCe3kX"> // low – repeated, meaningless class name.
        162. <div class="FCe3kX"> // low – same issue as above.
        163. <div class="FCe3kX"> // low – identical to previous ones.
        164. <div class="FCe3kX"> // low – no semantic value or context.
        165. <div class="FCe3kX"> // low – same pattern as above.
        166. <div class="FCe3kX"> // low – no indication of purpose or functionality.
        167. <div class="FCe3kX"> // low – repeated, meaningless class name.
        168. <div class="FCe3kX"> // low – same issue as above.
        169. <div class="vBXG95"> // low – non-descriptive and likely used only for styling.
        170. <div data-testid="homepage-seo-module"> // high – descriptive `data-testid` indicates a module with specific functionality.
        171. <div class="FfmyqR f9jOvO e4D1FP jngrXy"> // low – complex, likely used only for styling or layout.
        172. <div class="_2Zs8ez _4kafhd"> // low – non-descriptive and possibly used for styling.
        173. <div role="tablist" tabindex="-1" class="b7lys9 iv_ss_"> // medium – suggests a tab list, but lacks ARIA attributes.
        174. <div class="gwQlrp"> // low – purely presentational with no semantic clues.
        175. <div class="gwQlrp"> // same as above.
        176. <div class="nprlqe"> // low – non-descriptive and possibly used for styling.
        177. <div aria-labelledby="tab-list-popularSearchesCities" class="g3BCYF" id="tab-content-popularSearchesCities" role="tabpanel" tabindex="0"> // high – ARIA attributes provide clear semantic meaning.
        178. <div class="ccNHcf jWrdxc" data-testid="popularSearchesCities"> // medium – descriptive `data-testid` but lacks explicit role or ARIA attributes.
        179. <div class="_1s9ZIF"> // low – non-descriptive and possibly used for styling.
        180. <div class="iDPdsa" itemtype="http://schema.org/TouristDestination" itemscope="" data-testid="destination-item"> // high – explicit schema.org markup provides rich semantic detail.
        181. <div class="inUGa9 czDXE9"> // low – random alphanumeric class names, likely used only for styling.
        182. <div class="a2fgmx"> // low – meaningless string, no semantic indication.
        183. <div class="uYcwx7"> // low – similar to 182, lacks context.
        184. <div class="_1s9ZIF"> // low – unclear purpose without additional context or attributes.
        185. <div class="iDPdsa" itemtype="http://schema.org/TouristDestination" itemscope="" data-testid="destination-item"> // high – schema.org markup with clear semantic intent for a tourist destination.
        186. <div class="inUGa9 czDXE9"> // low – repeated, meaningless class names.
        187. <div class="a2fgmx"> // low – similar to 182, lacks context.
        188. <div class="uYcwx7"> // low – similar to 183, lacks context.
        189. <div class="_1s9ZIF"> // low – unclear purpose without additional context or attributes.
        190. <div class="iDPdsa" itemtype="http://schema.org/TouristDestination" itemscope="" data-testid="destination-item"> // high – schema.org markup with clear semantic intent for a tourist destination ( duplicate of 185).
        191. <div class="inUGa9 czDXE9"> // low – repeated, meaningless class names.
        192. <div class="a2fgmx"> // low – similar to 182, lacks context.
        193. <div class="uYcwx7"> // low – similar to 183, lacks context.
        194. <div class="_1s9ZIF"> // low – unclear purpose without additional context or attributes.
        195. <div class="iDPdsa" itemtype="http://schema.org/TouristDestination" itemscope="" data-testid="destination-item"> // high – schema.org markup with clear semantic intent for a tourist destination ( duplicate of 185).
        196. <div class="inUGa9 czDXE9"> // low – repeated, meaningless class names.
        197. <div class="a2fgmx"> // low – similar to 182, lacks context.
        198. <div class="uYcwx7"> // low – similar to 183, lacks context.
        199. <div class="_1s9ZIF"> // low – unclear purpose without additional context or attributes.
        200. <div class="iDPdsa" itemtype="http://schema.org/TouristDestination" itemscope="" data-testid="destination-item"> // high – schema.org markup with clear semantic intent for a tourist destination ( duplicate of 185).
        201. <div class="inUGa9 czDXE9"> // low – random alphanumeric class names, likely used only for styling.
        202. <div class="a2fgmx"> // low – meaningless string, no semantic indication.
        203. <div class="uYcwx7"> // low – similar to 202, lacks semantic meaning.
        204. <div class="_1s9ZIF"> // low – unclear purpose without additional context.
        205. <div class="iDPdsa" itemtype="http://schema.org/TouristDestination" itemscope="" data-testid="destination-item"> // high – descriptive `data-testid` and schema.org role for a tourist destination.
        206. <div class="inUGa9 czDXE9"> // low – duplicate of 201, no semantic value added.
        207. <div class="a2fgmx"> // low – duplicate of 202, lacks semantic meaning.
        208. <div class="uYcwx7"> // low – duplicate of 203, similar to previous response.
        209. <div class="rkMvFo"> // low – unclear purpose without additional context.
        210. <div aria-labelledby="tab-list-popularSearchesDestinations" class="g3BCYF" hidden="" id="tab-content-popularSearchesDestinations" role="tabpanel" tabindex="0"> // medium – ARIA `aria-labelledby` and role, but lacks descriptive content or `data-testid`.
        211. <div data-testid="homepage-seo-about"> // high – clear `data-testid` indicating a specific purpose.
        212. <div class="DBtHzd"> // low – unclear purpose without additional context.
        213. <div class="MY5CBV"> // low – similar to 212, lacks semantic meaning.
        214. <div class="rbHx_s _e605m"> // low – duplicate of 219, unclear purpose.
        215. <div class="_4IkIt5 dJwdoL"> // low – unclear purpose without additional context.
        216. <div class="BBzPm7"> // low – meaningless string, no semantic indication.
        217. <div class="_6U2Oif zrZMJk"> // low – unclear purpose without additional context.
        218. <div class="AsRyMw"> // low – similar to previous responses, lacks semantic meaning.
        219. <div class="rbHx_s _e605m"> // low – duplicate of 214, unclear purpose.
        220. <div class="_4IkIt5 dJwdoL"> // low – duplicate of 215, unclear purpose without additional context.
        1. <div class="BBzPm7"> // low – purely presentational with no semantic clues.
        2. <div class="_6U2Oif zrZMJk"> // low – non-descriptive and likely auto-generated.
        3. <div class="znfuh0"> // low – meaningless string, no semantic indication.
        4. <div class="tc3f9o WWIcxu" style="--_arrow-left: 816.0500106811523px; --_left: 0px;"> // low – styling attributes without semantic value.
        5. <div class="crTI4l qdNiQd" data-testid="homepage-login-arrow-floating-banner"> // high – descriptive `data-testid` shows component's purpose.
        6. <div class="_6zOBXg Bi3_id rtTdAh rF3bCh"> // low – framework wrapper, no direct semantic role.
        7. <div class="_7nWWSU"> // low – meaningless string, no semantic indication.
        8. <div class="A_xBXC"> // low – purely presentational with no semantic clues.
        9. <div class="M_LaaI"> // low – purely presentational with no semantic clues.
        10. <div class="P1XtUS"> // low – purely presentational with no semantic clues.
        11. <div class="ezyDS1"> // low – purely presentational with no semantic clues.
        12. <div class="YeF7SR"> // low – meaningless string, no semantic indication.
        13. <div class="_0EgS_E"> // low – framework wrapper, no direct semantic role.
        14. <div class="tq4LkB"> // low – purely presentational with no semantic clues.
        15. <div class="JrQCFx"> // low – purely presentational with no semantic clues.
        16. <div class="HYoYiI"> // low – purely presentational with no semantic clues.
        17. <div class="KhfsVb"> // low – meaningless string, no semantic indication.
        18. <div aria-hidden="true" class="BI72E3 _9xfhPE"> // medium – `aria-hidden` provides some accessibility context.
        19. <div aria-hidden="true" class="wLtsKG _0xWnnn"> // medium – `aria-hidden` provides some accessibility context.
        20. <div id="toast-root"> // high – explicit ID suggests a specific component or element.
        1. <div id="overlay-root"> // high – explicit ID suggests a root element for an overlay.
        2. <div id="usercentrics-root" data-created-at="1751387703584" style=""> // medium – name hints at its purpose, but ARIA attributes provide more context.

        ###Answer Format###  
        Div element: (completed chosen div)  
        Css selector: div(complete the css selector with the most unique attribute to be select by a selenium bot)  
        Stick strictly to this format. Do not add additional text outside the answer format.
    """.trimIndent()

    private val semanticPrompt = """"
        ###Task###  
        You are a HTML code analyst expert. Given a description of the element and a list of div elements each rated by level of semantic, decide what div would be the parent of the child element and create a css selector to access it using only it's attributes.
        
        ###Context###  
        A button with the purpose of starting the search after the a location has been selected.
          
        ###Div List###  
        1. <div id="__next"> // low – likely a framework wrapper, no direct semantic role.
        2. <div class="_7Mr3YA"> // low – meaningless string, lacks any semantic indication.
        3. <div data-testid="page-header-wrapper" class=""> // medium – descriptive `data-testid` but empty class attribute.
        4. <div class="FfmyqR e4D1FP jngrXy"> // low – pseudo-random class names with no clear purpose.
        5. <div class="vTDE1M"> // low – purely presentational, lacks any semantic value.
        6. <div class="j4pLyK"> // low – meaningless string, no indication of its purpose.
        7. <div data-testid="desktop-dropdown-menu" class="_4DcEqf"> // medium – descriptive `data-testid` but empty class attribute.
        8. <div class="tbKdsQ"> // low – purely presentational with no semantic clues.
        9. <div class="FfmyqR T99TF6 e4D1FP A5QoPl"> // low – pseudo-random class names, lacks clear purpose.
        10. <div class="jkemPj"> // low – meaningless string, no indication of its purpose.
        11. <div class="FfmyqR e4D1FP jngrXy"> // low – pseudo-random class names with no clear purpose.
        12. <div class="vzC9TR FrYDhH REZdEJ" data-testid="search-form"> // high – descriptive `data-testid` and rich semantic role.
        13. <div class="_3axGO1"> // low – meaningless string, lacks any semantic indication.
        14. <div class=""> // low – empty class attribute with no clear purpose.
        15. <div role="combobox" aria-expanded="false" aria-controls="suggestion-list" class="If79lQ yXXD2G"> // high – explicit role and ARIA attributes provide rich semantic detail.
        16. <div role="button" class="HxkFDQ aaN4L7" tabindex="0"> // medium – suggests interactivity but lacks specific context.
        17. <div class="Z8wU9_"> // low – purely presentational with no semantic clues.
        18. <div class="QpwdOT"> // low – meaningless string, lacks any semantic indication.
        19. <div class="FfmyqR e4D1FP jngrXy"> // low – pseudo-random class names with no clear purpose.
        20. <div data-testid="usp-module" class="ytw8QW"> // medium – descriptive `data-testid` but empty class attribute.
        
        ###Answer Format###  
        Div element: (completed chosen div)  
        Css selector: div(complete the css selector with the most unique attribute to be select by a selenium bot)  
        Stick strictly to this format. Do not add additional text outside the answer format.
    """.trimIndent()

    private val prompt = """"
        ###Task###  
        You are a HTML code analyst expert. Given a description of the element and a list of div elements each rated by level of semantic, decide what div would be the parent of the child element and create a css selector to access it using only it's attributes.
        
        ###Context###  
        A button with the purpose of starting the search after the a location has been selected.
          
        ###Div List###  
        1. <div id="__next">
        2. <div class="_7Mr3YA">
        3. <div data-testid="page-header-wrapper" class="">
        4. <div class="FfmyqR e4D1FP jngrXy">
        5. <div class="vTDE1M">
        6. <div class="j4pLyK">
        7. <div data-testid="desktop-dropdown-menu" class="_4DcEqf">
        8. <div class="tbKdsQ">
        9. <div class="FfmyqR T99TF6 e4D1FP A5QoPl">
        10. <div class="jkemPj">
        11. <div class="FfmyqR e4D1FP jngrXy">
        12. <div class="vzC9TR FrYDhH REZdEJ" data-testid="search-form">
        13. <div class="_3axGO1">
        14. <div class="">
        15. <div role="combobox" aria-expanded="false" aria-controls="suggestion-list" class="If79lQ yXXD2G">
        16. <div role="button" class="HxkFDQ aaN4L7" tabindex="0">
        17. <div class="Z8wU9_">
        18. <div class="QpwdOT">
        19. <div class="FfmyqR e4D1FP jngrXy">
        20. <div data-testid="usp-module" class="ytw8QW">
        
        ###Answer Format###  
        Div element: (completed chosen div)  
        Css selector: div(complete the css selector with the most unique attribute to be select by a selenium bot)  
        Stick strictly to this format. Do not add additional text outside the answer format.
    """.trimIndent()

    private val testSemaphore = Semaphore(1)

    @RepeatedTest(5)
    fun `Parent div of target HTML element`() = runBlocking {

        val model = "codegemma:7b"

        val ollamaClient = OllamaHttpClient()

        val ollamaRequest = OllamaRequestBodyFormat(
            model, prompt, ModelAnswerSchemas.divSearchFormatFinal, false
        )

        val startTime = System.currentTimeMillis()

        val list = (1..20).map {
            async {
                testSemaphore.withPermit {
                    try {
                        val response = ollamaClient.request(ollamaRequest)
                        val resp = Json.decodeFromString<DivRespFinal>(response)

                        val driver = ChromeDriverExtension(null)
                        driver.get(WEBSITE)
                        val expected = driver.waitForElementByCssSelector("div[data-testid='search-form']")
                        val actual = driver.waitForElementByCssSelector(resp.div_css_selector)
                        driver.quit()

                        val message = "${resp.div_css_selector}; ${resp.div_element}"

                        if (expected != actual)
                            throw Exception(message)
                        "success $message"
                    } catch (e: Exception) {
                        "failure ${e.message}"
                    }
                }
            }
        }.awaitAll()

        val endTime = System.currentTimeMillis()

        val successCount = list.count { result -> "success" in result }
        val failureCount = 20 - successCount
        val percentage: Double = (successCount * 100).toDouble() / 20

        val path =
            Paths.get("src\\test\\kotlin\\parentDivTest\\codegemma-7b-without-semantic.txt")
        if (!Files.exists(path)) Files.createFile(path)

        list.forEach {
            Files.write(
                path,
                "$it\n".toByteArray(),
                StandardOpenOption.APPEND
            )
        }

        Files.write(
            path,
            (
                    "Success: $successCount, Failure: $failureCount\n" +
                            "Percentage: $percentage%\n" +
                            "Duration: ${(endTime - startTime) / 1000.0}s\n"
                    ).toByteArray(),
            StandardOpenOption.APPEND
        )

        list.forEach { println(it) }
        println("Success: $successCount, Failure: $failureCount")
        println("Percentage: $percentage%")
        println("Duration: ${(endTime - startTime) / 1000.0}s")

    }

}