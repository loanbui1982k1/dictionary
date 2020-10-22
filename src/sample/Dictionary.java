package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class Dictionary {
    //private static WordLibrary[] arrayLibrary;
    private WordLibrary[] arrayLibrary = new WordLibrary[26];
    private int len = 0;

    public WordLibrary getLibraryAt(int index) {
        return arrayLibrary[index];
    }

    public Dictionary() {

    }

    public void print() {
        for (int i = 0; i < 1; i++) {
            int size = arrayLibrary[i].getSize();
            for (int j = 0; j < size; j++) {
                System.out.println(this.getWord(i, j).getWordTarget());
            }
        }
    }

    public Word getWord(int i, int j) {
        return arrayLibrary[i].getWordAt(j);
    }

    public void insertFromFile() {
        /*try {
            File text = new File("C:\\Users\\Bui Loan\\IdeaProjects\\Dictionary\\src\\sample\\wordA.txt");
            Scanner scanner = new Scanner(text);
            WordLibrary test = new WordLibrary();
            while (scanner.hasNextLine()) {
                String result = "";

                String s = scanner.nextLine();
                while (!(s.equals(""))) {
                    result = result + s + "\n";
                    s = scanner.nextLine();
                }
                Word tempW = new Word();
                tempW.setWordExplain(result);
                String Target = "";
                int i = 1;
                while ((result.charAt(i) != ' ') || (result.charAt(i) == ' ' && result.charAt(i + 1) != '/')) {
                    Target += result.charAt(i);
                    i++;
                }
                tempW.setWordTarget(Target);
                if (test.getSize() == 0) {
                    test.addWord(tempW);
                } else {
                    if (test.getWordAt(0).getWordTarget().charAt(0) == (tempW.getWordTarget().charAt(0))) {
                        test.addWord(tempW);
                        if (!scanner.hasNextLine()) {
                            arrayLibrary[test.getWordAt(0).getWordTarget().charAt(0) - 97] = test;
                        }
                    } else {
                        arrayLibrary[test.getWordAt(0).getWordTarget().charAt(0) - 97] = test;
                        test = new WordLibrary();
                        test.addWord(tempW);
                        if (!scanner.hasNextLine()) {
                            arrayLibrary[test.getWordAt(0).getWordTarget().charAt(0) - 97] = test;
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

         */
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/dictionary2";// your db name
            String user = "root"; // your db username
            String password = ""; // your db password
            Connection conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                System.out.println("Connect success!");
            }
            var sql = "select * from tbl_edict";
            var statement = conn.prepareStatement(sql);
            var resultSet = statement.executeQuery();
            showResult(resultSet);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void showResult(ResultSet resultSet) throws SQLException {
        int check[] = new int[26];
        for (int i = 0; i < 26; i++) {
            check[i] = 0;
        }
        while (resultSet.next()) {
            int idx = resultSet.getInt("idx");
            String word = resultSet.getString("word");
            String detail = resultSet.getString("detail");

            if (word.charAt(0) >= 'a' && word.charAt(0) <= 'z') {
                Word tempW = new Word();
                tempW.setWordExplain(detail);
                tempW.setWordTarget(word);

                int pos = word.charAt(0) - 97;
                if (check[pos] == 0) {
                    WordLibrary test = new WordLibrary();
                    test.addWord(tempW);
                    arrayLibrary[pos] = test;
                    check[pos]++;
                } else {
                    arrayLibrary[pos].addWord(tempW);
                }

            }
        }
        for (int i = 0; i < 26; i++) {
            Collections.sort(arrayLibrary[i].getLibrary());
        }
    }

    public void addDatabase(Word select) {
        String sql = "INSERT INTO tbl_edict( word, detail) VALUES ( ?, ?)";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/dictionary2";// your db name
            String user = "root"; // your db username
            String password = ""; // your db password
            Connection conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);
            if (conn != null) {
                System.out.println("Connect success!");
            }
            //var sql = "select * from tbl_edict";
            PreparedStatement statement = conn.prepareStatement(sql);
            // var resultSet = statement.executeQuery();
            if (conn != null) {
                //statement.setInt(1, 20000);
                statement.setString(1, select.getWordTarget());
                statement.setString(2, select.getWordExplain());
                int rowindex = statement.executeUpdate();
                conn.commit();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDatabase(String select) {
        String sql = "DELETE FROM tbl_edict WHERE word = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/dictionary2";// your db name
            String user = "root"; // your db username
            String password = ""; // your db password
            Connection conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);
            if (conn != null) {
                System.out.println("Connect success!");
            }
            //var sql = "select * from tbl_edict";
            PreparedStatement statement = conn.prepareStatement(sql);
            // var resultSet = statement.executeQuery();
            if (conn != null) {
                statement.setString(1, select);
                int rowindex = statement.executeUpdate();
                conn.commit();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void editDatabase(Word select) {
        String sql = "UPDATE tbl_edict SET detail = ? WHERE word = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/dictionary2";// your db name
            String user = "root"; // your db username
            String password = ""; // your db password
            Connection conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);
            if (conn != null) {
                System.out.println("Connect success!");
            }
            //var sql = "select * from tbl_edict";
            PreparedStatement statement = conn.prepareStatement(sql);
            // var resultSet = statement.executeQuery();
            if (conn != null) {
                //statement.setInt(1, 20000);
                statement.setString(2, select.getWordTarget());
                statement.setString(1, select.getWordExplain());
                int rowindex = statement.executeUpdate();
                conn.commit();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public String dictionaryLookup(String searchingWordTarget, int begin, int end) {
        /*int index = 0;
        int pos = searchingWordTarget.charAt(0) - 97;
        int size = arrayLibrary[pos].getSize();
        for (int i = 0; i < size; i++) {
            if (searchingWordTarget.equals(this.getWord(pos, i).getWordTarget())) {
                index = i;
                break;
            }
        }
        if (this.getWord(pos, index).getWordTarget().equals(searchingWordTarget)) {
            return this.getWord(pos, index).getWordExplain();
        } else {
            return null;
        }

         */
        int pos = searchingWordTarget.charAt(0) - 97;
        int index = (begin + end) / 2;
        if (begin > end) {
            String s = this.suggest(searchingWordTarget);
            return "<em>Can't find this word. Maybe you want to search for: </em><br/>" + s;
        }

        if (searchingWordTarget.equals(this.getWord(pos, index).getWordTarget())) {
            return this.getWord(pos, index).getWordExplain();
        } else {
            if (searchingWordTarget.compareTo(this.getWord(pos, index).getWordTarget()) < 0) {
                return dictionaryLookup(searchingWordTarget, begin, index - 1);
            } else {
                return dictionaryLookup(searchingWordTarget, index + 1, end);
            }
        }
    }

    public ArrayList<Word> dictionarySearcher(String select) {
        ArrayList<Word> temp = new ArrayList<>();
        int size = arrayLibrary[select.charAt(0) - 97].getSize();
        for (int i = 0; i < size; i++) {
            String s = arrayLibrary[select.charAt(0) - 97].getWordAt(i).getWordTarget();
            if (select.length() <= s.length() && select.equals(s.substring(0, select.length()))) {
                temp.add(arrayLibrary[select.charAt(0) - 97].getWordAt(i));
            }
        }

        return temp;
    }

    public String suggest(String searchingWord) {
        String result = "";
        final int maxSizeOfSuggestionWord = searchingWord.length() * 3 / 2;
        ArrayList <String> suggestion = new ArrayList<>();
        int pos = searchingWord.charAt(0) - 97;
        int size = getLibraryAt(pos).getSize();

        /** check the whole word. */
        for (int j = 0; j < size; j++) {
            String word = this.getWord(pos, j).getWordTarget();
            if (word.contains(searchingWord) && word.length() <= maxSizeOfSuggestionWord)
                if (!suggestion.contains(word))
                    suggestion.add(word);
            if (suggestion.size() >= 20) break;
        }

        /** check the word when having deleted a character */
        for (int i = searchingWord.length() - 1; i >= 0; --i) {
            StringBuffer s = new StringBuffer(searchingWord);
            s.deleteCharAt(i);
            for (int j = 0; j < size; j++) {
                String word = this.getWord(pos, j).getWordTarget();
                if (word.contains(s) && word.length() <= maxSizeOfSuggestionWord) {
                    if (!suggestion.contains(word))
                        suggestion.add(word);
                    if (suggestion.size() >= 20) break;
                }
            }
        }

        /** Check the word when having delete 2 letter. */
        if (suggestion.size() < 20) {
            for (int i = 0; i < searchingWord.length() - 1; i++) {
                for (int j = i + 1; j < searchingWord.length(); j++) {
                    StringBuffer s = new StringBuffer(searchingWord);
                    s.deleteCharAt(i);
                    s.deleteCharAt(j - 1);
                    for (int k = 0; k < size; k++) {
                        String word = this.getWord(pos, k).getWordTarget();
                        if (word.contains(s) && word.length() <= maxSizeOfSuggestionWord) {
                            if (!suggestion.contains(word))
                                suggestion.add(word);
                            if (suggestion.size() >= 20) break;
                        }
                    }
                }
            }
        }

        /** find word with 3, 2, 1 first letters. */
        if (suggestion.size() < 20) {
            for (int j = 3; j >= 1; j--) {
                String s = searchingWord.substring(0, j);
                for (int i = 0; i < size; i++) {
                    String word = this.getWord(pos, i).getWordTarget();
                    if (word.contains(s) && word.length() < maxSizeOfSuggestionWord) {
                        if (!suggestion.contains(word))
                            suggestion.add(word);
                        if (suggestion.size() >= 20) break;
                    }
                }
            }
        }

        if (suggestion.size() == 0) suggestion.add("No data");

        for (int i = 0; i < suggestion.size(); i++) result += "<li><b>" + suggestion.get(i) + "</b></li>" + "<br/>";
        return result;
    }

    public static void main(String[] args) {
        Dictionary test = new Dictionary();
        Word a = new Word("sds", "aaaaaa");
        //test.addDatabase(a);
        test.editDatabase(a);
    }
}