package lilyes.oslobicycle.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class StationInfoServiceTest {

    private val objectMapper = jacksonObjectMapper()
    private val stationInfoService = StationInfoService("", "", "", "", mockk(), objectMapper)
    private val stationInfo = javaClass.classLoader.getResource("stationInfo.json")!!.readText(Charsets.UTF_8)
    private val stationStatus = javaClass.classLoader.getResource("stationStatus.json")!!.readText(Charsets.UTF_8)

    @Test
    fun `Test that processStationNames processes json correctly`() {
        val expectedNames = mapOf(
                "627" to "Skøyen Stasjon",
                "623" to "7 Juni Plassen",
                "610" to "Sotahjørnet"
        )

        val jsonObject = objectMapper.readTree(stationInfo)

        assertEquals(expectedNames, stationInfoService.processStationNames(jsonObject))
    }

    @Test
    fun `Test that processStations processes json correctly`() {
        val expectedStations = listOf(
                Station("Skøyen Stasjon", 7, 5),
                Station("7 Juni Plassen", 4, 8),
                Station("Sotahjørnet", 4, 9)
        )

        val stationInfoObject = objectMapper.readTree(stationInfo)
        val stationStatusObject = objectMapper.readTree(stationStatus)

        assertEquals(expectedStations, stationInfoService.processStations(stationInfoObject, stationStatusObject))
    }
}