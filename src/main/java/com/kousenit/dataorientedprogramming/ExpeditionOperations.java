package com.kousenit.dataorientedprogramming;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kousenit.dataorientedprogramming.SpaceDevRecords.*;

/**
 * Operations over the expedition data — behavior lives here, not on the records.
 *
 * This is the DOP "expression problem" sweet spot: the set of types (Expedition,
 * CrewMember, Astronaut, etc.) is fixed by the API, but we keep adding new
 * operations without touching the records themselves.
 */
public class ExpeditionOperations {

    // Flatten nested records into simpler views
    public static List<AstronautAssignment> toAssignments(List<Expedition> expeditions) {
        return expeditions.stream()
                .flatMap(expedition -> expedition.crew().stream()
                        .map(member -> new AstronautAssignment(
                                member.astronaut().name(),
                                member.role().role(),
                                member.astronaut().agency().abbrev(),
                                expedition.spacestation().name()
                        )))
                .toList();
    }

    public static Map<String, List<String>> crewByStation(List<Expedition> expeditions) {
        return toAssignments(expeditions).stream()
                .collect(Collectors.groupingBy(
                        AstronautAssignment::stationName,
                        Collectors.mapping(AstronautAssignment::astronautName,
                                Collectors.toList())
                ));
    }

    public static Map<String, List<String>> crewByAgency(List<Expedition> expeditions) {
        return toAssignments(expeditions).stream()
                .collect(Collectors.groupingBy(
                        AstronautAssignment::agency,
                        Collectors.mapping(AstronautAssignment::astronautName,
                                Collectors.toList())
                ));
    }

    public static Map<String, Long> crewCountByStation(List<Expedition> expeditions) {
        return expeditions.stream()
                .collect(Collectors.groupingBy(
                        exp -> exp.spacestation().name(),
                        Collectors.summingLong(exp -> exp.crew().size())
                ));
    }

    public static List<AstronautAssignment> filterByRole(
            List<Expedition> expeditions, String role) {
        return toAssignments(expeditions).stream()
                .filter(a -> a.role().equalsIgnoreCase(role))
                .toList();
    }

    public static List<AstronautAssignment> filterByAgency(
            List<Expedition> expeditions, String agencyAbbrev) {
        return toAssignments(expeditions).stream()
                .filter(a -> a.agency().equalsIgnoreCase(agencyAbbrev))
                .toList();
    }

    // Turn Result into human-readable output via pattern matching
    public static String describeResult(Result result) {
        return switch (result) {
            case Result.Success(var expeditions) ->
                    formatExpeditions(expeditions);
            case Result.NetworkError(var message) ->
                    "Network error: " + message;
            case Result.ClientError(var code, var body) ->
                    "Client error %d: %s".formatted(code, body);
            case Result.ServerError(var code) ->
                    "Server error %d — try again later".formatted(code);
            case Result.RateLimited(var retryAfter) ->
                    "Rate limited. Retry after: " + retryAfter;
        };
    }

    private static String formatExpeditions(List<Expedition> expeditions) {
        var sb = new StringBuilder("Astronauts currently in space:\n");
        for (var expedition : expeditions) {
            sb.append("\n%s (%s):\n".formatted(
                    expedition.spacestation().name(),
                    expedition.spacestation().orbit()));
            for (var member : expedition.crew()) {
                sb.append("  - %s (%s, %s)\n".formatted(
                        member.astronaut().name(),
                        member.role().role(),
                        member.astronaut().agency().abbrev()));
            }
        }
        return sb.toString();
    }
}
