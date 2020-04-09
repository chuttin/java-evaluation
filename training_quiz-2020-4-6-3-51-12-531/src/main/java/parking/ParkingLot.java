package parking;

import utils.RepositoryOperationUtils;
import java.util.List;

public class ParkingLot {
  private List<Integer> remainingParkingSpaces;
  private String parkingLotIndex;

  public ParkingLot(String parkingLotIndex) {
    this.parkingLotIndex = parkingLotIndex;
  }

  public int parking(int parkingSpaceIndex, String carNumber) {
    String saveSql = String.format("INSERT INTO parking_lot_%s(id, car_number) VALUES (?, ?);", this.parkingLotIndex);
    int saveCarNum = RepositoryOperationUtils.saveCar(saveSql, parkingSpaceIndex, carNumber);
    return saveCarNum;
  }

  public int pickCar(int parkingSpaceIndex, String carNumber) {
    String removeSql = String.format("DELETE FROM parking_lot_%s WHERE id = ? AND car_number = ?;", this.parkingLotIndex);
    int removeCarNum = RepositoryOperationUtils.removeCar(removeSql, parkingSpaceIndex, carNumber);
    return removeCarNum;
  }

  public void readRemainingId(String parkingLotIndex) {
    List<Integer> remainingIdList = RepositoryOperationUtils.queryRemainingSpaces(parkingLotIndex);
    this.remainingParkingSpaces = remainingIdList;
  }

  public String getParkingLotIndex() {
    return parkingLotIndex;
  }

  public List<Integer> getRemainingParkingSpaces() {
    return remainingParkingSpaces;
  }
}
