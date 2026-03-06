package com.kousenit.dataorientedprogramming;

import java.util.List;

/**
 * Records mapping the Launch Library 2 API response for active space station expeditions.
 * Endpoint: https://ll.thespacedevs.com/2.3.0/expeditions/?is_active=true&mode=detailed
 *
 * The nested hierarchy (Expedition -> SpaceStation + CrewMember -> Astronaut -> Agency)
 * makes this a natural fit for data-oriented programming: a fixed set of types
 * with an open set of operations.
 */
public class SpaceDevRecords {

    public record ExpeditionResponse(
            int count,
            List<Expedition> results
    ) {}

    public record Expedition(
            int id,
            String name,
            String start,
            String end,
            SpaceStation spacestation,
            List<CrewMember> crew
    ) {}

    public record SpaceStation(
            int id,
            String name,
            String orbit
    ) {}

    public record CrewMember(
            Role role,
            Astronaut astronaut
    ) {}

    public record Role(
            String role
    ) {}

    public record Astronaut(
            int id,
            String name,
            Agency agency
    ) {}

    public record Agency(
            String name,
            String abbrev
    ) {}

    // Flattened view for downstream processing
    public record AstronautAssignment(
            String astronautName,
            String role,
            String agency,
            String stationName
    ) {}

    // Result hierarchy: the DOP showcase
    public sealed interface Result {
        record Success(List<Expedition> expeditions) implements Result {}
        record NetworkError(String message) implements Result {}
        record ClientError(int statusCode, String body) implements Result {}
        record ServerError(int statusCode) implements Result {}
        record RateLimited(String retryAfter) implements Result {}
    }
}
