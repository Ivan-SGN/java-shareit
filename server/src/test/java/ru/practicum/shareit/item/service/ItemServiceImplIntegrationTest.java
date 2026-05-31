package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Test
    void createItemTest() {
        User owner = userRepository.save(createUser());

        ItemDto createdItem =
                itemService.create(owner.getId(), createItemDto());

        Item savedItem = itemRepository.findById(createdItem.getId())
                .orElseThrow();

        assertThat(savedItem.getName()).isEqualTo("Drill");
        assertThat(savedItem.getDescription()).isEqualTo("Power drill");
        assertThat(savedItem.getAvailable()).isTrue();
        assertThat(savedItem.getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void createItemWithRequestTest() {
        User owner = userRepository.save(createUser());

        ItemRequest request =
                itemRequestRepository.save(createRequest(owner));

        ItemCreateDto itemDto = createItemDto();
        itemDto.setRequestId(request.getId());

        ItemDto createdItem =
                itemService.create(owner.getId(), itemDto);

        Item savedItem = itemRepository.findById(createdItem.getId())
                .orElseThrow();

        assertThat(savedItem.getRequest()).isNotNull();
        assertThat(savedItem.getRequest().getId())
                .isEqualTo(request.getId());
    }

    @Test
    void updateItemTest() {
        User owner = userRepository.save(createUser());

        ItemDto createdItem =
                itemService.create(owner.getId(), createItemDto());

        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Updated");
        updateDto.setDescription("Updated description");
        updateDto.setAvailable(false);

        itemService.update(owner.getId(), createdItem.getId(), updateDto);

        Item updatedItem = itemRepository.findById(createdItem.getId())
                .orElseThrow();

        assertThat(updatedItem.getName()).isEqualTo("Updated");
        assertThat(updatedItem.getDescription())
                .isEqualTo("Updated description");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void getItemByIdTest() {
        User owner = userRepository.save(createUser());

        ItemDto createdItem =
                itemService.create(owner.getId(), createItemDto());

        ItemDto foundItem =
                itemService.getById(owner.getId(), createdItem.getId());

        assertThat(foundItem.getId()).isEqualTo(createdItem.getId());
        assertThat(foundItem.getName()).isEqualTo("Drill");
        assertThat(foundItem.getDescription()).isEqualTo("Power drill");
        assertThat(foundItem.getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void getItemsByOwnerTest() {
        User owner = userRepository.save(createUser());

        itemService.create(owner.getId(), createItemDto());

        ItemCreateDto secondItem = createItemDto();
        secondItem.setName("Saw");

        itemService.create(owner.getId(), secondItem);

        Collection<ItemDto> items =
                itemService.getByOwner(owner.getId());

        assertThat(items)
                .hasSize(2)
                .extracting(ItemDto::getName)
                .containsExactly("Drill", "Saw");
    }

    @Test
    void searchItemsTest() {
        User owner = userRepository.save(createUser());

        itemService.create(owner.getId(), createItemDto());

        Collection<ItemDto> items =
                itemService.search("drill");

        assertThat(items).hasSize(1);
        assertThat(items.iterator().next().getName())
                .isEqualTo("Drill");
    }

    @Test
    void updateItemOnlyNameTest() {
        User owner = userRepository.save(createUser());

        ItemDto createdItem =
                itemService.create(owner.getId(), createItemDto());

        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("New name");

        itemService.update(owner.getId(), createdItem.getId(), updateDto);

        Item updatedItem = itemRepository.findById(createdItem.getId())
                .orElseThrow();

        assertThat(updatedItem.getName()).isEqualTo("New name");
        assertThat(updatedItem.getDescription()).isEqualTo("Power drill");
        assertThat(updatedItem.getAvailable()).isTrue();
    }

    @Test
    void searchEmptyTextTest() {
        User owner = userRepository.save(createUser());

        itemService.create(owner.getId(), createItemDto());

        Collection<ItemDto> items =
                itemService.search("");

        assertThat(items).isEmpty();
    }

    @Test
    void searchUnavailableItemTest() {
        User owner = userRepository.save(createUser());

        ItemCreateDto itemDto = createItemDto();
        itemDto.setAvailable(false);

        itemService.create(owner.getId(), itemDto);

        Collection<ItemDto> items =
                itemService.search("drill");

        assertThat(items).isEmpty();
    }

    @Test
    void searchByDescriptionTest() {
        User owner = userRepository.save(createUser());

        itemService.create(owner.getId(), createItemDto());

        Collection<ItemDto> items =
                itemService.search("power");

        assertThat(items).hasSize(1);
    }

    @Test
    void getItemsOnlyForOwnerTest() {
        User firstOwner = userRepository.save(createUser());

        User secondOwner = new User();
        secondOwner.setName("Petr");
        secondOwner.setEmail("petr@test.ru");
        secondOwner = userRepository.save(secondOwner);

        itemService.create(firstOwner.getId(), createItemDto());

        ItemCreateDto secondItem = createItemDto();
        secondItem.setName("Saw");

        itemService.create(secondOwner.getId(), secondItem);

        Collection<ItemDto> items =
                itemService.getByOwner(firstOwner.getId());

        assertThat(items).hasSize(1);
        assertThat(items.iterator().next().getName())
                .isEqualTo("Drill");
    }

    private User createUser() {
        User user = new User();
        user.setName("Ivan");
        user.setEmail("ivan@test.ru");
        return user;
    }

    private ItemCreateDto createItemDto() {
        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("Drill");
        dto.setDescription("Power drill");
        dto.setAvailable(true);
        return dto;
    }

    private ItemRequest createRequest(User requestor) {
        ItemRequest request = new ItemRequest();
        request.setDescription("Need drill");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return request;
    }
}