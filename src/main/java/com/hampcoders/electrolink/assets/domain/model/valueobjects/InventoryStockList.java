package com.hampcoders.electrolink.assets.domain.model.valueobjects;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Component;
import com.hampcoders.electrolink.assets.domain.model.aggregates.TechnicianInventory;
import com.hampcoders.electrolink.assets.domain.model.entities.ComponentStock;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a list of inventory stock items, typically embedded within an aggregate.
 */
@ToString
@Embeddable
@Getter
public class InventoryStockList {

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "technicianInventory", cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<ComponentStock> items;

  /**
   * Constructs an empty InventoryStockList.
   */
  public InventoryStockList() {
    this.items = new ArrayList<>();
  }

  /**
   * Adds a new component stock item to the list.
   *
   * @param inventory The technician inventory associated with the stock.
   * @param component The component being stocked.
   * @param quantity The current quantity of the component.
   * @param threshold The minimum required quantity.
   */
  public void addItem(final TechnicianInventory inventory, final Component component,
                      final int quantity,
                      final int threshold) {
    this.items.add(new ComponentStock(inventory, component, quantity, threshold, new Date()));
  }

}