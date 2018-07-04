import java.io.*;
import java.util.*;

public class ScrabbleCheater {
    public static void main (String[] args) {
        int length = 7;
        boolean perm = true;
        ScrabbleCheater sc = new ScrabbleCheater(12121, "words_alpha.txt",length);

        PrintWriter out = null;
        try {
            out = new PrintWriter(new File("hashtable.txt"));
            out.print(sc.toString());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(sc.getStats());

        InputStreamReader sr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(sr);
        boolean quit = false;

        if (!perm) System.out.println("Enter String to get potential permutations...");
        else System.out.println("Enter String to get all permutations");

        while(!quit){
            String s = "";
            try {
                s = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (s.equals("/quit")) quit = true;
            else if (s.length()!=7) System.out.println("INFO: Word needs to have "+length+" characters.");
            else System.out.println(sc.cheat(s, perm));
        }

    }

    private int size;
    private int wordsize;
    private LinkedList<String>[] hashtable;

    private String cheat(String s, boolean perm) {
        StringBuilder returnString = new StringBuilder();
        int pos = getHashPosition(s.toLowerCase());
        for (String str : hashtable[pos]){
            if (!perm)
                returnString.append(str).append("  ");
            else if (perm && eqalArray(normalize(str.toCharArray()),normalize(s.toCharArray())))
                    returnString.append(str).append("  ");
        }
        return returnString.toString();
    }

    private boolean eqalArray(char[] normalize, char[] normalize1) {
        if (normalize.length != normalize1.length) return false;
        for (int i = 0; i<normalize.length; i++){
            if (normalize[i]!=normalize1[i]) return false;
        }
        return true;
    }


    private ScrabbleCheater(int size, String file, int wordsize){

        hashtable = new LinkedList[size];

        for (int i = 0; i < hashtable.length; i++){
            hashtable[i] = new LinkedList<>();
        }
        this.size = size;
        this.wordsize = wordsize;

        processFile(file);

    }

    private void processFile(String file){
        FileReader fr = null;
        try {
            fr = new FileReader(new File(file));
            BufferedReader br = new BufferedReader(fr);
            while(br.ready()){
                add(br.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void add(String s) {
        if (s.length()!=wordsize)return;
        //calculate hash
        int position = getHashPosition(s);
        //add to hashtable
        hashtable[position].add(s);
    }

    private int getHashPosition(String s) {
        long hash = 1;

        char[] c = normalize(s.toCharArray());

        for (int i = 0; i<c.length; i++){
            hash+=(c[i]-96)*Math.pow(26,i);
        }
        //modulo to determine array position
        return (int)(hash % size);
    }

    private char[] normalize(char[] chars) {
        Arrays.sort(chars);
        return chars;
    }

    public String toString(){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i<hashtable.length; i++){
            str.append(i).append(" |");
            for (String s : hashtable[i]){
                str.append(s).append("  ");
            }
            str.append("\n");
        }
        return str.toString();
    }

    public String getStats(){
        int collisions = 0;
        int largest = 0;
        int words = 0;
        for (LinkedList<String>li:hashtable){
            int size = li.size();
            collisions += size>1 ? size-1 : 0;
            words+=size;
            if (size>largest) {
                largest = size;
            }
        }
        return " largest chain: "+largest+"\n number of collisions: "+collisions+"\n number of words: "+words;
    }
}
