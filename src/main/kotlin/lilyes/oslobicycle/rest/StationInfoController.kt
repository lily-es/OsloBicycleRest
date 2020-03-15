package lilyes.oslobicycle.rest

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException

import java.lang.Exception

@RestController
class StationInfoController(
        @Value("\${application.bikeapi.host}") private val host: String,
        @Value("\${application.bikeapi.status.url}") private val statusUrl: String,
        @Value("\${application.bikeapi.info.url}") private val infoUrl: String,
        @Value("\${application.identifier}") private val identifier: String,
        private val restTemplate: RestTemplate
) {

    @GetMapping("/info", produces = ["application/json;charset=UTF-8"])
    fun getInfo(): List<Station> {
        try {
            return getStationInfo()
        } catch (e: RequestFailedException) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not get station information", e)
        }
    }

    private fun getStationInfo(): List<Station> {
        val headers = HttpHeaders()
        headers.set("Client-Identifier", identifier)
        headers.accept = listOf(MediaType.APPLICATION_JSON)

        val entity = HttpEntity<String>(headers)
        try {
            val stationStatusResponse = restTemplate.exchange("$host/$statusUrl",
                    HttpMethod.GET, entity, ObjectNode::class.java)


            val stationInfoResponse = restTemplate.exchange("$host/$infoUrl",
                    HttpMethod.GET, entity, ObjectNode::class.java)

            if (stationStatusResponse.statusCode.is2xxSuccessful && stationInfoResponse.statusCode.is2xxSuccessful) {
                val infoBody = stationInfoResponse.body
                val statusBody = stationStatusResponse.body
                if (infoBody == null || statusBody == null) {
                    throw RequestFailedException("No info found")
                }
                return processStations(infoBody, statusBody)
            } else {
                throw RequestFailedException("Failed to get info")
            }
        } catch (e: HttpStatusCodeException){
            throw RequestFailedException("Failed to get info", e)
        }

    }

    private fun processStations(stationInfoJson: ObjectNode, stationStatusJson: ObjectNode): List<Station> {
        val stations = mutableListOf<Station>()

        val stationNames = processStationNames(stationInfoJson)
        val stationsStatus: List<StationStatus> = MAPPER.readValue(stationStatusJson.get("data").get("stations").toString())

        for (status: StationStatus in stationsStatus) {
            val name = stationNames[status.stationId] ?: continue//log this
            if (status.isInstalled) {
                stations.add(Station(name, status.numBikesAvailable, status.numDocksAvailable))
            }

        }
        return stations
    }

    private fun processStationNames(stationInfoJson: JsonNode): Map<String, String> {
        val stationNames = mutableMapOf<String, String>()
        val stations = stationInfoJson.get("data").get("stations")
        for (station in stations) {
            stationNames[station.get("station_id").asText()] = station.get("name").asText()
        }
        return stationNames
    }

    companion object {
        private val MAPPER = jacksonObjectMapper()
    }
}

class RequestFailedException(message: String, override val cause: Throwable?) : Exception(message, cause) {
    constructor(message: String) : this(message, null)
}