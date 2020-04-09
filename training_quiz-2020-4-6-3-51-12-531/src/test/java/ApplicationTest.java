import exception.InvalidTicketException;
import exception.ParkingLotFullException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationTest {

  @Test
  void should_return_ticket_information_when_park_given_init_and_car_and_general_boy() {
    Application.init("A:2,B:2");

    String aTicket = Application.park("A12098");
    assertEquals("A,1,A12098", aTicket);
    String bTicket = Application.park("B16920");
    assertEquals("A,2,B16920", bTicket);
    String cTicket = Application.park("C76129");
    assertEquals("B,1,C76129", cTicket);

  }

  @Test
  void should_throw_exception_with_message_when_park_given_init_and_car_and_general_boy() {
    Application.init("A:1,B:1");
    Application.park("A12908");
    Application.park("B38201");
    assertThrows(ParkingLotFullException.class, () -> Application.park("H12626"));
  }

  @Test
  void should_return_car_information_when_fetch_given_ticket_and_car() {
    Application.init("A:8,B:10");

    String aTicket = Application.park("A12198");
    String acar = Application.fetch(aTicket);
    assertEquals("A12198", acar);

    String bTicket = Application.park("B78210");
    String bCar = Application.fetch(bTicket);
    assertEquals("B78210", bCar);

    String cTicket = Application.park("C98201");
    String cCar = Application.fetch(cTicket);
    assertEquals("C98201", cCar);
  }

  @Test
  void should_throw_exception_with_message_when_fetch_given_ticket_information_is_not_correct() {
    Application.init("A:8,B:10");

    assertThrows(InvalidTicketException.class, () -> Application.fetch("C,1,A12098"));
    assertThrows(InvalidTicketException.class, () -> Application.fetch("A,9,A12098"), "停车券无效");
    assertThrows(InvalidTicketException.class, () -> Application.fetch("B,-1,A12098"), "停车券无效");
  }

  @Test
  void should_throw_exception_with_message_when_fetch_given_space_has_no_car() {
    Application.init("A:8,B:10");

    String aTicket = Application.park("A12098");
    Application.fetch(aTicket);

    assertThrows(InvalidTicketException.class, () -> Application.fetch(aTicket));
  }

  @Test
  void should_throw_exception_with_message_when_fetch_given_space_is_other_car() {
    Application.init("A:8,B:10");

    String aTicket = Application.park("A12098");
    Application.fetch(aTicket);
    Application.park("B12598");

    assertThrows(InvalidTicketException.class, () -> Application.fetch(aTicket));

  }
}