package org.example.thymeleaf.thymeleafrs.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
  public UsernameAlreadyExistsException(String username) {
    super("Username " + username + " sudah terdaftar.");
  }
}
