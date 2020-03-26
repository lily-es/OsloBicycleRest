package lilyes.oslobicycle.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.*
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.RestTemplate

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StationInfoControllerTest(
        @Value("\${application.bikeapi.host}") private val host: String,
        @Value("\${application.bikeapi.status.url}") private val statusUrl: String,
        @Value("\${application.bikeapi.info.url}") private val infoUrl: String,
        @Value("\${application.identifier}") private val identifier: String,
        @Value("classpath:stationInfo.json") private val stationInfo: Resource,
        @Value("classpath:stationStatus.json") private val stationStatus: Resource
) {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mockServer: MockRestServiceServer

    @BeforeAll
    fun setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @BeforeEach
    fun resetMockServer() {
        mockServer.reset()
    }

    @Test
    fun `test endpoint returns correct data`() {
        mockServer.expect(requestTo("$host/$statusUrl"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Client-Identifier", identifier))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(stationStatus))

        mockServer.expect(requestTo("$host/$infoUrl"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Client-Identifier", identifier))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(stationInfo))


        val stations = listOf(
                Station("Skøyen Stasjon", 7, 5),
                Station("7 Juni Plassen", 4, 8),
                Station("Sotahjørnet", 4, 9)
        )

        val json = mockMvc.perform(get("/info"))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        val result = objectMapper.readValue<List<Station>>(json)
        assertEquals(stations, result)
    }

    @Test
    fun `test endpoint returns status 500 when it cannot fetch the data`() {
        mockServer.expect(requestTo("$host/$statusUrl"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Client-Identifier", identifier))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR))

        mockServer.expect(requestTo("$host/$infoUrl"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Client-Identifier", identifier))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR))


        mockMvc.perform(get("/info"))
                .andExpect(status().isInternalServerError)
    }
}
