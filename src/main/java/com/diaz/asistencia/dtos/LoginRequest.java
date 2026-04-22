package com.diaz.asistencia.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String dni;
    private String password;
}