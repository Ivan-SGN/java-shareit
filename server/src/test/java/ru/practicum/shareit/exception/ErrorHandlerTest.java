package ru.practicum.shareit.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ErrorHandlerTest.TestController.class)
class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void notFoundExceptionTest() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Not found"));
    }

    @Test
    void validationExceptionTest() throws Exception {
        mockMvc.perform(get("/test/validation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Validation error"));
    }

    @Test
    void conflictExceptionTest() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Conflict"));
    }

    @Test
    void forbiddenExceptionTest() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error")
                        .value("Forbidden"));
    }

    @Test
    void constraintViolationExceptionTest() throws Exception {
        mockMvc.perform(get("/test/constraint")
                        .param("id", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isArray());
    }

    @Test
    void methodArgumentNotValidExceptionTest() throws Exception {
        mockMvc.perform(post("/test/dto")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isArray());
    }

    @Test
    void unexpectedExceptionTest() throws Exception {
        mockMvc.perform(get("/test/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("Internal server error"));
    }

    @RestController
    @Validated
    static class TestController {

        @GetMapping("/test/not-found")
        public void notFound() {
            throw new NotFoundException("Not found");
        }

        @GetMapping("/test/validation")
        public void validation() {
            throw new ValidationException("Validation error");
        }

        @GetMapping("/test/conflict")
        public void conflict() {
            throw new ConflictException("Conflict");
        }

        @GetMapping("/test/forbidden")
        public void forbidden() {
            throw new ForbiddenException("Forbidden");
        }

        @GetMapping("/test/error")
        public void error() {
            throw new RuntimeException();
        }

        @GetMapping("/test/constraint")
        public void constraint(
                @RequestParam
                @Positive
                Long id
        ) {
        }

        @PostMapping("/test/dto")
        public void dto(
                @Valid
                @RequestBody
                TestDto dto
        ) {
        }
    }

    @Data
    static class TestDto {

        @NotBlank
        private String name;
    }
}