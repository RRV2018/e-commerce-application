package com.omsoft.retail.inventory.service;

import com.omsoft.retail.inventory.entity.Inventory;
import com.omsoft.retail.inventory.repo.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository repo;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(repo);
    }

    @Test
    void reserve_whenEnoughStock_updatesInventory() {
        Inventory inv = new Inventory();
        inv.setProductId(1L);
        inv.setAvailable(10);
        inv.setReserved(0);
        when(repo.findById(1L)).thenReturn(Optional.of(inv));

        inventoryService.reserve(1L, 3);

        assertEquals(7, inv.getAvailable());
        assertEquals(3, inv.getReserved());
        verify(repo).save(inv);
    }

    @Test
    void reserve_whenProductNotFound_throws() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> inventoryService.reserve(999L, 1));
    }

    @Test
    void reserve_whenInsufficientStock_throws() {
        Inventory inv = new Inventory();
        inv.setProductId(1L);
        inv.setAvailable(2);
        inv.setReserved(0);
        when(repo.findById(1L)).thenReturn(Optional.of(inv));

        assertThrows(IllegalStateException.class, () -> inventoryService.reserve(1L, 5));
    }

    @Test
    void release_increasesAvailableAndDecreasesReserved() {
        Inventory inv = new Inventory();
        inv.setProductId(1L);
        inv.setAvailable(5);
        inv.setReserved(5);
        when(repo.findById(1L)).thenReturn(Optional.of(inv));

        inventoryService.release(1L, 2);

        assertEquals(7, inv.getAvailable());
        assertEquals(3, inv.getReserved());
        verify(repo).save(inv);
    }

    @Test
    void confirm_decreasesReserved() {
        Inventory inv = new Inventory();
        inv.setProductId(1L);
        inv.setAvailable(0);
        inv.setReserved(5);
        when(repo.findById(1L)).thenReturn(Optional.of(inv));

        inventoryService.confirm(1L, 2);

        assertEquals(3, inv.getReserved());
        verify(repo).save(inv);
    }
}
