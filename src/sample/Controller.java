package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;

public class Controller {

    private Stage mainStage;
    private Stage stage;
    private Scene scene;

    @FXML
    public ScrollPane scrollpane;
    @FXML
    private MenuBar menuBar;
    @FXML
    public VBox content;
    @FXML
    public AnchorPane langChoose;
    @FXML
    public ChoiceBox<String> lang1;
    @FXML
    public ChoiceBox<String> lang2;

    private ArrayList<LangRefItem> langRefItems = new ArrayList<>();
    private ATreesItem items;
    private GeneralSchema generalSchemaschema;
    private JSONObject json;

    private boolean lang = false;
    private boolean schema = false;
    private boolean jsonb = false;

    public void init(Stage s, Stage mainStage, Scene scene) {
        stage = s;
        this.mainStage = mainStage;
        this.scene = scene;

        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Lazy Team");
        stage.getIcons().add(new Image("sample/ico.ico"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        langChoose.setVisible(false);

        mainStage.setOnCloseRequest(event -> {
            System.out.println("Close button on view clicked!");
            close();
        });
        new MoveListener(menuBar, stage);
        new MoveListener(scrollpane, stage);
        stage.show();
    }

    private void close() {
        int i = new AllertBox(scene, Modality.APPLICATION_MODAL).displayEvenOd("Sure you want to leave?", "End programm", "yes", "no", false);
        if (i == 0) {
            System.exit(0);
        }
    }

    public void onCheckMonster(ActionEvent actionEvent) {
        JSONObject json = loadJsonFile();
        Object[] a = json.keySet().toArray();

        ArrayList<MnsterError> errors = new ArrayList<>();

        for (Object aS:a) {
            MnsterError er = new MnsterError();
            String s = (String) aS;
            er.setMnsterName(s);
            JSONObject mnster = (JSONObject) json.get(s);

            if(isStringInvalid(s))
                er.addError("name invalid");
            if(isStringInvalid((String) mnster.get("img")))
            er.addError("image invalid");
            try {
                Integer.parseInt((String) mnster.get("hp"));
            } catch (Exception e){
                er.addError("hp invalid");
            }
            try {
                Integer.parseInt((String) mnster.get("lvl"));
            } catch (Exception e){
                er.addError("level invalid");
            }
            if(isStringInvalid((String) mnster.get("rar")))
                er.addError("rarity invalid");

            JSONArray arr = (JSONArray) mnster.get("attacks");
            if(arr == null){
                er.addError("attacks list invalid");
            } else {
                for (Object o:arr) {
                    JSONObject att = (JSONObject) o;

                    String name = ((String) att.get("name"));
                    if(isStringInvalid(name)) {
                        er.addError("attack name invalid");
                        name = "invliad";
                    }

                    try {
                        Integer.parseInt((String) att.get("usage"));
                    } catch (Exception e){
                        er.addError("attack: " + name +" usage invalid");
                    }
                    try {
                        Integer.parseInt((String) att.get("dmg"));
                    } catch (Exception e){
                        er.addError("attack: " + name +" damage invalid");
                    }
                    try {
                        Integer.parseInt((String) att.get("lvl"));
                    } catch (Exception e){
                        er.addError("attack: " + name +" level invalid");
                    }
                }
            }
            errors.add(er);
        }

        if(errors.size()>=1){
            content.getChildren().clear();
            TreeView<String> map = new TreeView<>();
            map.setPrefHeight(719);
            content.getChildren().add(map);
            TreeItem root = new TreeItem();
            root.setExpanded(true);
            root.setValue("errors");
            map.setRoot(root);
            for (MnsterError err:errors) {
                if(err.getError().size()>=1){
                    TreeItem<String> s = new TreeItem<>();
                    s.setExpanded(true);
                    s.setValue(err.getMnsterName());
                    s.getChildren().addAll(arrToTreeItem(err.getError()));
                    root.getChildren().add(s);
                }
            }
        }
    }

    private ArrayList<TreeItem<String>> arrToTreeItem(ArrayList<String> a){
        ArrayList<TreeItem<String>> arr = new ArrayList<>();
        for (String s:a) {
            TreeItem<String> t = new TreeItem<>();
            t.setValue(s);
            arr.add(t);
        }
        return arr;
    }
    
    private boolean isStringInvalid(String s){
        if(s==null)
            return true;

        if(s.equals(""))
            return true;

        return false;
    }

    public class MnsterError {
        String mnsterName;
        ArrayList<String> error = new ArrayList<>();

        public String getMnsterName() {
            return mnsterName;
        }

        public void setMnsterName(String mnsterName) {
            this.mnsterName = mnsterName;
        }

        public void addError(String error){
            this.error.add(error);
        }

        public ArrayList<String> getError() {
            return error;
        }
    }


    public void onImportJson(ActionEvent actionEvent) {
        if(jsonb){
            JSONObject json = loadJsonFile();
            Object[] a = json.keySet().toArray();

            for (Object aS:a) {
                String s = (String) aS;
                if(this.json.containsKey(s))
                    this.json.remove(s);
                JSONObject obj = (JSONObject) json.get(s);
                this.json.put(s, obj);
            }
            buildJsonTree();
        }
    }

    public void onImportJsons(ActionEvent actionEvent) {
        if(jsonb){
            JSONParser parser = new JSONParser();
            Reader reader;
            JSONObject obj;
            DirectoryChooser dc = new DirectoryChooser();
            File dir = dc.showDialog(stage);
            if(dir != null){
                for (File f:dir.listFiles()){
                    if(!f.isFile())
                        continue;
                    try {
                        reader = new FileReader(f.getAbsolutePath());
                        obj = (JSONObject) parser.parse(reader);
                        reader.close();
                    } catch (Exception e) {
                        System.out.println("ERRORORORRO!!!!");
                        return;
                    }

                    Object[] a = obj.keySet().toArray();
                    for (Object aS:a) {
                        String s = (String) aS;
                        JSONObject objj = (JSONObject) obj.get(s);
                        this.json.put(s, objj);
                    }
                }
                buildJsonTree();
            }
        }
    }

    public void onOpenJson(ActionEvent actionEvent) {
        lang = false;
        schema = false;
        jsonb = true;
        langChoose.setVisible(false);
        json = loadJsonFile();
        buildJsonTree();
    }

    public void onOpenJsonLang(ActionEvent actionEvent) {
        lang = true;
        schema = false;
        jsonb = false;
        JSONObject o = loadJsonFile();
        json = o;
        Object[] s = o.keySet().toArray();
        String list = "";
        lang1.getItems().clear();
        lang2.getItems().clear();
        for (int i = 0; i < s.length; i++) {
            String st = (String) s[i];
            lang1.getItems().add(st);
            lang2.getItems().add(st);
        }
        langChoose.setVisible(true);
    }

    public void onLoadAsSchema(ActionEvent actionEvent) {
        lang = false;
        schema = true;
        jsonb = false;
        langChoose.setVisible(false);
        json = loadJsonFile();
        buildSchema();
    }

    public void onJsonSave(ActionEvent actionEvent) {
        JSONObject o = json;
        if (lang) {
            updateLangList();

            JSONObject lang1 = (JSONObject) o.get(langRefItems.get(0).getlLang());
            JSONObject lang2 = (JSONObject) o.get(langRefItems.get(0).getrLang());

            for (LangRefItem i : langRefItems) {
                lang1.remove(i.getlInv());
                lang2.remove(i.getrInv());
                lang1.put(i.getlInv(), i.getlTrans());
                lang2.put(i.getrInv(), i.getrTrans());
            }
        } else if (jsonb) {
            o = new JSONObject();
            buildJsonObjectFromTreeItem(o, items);
        } else if (schema) {
            o = new JSONObject();
            buildJsonObjectFromSchema(generalSchemaschema, o);
        }

        String path;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        try {
            path = fileChooser.showSaveDialog(mainStage).getAbsolutePath();
        } catch (Exception e) {
            new AllertBox(null, Modality.APPLICATION_MODAL).displayMessage("Error", "Please select a valid path!", "ok", "buttonBlue", false);
            return;
        }

        try {
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(o.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            new AllertBox(null, Modality.APPLICATION_MODAL).displayMessage("Error", "Error while save!", "ok", "buttonBlue", false);
        }
    }

    private void buildJsonObjectFromSchema(GeneralSchema s, JSONObject o) {
        for (GeneralSchema g : s.getSchemaObjects()) {
            JSONObject object = new JSONObject();
            buildJsonObjectFromSchema(g, object);
            o.put(g.getName(), object);
        }

        for (SchemaTxtField g : s.getTextFields()) {
            if (g.isChoiceBox())
                o.put(g.getName(), g.getCb().getValue());
            else
                o.put(g.getName(), g.getTxtField().getText());
        }

        for (SchemaArray g : s.getSchemaArrays()) {
            JSONArray jsonArray = new JSONArray();
            buildJsonArrayFromSchema(g, jsonArray);
            o.put(g.getName(), jsonArray);
        }
    }

    private void buildJsonArrayFromSchema(SchemaArray s, JSONArray o) {
        for (GeneralSchema g : s.getSchemaObjects()) {
            JSONObject object = new JSONObject();
            buildJsonObjectFromSchema(g, object);

            o.add(object);
        }

        for (SchemaTxtField g : s.getTextFields()) {
            if (g.isChoiceBox())
                o.add(g.getCb().getValue());
            else
                o.add(g.getTxtField().getText());
        }

        for (SchemaArray g : s.getSchemaArrays()) {
            JSONArray jsonArray = new JSONArray();
            buildJsonArrayFromSchema(g, jsonArray);
            o.add(jsonArray);
        }
    }

    private void buildJsonObjectFromTreeItem(JSONObject o, ATreesItem t) {
        for (ATreesItem item : t.getItems()) {
            switch (item.getContentType()) {
                case Object:
                    JSONObject nObj = new JSONObject();
                    o.put(item.getTextField().getText(), nObj);
                    buildJsonObjectFromTreeItem(nObj, item);
                    break;

                case StringIdent:
                    o.put(item.getTextField().getText(), item.getItems().get(0).getTextField().getText());
                    break;

                case Array:
                    JSONArray arr = new JSONArray();
                    o.put(item.getTextField().getText(), arr);
                    buildJsonArrayFromTreeItem(arr, item);
                    break;
            }
        }
    }

    private void buildJsonArrayFromTreeItem(JSONArray a, ATreesItem t) {
        for (ATreesItem item : t.getItems()) {
            switch (item.getContentType()) {
                case Object:
                    JSONObject nObj = new JSONObject();
                    a.add(nObj);
                    buildJsonObjectFromTreeItem(nObj, item);
                    break;

                case String:
                    a.add(item.getTextField().getText());
                    break;

                case Array:
                    JSONArray arr = new JSONArray();
                    a.addAll(arr);
                    buildJsonArrayFromTreeItem(arr, item);
                    break;
            }
        }
    }

    public void onClose(ActionEvent actionEvent) {
        close();
    }

    public void onApply(ActionEvent actionEvent) {
        buildLangList();
    }

    private void buildJsonTree() {
        content.getChildren().clear();
        items = new ATreesItem(null);
        items.setContentType(ContentType.Object);
        TreeItem<HBox> hBoxTreeItem = items.build("root object");
        hBoxTreeItem.setExpanded(true);
        items.getButtonDelete().setDisable(true);
        try {
            makeItemFromObject(hBoxTreeItem, json, items);
        } catch (Exception e) {
            new AllertBox(scene, Modality.APPLICATION_MODAL).displayMessage("Error", "Error?", "dope", "buttonYellow", false);
            return;
        }

        TreeView<HBox> tree = new TreeView<>();
        tree.setRoot(hBoxTreeItem);
        tree.setPrefHeight(719);
        content.getChildren().add(tree);
    }

    private void makeItemFromObject(TreeItem<HBox> i, JSONObject o, ATreesItem root) {
        Object[] or = o.keySet().toArray();
        for (Object object : or) {
            ATreesItem aTreesItem = new ATreesItem(root);
            String inv = (String) object;
            TreeItem<HBox> treeItem = null;

            if (o.get(inv) instanceof String) {
                System.out.println("is string");
                aTreesItem.setContentType(ContentType.StringIdent);
                ATreesItem aTreesItem1 = new ATreesItem(aTreesItem);
                aTreesItem1.setContentType(ContentType.String);
                treeItem = aTreesItem.build(((String) object).replace("\n", "\\n"));
                treeItem.getChildren().add(aTreesItem1.build(((String) o.get(inv)).replace("\n", "\\n")));
                aTreesItem.getItems().add(aTreesItem1);
            } else if ((o.get(inv)) instanceof JSONObject) {
                System.out.println("is Json Object");
                aTreesItem.setContentType(ContentType.Object);
                treeItem = aTreesItem.build((String) object);
                makeItemFromObject(aTreesItem.getOwnTreeItem(), (JSONObject) o.get(inv), aTreesItem);
            } else if ((o.get(inv)) instanceof JSONArray) {
                System.out.println("is json array");
                aTreesItem.setContentType(ContentType.Array);
                treeItem = aTreesItem.build((String) object);
                makeItemFromArray(treeItem, (JSONArray) o.get(inv), aTreesItem);
            } else {
                System.out.println("Error in parsing Json to Tree Item | Not a valid Class");
                continue;
            }
            i.getChildren().add(treeItem);
            aTreesItem.updateLabel();
            root.getItems().add(aTreesItem);
            if (aTreesItem.getContentType() != ContentType.StringIdent || aTreesItem.getContentType() != ContentType.Array)
                treeItem.setExpanded(true);
        }
    }

    private void makeItemFromArray(TreeItem<HBox> i, JSONArray a, ATreesItem root) {
        for (Object stOb : a) {
            ATreesItem aTreesItem = new ATreesItem(root);
            TreeItem<HBox> hBoxTreeItem = null;
            if (stOb instanceof String) {
                System.out.println("is string");
                aTreesItem.setContentType(ContentType.String);
                hBoxTreeItem = aTreesItem.build(((String) stOb).replace("\n", "\\n"));
            } else if (stOb instanceof JSONObject) {
                System.out.println("is Json Object");
                hBoxTreeItem = aTreesItem.build("");
                aTreesItem.setContentType(ContentType.Object);
                makeItemFromObject(hBoxTreeItem, (JSONObject) stOb, aTreesItem);
            } else if (stOb instanceof JSONArray) {
                System.out.println("is json array");
                aTreesItem.setContentType(ContentType.Array);
                hBoxTreeItem = aTreesItem.build("");
                makeItemFromArray(hBoxTreeItem, (JSONArray) stOb, aTreesItem);
            } else {
                System.out.println("Error in parsing Json to Tree Item | Not a valid Class");
                continue;
            }
            i.getChildren().add(hBoxTreeItem);
            root.getItems().add(aTreesItem);
            aTreesItem.updateLabel();
            hBoxTreeItem.setExpanded(true);
        }
    }

    private void buildLangList() {
        content.getChildren().clear();
        langRefItems.clear();
        String langO = this.lang2.getValue();
        String langB = this.lang1.getValue();
        JSONObject lang1 = (JSONObject) json.get(langO);
        JSONObject lang2 = (JSONObject) json.get(langB);
        Object[] o = lang1.keySet().toArray();
        for (Object oLang : o) {
            String s = (String) oLang;
            HBox hBox = new HBox();
            content.getChildren().add(hBox);
            LangRefItem ref = new LangRefItem(hBox);
            ref.setlInv(s);
            ref.setrInv(s);
            ref.setlLang(langO);
            ref.setrLang(langB);
            if (lang1.get(s) != null) {
                ref.setlTrans(((String) lang1.get(s)).replace("\n", "\\n"));
            }
            if (lang2.get(s) != null) {
                ref.setrTrans(((String) lang2.get(s)).replace("\n", "\\n"));
            }
            ref.build();
            langRefItems.add(ref);
        }
    }

    private void buildSchema() {
        GeneralSchema s = jsonToSchema(json, null);
        s.init2 = true;
        generalSchemaschema = s;
        content.getChildren().clear();

        TreeView<HBox> tree = new TreeView<>();
        tree.setRoot(s.buildSchema(false));
        tree.setPrefHeight(719);
        content.getChildren().add(tree);
    }

    private GeneralSchema jsonToSchema(JSONObject o, GeneralSchema schema) {
        if (schema == null)
            schema = new GeneralSchema();
        for (Object oS : o.keySet().toArray()) {
            String g = (String) oS;
            Object oE = o.get(g);
            if (oE instanceof String) {
                SchemaTxtField txtField = new SchemaTxtField();
                txtField.setName(g);
                txtField.setCbCont((String) oE);
                txtField.setParent(schema);
                schema.getTextFields().add(txtField);
            } else if (oE instanceof JSONObject) {
                GeneralSchema s = new GeneralSchema();
                s.setName(g);
                jsonToSchema((JSONObject) oE, s);
                s.setParent(schema);
                schema.getSchemaObjects().add(s);
            } else if (oE instanceof JSONArray) {
                SchemaArray a = new SchemaArray();
                a.setName(g);
                loadInnerArraySchema(a, (JSONArray) oE);
                a.setParent(schema);
                schema.getSchemaArrays().add(a);
            }
        }
        return schema;
    }

    private void loadInnerArraySchema(SchemaArray o, JSONArray obj) {
        for (Object oS : obj.toArray()) {
            if (oS instanceof String) {
                SchemaTxtField txtField = new SchemaTxtField();
                txtField.setParent(o);
                txtField.setCbCont((String) oS);
                o.getTextFields().add(txtField);
            } else if (oS instanceof JSONObject) {
                GeneralSchema s = new GeneralSchema();
                s.setParent(o);
                jsonToSchema((JSONObject) oS, s);
                o.getSchemaObjects().add(s);
            } else if (oS instanceof JSONArray) {
                SchemaArray a = new SchemaArray();
                a.setParent(o);
                loadInnerArraySchema(a, (JSONArray) oS);
                o.getSchemaArrays().add(a);
            }
        }
    }

    private void rebuildLangList() {
        content.getChildren().clear();
        for (LangRefItem i : langRefItems) {
            i.getBox().getChildren().clear();
            i.build();
            content.getChildren().add(i.getBox());
        }
    }

    private void updateLangList() {
        for (LangRefItem i : langRefItems) {
            i.update();
        }
    }

    private JSONObject loadJsonFile() {
        String path;
        FileChooser fileChooser = new FileChooser();
        //fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        try {
            path = fileChooser.showOpenDialog(mainStage).getAbsolutePath();
        } catch (Exception e) {
            new AllertBox(null, Modality.APPLICATION_MODAL).displayMessage("Error", "Please select a valid path!", "ok", "buttonBlue", false);
            e.printStackTrace();
            return null;
        }

        File file = new File(path);
        JSONObject object;
        JSONParser parser = new JSONParser();
        try {
            Reader reader = new FileReader(file.getAbsolutePath());
            object = (JSONObject) parser.parse(reader);
            reader.close();
        } catch (Exception e) {
            System.out.println("ERRORORORRO!!!!");
            return null;
        }
        return object;
    }

    public void onJsonCreate(ActionEvent actionEvent) {
        langChoose.setVisible(false);
        lang = false;
        json = new JSONObject();
        buildJsonTree();
    }

    public void onCreateLang(ActionEvent actionEvent) {
        if (langRefItems == null) {
            new AllertBox(scene, Modality.APPLICATION_MODAL).displayMessage("Error", "You didn't opened a lang file yet!", "dough", "buttonYellow", false);
            return;
        }
        if (lang || langRefItems.size() > 0) {
            HBox hBox = new HBox();
            LangRefItem langRefItem = new LangRefItem(hBox);
            langRefItem.setrLang(lang1.getValue());
            langRefItem.setlLang(lang2.getValue());
            langRefItem.build();
            langRefItems.add(langRefItem);
            content.getChildren().add(hBox);
        } else {
            new AllertBox(scene, Modality.APPLICATION_MODAL).displayMessage("Error", "You didn't opened a lang file yet!", "dough", "buttonYellow", false);
        }
    }


    private class GeneralSchema {
        private ArrayList<SchemaTxtField> textFields = new ArrayList<>();
        private ArrayList<SchemaArray> schemaArrays = new ArrayList<>();
        private ArrayList<GeneralSchema> schemaObjects = new ArrayList<>();
        private String name = "root";
        private GeneralSchema parent;
        private HBox ownBox;
        private TreeItem<HBox> ownTreeItem;
        private Label label = new Label();
        private TextField textField = new TextField();
        private boolean init2 = false;
        private boolean init = false;

        public TreeItem<HBox> buildSchema(boolean buttons) {
            ownTreeItem = new TreeItem<>();
            ownBox = new HBox();
            ownBox.setSpacing(10);
            ownTreeItem.setValue(ownBox);
            label.setText(name);
            if (init)
                ownBox.getChildren().add(textField);
            else
                ownBox.getChildren().add(label);
            for (SchemaTxtField txtField : textFields) {
                ownTreeItem.getChildren().add(txtField.buildSchema());
            }

            for (SchemaArray array : schemaArrays) {
                ownTreeItem.getChildren().add(array.buildSchema());
            }

            for (GeneralSchema schema : schemaObjects) {
                if(init2)
                    schema.init = true;
                ownTreeItem.getChildren().add(schema.buildSchema(false));
            }
            if (buttons)
                putTxtButtons(false, true);
            return ownTreeItem;
        }

        private void putTxtButtons(boolean adding, boolean del) {
            Button delete = new Button("X");
            delete.setId("buttonRed");
            delete.setOnAction(e -> {
                getParent().getSchemaObjects().remove(this);
                getParent().getOwnTreeItem().getChildren().remove(getOwnTreeItem());
            });

            Button add = null;
            if (adding) {
                add = new Button("+");
                add.setId("buttonGreen");
                add.setOnAction(e -> {
                    GeneralSchema g = cloneBase();
                    getOwnTreeItem().getChildren().add(g.buildSchema(true));
                    getSchemaObjects().add(g);
                });
            }
            if (add != null)
                ownBox.getChildren().add(add);

            if (del)
                ownBox.getChildren().add(delete);
        }

        public GeneralSchema cloneBase() {
            GeneralSchema gs = new GeneralSchema();
            gs.setParent(getParent());
            gs.setName(getName());
            gs.setSchemaArrays(copyArray());
            gs.setSchemaObjects(copyObj());
            gs.setTextFields(copyTxt());

            return gs;
        }

        public ArrayList<SchemaArray> copyArray() {
            ArrayList<SchemaArray> ar = new ArrayList<>();
            for (SchemaArray arr : schemaArrays)
                ar.add(arr.clone());
            return ar;
        }

        public ArrayList<GeneralSchema> copyObj() {
            ArrayList<GeneralSchema> ar = new ArrayList<>();
            for (GeneralSchema arr : schemaObjects)
                ar.add(arr.cloneBase());
            return ar;
        }

        public ArrayList<SchemaTxtField> copyTxt() {
            ArrayList<SchemaTxtField> ar = new ArrayList<>();
            for (SchemaTxtField arr : textFields)
                ar.add(arr.clone());
            return ar;
        }

        public TreeItem<HBox> getOwnTreeItem() {
            return ownTreeItem;
        }

        public void setOwnTreeItem(TreeItem<HBox> ownTreeItem) {
            this.ownTreeItem = ownTreeItem;
        }

        public HBox getOwnBox() {
            return ownBox;
        }

        public void setOwnBox(HBox ownBox) {
            this.ownBox = ownBox;
        }

        public GeneralSchema getParent() {
            return parent;
        }

        public void setParent(GeneralSchema parent) {
            this.parent = parent;
        }

        public ArrayList<SchemaTxtField> getTextFields() {
            return textFields;
        }

        public void setTextFields(ArrayList<SchemaTxtField> textFields) {
            this.textFields = textFields;
        }

        public ArrayList<SchemaArray> getSchemaArrays() {
            return schemaArrays;
        }

        public void setSchemaArrays(ArrayList<SchemaArray> schemaArrays) {
            this.schemaArrays = schemaArrays;
        }

        public ArrayList<GeneralSchema> getSchemaObjects() {
            return schemaObjects;
        }

        public void setSchemaObjects(ArrayList<GeneralSchema> schemaObjects) {
            this.schemaObjects = schemaObjects;
        }

        public String getName() {
            if(init)
                return textField.getText();
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private class SchemaTxtField extends GeneralSchema {
        private TextField txtField = new TextField();
        private ChoiceBox<String> cb = new ChoiceBox<>();
        private boolean choiceBox = false;
        private String cbCont;
        boolean isInArray = false;

        private void putTxtButtons(boolean adding) {
            Button delete = new Button("X");
            delete.setId("buttonRed");
            delete.setOnAction(e -> {
                getParent().getTextFields().remove(this);
                getParent().getOwnTreeItem().getChildren().remove(getOwnTreeItem());
            });

            Button add = null;
            if (adding) {
                add = new Button("+");
                add.setId("buttonGreen");
                add.setOnAction(e -> {
                    SchemaTxtField t = clone();
                    getParent().getTextFields().add(t);
                    getParent().getOwnTreeItem().getChildren().add(t.buildSchema());
                });
            }
            if (add != null)
                getOwnBox().getChildren().add(add);
            getOwnBox().getChildren().add(delete);
        }

        public TreeItem<HBox> buildSchema() {
            super.buildSchema(false);
            loadTxtSchema();
            if (choiceBox)
                getOwnBox().getChildren().add(cb);
            else
                getOwnBox().getChildren().add(txtField);

            getOwnTreeItem().setValue(getOwnBox());

            if (isInArray)
                putTxtButtons(false);
            return getOwnTreeItem();
        }

        public SchemaTxtField clone() {
            SchemaTxtField t = new SchemaTxtField();
            t.setParent(getParent());
            t.setName(getName());
            t.setChoiceBox(choiceBox);
            t.setCbCont(cbCont);
            t.setSchemaArrays(copyArray());
            t.setSchemaObjects(copyObj());
            t.setTextFields(copyTxt());

            return t;
        }

        public void loadTxtSchema() {
            if (cbCont != null)
                if (!cbCont.equals("")) {

                    String[] ar = cbCont.split("/");
                    if (ar.length > 0) {
                        for (String s : ar) {
                            cb.getItems().add(s);
                        }
                        choiceBox = true;
                    }
                }
        }

        public boolean isInArray() {
            return isInArray;
        }

        public void setInArray(boolean inArray) {
            isInArray = inArray;
        }

        public String getCbCont() {
            return cbCont;
        }

        public void setCbCont(String cbCont) {
            this.cbCont = cbCont;
        }

        public boolean isChoiceBox() {
            return choiceBox;
        }

        public void setChoiceBox(boolean choiceBox) {
            this.choiceBox = choiceBox;
        }

        public ChoiceBox<String> getCb() {
            return cb;
        }

        public void setCb(ChoiceBox<String> cb) {
            this.cb = cb;
        }

        public TextField getTxtField() {
            return txtField;
        }

        public void setTxtField(TextField txtField) {
            this.txtField = txtField;
        }
    }

    private class SchemaArray extends GeneralSchema {

        String schema;

        public TreeItem<HBox> buildSchema() {
            boolean b = getSchemaObjects().size() > 0;
            super.buildSchema(false);

            Button add;
            add = new Button("+");
            add.setId("buttonGreen");
            add.setOnAction(e -> {
                if (getSchemaObjects().size() > 0) {
                    GeneralSchema s = getSchemaObjects().get(0).cloneBase();
                    getOwnTreeItem().getChildren().add(s.buildSchema(true));
                    getSchemaObjects().add(s);
                } else {
                    SchemaTxtField txtField = new SchemaTxtField();
                    txtField.setCbCont(schema);
                    txtField.setParent(this);
                    txtField.setInArray(true);
                    getTextFields().add(txtField);
                    getOwnTreeItem().getChildren().add(txtField.buildSchema());
                }
            });
            getOwnBox().getChildren().add(add);

            return getOwnTreeItem();
        }

        public SchemaArray clone() {
            SchemaArray t = new SchemaArray();
            t.setParent(getParent());
            t.setName(getName());
            t.setSchemaArrays(copyArray());
            t.setSchemaObjects(copyObj());
            t.setTextFields(copyTxt());

            return t;
        }
    }

    private class ATreesItem {
        private TreeItem<HBox> ownTreeItem;
        private TextField textField;
        private HBox box;

        private ATreesItem parent;
        private ArrayList<ATreesItem> items;

        private ContentType contentType;

        private Button buttonDelete;
        private Button buttonAdd;

        private Label label;

        private Button o;
        private Button a;
        private Button s;

        public ATreesItem(ATreesItem parent) {
            this.parent = parent;
        }

        public void updateLabel() {
            label.setText(contentTypeToString(contentType));
        }

        public TreeItem<HBox> build(String initialText) {
            label = new Label(contentTypeToString(contentType));
            items = new ArrayList<>();
            box = new HBox();
            box.setSpacing(10);
            textField = new TextField(initialText);
            buttonDelete = new Button("X");
            buttonDelete.setId("buttonRed");
            buttonDelete.setOnAction(e -> {
                if (contentType == ContentType.String) {
                    if (parent.contentType == ContentType.StringIdent) {
                        parent.getParent().getItems().remove(parent);
                        parent.getParent().getOwnTreeItem().getChildren().remove(parent.getOwnTreeItem());
                    }
                }
                parent.getItems().remove(this);
                parent.getOwnTreeItem().getChildren().remove(ownTreeItem);
            });

            buttonAdd = new Button("+");
            buttonAdd.setId("buttonGreen");
            buttonAdd.setOnAction(e -> {
                box.getChildren().remove(buttonAdd);
                o = new Button("Object");
                a = new Button("Array");
                s = new Button("String");
                setDefaultAddButton(o, a, s);

                o.setOnAction(ev -> {
                    ATreesItem aTreesItem = new ATreesItem(this);
                    aTreesItem.setContentType(ContentType.Object);
                    ownTreeItem.getChildren().add(aTreesItem.build(""));
                    items.add(aTreesItem);
                    redoEdit();
                });

                a.setOnAction(ev -> {
                    ATreesItem aTreesItem = new ATreesItem(this);
                    aTreesItem.setContentType(ContentType.Array);
                    ownTreeItem.getChildren().add(aTreesItem.build(""));
                    items.add(aTreesItem);
                    redoEdit();
                });

                s.setOnAction(ev -> {
                    ATreesItem aTreesItem = new ATreesItem(this);
                    if (contentType != ContentType.Array) {
                        aTreesItem.setContentType(ContentType.StringIdent);
                        ATreesItem aTreesItem1 = new ATreesItem(aTreesItem);
                        aTreesItem1.setContentType(ContentType.String);
                        ownTreeItem.getChildren().add(aTreesItem.build(""));
                        aTreesItem.getOwnTreeItem().getChildren().add(aTreesItem1.build(""));
                        aTreesItem.getItems().add(aTreesItem1);
                    } else {
                        aTreesItem.setContentType(ContentType.String);
                        ownTreeItem.getChildren().add(aTreesItem.build(""));
                    }
                    items.add(aTreesItem);
                    redoEdit();
                });

                box.getChildren().addAll(o, a, s);
            });

            if (contentType == ContentType.String || contentType == ContentType.StringIdent)
                box.getChildren().addAll(label, textField, buttonDelete);
            else
                box.getChildren().addAll(label, textField, buttonDelete, buttonAdd);

            ownTreeItem = new TreeItem<>();
            ownTreeItem.setValue(box);

            return ownTreeItem;
        }

        private void setDefaultAddButton(Button... bn) {
            for (Button b : bn) {
                b.setId("buttonYellow");
            }
        }

        private void redoEdit() {
            box.getChildren().removeAll(o, a, s);
            box.getChildren().add(buttonAdd);
        }

        public TextField getTextField() {
            return textField;
        }

        public void setTextField(TextField textField) {
            this.textField = textField;
        }

        public HBox getBox() {
            return box;
        }

        public void setBox(HBox box) {
            this.box = box;
        }

        public ContentType getContentType() {
            return contentType;
        }

        public void setContentType(ContentType contentType) {
            this.contentType = contentType;
        }

        public ArrayList<ATreesItem> getItems() {
            return items;
        }

        public void setItems(ArrayList<ATreesItem> items) {
            this.items = items;
        }

        public TreeItem<HBox> getOwnTreeItem() {
            return ownTreeItem;
        }

        public void setOwnTreeItem(TreeItem<HBox> ownTreeItem) {
            this.ownTreeItem = ownTreeItem;
        }

        public ATreesItem getParent() {
            return parent;
        }

        public void setParent(ATreesItem parent) {
            this.parent = parent;
        }

        public Button getButtonDelete() {
            return buttonDelete;
        }

        public void setButtonDelete(Button buttonDelete) {
            this.buttonDelete = buttonDelete;
        }
    }

    private enum ContentType {
        StringIdent, Array, Object, String
    }

    private String contentTypeToString(ContentType type) {
        if (type == null) {
            return "undefined";
        }
        switch (type) {
            case Array:
                return "Array";

            case Object:
                return "Object";

            case String:
                return "String";

            case StringIdent:
                return "String identifier";
        }
        return "undefined";
    }

    private class LangRefItem {
        private String lLang;
        private String lInv;
        private String lTrans;

        private String rLang;
        private String rInv;
        private String rTrans;

        private HBox box;

        private Label lLangTxt;
        private TextField lInvTxt;
        private TextField lTransTxt;

        private Label rLangTxt;
        private TextField rInvTxt;
        private TextField rTransTxt;

        public LangRefItem(HBox box) {
            this.box = box;
        }

        public void update() {
            lInv = lInvTxt.getText();
            lTrans = lTransTxt.getText();

            rInv = rInvTxt.getText();
            rTrans = rTransTxt.getText();
        }

        public void build() {
            box.setStyle("-fx-spacing: 10px;");
            box.setId("cItem");
            lLangTxt = setupLabelTxt(lLang);
            lLangTxt.setPadding(new Insets(0, 0, 0, 15));

            lInvTxt = setupParmTxt(lInv);

            lTransTxt = setupParmTxt(lTrans);

            Separator separator = new Separator();
            separator.setOrientation(Orientation.VERTICAL);
            separator.setPadding(new Insets(0, 10, 0, 20));

            rLangTxt = setupLabelTxt(rLang);

            rInvTxt = setupParmTxt(rInv);

            rTransTxt = setupParmTxt(rTrans);

            Button b = new Button("X");
            b.setId("buttonRed");
            b.setPadding(new Insets(4, 4, 4, 4));
            b.setOnAction(e -> {
                langRefItems.remove(this);
                rebuildLangList();
            });

            box.getChildren().addAll(lLangTxt, lInvTxt, lTransTxt, separator, rLangTxt, rInvTxt, rTransTxt, b);
        }

        private TextField setupParmTxt(String txt) {
            TextField n = new TextField();
            if (txt == null)
                n.setStyle("-fx-background-color: \"red\";");
            else
                n.setText(txt);

            if(txt.equals(""))
                n.setStyle("-fx-background-color: \"red\";");

            n.setOnMouseClicked(l -> {
                n.setStyle("");
            });

            n.setPrefWidth(250);

            return n;
        }

        private Label setupLabelTxt(String txt) {
            Label l = new Label(txt);
            l.setPrefWidth(30);
            l.setPrefHeight(10);
            //l.setPadding(new Insets(5, 0,5,15));
            return l;
        }

        public String getlLang() {
            return lLang;
        }

        public void setlLang(String lLang) {
            this.lLang = lLang;
        }

        public String getlInv() {
            return lInv;
        }

        public void setlInv(String lInv) {
            this.lInv = lInv;
        }

        public String getlTrans() {
            return lTrans;
        }

        public void setlTrans(String lTrans) {
            this.lTrans = lTrans;
        }

        public String getrLang() {
            return rLang;
        }

        public void setrLang(String rLang) {
            this.rLang = rLang;
        }

        public String getrInv() {
            return rInv;
        }

        public void setrInv(String rInv) {
            this.rInv = rInv;
        }

        public String getrTrans() {
            return rTrans;
        }

        public void setrTrans(String rTrans) {
            this.rTrans = rTrans;
        }

        public HBox getBox() {
            return box;
        }
    }
}