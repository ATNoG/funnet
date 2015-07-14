package pt.it.av.atnog.funnet;

import pt.it.av.atnog.funnetlib.IMClient;
import pt.it.av.atnog.funnetlib.Msg;

import java.util.List;
import java.util.Scanner;

public class Tarefa03 {

    public static void main(String[] args) {
        IMClient im = new IMClient("user");
        while (true) {
            List<Msg> msg = im.recv();
            readAndReply(msg, im);
        }
    }

    private static void readAndReply(List<Msg> msgs, IMClient im) {

    }
}
