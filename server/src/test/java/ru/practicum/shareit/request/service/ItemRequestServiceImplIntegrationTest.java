package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Test
    void CreateRequestTest() {
        User user = userRepository.save(createUser());

        ItemRequestDto createdRequest =
                itemRequestService.create(user.getId(), createRequestDto());

        ItemRequest savedRequest = itemRequestRepository.findById(createdRequest.getId())
                .orElseThrow();

        assertThat(savedRequest.getDescription()).isEqualTo("Need drill");
        assertThat(savedRequest.getRequestor().getId()).isEqualTo(user.getId());
        assertThat(savedRequest.getCreated()).isNotNull();
    }

    @Test
    void GetOwnRequestsTest() {
        User user = userRepository.save(createUser());

        ItemRequest request = new ItemRequest();
        request.setDescription("Need drill");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        itemRequestRepository.save(request);

        Collection<ItemRequestDto> requests =
                itemRequestService.getOwnRequests(user.getId());

        assertThat(requests).hasSize(1);
    }

    @Test
    void GetAllRequestsTest() {
        User firstUser = userRepository.save(createUser());

        User secondUser = createUser();
        secondUser.setEmail("second@test.ru");
        secondUser = userRepository.save(secondUser);

        ItemRequest request = new ItemRequest();
        request.setDescription("Need drill");
        request.setRequestor(secondUser);
        request.setCreated(LocalDateTime.now());

        itemRequestRepository.save(request);

        Collection<ItemRequestDto> requests =
                itemRequestService.getAllRequests(firstUser.getId());

        assertThat(requests).hasSize(1);
    }

    @Test
    void GetRequestByIdTest() {
        User user = userRepository.save(createUser());

        ItemRequest request = new ItemRequest();
        request.setDescription("Need drill");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(request);

        ItemRequestDto result =
                itemRequestService.getById(user.getId(), savedRequest.getId());

        assertThat(result.getId()).isEqualTo(savedRequest.getId());
        assertThat(result.getDescription()).isEqualTo("Need drill");
    }

    @Test
    void GetRequestByIdWithItemsTest() {
        User user = userRepository.save(createUser());

        ItemRequest request = new ItemRequest();
        request.setDescription("Need drill");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(request);

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Power drill");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(savedRequest);

        itemRepository.save(item);

        ItemRequestDto result =
                itemRequestService.getById(user.getId(), savedRequest.getId());

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().getFirst().getName()).isEqualTo("Drill");
    }

    private User createUser() {
        User user = new User();
        user.setName("Ivan");
        user.setEmail("ivan@test.ru");
        return user;
    }

    private ItemRequestCreateDto createRequestDto() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need drill");
        return dto;
    }
}