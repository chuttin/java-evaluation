package utils;

import parking.ParkingLot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RepositoryOperationUtils {
  private static Connection sqlConnection;

  static {
    try {
      sqlConnection = JdbcUtils.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private RepositoryOperationUtils() {}

  public static int saveCar(String sql, int parkingSpaceIndex, String carNumber) {
    PreparedStatement sqlSaveStatement = null;
    try {
      sqlSaveStatement = sqlConnection.prepareStatement(sql);
      sqlSaveStatement.setInt(1, parkingSpaceIndex);
      sqlSaveStatement.setString(2, carNumber);
      int insertNum = sqlSaveStatement.executeUpdate();
      return insertNum;
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    } finally {
      JdbcUtils.close(sqlSaveStatement);
    }
  }

  public static int removeCar(String sql, int parkingSpaceIndex, String carNumber) {
    PreparedStatement sqlRemoveStatement = null;
    try {
      sqlRemoveStatement = sqlConnection.prepareStatement(sql);
      sqlRemoveStatement.setInt(1, parkingSpaceIndex);
      sqlRemoveStatement.setString(2, carNumber);

      int deleteNum = sqlRemoveStatement.executeUpdate();
      return deleteNum;
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    } finally {
      JdbcUtils.close(sqlRemoveStatement);
    }
  }

  public static void removeAllSubTAble() {
    PreparedStatement sqlRemoveAllStatement = null;
    try {
      List<ParkingLot> allParkingLotList = queryAllParkingLot();

      for (ParkingLot parkingLot : allParkingLotList) {
        String sqlRemoveAll = String.format("DROP TABLE parking_lot_%s", parkingLot.getParkingLotIndex());
        sqlRemoveAllStatement = sqlConnection.prepareStatement(sqlRemoveAll);
        sqlRemoveAllStatement.executeUpdate();
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      JdbcUtils.close(sqlRemoveAllStatement);
    }
  }

  public static List<ParkingLot> queryAllParkingLot() {
    PreparedStatement sqlQueryAllParkingLotNameStatement = null;
    ResultSet sqlQueryAllParkingLotNameResultSet = null;
    List<ParkingLot> allParkingLot = new LinkedList<>();
    try {

      String sqlCommand = "SELECT parking_lot_name FROM parking_lot_main;";
      sqlQueryAllParkingLotNameStatement = sqlConnection.prepareStatement(sqlCommand);
      sqlQueryAllParkingLotNameResultSet = sqlQueryAllParkingLotNameStatement.executeQuery();
      while (sqlQueryAllParkingLotNameResultSet.next()) {
        String[] parkingLotInfo = sqlQueryAllParkingLotNameResultSet.getString(1).split("_");
        String parkingLotIndex = parkingLotInfo[parkingLotInfo.length - 1];
        allParkingLot.add(new ParkingLot(parkingLotIndex));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      JdbcUtils.close(sqlQueryAllParkingLotNameStatement, sqlQueryAllParkingLotNameResultSet);
    }
    return allParkingLot;
  }

  private static void initialSubTable(String parkingLotIndex) {
    PreparedStatement sqlStatement = null;
    try {
      String createSubTableCommand = String.format("CREATE TABLE parking_lot_%s (\n" +
              "  id INT NOT NULL,\n" +
              "  car_number VARCHAR(6) NOT NULL,\n" +
              "  PRIMARY KEY (id));", parkingLotIndex);

      sqlStatement = sqlConnection.prepareStatement(createSubTableCommand);
      sqlStatement.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      JdbcUtils.close(sqlStatement);
    }
  }

  public static void initialAllTable(List<String> parkingLotIndexList, List<Integer> parkingLotSpacesNumList) {
    PreparedStatement sqlSaveStatement = null;
    try {
      sqlSaveStatement = sqlConnection.prepareStatement("TRUNCATE TABLE parking_lot_main;");
      sqlSaveStatement.executeUpdate();

      for (int i = 0; i < parkingLotIndexList.size(); i++) {
        String sqlCommand = "INSERT INTO parking_lot_main(parking_lot_name, spaces_number) VALUES (?, ?);";
        sqlSaveStatement = sqlConnection.prepareStatement(sqlCommand);
        sqlSaveStatement.setString(1, String.format("parking_lot_%s", parkingLotIndexList.get(i)));
        sqlSaveStatement.setInt(2, parkingLotSpacesNumList.get(i));
        sqlSaveStatement.executeUpdate();
        initialSubTable(parkingLotIndexList.get(i));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      JdbcUtils.close(sqlSaveStatement);
    }
  }

  public static List<Integer> queryRemainingSpaces(String parkingLotIndex) {
    PreparedStatement sqlQueryStatement = null;
    ResultSet sqlQueryResultSet = null;
    try {
      int totalSpacesNumber = queryTotalSpacesNum(parkingLotIndex);

      String sqlAlreadyOccupiedId = String.format("SELECT id FROM parking_lot_%s;", parkingLotIndex);
      sqlQueryStatement = sqlConnection.prepareStatement(sqlAlreadyOccupiedId);
      sqlQueryResultSet = sqlQueryStatement.executeQuery();

      List<Integer> alreadyOccupiedIdList = new LinkedList<>();
      while (sqlQueryResultSet.next()) {
        alreadyOccupiedIdList.add(sqlQueryResultSet.getInt(1));
      }

      return Stream.iterate(1, i -> i + 1)
              .limit(totalSpacesNumber)
              .filter(num -> !alreadyOccupiedIdList.contains(num))
              .collect(Collectors.toList());

    } catch (SQLException e) {
      e.printStackTrace();
      return new LinkedList<>();
    } finally {
      JdbcUtils.close(sqlQueryStatement, sqlQueryResultSet);
    }
  }

  public static int queryTotalSpacesNum(String parkingLotIndex) {
    PreparedStatement sqlQueryStatement = null;
    ResultSet sqlQueryResultSet = null;
    try {
      String sqlQueryTotal = "SELECT spaces_number FROM parking_lot_main WHERE parking_lot_name = ?;";
      sqlQueryStatement = sqlConnection.prepareStatement(sqlQueryTotal);
      sqlQueryStatement.setString(1, String.format("parking_lot_%s", parkingLotIndex));
      sqlQueryResultSet = sqlQueryStatement.executeQuery();

      int totalSpacesNumber = 0;
      if (sqlQueryResultSet.next()) {
        totalSpacesNumber = sqlQueryResultSet.getInt(1);
      }
      return totalSpacesNumber;

    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    } finally {
      JdbcUtils.close(sqlQueryStatement, sqlQueryResultSet);
    }
  }

  public static void close() {
    JdbcUtils.close(sqlConnection);
  }
}