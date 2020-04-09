import parking.ParkingLot;
import utils.ParkingSpaceOperationUtils;
import utils.RepositoryOperationUtils;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Application {
  private static final String CAR_NUMBER_FORMAT_WRONG = "您输入的车牌号有误（以大写字母为开头，共6位）";
  private static final String INITIAL_PARKING_SPACES_NUMBER_WRONG = "您初始化的停车场数据有误 车位数必须大于0";
  private static List<ParkingLot> parkingLots = RepositoryOperationUtils.queryAllParkingLot();

  public static void main(String[] args) {
    readDataBase();
    operateParking();
  }

  public static void operateParking() {
    while (true) {
      System.out.println("1. 初始化停车场数据\n2. 停车\n3. 取车\n4. 退出\n请输入你的选择(1~4)：");
      Scanner printItem = new Scanner(System.in);
      String choice = printItem.next();
      if (choice.equals("4")) {
        RepositoryOperationUtils.close();
        System.out.println("系统已退出");
        break;
      }
      handle(choice);
    }
  }

  private static void handle(String choice) {
    Scanner scanner = new Scanner(System.in);
    if (choice.equals("1")) {
      System.out.println("请输入初始化数据\n格式为\"停车场编号1：车位数,停车场编号2：车位数, 以此类推\" 如 \"A:8,B:9...\"：");
      String initInfo = scanner.next();
      init(initInfo);
    }
    else if (choice.equals("2")) {
      System.out.println("请输入车牌号\n格式为\"车牌号\" 如: \"A12098\"：");
      String carInfo = scanner.next();
      if (isCarNumberFormatCorrect(carInfo)) {
        String ticket = park(carInfo);
        String[] ticketDetails = ticket.split(",");
        System.out.format("已将您的车牌号为%s的车辆停到%s停车场%s号车位，停车券为：%s，请您妥善保存。\n", ticketDetails[2], ticketDetails[0], ticketDetails[1], ticket);
      } else {
        System.out.println(CAR_NUMBER_FORMAT_WRONG);
        handle("2");
      }
    }
    else if (choice.equals("3")) {
      System.out.println("请输入停车券信息\n格式为\"停车场编号1,车位编号,车牌号\" 如 \"A,1,8\"：");
      String ticket = scanner.next();
      String car = fetch(ticket);
      System.out.format("已为您取到车牌号为%s的车辆，很高兴为您服务，祝您生活愉快!\n", car);
    }
  }

  public static void init(String initInfo) {
    RepositoryOperationUtils.removeAllSubTAble();

    List<String> parkingLotIndexList = new LinkedList<>();
    List<Integer> parkingLotSpacesNumList = new LinkedList<>();
    String[] parkingLotInfoArr = initInfo.split(",");

    for (String parkingLotInfo : parkingLotInfoArr) {
      String parkingLotIndex = parkingLotInfo.split(":")[0];
      int totalParkingNum = Integer.parseInt(parkingLotInfo.split(":")[1]);
      if (totalParkingNum <= 0) {
        System.out.println(INITIAL_PARKING_SPACES_NUMBER_WRONG);
        handle("1");
        break;
      } else {
        parkingLotIndexList.add(parkingLotIndex);
        parkingLotSpacesNumList.add(totalParkingNum);
      }
    }

    RepositoryOperationUtils.initialAllTable(parkingLotIndexList, parkingLotSpacesNumList);

    parkingLots.clear();

    parkingLotIndexList.stream().forEach(parkingLotIndex -> parkingLots.add(new ParkingLot(parkingLotIndex)));
    readDataBase();
  }

  public static String park(String carNumber) {
    String distributedParkingIndex = ParkingSpaceOperationUtils.distributeParkingSpaces(parkingLots, carNumber);
    return distributedParkingIndex + "," + carNumber;
  }

  public static String fetch(String ticket) {
    ParkingSpaceOperationUtils.returnCar(parkingLots, ticket);
    return ticket.split(",")[2];
  }

  private static boolean isCarNumberFormatCorrect(String carNumber) {
    return carNumber.matches("^[A-Z]\\w{5}");
  }

  private static void readDataBase() {
    for (ParkingLot parkingLot : parkingLots) {
      parkingLot.readRemainingId(parkingLot.getParkingLotIndex());
    }
  }
}