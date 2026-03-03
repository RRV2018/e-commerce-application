package com.omsoft.retail.inventory.controller;

import com.omsoft.retail.inventory.dto.InventoryRequest;
import com.omsoft.retail.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    private InventoryController inventoryController;

    @BeforeEach
    void setUp() {
        inventoryController = new InventoryController(inventoryService);
    }

    @Test
    void reserve_callsService() {
        InventoryRequest request = new InventoryRequest(1L, 2);

        inventoryController.reserve(request);

        verify(inventoryService).reserve(1L, 2);
    }

    @Test
    void confirm_callsService() {
        InventoryRequest request = new InventoryRequest(1L, 3);

        inventoryController.confirm(request);

        verify(inventoryService).confirm(1L, 3);
    }

    @Test
    void release_callsService() {
        InventoryRequest request = new InventoryRequest(1L, 1);

        inventoryController.release(request);

        verify(inventoryService).release(1L, 1);
    }
}
