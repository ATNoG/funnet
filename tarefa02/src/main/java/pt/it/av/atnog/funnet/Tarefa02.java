package pt.it.av.atnog.funnet;

import java.util.List;
import java.util.Scanner;

public class Tarefa02 {

    public static void main(String[] args) {
        IMClient im = new IMClient("user");
        Scanner reader = new Scanner(System.in);
        while (true) {
            String txt = reader.nextLine();
            im.send(txt);
            List<Msg> msg = im.recv();
            printMessages(msg);
        }
    }

    private static void printMessages(List<Msg> msgs) {

    }
}
