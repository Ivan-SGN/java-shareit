package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void CreateRequestTest() throws Exception {
        ItemRequestDto requestDto = createRequestDto();

        Mockito.when(itemRequestService.create(eq(1L), any(ItemRequestCreateDto.class)))
                .thenReturn(requestDto);

        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need drill");

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need drill"));
    }

    @Test
    void GetOwnRequestsTest() throws Exception {
        ItemRequestDto requestDto = createRequestDto();

        Mockito.when(itemRequestService.getOwnRequests(1L))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need drill"));
    }

    @Test
    void GetAllRequestsTest() throws Exception {
        ItemRequestDto requestDto = createRequestDto();

        Mockito.when(itemRequestService.getAllRequests(1L))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need drill"));
    }

    @Test
    void GetRequestByIdTest() throws Exception {
        ItemRequestDto requestDto = createRequestDto();

        Mockito.when(itemRequestService.getById(1L, 1L))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need drill"));
    }

    private ItemRequestDto createRequestDto() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need drill");
        requestDto.setCreated(LocalDateTime.now());

        return requestDto;
    }
}