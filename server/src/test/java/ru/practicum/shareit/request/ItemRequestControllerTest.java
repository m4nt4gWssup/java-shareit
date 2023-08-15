package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    ItemRequestService service;
    @InjectMocks
    ItemRequestController controller;
    private MockMvc mvc;
    private static final String OWNER = "X-Sharer-User-Id";;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testPostRequest() throws Exception {
        long userId = 1;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test request description");

        ItemRequestDto expectedResponse = new ItemRequestDto();
        expectedResponse.setDescription(requestDto.getDescription());

        Mockito.when(service.create(requestDto, userId)).thenReturn(expectedResponse);

        mvc.perform(post("/requests")
                        .header(OWNER, String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testGetRequest() throws Exception {
        long userId = 1;

        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Request 1");

        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Request 2");

        List<ItemRequestDto> expectedResponse = Arrays.asList(requestDto1, requestDto2);

        Mockito.when(service.getRequest(userId)).thenReturn(expectedResponse);

        mvc.perform(get("/requests")
                        .header(OWNER, String.valueOf(userId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testGetAllRequest() throws Exception {
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Request 1");

        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Request 2");

        List<ItemRequestDto> expectedResponse = Arrays.asList(requestDto1, requestDto2);

        Mockito.when(service.getAllRequest(1L, 0, 10)).thenReturn(expectedResponse);

        mvc.perform(get("/requests/all")
                        .header(OWNER, String.valueOf(1))
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testGetRequestById() throws Exception {
        ItemRequestDto expectedResponse = new ItemRequestDto();
        expectedResponse.setDescription("Test request");

        Mockito.when(service.getItemRequestById(100L, 1L)).thenReturn(expectedResponse);

        mvc.perform(get("/requests/{requestId}", 100)
                        .header(OWNER, String.valueOf(1)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedResponse)));
    }
}