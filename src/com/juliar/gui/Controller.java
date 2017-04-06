package com.juliar.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;

import com.juliar.JuliarCompiler;

import java.io.*;
import java.lang.ProcessBuilder;

/**
 * Created by AndreiM on 3/18/2017.
 */
public class Controller {

    @FXML
    private TextArea areaText;

    @FXML
    private TextArea areaOutText;

    @FXML
    private TabPane tabPane;

    private TextFile currentTextFile;

    private Model model;

    public Controller(Model model) {
        this.model = model;
    }

    /*@FXML
    public void tabPane.setOnCloseRequest(new EventHandler<Event>(){
        @Override void handle(Event e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("TEST");
            alert.setTitle("TEST");
            alert.setContentText("TEST");
            alert.show();
        }
    });*/

    @FXML
    private void onSave() {
        TextFile textFile = new TextFile(currentTextFile.getFile(), Arrays.asList(areaText.getText().split("\n")));
        model.save(textFile);
    }

    @FXML
    private void onSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        File file =  fileChooser.showSaveDialog(null);
        if (file != null) {
            TextFile textFile = new TextFile(file.toPath(), Arrays.asList(areaText.getText().split("\n")));
            model.save(textFile);
        }
    }

    @FXML
    private void onRunInterpreter(){

        // Create a stream to hold the output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        // IMPORTANT: Save the old System.out!
        PrintStream old = System.out;
        // Tell Java to use your special stream
        System.setOut(ps);
        // Print some output: goes to your special stream
        JuliarCompiler compiler = new JuliarCompiler();
        InputStream is = new ByteArrayInputStream(areaText.getText().getBytes());
        compiler.compile(is, "/", false, false);

        System.out.flush();
        System.setOut(old);
        areaOutText.clear();
        areaOutText.appendText(baos.toString());
        //com.juliar.symbolTable.SymbolTable.DeleteSymbolTable();
    }

    @FXML
    private void onCompileAndRun(){

        // Create a stream to hold the output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        // IMPORTANT: Save the old System.out!
        PrintStream old = System.out;
        // Tell Java to use your special stream
        System.setOut(ps);
        // Print some output: goes to your special stream
        JuliarCompiler compiler = new JuliarCompiler();
        InputStream is = new ByteArrayInputStream(areaText.getText().getBytes());
        compiler.compile(is, "/", false, false);

        System.out.flush();
        System.setOut(old);
        areaOutText.clear();
        areaOutText.appendText(baos.toString());
        //com.juliar.symbolTable.SymbolTable.DeleteSymbolTable();
    }

    @FXML
    private void onRunFCGI(){
        ProcessBuilder pb = new ProcessBuilder("java","-DFCGI_PORT=9000", "-jar", new java.io.File(Controller.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName());
        try {
            File log = new File("log");
            pb.redirectErrorStream(true);
            pb.redirectOutput(log);
            Process p = pb.start();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Juliar FastCGI");
            alert.setHeaderText(null);
            alert.setContentText("Juliar Successfully launched!");
            alert.showAndWait();
        }
        catch(IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Juliar FastCGI Cannot Start");
            alert.setHeaderText("Cannot Start a new instance of Juliar!");
            alert.setContentText(" Error: "+e);
            alert.showAndWait();
        }
    }

    @FXML
    private void onLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            IOResult<TextFile> io = model.load(file.toPath());

            if (io.isOk() && io.hasData()) {
                currentTextFile = io.getData();

                areaText.clear();
                currentTextFile.getContent().forEach(line -> areaText.appendText(line + "\n"));
            } else {
                System.out.println("Failed");
            }
        }
    }

    @FXML
    private void onExit() {
        model.close();
    }

    @FXML
    private void onNew(){
        Tab tab = new Tab("Tab " + (tabPane.getTabs().size() + 1));
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
    }

    @FXML
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("About Juliar.Future");
        alert.setTitle("Juliar.Future");
        alert.setContentText("Juliar - Copyright (C) 2017");
        alert.show();
    }
}
