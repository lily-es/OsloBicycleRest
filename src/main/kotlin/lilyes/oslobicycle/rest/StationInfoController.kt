package lilyes.oslobicycle.rest

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class StationInfoController(private val stationInfoService: StationInfoService) {

    @GetMapping("/info", produces = ["application/json;charset=UTF-8"])
    fun getInfo(): List<Station> {
        try {
            return stationInfoService.getStationInfo()
        } catch (e: RequestFailedException) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not get station information", e)
        }
    }
}