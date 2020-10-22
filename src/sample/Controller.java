package sample;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class Controller implements Initializable {

    @FXML
    Button search;

    @FXML
    TextField textSearch;

    @FXML
    TextArea result;

    @FXML
    ListView<String> words;

    @FXML
    Button translate;

    @FXML
    Button voice;

    @FXML
    WebView webView;
    WebEngine engine;

    @FXML
    ContextMenu contextMenu;

    String wordClick;
    public Dictionary myDictionary = new Dictionary();

    public ObservableList names = FXCollections.observableArrayList();

    public Translate Google = new Translate();
    public Voice speak = new Voice();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        engine = webView.getEngine();
        textSearch.setPromptText("Searching...");
        search.setOnAction(event -> {
            words.scrollTo(0);
            String wordSearch = textSearch.getText().toLowerCase();
            if (wordSearch != null && wordSearch.equals("") == false && Word.check(wordSearch)) {
                int size = myDictionary.getLibraryAt(wordSearch.charAt(0) - 97).getSize();
                String wordMeaning = myDictionary.dictionaryLookup(wordSearch, 0, size);

                //result.setText(wordMeaning);
                engine.loadContent(wordMeaning);

                ArrayList<Word> temp = myDictionary.dictionarySearcher(wordSearch);
                names.clear();
                for (int i = 0; i < temp.size(); i++) {
                    names.add(temp.get(i).getWordTarget());
                }
                words.setItems(names);
            } else {
                //result.setText("No data");
                engine.loadContent("This word is not correct!");
            }
        });

        /** press enter to show translation. */

        textSearch.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    words.scrollTo(0);
                    String wordSearch = textSearch.getText().toLowerCase();
                    if (wordSearch != null && wordSearch.equals("") == false && Word.check(wordSearch)) {
                        int size = myDictionary.getLibraryAt(wordSearch.charAt(0) - 97).getSize();
                        String wordMeaning = myDictionary.dictionaryLookup(wordSearch, 0, size);

                        //result.setText(wordMeaning);
                        engine.loadContent(wordMeaning);

                        ArrayList<Word> temp = myDictionary.dictionarySearcher(wordSearch);
                        names.clear();
                        for (int i = 0; i < temp.size(); i++) {
                            names.add(temp.get(i).getWordTarget());
                        }
                        words.setItems(names);
                    } else {
                        //result.setText("No data");
                        engine.loadContent("This word is not correct!");
                    }
                }
            }
        });

        this.initializeWordList();

        words.setOnMouseClicked(event -> {
            wordClick = words.getSelectionModel().getSelectedItem();
            textSearch.setText(wordClick);
            if (wordClick != null && wordClick.equals("") == false) {
                int size = myDictionary.getLibraryAt(wordClick.charAt(0) - 97).getSize();
                String wordMeaning = myDictionary.dictionaryLookup(wordClick, 0, size);
                //result.setText(wordMeaning);
                engine.loadContent(wordMeaning);
            }
        });

        translate.setOnAction(e -> {
            String wordTarget = textSearch.getText();
            String wordExplain = null;
            try {
                wordExplain = Google.translate("en", "vi", wordTarget);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            //result.setText(wordExplain + "\n" + "Translate by Google API");
            engine.loadContent(wordExplain+"<br/>Translate by Google API");
        });

        voice.setOnAction(e -> {
            String wordTarget = textSearch.getText();
            speak.sayMultiple(wordTarget);
        });
    }

    public void initializeWordList() {
        myDictionary.insertFromFile();
        for (int i = 0; i < 26; i++) {
            int size = myDictionary.getLibraryAt(i).getSize();
            for (int j = 0; j < size; j++) {
                words.getItems().add(myDictionary.getWord(i, j).getWordTarget());
                names.add(myDictionary.getWord(i, j).getWordTarget());
            }
        }
    }

    public void addANewWord() {
        Dialog<Word> dialog = new Dialog<>();
        dialog.setTitle("Add a new word");
        dialog.setHeaderText("English");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField wordTarget = new TextField();
        wordTarget.setPromptText("Word target");
        TextField wordExplain = new TextField();
        wordExplain.setPromptText("Word explain");

        grid.add(new Label("Word target:"), 0, 0);
        grid.add(wordTarget, 1, 0);
        grid.add(new Label("Word explain:"), 0, 1);
        grid.add(wordExplain, 1, 1);

        Node loginButton = dialog.getDialogPane().lookupButton(addButtonType);
        loginButton.setDisable(true);

        wordExplain.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Word(wordTarget.getText(), wordExplain.getText());
            }
            return null;
        });
        Optional<Word> result = dialog.showAndWait();
        result.ifPresent(newWord -> {
            newWord.setWordTarget(newWord.getWordTarget().toLowerCase());
            WordLibrary currentLibrary = myDictionary.getLibraryAt(newWord.getWordTarget().charAt(0) - 97);
            String newWordExplain = myDictionary.dictionaryLookup(newWord.getWordTarget(), 0, currentLibrary.getSize());
            if (newWordExplain.charAt(1) == 'e') {
                Word add = new Word(newWord.getWordTarget(), newWord.getWordExplain());
                myDictionary.getLibraryAt(newWord.getWordTarget().charAt(0) - 97).addWord(add);
                int index = myDictionary.getLibraryAt(newWord.getWordTarget().charAt(0) - 97).getSize();
                //words.getItems().add(myDictionary.getWord(wordTarget.getText().charAt(0) - 97, index - 1).getWordTarget());
                names.add(add.getWordTarget());
                Collections.sort(myDictionary.getLibraryAt(add.getWordTarget().charAt(0) - 97).getLibrary());
                FXCollections.sort(names);
                words.setItems(names);
                myDictionary.addDatabase(add);


                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Add word");
                alert1.setHeaderText("Notification");
                alert1.setContentText("You've already added a new word: " +
                        wordTarget.getText().toLowerCase() + "\n with the explanation: " + wordExplain.getText());
                alert1.show();
                this.print();
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("This word have already existed in the dictionary");
                alert.setContentText("Do you want to edit?");

                ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
                ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

                Optional<ButtonType> result1 = alert.showAndWait();

                if (result1.get() == buttonTypeYes) {
                    this.EditWord();
                }
            }
        });
    }

    public void DeleteWord() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Delete a word");
        dialog.setHeaderText("English");

        ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField wordTarget = new TextField();
        wordTarget.setPromptText("Word target");

        grid.add(new Label("Word target:"), 0, 0);
        grid.add(wordTarget, 1, 0);

        Node loginButton = dialog.getDialogPane().lookupButton(deleteButtonType);
        loginButton.setDisable(true);

        wordTarget.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == deleteButtonType) {
                return wordTarget.getText();
            }
            return null;
        });
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newWord -> {
            newWord = newWord.toLowerCase();
            WordLibrary currentLibrary = myDictionary.getLibraryAt(newWord.charAt(0) - 97);
            String newWordExplain = myDictionary.dictionaryLookup(newWord, 0, currentLibrary.getSize());
            // check if the word exists.
            if (newWordExplain.charAt(1) != 'e') {
                currentLibrary.deleteWord(newWord);
                names.remove(newWord);
                words.setItems(names);
                myDictionary.deleteDatabase(newWord);

                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Delete word");
                alert1.setHeaderText("Notification");
                alert1.setContentText("You've already deleted the word: " + newWord);
                alert1.show();
                this.print();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Delete word");
                alert1.setHeaderText("Notification");
                alert1.setContentText("This word doesn't exist in this dictionary!"
                        + "\nPlease try again.");
                alert1.show();
            }
        });
    }

    public void EditWord() {
        Dialog<Word> dialog = new Dialog<>();
        dialog.setTitle("Edit a word");
        dialog.setHeaderText("English");

        ButtonType editButtonType = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(editButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField wordTarget = new TextField();
        wordTarget.setPromptText("Word target");
        TextField wordExplain = new TextField();
        wordExplain.setPromptText("Word explain");

        grid.add(new Label("Word target:"), 0, 0);
        grid.add(wordTarget, 1, 0);
        grid.add(new Label("Word explain:"), 0, 1);
        grid.add(wordExplain, 1, 1);

        Node loginButton = dialog.getDialogPane().lookupButton(editButtonType);
        loginButton.setDisable(true);

        wordTarget.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == editButtonType) {
                return new Word(wordTarget.getText(), wordExplain.getText());
            }
            return null;
        });
        Optional<Word> result = dialog.showAndWait();
        result.ifPresent(newWord -> {
            newWord.setWordTarget(newWord.getWordTarget().toLowerCase());
            WordLibrary currentLibrary = myDictionary.getLibraryAt(newWord.getWordTarget().charAt(0) - 97);
            String newWordExplain = myDictionary.dictionaryLookup(newWord.getWordTarget(), 0, currentLibrary.getSize());
            if (newWordExplain.charAt(1) != 'e') {
                String temp = newWord.getWordTarget();
                myDictionary.getLibraryAt(temp.charAt(0) - 97).editWord(newWord);
                myDictionary.editDatabase(newWord);

                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Add word");
                alert1.setHeaderText("Notification");
                alert1.setContentText("You've already added a new word: " +
                        wordTarget.getText() + "\n with the explanation: " + wordExplain.getText());
                alert1.show();
                this.print();
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("This word have not existed in the dictionary");
                alert.setContentText("Do you want to add?");

                ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
                ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

                Optional<ButtonType> result1 = alert.showAndWait();

                if (result1.get() == buttonTypeYes) {
                    this.addANewWord();
                }
            }
        });

        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
        alert1.setTitle("Edit word");
        alert1.setHeaderText("Notification");
        alert1.setContentText("You've already changed the explain of word: "
                + wordTarget.getText() + " to " + wordExplain.getText());
        alert1.show();
    }

    public void DeleteWordRightMouse() {
        // Show alert.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Deletion Confirmation");
        alert.setHeaderText("Are you sure to delete this word permanently?");

        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeYes) {
            myDictionary.getLibraryAt(wordClick.charAt(0) - 97).deleteWord(wordClick);
            names.remove(wordClick);
            words.setItems(names);
            myDictionary.deleteDatabase(wordClick);

            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Delete word");
            alert1.setHeaderText("Notification");
            alert1.setContentText("You've already delete the word: " + wordClick);
            alert1.show();
        }
    }

    public void EditWordRightMouse() {
        Dialog<Word> dialog = new Dialog<>();
        dialog.setTitle("Edit a word");
        dialog.setHeaderText("English");

        ButtonType editButtonType = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(editButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField wordExplain = new TextField();
        wordExplain.setPromptText("New word explain");

        grid.add(new Label("New word explain:"), 0, 0);
        grid.add(wordExplain, 0, 1);

        Node loginButton = dialog.getDialogPane().lookupButton(editButtonType);
        //loginButton.setDisable(true);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == editButtonType) {
                return new Word(wordClick, wordExplain.getText());
            }
            return null;
        });
        Optional<Word> result = dialog.showAndWait();
        result.ifPresent(newWord -> {
            String temp = newWord.getWordTarget();
            myDictionary.getLibraryAt(temp.charAt(0) - 97).editWord(newWord);
            myDictionary.editDatabase(newWord);
        });

        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
        alert1.setTitle("Edit word");
        alert1.setHeaderText("Notification");
        alert1.setContentText("You've already changed the explain of word: " + wordClick);
        alert1.show();
    }

    public void Action (ActionEvent event){
        Platform.exit();
        System.exit(0);
    }

    public void print() {
        names.clear();
        for (int i = 0; i < 26; i++) {
            int size = myDictionary.getLibraryAt(i).getSize();
            for (int j = 0; j < size; j++) {
                names.add(myDictionary.getWord(i, j).getWordTarget());
            }
        }
        words.setItems(names);
    }
}