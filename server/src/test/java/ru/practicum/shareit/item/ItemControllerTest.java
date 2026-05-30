package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    @Test
    void CreateItemTest() throws Exception {
        ItemDto itemDto = createItemDto();

        Mockito.when(itemService.create(eq(1L), any(ItemCreateDto.class)))
                .thenReturn(itemDto);

        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("Drill");
        createDto.setDescription("Power drill");
        createDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void UpdateItemTest() throws Exception {
        ItemDto itemDto = createItemDto();
        itemDto.setName("Updated Drill");

        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Updated Drill");

        Mockito.when(itemService.update(eq(1L), eq(1L), any(ItemUpdateDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Drill"));
    }

    @Test
    void GetItemByIdTest() throws Exception {
        ItemDto itemDto = createItemDto();

        Mockito.when(itemService.getById(1L, 1L))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void GetItemsByOwnerTest() throws Exception {
        ItemDto itemDto = createItemDto();

        Mockito.when(itemService.getByOwner(1L))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void SearchItemsTest() throws Exception {
        ItemDto itemDto = createItemDto();

        Mockito.when(itemService.search("drill"))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void CreateCommentTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Good item");

        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("Good item");

        Mockito.when(commentService.create(any(CommentCreateDto.class), eq(1L), eq(1L)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Good item"));
    }

    private ItemDto createItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Drill");
        itemDto.setDescription("Power drill");
        itemDto.setAvailable(true);

        return itemDto;
    }
}