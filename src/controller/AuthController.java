package controller;

import model.Biblioteca;
import model.Usuario;

import java.util.Scanner;

public class AuthController {
    Scanner sc = new Scanner(System.in);
    //public Usuario login(String email, String senha) {}

    public void Login(){
        System.out.println("Vamos fazer login");
        System.out.println("Digite seu e-mail");
        String email= sc.nextLine();
        String senha= sc.nextLine();

    }
    public void cadastrarUsuario(Usuario u) {

        System.out.println("Vamos te cadastrar");

    }
}