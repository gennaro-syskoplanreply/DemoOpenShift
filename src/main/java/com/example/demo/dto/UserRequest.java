package com.example.demo.dto;

import com.example.demo.model.Role;

public class UserRequest {

    private String name;
    private String surname;
    private Role role;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
