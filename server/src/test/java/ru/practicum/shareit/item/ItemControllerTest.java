package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String OWNER = "X-Sharer-User-Id";

    @BeforeEach
    public void setup() {
        ItemDto mockItem = new ItemDto();
        mockItem.setId(1L);
        mockItem.setName("Test item");
        when(itemService.getItemById(1L, 123L)).thenReturn(mockItem);
    }

    @Test
    public void testGetItemById() throws Exception {
        mockMvc.perform(get("/items/1")
                        .header(OWNER, "123"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetItemsByOwner() throws Exception {
        List<ItemDto> mockItems = Collections.singletonList(new ItemDto());
        given(itemService.getItemsByOwner(123L, 0, 10)).willReturn(mockItems);

        mockMvc.perform(get("/items")
                        .header(OWNER, "123")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetItemsBySearchQuery() throws Exception {
        List<ItemDto> mockItems = Collections.singletonList(new ItemDto());
        given(itemService.getItemsBySearchQuery("Test", 0, 10)).willReturn(mockItems);

        mockMvc.perform(get("/items/search")
                        .param("text", "Test")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateItem() throws Exception {
        ItemDto requestDto = new ItemDto(null, "TestItem", "Description", true, null, null, null, null, null);
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header(OWNER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        ;
    }

    @Test
    public void testUpdateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Updated Name");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header(OWNER, "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header(OWNER, "123"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCreateComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Nice Item!");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(OWNER, "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}