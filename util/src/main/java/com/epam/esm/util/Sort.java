package com.epam.esm.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Sort {
    private final List<Order> orders;

    private Sort(List<Order> orders) {
        this.orders = new LinkedList<>(orders);
    }

    public static Sort by(Order... orders) {
        return new Sort(Arrays.asList(orders));
    }

    public static Sort by(List<Order> orders) {
        return new Sort(orders);
    }

    public List<Order> getOrders() {
        return new LinkedList<>(orders);
    }

    /**
     * Object, encapsulating field to target in sorting and sort direction
     */
    public static class Order {
        private final String field;
        private final SortDirection direction;

        public Order(String field, SortDirection sortDirection) {
            this.field = field;
            this.direction = sortDirection;
        }

        public static Order asc(String property) {
            return new Order(property, SortDirection.ASC);
        }

        public static Order desc(String property) {
            return new Order(property, SortDirection.DESC);
        }

        public SortDirection getDirection() {
            return direction;
        }

        public String getField() {
            return field;
        }
    }
}
