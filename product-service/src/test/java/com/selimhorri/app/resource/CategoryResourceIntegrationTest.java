package com.selimhorri.app.resource;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.exception.wrapper.CategoryNotFoundException;
import com.selimhorri.app.service.CategoryService;

@WebMvcTest(CategoryResource.class)
public class CategoryResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private CategoryDto categoryDto1;
    private CategoryDto categoryDto2;
    private List<CategoryDto> categoryDtos;

    @BeforeEach
    void setUp() {
        // Create parent category
        categoryDto1 = new CategoryDto();
        categoryDto1.setCategoryId(1);
        categoryDto1.setCategoryTitle("Electronics");
        categoryDto1.setImageUrl("http://example.com/electronics.jpg");
        categoryDto1.setSubCategoriesDtos(new HashSet<>());
        categoryDto1.setProductDtos(new HashSet<>());

        // Create child category
        categoryDto2 = new CategoryDto();
        categoryDto2.setCategoryId(2);
        categoryDto2.setCategoryTitle("Clothing");
        categoryDto2.setImageUrl("http://example.com/clothing.jpg");
        categoryDto2.setParentCategoryDto(categoryDto1);
        categoryDto2.setSubCategoriesDtos(new HashSet<>());
        categoryDto2.setProductDtos(new HashSet<>());

        categoryDtos = Arrays.asList(categoryDto1, categoryDto2);
    }

    @Test
    void testFindAll() throws Exception {
        when(categoryService.findAll()).thenReturn(categoryDtos);

        mockMvc.perform(get("/api/categories"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data").isArray())
               .andExpect(jsonPath("$.data.length()").value(2))
               .andExpect(jsonPath("$.data[0].categoryTitle").value("Electronics"))
               .andExpect(jsonPath("$.data[1].categoryTitle").value("Clothing"));

        verify(categoryService, times(1)).findAll();
    }

    @Test
    void testFindById() throws Exception {
        when(categoryService.findById(1)).thenReturn(categoryDto1);

        mockMvc.perform(get("/api/categories/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.categoryId").value(1))
               .andExpect(jsonPath("$.categoryTitle").value("Electronics"))
               .andExpect(jsonPath("$.imageUrl").value("http://example.com/electronics.jpg"));

        verify(categoryService, times(1)).findById(1);
    }

    @Test
    void testFindById_NotFound() throws Exception {
        when(categoryService.findById(99)).thenThrow(new CategoryNotFoundException("Category not found"));

        mockMvc.perform(get("/api/categories/99"))
               .andExpect(status().isNotFound());

        verify(categoryService, times(1)).findById(99);
    }

    @Test
    void testSave() throws Exception {
        when(categoryService.save(any(CategoryDto.class))).thenReturn(categoryDto1);

        mockMvc.perform(post("/api/categories")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(categoryDto1)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.categoryTitle").value("Electronics"))
               .andExpect(jsonPath("$.imageUrl").value("http://example.com/electronics.jpg"));

        verify(categoryService, times(1)).save(any(CategoryDto.class));
    }

    @Test
    void testUpdate() throws Exception {
        when(categoryService.update(any(CategoryDto.class))).thenReturn(categoryDto1);

        mockMvc.perform(put("/api/categories")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(categoryDto1)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.categoryTitle").value("Electronics"));

        verify(categoryService, times(1)).update(any(CategoryDto.class));
    }

    @Test
    void testUpdateWithId() throws Exception {
        when(categoryService.update(eq(1), any(CategoryDto.class))).thenReturn(categoryDto1);

        mockMvc.perform(put("/api/categories/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(categoryDto1)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.categoryTitle").value("Electronics"));

        verify(categoryService, times(1)).update(eq(1), any(CategoryDto.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(categoryService).deleteById(1);

        mockMvc.perform(delete("/api/categories/1"))
               .andExpect(status().isOk())
               .andExpect(content().string("true"));

        verify(categoryService, times(1)).deleteById(1);
    }
}
