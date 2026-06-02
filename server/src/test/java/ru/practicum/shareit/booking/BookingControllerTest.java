package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void createBookingTest() throws Exception {
        BookingDto bookingDto = createBookingDto();

        Mockito.when(bookingService.create(eq(1L), any(BookingCreateDto.class)))
                .thenReturn(bookingDto);

        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(1L);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveBookingTest() throws Exception {
        BookingDto bookingDto = createBookingDto();
        bookingDto.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingService.approve(1L, 1L, true))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        BookingDto bookingDto = createBookingDto();

        Mockito.when(bookingService.getById(1L, 1L))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getBookingsByUserTest() throws Exception {
        BookingDto bookingDto = createBookingDto();

        Mockito.when(bookingService.getByUser(1L, "ALL"))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getBookingsByOwnerTest() throws Exception {
        BookingDto bookingDto = createBookingDto();

        Mockito.when(bookingService.getByOwner(1L, "ALL"))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    private BookingDto createBookingDto() {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setStatus(BookingStatus.WAITING);

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");

        bookingDto.setBooker(user);
        bookingDto.setItem(item);

        return bookingDto;
    }
}