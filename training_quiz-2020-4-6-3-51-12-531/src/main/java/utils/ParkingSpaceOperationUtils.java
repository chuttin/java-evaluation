package utils;

import exception.InvalidTicketException;
import exception.ParkingLotFullException;
import parking.ParkingLot;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingSpaceOperationUtils {
  private static final String PARKING_LOT_FULL_MESSAGE = "非常抱歉，由于车位已满，暂时无法为您停车！";
  private static final String INVALID_TICKET_MESSAGE = "很抱歉，无法通过您提供的停车券为您找到相应的车辆，请您再次核对停车券是否有效！";

  private ParkingSpaceOperationUtils() {}

  public static String distributeParkingSpaces(List<ParkingLot> parkingLots, String carNumber) {
    List<ParkingLot> parkingLotSortedByIndexWithRemainingSpaces = parkingLots.stream()
            .sorted((parkingLot1, parkingLot2) -> parkingLot1.getParkingLotIndex().compareTo(parkingLot2.getParkingLotIndex()))
            .filter(parkingLot -> parkingLot.getRemainingParkingSpaces().size() != 0)
            .collect(Collectors.toList());

    if (parkingLotSortedByIndexWithRemainingSpaces.size() == 0) {
      throw new ParkingLotFullException(PARKING_LOT_FULL_MESSAGE);
    } else {
      ParkingLot distributeParkingLot = parkingLotSortedByIndexWithRemainingSpaces.get(0);
      String distributeParkingLotIndex = distributeParkingLot.getParkingLotIndex();
      List<Integer> remainingParkingSpacesList = distributeParkingLot.getRemainingParkingSpaces();
      int distributeParkingSpaceIndex = Collections.min(remainingParkingSpacesList);
      remainingParkingSpacesList.remove(remainingParkingSpacesList.indexOf(distributeParkingSpaceIndex));

      distributeParkingLot.parking(distributeParkingSpaceIndex, carNumber);
      return distributeParkingLotIndex + "," + distributeParkingSpaceIndex;
    }
  }

  public static void returnCar(List<ParkingLot> parkingLots, String ticket) {
    String[] ticketInfo = ticket.split(",");
    String parkingLotIndex = ticketInfo[0];
    String parkingSpaceIndex = ticketInfo[1];
    String carNumber = ticketInfo[2];

    if(isTicketValid(parkingLotIndex, parkingSpaceIndex)) {
      parkingLots.stream().filter(parkingLot -> parkingLot.getParkingLotIndex().equals(parkingLotIndex))
              .forEach(parkingLot -> {
                int removeCarNum = parkingLot.pickCar(Integer.parseInt(parkingSpaceIndex), carNumber);
                if (removeCarNum == 0) {
                  throw new InvalidTicketException(INVALID_TICKET_MESSAGE);
                }
                parkingLot.getRemainingParkingSpaces().add(Integer.parseInt(parkingSpaceIndex));
              });
    } else {
      throw new InvalidTicketException(INVALID_TICKET_MESSAGE);
    }
  }

  private static boolean isTicketValid(String parkingLotIndex, String parkingSpaceIndex) {
    List<ParkingLot> allParkingLots = RepositoryOperationUtils.queryAllParkingLot();
    List<String> allParkingLotsIndex = allParkingLots.stream()
            .map(parkingLot -> parkingLot.getParkingLotIndex())
            .collect(Collectors.toList());

    if (!allParkingLotsIndex.contains(parkingLotIndex)) {
      return false;
    }

    int totalSpacesNum = RepositoryOperationUtils.queryTotalSpacesNum(parkingLotIndex);
    int parkingSpaceInTicket = Integer.parseInt(parkingSpaceIndex);
    return parkingSpaceInTicket > 0 && parkingSpaceInTicket <= totalSpacesNum;
  }
}