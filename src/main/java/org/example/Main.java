package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Main {
    public static String ORG = "VVO";
    public static String DST = "TLV";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar Test_Idea_Platform-1.0-SNAPSHOT.jar <path/to/tickets.json>");
            System.exit(1);
        }
        String path = args[0];
        List<Ticket> tickets = getTickets(path);

        printMap(mapOfCarrier(tickets));
        System.out.println("Difference between average and median price is: " + (avgPrice(tickets) - medianPrice(tickets)));
    }

    public static List<Ticket> getTickets(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root;
        JsonNode arr;
        List<Ticket> listTicket = new ArrayList<>();
        try {
            root = objectMapper.readTree(new File(path));
            arr = root.path("tickets");
            if (!arr.isArray()) {
                System.err.println("Expected a json array");
                System.exit(1);
            }
            for (JsonNode node : arr) {
                Ticket ticket = objectMapper.treeToValue(node, Ticket.class);
                if (ticket.getOrigin().equals(ORG) && ticket.getDestination().equals(DST)) {
                    listTicket.add(ticket);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (listTicket.isEmpty()) {
            System.err.println("Tickets not found");
            System.exit(1);
        }
        return listTicket;
    }

    public static Duration timeOfFlight(Ticket ticket) {
        DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("dd.MM.yy.H:mm");
        String departureSt = ticket.getDepartureDate()+"."+ticket.getDepartureTime();
        LocalDateTime departure = LocalDateTime.parse(departureSt, formatterDateTime);
        String arrivalSt = ticket.getArrivalDate()+"."+ticket.getArrivalTime();
        LocalDateTime arrival = LocalDateTime.parse(arrivalSt, formatterDateTime);
        return Duration.between(departure, arrival);
    }

    public static Duration minDuration(Duration duration1, Duration duration2) {
        return duration1.compareTo(duration2) <= 0 ? duration1 : duration2;
    }

    public static Map<String, Duration> mapOfCarrier (List<Ticket> tickets) {
        Map<String, Duration> map = new HashMap<>();
        for (Ticket ticket : tickets) {
            String carrier = ticket.getCarrier();
            if(!map.containsKey(carrier)) {
                map.put(carrier, timeOfFlight(ticket));
            } else {
                map.put(carrier, minDuration(map.get(carrier), timeOfFlight(ticket)));
            }
        }
        return map;
    }

    public static Double avgPrice(List<Ticket> tickets) {
        List<Integer> prices = new ArrayList<>();
        for (Ticket ticket : tickets) {
            prices.add(ticket.getPrice());
        }
        OptionalDouble average = prices.stream().mapToInt(p->p).average();
        if (average.isPresent()) {
            return average.getAsDouble();
        }
        return null;
    }

    public static Double medianPrice(List<Ticket> tickets) {
        List<Integer> prices = new ArrayList<>();
        for (Ticket ticket : tickets) {
            prices.add(ticket.getPrice());
        }
        Double median;
        prices.sort(Integer::compareTo);
        if (prices.size() % 2 == 0) {
            median = (double) ((prices.get(prices.size()/ 2) + prices.get(prices.size()/ 2 - 1))/2);
        } else {
            median = (double) prices.get(prices.size()/ 2);
        }
        return median;
    }
    private static String pretty(Duration d) {
        long days = d.toDays();
        long hours = d.minusDays(days).toHours();
        long minutes = d.minusHours(hours).toMinutes();
        return String.format("%02dd %02dh %02dm", days, hours, minutes);
    }
    private static void printMap(Map<String, Duration> map) {
        System.out.printf("%-10s| %s%n", "Carrier", "Min Duration");
        System.out.println("----------+------------");
        for (Map.Entry<String, Duration> entry : map.entrySet()) {
            System.out.printf("%-10s| %s%n", entry.getKey(), pretty(entry.getValue()));
        }
    }


}