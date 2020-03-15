package lilyes.oslobicycle.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class StationStatusTest {

    @Test
    fun `test number of bikes and locks is set correctly`() {
        var json = """
            {
        "is_installed": 1,
        "is_renting": 0,
        "num_bikes_available": 7,
        "num_docks_available": 5,
        "last_reported": 1540219230,
        "is_returning": 1,
        "station_id": "627"
      }
        """.trimIndent()

        var stationStatus: StationStatus = objectMapper.readValue(json)
        var expected = StationStatus("627", 0, 5, true)
        assertEquals(expected, stationStatus)

        json = """
            {
        "is_installed": 1,
        "is_renting": 1,
        "num_bikes_available": 7,
        "num_docks_available": 5,
        "last_reported": 1540219230,
        "is_returning": 0,
        "station_id": "627"
      }
        """.trimIndent()

        stationStatus = objectMapper.readValue(json)
        expected = StationStatus("627", 7, 0, true)
        assertEquals(expected, stationStatus)
    }

    @Test
    fun `test that virtual station maps correctly`() {
        val json = """
            {
        "is_installed": 1,
        "is_renting": 1,
        "num_bikes_available": 7,
        "last_reported": 1540219230,
        "is_returning": 1,
        "station_id": "627"
      }
        """.trimIndent()
        val expected = StationStatus("627", 7, 99, true)
        val stationStatus: StationStatus = objectMapper.readValue(json)
        assertEquals(expected, stationStatus)
    }

    companion object {
        private val objectMapper = jacksonObjectMapper()
    }
}