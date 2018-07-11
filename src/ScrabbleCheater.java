import java.io.*;
import java.util.*;

public class ScrabbleCheater {
    public static void main (String[] args) {
        //some adjustable parameters
        int length = 7;
        boolean perm = true;

        //new instance of our object, that reads in our file and creates a hashtable
        ScrabbleCheater sc = new ScrabbleCheater(20103, "words_alpha.txt",length);

        //prints the hashtable to a .txt file
        PrintWriter out;
        try {
            out = new PrintWriter(new File("hashtable.txt"));
            out.print(sc.toString());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //prints stats about the hashtable to the console
        System.out.println(sc.getStats());

        //takes input and outputs permutations found in the dictionary
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
            else if (s.equals("/perm")) perm = !perm; //change mode from permutations to potential permutations
            else if (s.equals("/rnd")) {
                String str = randomString(length);
                System.out.println(str);
                System.out.println(sc.cheat(str, perm));
            }
            else if (s.length()>length) System.out.println("INFO: Do not input more than "+ length + " characters!"); //quit
            else System.out.println(sc.cheat(s, perm)); //print out permutations
        }

    }

    /**
     * generates a random string of the specified size. All letters have the same probability
     * @param length length of the random string
     * @return randomly generated String
     */
    private static String randomString(int length) {
        Random r = new Random();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i<length; i++) {
            char ch = (char) (r.nextInt(26)+97);
            s.append(ch);
        }

        return s.toString();
    }

    private int size; //size of the hashtable
    private int wordsize; //size of each word
    private LinkedList<String>[] hashtable;

    /**
     * Creates a new scrabble cheater for a defined maximum wordlength.
     * It uses a HashTable to access information quickly
     * @param size size of the hashtable
     * @param file path of the dictionary file
     * @param wordsize size of each word
     */
    private ScrabbleCheater(int size, String file, int wordsize){

        hashtable = new LinkedList[size];

        //initialize empty linked lists
        for (int i = 0; i < hashtable.length; i++){
            hashtable[i] = new LinkedList<>();
        }
        this.size = size;
        this.wordsize = wordsize;

        //start processing the dictonary file
        processFile(file);

    }

    /**
     * method that takes a string and returns either all permutations
     * or all potential permutations (words with the same position in the hashtable)
     */
    private String cheat(String s, boolean perm) {

        HashSet<String> hs = getSubstrings(s);
        String[] stra = new String[wordsize];
        for (int i = 0; i<stra.length; i++){
            stra[i] = "";
        }
        for (String mstr:hs) {
            int pos = getHashPosition(mstr.toLowerCase());
            for (String str : hashtable[pos]){
                if (!perm)
                    stra[mstr.length()-1]+=str+"  ";
                else if (perm && charArrayEquals(normalize(str.toCharArray()),normalize(mstr.toCharArray())))
                    stra[mstr.length()-1]+=str+"  ";
            }
        }

        StringBuilder outputstring = new StringBuilder();
        for (String string : stra){
            if (string.length()>0)
            outputstring.append(string).append("\n");
        }
        return outputstring.toString();
    }

    /**
     * generates all substrings of a given string
     * @param s String to get the substrings from
     * @return HashSet containing all unique substrings of a String with a minimum length of 2
     */
    private HashSet<String> getSubstrings(String s){
        HashSet<String> hs = new HashSet<>();
        int n = s.length();
        for (int i = 0; i<n; i++){
            for (int j = i+1; j<=n; j++){
                String str = normalizeString(s.substring(i,j));
                if (str.length()>1)hs.add(str);
            }
        }
        return hs;
    }

    /**
     * creates a String out of a HashSet - only for debugging
     * @param hs HashSet
     * @return String representation of it
     */
    private String printHashSet(HashSet<String> hs){
        StringBuilder str = new StringBuilder();
        for (String s:hs) {
            str.append(s).append("  ");
        }
        return str.toString();
    }

    /**
     * checks if the normalized char arrays out of two strings are permutations.
     * @param normalize first already normalized char array
     * @param normalize1 second already normalized char array
     * @return true its a permutation
     */
    private boolean charArrayEquals(char[] normalize, char[] normalize1) {
        //arrays have to be the same size
        if (normalize.length != normalize1.length) return false;
        //go through word back to front and abort if difference is found.
        for (int i = normalize.length-1; i>0; i--){
            if (normalize[i]!=normalize1[i]) return false;
        }
        //no difference found, its a permutation
        return true;
    }


    /**
     * method that processes the dictionary file and calls add for all elements in the text file.
     * @param file path of the dictionary file
     */
    private void processFile(String file){
        FileReader fr;
        try {
            fr = new FileReader(new File(file));
            BufferedReader br = new BufferedReader(fr);
            //read in line by line
            while(br.ready()){
                add(br.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Takes a String and adds it to the HashTable if it meets the requirements
     * @param s the String we want to add to our hashtable
     */
    private void add(String s) {
        //abort if the word has not the required size and do not add
        if (s.length()>wordsize || s.length()<=1)return;
        //calculate hash
        int position = getHashPosition(s);
        //add to hashtable
        hashtable[position].add(s);
    }

    /**
     * calculates hash value and the position in the hash table
     * @param s String we want to get the HashValue from
     * @return the position in the hashtable
     */
    private int getHashPosition(String s) {
        long hash = 1;

        //normalize to lowercase and alphabetic ordering
        char[] c = normalize(s.trim().toCharArray());

        //calculate hash value by multiplying each character value with 26^position
        //this hash function creates a unique hash for each possible word
        for (int i = 0; i<c.length; i++){
            hash+=(c[i]-96)*Math.pow(26,i);
        }
        //modulo to determine array position
        return (int)(hash % size);
    }

    /**
     * normalize a char[] to alphabetic ordering of lower case letters
     * @param chars char[] to be ordered alphabetically
     * @return new char[] for calculation of the hash value
     */
    private char[] normalize(char[] chars) {
        //call the sort Method of Arrays
        Arrays.sort(chars);
        return chars;
    }

    /**
     * normalizes a String to alphabetic order
     * @param s Sring that is to be normalized
     * @return normalized String
     */
    private String normalizeString(String s){
        char[] c = s.toCharArray();
        Arrays.sort(c);
        StringBuilder sb = new StringBuilder();
        for (char ch : c){
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * make a nice String out of our HashTable
     * @return String of the HashTable
     */
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

    /**
     * gets several statistics from our current HashTable
     * -number of collisions
     * -number of words
     * -largest chain
     * @return A String containing all three values
     */
    private String getStats(){
        int collisions = 0;
        int largest = 0;
        int words = 0;
        //go through each LinkedList in the HashTable and get parameter values
        for (LinkedList<String>li:hashtable){
            int size = li.size();
            collisions += size>1 ? size-1 : 0;
            words+=size;
            if (size>largest) {
                largest = size;
            }
        }
        return " longest chain: "+largest+"\n number of collisions: "+collisions+"\n number of words: "+words;
    }
}
