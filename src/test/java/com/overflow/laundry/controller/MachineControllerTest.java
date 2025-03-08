package com.overflow.laundry.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.service.MachineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MachineController.class)
public class MachineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MachineService machineService;

    @Test
    void should_returnAccepted_whenMachineIsCreated() throws Exception {

        MachineDto mockMachine = getMachineDto(null, "Washer 1", "Condominium 1", "Washer");

        when(machineService.createMachine(any(MachineDto.class))).thenReturn(mockMachine);

        String machineJson = objectMapper.writeValueAsString(mockMachine);
        mockMvc.perform(post("/machine")
                        .contentType("application/json")
                        .content(machineJson))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsNull")
    void should_returnBadRequest_whenOneOfTheValuesIsNull(String identifier,
                                                      String condominium,
                                                      String typeOfMachine) throws Exception {
        MachineDto mockMachine = getMachineDto(null, identifier, condominium, typeOfMachine);

        String machineJson = objectMapper.writeValueAsString(mockMachine);
        mockMvc.perform(post("/machine")
                        .contentType("application/json")
                        .content(machineJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsEmpty")
    void should_returnBadRequest_whenOneOfTheValuesIsEmpty(String identifier,
                                                          String condominium,
                                                          String typeOfMachine) throws Exception {
        MachineDto mockMachine = getMachineDto(null, identifier, condominium, typeOfMachine);

        String machineJson = objectMapper.writeValueAsString(mockMachine);
        mockMvc.perform(post("/machine")
                        .contentType("application/json")
                        .content(machineJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_returnMachine_whenGetMachineById() throws Exception {
        MachineDto mockMachine = getMachineDto(1L,"1", "Condominium 1", "Washer");

        when(machineService.getMachineById(any())).thenReturn(mockMachine);

        String machineJson = objectMapper.writeValueAsString(mockMachine);
        mockMvc.perform(get("/machine/1")
                        .contentType("application/json")
                        .content(machineJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void should_returnMachineNotFoundException_whenGetMachineById() throws Exception {

        when(machineService.getMachineById(any())).thenThrow(MachineNotFoundException.class);
        mockMvc.perform(get("/machine/1")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_updateMachine_whenMachineIsUpdated() throws Exception {
        MachineDto mockMachine = getMachineDto(1L,"1", "Condominium 1", "Washer");

        when(machineService.updateMachine(any(MachineDto.class))).thenReturn(mockMachine);

        String machineJson = objectMapper.writeValueAsString(mockMachine);
        mockMvc.perform(put("/machine")
                        .contentType("application/json")
                        .content(machineJson))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    void should_deleteMachine_whenMachineIsDeleted() throws Exception {

        when(machineService.deleteMachine(any())).thenReturn(true);

        mockMvc.perform(delete("/machine/1")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private static MachineDto getMachineDto(Long id, String identifier, String condominium, String typeOfMachine) {
        return MachineDto.builder()
                .id(id)
                .identifier(identifier)
                .condominium(condominium)
                .typeOfMachine(typeOfMachine)
                .build();
    }

    private static Stream<Arguments> provideStringsForIsNull() {
        return Stream.of(
                Arguments.of(null, "central", "Washer"),
                Arguments.of("123", null, "Washer"),
                Arguments.of("123", "central", null)
        );
    }

    private static Stream<Arguments> provideStringsForIsEmpty() {
        return Stream.of(
                Arguments.of("", "central", "Washer"),
                Arguments.of("123", "", "Washer"),
                Arguments.of("123", "central", "")
        );
    }
}
