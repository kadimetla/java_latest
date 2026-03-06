package com.kousenit.dataorientedprogramming;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.kousenit.dataorientedprogramming.SpaceDevRecords.*;
import static org.assertj.core.api.Assertions.assertThat;

class SpaceDevServiceTest {

    private final SpaceDevService service = new SpaceDevService();

    @Test
    void fetchExpeditions_returnsLiveData() {
        Result result = service.fetchExpeditions();

        // Pattern match on the sealed Result — all five cases handled
        switch (result) {
            case Result.Success(var expeditions) -> {
                System.out.println(ExpeditionOperations.describeResult(result));
                assertThat(expeditions).isNotEmpty();
                expeditions.forEach(exp -> {
                    assertThat(exp.spacestation()).isNotNull();
                    assertThat(exp.crew()).isNotEmpty();
                });
            }
            case Result.RateLimited(var retryAfter) ->
                    System.out.println("Rate limited (free API tier), retry after: " + retryAfter);
            case Result.NetworkError(var msg) ->
                    System.out.println("Network error (expected in offline environments): " + msg);
            case Result.ClientError(var code, var body) ->
                    System.out.println("Client error %d: %s".formatted(code, body));
            case Result.ServerError(var code) ->
                    System.out.println("Server error: " + code);
        }
    }

    @Test
    void expeditionOperations_withMockData() {
        // Mock data for reliable, offline testing
        var expeditions = List.of(
                new Expedition(1, "Expedition 72",
                        "2025-09-01", null,
                        new SpaceStation(1, "International Space Station", "Low Earth Orbit"),
                        List.of(
                                new CrewMember(new Role("Commander"),
                                        new Astronaut(1, "Sunita Williams",
                                                new Agency("National Aeronautics and Space Administration", "NASA"))),
                                new CrewMember(new Role("Flight Engineer"),
                                        new Astronaut(2, "Butch Wilmore",
                                                new Agency("National Aeronautics and Space Administration", "NASA"))),
                                new CrewMember(new Role("Flight Engineer"),
                                        new Astronaut(3, "Oleg Kononenko",
                                                new Agency("Russian Federal Space Agency", "RFSA")))
                        )),
                new Expedition(2, "Shenzhou 19",
                        "2024-10-30", null,
                        new SpaceStation(2, "Tiangong", "Low Earth Orbit"),
                        List.of(
                                new CrewMember(new Role("Commander"),
                                        new Astronaut(4, "Cai Xuzhe",
                                                new Agency("China National Space Administration", "CNSA"))),
                                new CrewMember(new Role("Operator"),
                                        new Astronaut(5, "Song Lingdong",
                                                new Agency("China National Space Administration", "CNSA")))
                        ))
        );

        // crewByStation
        Map<String, List<String>> byStation = ExpeditionOperations.crewByStation(expeditions);
        assertThat(byStation).containsKey("International Space Station");
        assertThat(byStation.get("International Space Station")).hasSize(3);
        assertThat(byStation.get("Tiangong")).hasSize(2);

        // crewByAgency
        Map<String, List<String>> byAgency = ExpeditionOperations.crewByAgency(expeditions);
        assertThat(byAgency).containsKeys("NASA", "RFSA", "CNSA");
        assertThat(byAgency.get("NASA")).containsExactlyInAnyOrder("Sunita Williams", "Butch Wilmore");

        // crewCountByStation
        Map<String, Long> counts = ExpeditionOperations.crewCountByStation(expeditions);
        assertThat(counts)
                .containsEntry("International Space Station", 3L)
                .containsEntry("Tiangong", 2L);

        // filterByRole
        List<AstronautAssignment> commanders = ExpeditionOperations.filterByRole(expeditions, "Commander");
        assertThat(commanders).hasSize(2)
                .extracting(AstronautAssignment::astronautName)
                .containsExactlyInAnyOrder("Sunita Williams", "Cai Xuzhe");

        // filterByAgency
        List<AstronautAssignment> nasaCrew = ExpeditionOperations.filterByAgency(expeditions, "NASA");
        assertThat(nasaCrew).hasSize(2);
    }

    @Test
    void describeResult_allVariants() {
        // Verify pattern matching handles all five Result variants
        var success = new Result.Success(List.of());
        assertThat(ExpeditionOperations.describeResult(success))
                .contains("Astronauts currently in space");

        var networkError = new Result.NetworkError("Connection refused");
        assertThat(ExpeditionOperations.describeResult(networkError))
                .contains("Network error").contains("Connection refused");

        var clientError = new Result.ClientError(404, "Not Found");
        assertThat(ExpeditionOperations.describeResult(clientError))
                .contains("Client error 404");

        var serverError = new Result.ServerError(503);
        assertThat(ExpeditionOperations.describeResult(serverError))
                .contains("Server error 503");

        var rateLimited = new Result.RateLimited("60");
        assertThat(ExpeditionOperations.describeResult(rateLimited))
                .contains("Rate limited").contains("60");
    }
}
