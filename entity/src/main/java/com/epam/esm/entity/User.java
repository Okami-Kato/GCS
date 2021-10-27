package com.epam.esm.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "full_name", nullable = false, length = 50)
    private String fullName;

    @Column(name = "username", nullable = false, unique = true, updatable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 50)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = false;

    @OneToMany(mappedBy = "user")
    private final Set<UserOrder> orders = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private final Set<Role> authorities = new HashSet<>();

    protected User() {
    }

    @Builder
    public User(Integer id, String fullName, String username, String password, Set<UserOrder> orders) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        if (orders != null) {
            orders.forEach(this::addOrder);
        }
    }

    @PreRemove
    private void toRemove() {
        HashSet<UserOrder> ordersCopy = new HashSet<>(orders);
        for (UserOrder order : ordersCopy) {
            removeOrder(order);
        }
    }

    public Set<UserOrder> getOrders() {
        return new HashSet<>(orders);
    }

    void addOrder(UserOrder order) {
        if (orders.contains(order))
            return;
        orders.add(order);
        order.setUser(this);
    }

    void removeOrder(UserOrder order) {
        if (!orders.contains(order))
            return;
        orders.remove(order);
        order.removeUser();
    }

    public Set<Role> getAuthorities() {
        return new HashSet<>(authorities);
    }

    void addAuthority(Role role) {
        authorities.add(role);
    }

    public void removeAuthority(Role role) {
        authorities.remove(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return username != null && username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                '}';
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }
}