package model;

import repository.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Biblioteca {
    private String[] idsOfStudents= {"s000001","s000002","s000003","s000004"};
    //private ArrayList<String> idsOfStudents = new ArrayList<>(
    //        Arrays.asList("s000001", "s000002", "s000003", "s000004")
    //);

    private String[] idsOfTeachers= {"p000001","p000002","p000003","p000004"};

    private String[] idsOfLibrarians= {"l000001"};

    public LivroDaoLista acervo;
    public TituloDaoLista listaDeTitulos;
    public EmprestimoDaoLista listaDeEmprestimos;
    public ReservaDaoLista ListaDeReservas;

    public ArrayList<Usuario> usuariosList= ArrayList<Usuario>;
    public boolean thisIDIsValid(String id){
        if(id.charAt(0)=='t'){
            for(String idOfATeacher: idsOfTeachers){
                if(idOfATeacher.equals(id))
                    return true;
            }

        }else if(id.charAt(0)=='s') {
            for (String idOfATeacher : idsOfTeachers) {
                if (idOfATeacher.equals(id))
                    return true;
            }
        }else if(id.charAt(0)=='l'){
            for(String idOfATeacher: idsOfTeachers){
                if(idOfATeacher.equals(id))
                    return true;
            }
        }
        return false;
    }
}
