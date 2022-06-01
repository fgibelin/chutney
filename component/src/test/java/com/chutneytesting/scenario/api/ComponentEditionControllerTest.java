package com.chutneytesting.scenario.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chutneytesting.ComponentRestExceptionHandler;
import com.chutneytesting.execution.domain.ExecutableComposedScenario;
import com.chutneytesting.execution.domain.ExecutableComposedTestCase;
import com.chutneytesting.execution.domain.TestCasePreProcessors;
import com.chutneytesting.scenario.api.dto.ComposableTestCaseDto;
import com.chutneytesting.scenario.api.dto.ImmutableComposableScenarioDto;
import com.chutneytesting.scenario.api.dto.ImmutableComposableTestCaseDto;
import com.chutneytesting.scenario.domain.ComposableScenario;
import com.chutneytesting.scenario.domain.ComposableTestCase;
import com.chutneytesting.scenario.domain.ComposableTestCaseRepository;
import com.chutneytesting.scenario.domain.TestCaseMetadataImpl;
import com.chutneytesting.scenario.domain.TestCaseRepository;
import com.chutneytesting.security.domain.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ComponentEditionControllerTest {

    private final String DEFAULT_COMPOSABLE_TESTCASE_ID = "30-1";

    private final ObjectMapper om = new ObjectMapper().findAndRegisterModules();
    private final ComposableTestCaseDto composableTestCaseDto =
        ImmutableComposableTestCaseDto.builder()
            .id(DEFAULT_COMPOSABLE_TESTCASE_ID)
            .title("Default title")
            .scenario(
                ImmutableComposableScenarioDto.builder().build()
            )
            .build();

    private final ComposableTestCaseRepository composableTestCaseRepository = mock(ComposableTestCaseRepository.class);
    private final TestCaseRepository testCaseRepository = mock(TestCaseRepository.class);
    private final UserService userService = mock(UserService.class);
    private final TestCasePreProcessors testCasePreProcessors  = mock(TestCasePreProcessors.class);

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        ComponentEditionController sut = new ComponentEditionController(composableTestCaseRepository, testCaseRepository, userService, testCasePreProcessors);

        mockMvc = MockMvcBuilders.standaloneSetup(sut)
            .setControllerAdvice(new ComponentRestExceptionHandler())
            .build();

        when(composableTestCaseRepository.findById(any()))
            .thenReturn(new ComposableTestCase(DEFAULT_COMPOSABLE_TESTCASE_ID, TestCaseMetadataImpl.builder().build(), ComposableScenario.builder().build()));

        when(composableTestCaseRepository.save(any()))
            .thenReturn(DEFAULT_COMPOSABLE_TESTCASE_ID);

        when(userService.currentUserId()).thenReturn("currentUser");
    }

    @Test
    public void should_save_testCase() throws Exception {
        // When
        mockMvc.perform(post(ComponentEditionController.BASE_URL)
            .contentType(APPLICATION_JSON_VALUE)
            .content(om.writeValueAsString(composableTestCaseDto)))
            .andExpect(status().isOk());

        // Then
        verify(composableTestCaseRepository).save(any());
    }

    @Test
    public void should_find_testCase() throws Exception {
        // When
        mockMvc.perform(get(ComponentEditionController.BASE_URL + "/" + DEFAULT_COMPOSABLE_TESTCASE_ID));

        // Then
        verify(composableTestCaseRepository).findById(DEFAULT_COMPOSABLE_TESTCASE_ID);
    }

    @Test
    public void should_find_testCase_with_parameters_replaced() throws Exception {
        // Given
        when(testCasePreProcessors.apply(any()))
            .thenReturn(new ExecutableComposedTestCase(
            TestCaseMetadataImpl.builder()
                .build(),
            ExecutableComposedScenario.builder().build()
        ));

        // When
        mockMvc.perform(get(ComponentEditionController.BASE_URL + "/" + DEFAULT_COMPOSABLE_TESTCASE_ID + "/executable" ));

        // Then
        verify(testCaseRepository).findById(DEFAULT_COMPOSABLE_TESTCASE_ID);
        verify(testCasePreProcessors).apply(any());
    }

    @Test
    public void should_delete_testCase() throws Exception {
        // When
        mockMvc.perform(delete(ComponentEditionController.BASE_URL + "/" + DEFAULT_COMPOSABLE_TESTCASE_ID));

        // Then
        verify(composableTestCaseRepository).removeById(DEFAULT_COMPOSABLE_TESTCASE_ID);
        verify(testCaseRepository).removeById(DEFAULT_COMPOSABLE_TESTCASE_ID);
    }
}